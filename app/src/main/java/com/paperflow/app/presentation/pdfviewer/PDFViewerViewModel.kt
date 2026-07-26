package com.paperflow.app.presentation.pdfviewer

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.PDFEngine
import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.repository.ActivityRepository
import com.paperflow.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.io.File
import javax.inject.Inject

data class PDFViewerUiState(
    val document: Document? = null,
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val pageBitmaps: Map<Int, Bitmap> = emptyMap(), // Cached rendered bitmaps
    val isLoading: Boolean = true,
    val isToolbarVisible: Boolean = true,
    val brightness: Float = 1f,
    val viewMode: PDFViewMode = PDFViewMode.SINGLE,
    val zoomLevel: Float = 1f,
    val annotations: List<Annotation> = emptyList(),
    val error: String? = null,
)

enum class PDFViewMode { SINGLE, SCROLL }

@HiltViewModel
class PDFViewerViewModel @Inject constructor(
    private val getDocumentById: GetDocumentByIdUseCase,
    private val saveReadingPosition: SaveReadingPositionUseCase,
    private val getAnnotations: GetAnnotationsUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val activityRepo: ActivityRepository,
    private val pdfEngine: PDFEngine,
    private val storage: FileStorage,
) : ViewModel() {

    private val _state = MutableStateFlow(PDFViewerUiState())
    val state: StateFlow<PDFViewerUiState> = _state.asStateFlow()

    private val renderScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val renderJobs = mutableMapOf<Int, Job>()

    fun loadDocument(documentId: Long, initialPage: Int) {
        viewModelScope.launch {
            val doc = getDocumentById(documentId)
            if (doc == null) {
                _state.update { it.copy(error = "Document not found", isLoading = false) }
                return@launch
            }
            val file = File(doc.filePath)
            val pageCount = if (doc.type == DocumentType.PDF) pdfEngine.getPageCount(file) else 1
            _state.update {
                it.copy(
                    document = doc,
                    currentPage = initialPage.coerceIn(0, (pageCount - 1).coerceAtLeast(0)),
                    totalPages = pageCount,
                    isLoading = false,
                )
            }
            activityRepo.logActivity(documentId, "opened")
            // Pre-render current + adjacent pages
            preRenderPages(doc, initialPage, pageCount)
        }
    }

    fun goToPage(page: Int) {
        val state = _state.value
        val doc = state.document ?: return
        val clamped = page.coerceIn(0, (state.totalPages - 1).coerceAtLeast(0))
        _state.update { it.copy(currentPage = clamped) }
        saveReadingPosition(clamped, doc.id)
        preRenderPages(doc, clamped, state.totalPages)
    }

    fun toggleToolbar() = _state.update { it.copy(isToolbarVisible = !it.isToolbarVisible) }

    fun setBrightness(brightness: Float) = _state.update { it.copy(brightness = brightness) }

    fun setViewMode(mode: PDFViewMode) = _state.update { it.copy(viewMode = mode) }

    fun setZoom(zoom: Float) = _state.update { it.copy(zoomLevel = zoom.coerceIn(0.5f, 4f)) }

    fun toggleFavorite() {
        val doc = _state.value.document ?: return
        viewModelScope.launch { toggleFavorite(doc.id, doc.isFavorite) }
    }

    /**
     * Render current page ± 2 pages into bitmap cache.
     * Evicts pages outside window to limit memory usage.
     */
    private fun preRenderPages(doc: Document, currentPage: Int, totalPages: Int) {
        if (doc.type != DocumentType.PDF) return
        val file = File(doc.filePath)
        val window = (currentPage - 2..currentPage + 2).filter { it in 0 until totalPages }

        // Cancel render jobs outside window
        renderJobs.entries
            .filter { it.key !in window }
            .forEach { (page, job) -> job.cancel(); renderJobs.remove(page) }

        window.forEach { page ->
            if (_state.value.pageBitmaps.containsKey(page)) return@forEach
            if (renderJobs.containsKey(page)) return@forEach
            renderJobs[page] = renderScope.launch {
                val bitmap = pdfEngine.renderPage(file, page, 900) ?: return@launch
                _state.update { state ->
                    // Evict far pages to prevent OOM (keep ±3 around current)
                    val current = state.currentPage
                    val evicted = state.pageBitmaps.filter { (p, _) -> kotlin.math.abs(p - current) <= 3 }
                    state.copy(pageBitmaps = evicted + (page to bitmap))
                }
            }
        }
    }

    private fun saveReadingPosition(page: Int, docId: Long) {
        viewModelScope.launch { saveReadingPosition(docId, page) }
    }

    override fun onCleared() {
        super.onCleared()
        renderScope.cancel()
        // Recycle all cached bitmaps
        _state.value.pageBitmaps.values.forEach { it.recycle() }
    }
}
