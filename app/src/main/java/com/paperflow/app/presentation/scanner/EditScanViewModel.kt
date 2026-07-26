package com.paperflow.app.presentation.scanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.ScanFilterMode
import com.paperflow.app.data.local.file.ScanProcessor
import com.paperflow.app.domain.usecase.SaveScanAsDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class EditScanUiState(
    val pages: List<String> = emptyList(), // image file paths in order
    val selectedPage: Int = 0,
    val selectedFilter: Int = 0,
    val isReordering: Boolean = false,
    val isSaving: Boolean = false,
    val savedDocumentId: Long? = null,
)

@HiltViewModel
class EditScanViewModel @Inject constructor(
    private val storage: FileStorage,
    private val scanProcessor: ScanProcessor,
    private val saveScan: SaveScanAsDocumentUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(EditScanUiState())
    val state: StateFlow<EditScanUiState> = _state.asStateFlow()

    /** Load scan session by scanning the scans dir for files matching sessionId. */
    fun loadSession(sessionId: String) {
        val files = storage.scansDir.listFiles { f -> f.name.startsWith(sessionId) }
            ?.sortedBy { it.name }
            ?.map { it.absolutePath }
            ?: emptyList()
        _state.update { it.copy(pages = files) }
    }

    fun selectPage(index: Int) = _state.update { it.copy(selectedPage = index) }

    fun rotatePage(index: Int) {
        viewModelScope.launch {
            val path = _state.value.pages.getOrNull(index) ?: return@launch
            val file = File(path)
            val bitmap = android.graphics.BitmapFactory.decodeFile(path) ?: return@launch
            val matrix = android.graphics.Matrix().apply { postRotate(90f) }
            val rotated = android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            scanProcessor.saveBitmap(rotated, file)
            bitmap.recycle()
            rotated.recycle()
            // Force recompose by toggling paths
            _state.update { it.copy(pages = it.pages.toMutableList()) }
        }
    }

    fun deletePage(index: Int) {
        val path = _state.value.pages.getOrNull(index) ?: return
        storage.secureDelete(File(path))
        val newPages = _state.value.pages.toMutableList().also { it.removeAt(index) }
        _state.update { it.copy(pages = newPages, selectedPage = index.coerceAtMost((newPages.size - 1).coerceAtLeast(0))) }
    }

    fun applyFilterToAll(filterIndex: Int) {
        _state.update { it.copy(selectedFilter = filterIndex) }
        val mode = ScanFilterMode.entries[filterIndex]
        viewModelScope.launch {
            _state.value.pages.forEach { path ->
                val file = File(path)
                val bitmap = android.graphics.BitmapFactory.decodeFile(path) ?: return@forEach
                val filtered = scanProcessor.applyFilter(bitmap, mode)
                if (filtered != bitmap) {
                    scanProcessor.saveBitmap(filtered, file)
                    filtered.recycle()
                }
                bitmap.recycle()
            }
        }
    }

    fun toggleReorderMode() = _state.update { it.copy(isReordering = !it.isReordering) }

    fun movePage(from: Int, to: Int) {
        val newPages = _state.value.pages.toMutableList().also { it.add(to, it.removeAt(from)) }
        _state.update { it.copy(pages = newPages, selectedPage = to) }
    }

    fun saveDocument(name: String, asPdf: Boolean = true) {
        _state.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val docId = saveScan(
                imagePaths = _state.value.pages,
                name = name,
                saveAsPdf = asPdf,
            )
            _state.update { it.copy(isSaving = false, savedDocumentId = docId) }
        }
    }
}
