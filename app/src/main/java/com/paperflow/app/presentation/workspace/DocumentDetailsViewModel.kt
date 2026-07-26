package com.paperflow.app.presentation.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.Document
import com.paperflow.app.domain.model.DocumentType
import com.paperflow.app.domain.model.OcrStatus
import com.paperflow.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DocumentDetailsUiState(
    val document: Document? = null,
    val isLoading: Boolean = true,
    val ocrProgress: Int = 0,
)

@HiltViewModel
class DocumentDetailsViewModel @Inject constructor(
    private val getDocument: GetDocumentByIdUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val deleteDocument: DeleteDocumentUseCase,
    private val convertToNote: ConvertScanToNoteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(DocumentDetailsUiState())
    val state: StateFlow<DocumentDetailsUiState> = _state.asStateFlow()

    fun loadDocument(id: Long) {
        viewModelScope.launch {
            val doc = getDocument(id)
            _state.update { it.copy(document = doc, isLoading = false) }
        }
    }

    fun toggleFavorite() {
        val doc = _state.value.document ?: return
        viewModelScope.launch { toggleFavorite(doc.id, doc.isFavorite) }
    }

    fun deleteDocument(onDeleted: () -> Unit) {
        val doc = _state.value.document ?: return
        viewModelScope.launch {
            deleteDocument(doc)
            onDeleted()
        }
    }

    fun convertToNote(onConverted: (Long) -> Unit) {
        val doc = _state.value.document ?: return
        viewModelScope.launch {
            val noteId = convertToNote(doc.id)
            noteId?.let { onConverted(it) }
        }
    }
}
