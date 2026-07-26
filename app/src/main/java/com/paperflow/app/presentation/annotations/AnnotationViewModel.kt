package com.paperflow.app.presentation.annotations

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.file.PDFEngine
import com.paperflow.app.domain.repository.AnnotationRepository
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import java.io.File
import javax.inject.Inject

data class AnnotationUiState(
    val pageBitmap: Bitmap? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class AnnotationViewModel @Inject constructor(
    private val getDocument: GetDocumentByIdUseCase,
    private val annotationRepo: AnnotationRepository,
    private val pdfEngine: PDFEngine,
) : ViewModel() {

    private val _state = MutableStateFlow(AnnotationUiState())
    val state: StateFlow<AnnotationUiState> = _state.asStateFlow()

    fun loadPage(documentId: Long, pageIndex: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val doc = getDocument(documentId) ?: return@launch
            val bitmap = pdfEngine.renderPage(File(doc.filePath), pageIndex, width = 900)
            _state.update { it.copy(pageBitmap = bitmap, isLoading = false) }
        }
    }

    fun saveAnnotations(strokes: List<List<Offset>>, color: Color, documentId: Long, pageIndex: Int) {
        viewModelScope.launch {
            annotationRepo.saveStrokes(
                documentId = documentId,
                pageIndex = pageIndex,
                strokes = strokes,
                color = color,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        _state.value.pageBitmap?.recycle()
    }
}
