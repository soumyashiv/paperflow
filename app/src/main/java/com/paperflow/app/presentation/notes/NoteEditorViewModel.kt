package com.paperflow.app.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.Note
import com.paperflow.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class NoteEditorUiState(
    val note: Note? = null,
    val isSaving: Boolean = false,
    val isSaved: Boolean = true,
    val isLoading: Boolean = true,
)

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val getAllNotes: GetAllNotesUseCase,
    private val createNote: CreateNoteUseCase,
    private val saveNote: SaveNoteUseCase,
    private val autoSave: AutoSaveNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NoteEditorUiState())
    val state: StateFlow<NoteEditorUiState> = _state.asStateFlow()

    private var autoSaveJob: Job? = null

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            if (noteId < 0L) {
                // New note — create blank one immediately
                val newId = createNote("Untitled Note")
                loadNoteById(newId)
            } else {
                loadNoteById(noteId)
            }
        }
    }

    private fun loadNoteById(id: Long) {
        viewModelScope.launch {
            // Observe from DB so any external changes are reflected
            getAllNotes().map { list -> list.firstOrNull { it.id == id } }
                .collect { note ->
                    _state.update { it.copy(note = note, isLoading = false) }
                }
        }
    }

    fun updateTitle(title: String) {
        val current = _state.value.note ?: return
        val updated = current.copy(title = title)
        _state.update { it.copy(note = updated, isSaved = false) }
        scheduleAutoSave(updated)
    }

    fun updateContent(content: String) {
        val current = _state.value.note ?: return
        val updated = current.copy(content = content)
        _state.update { it.copy(note = updated, isSaved = false) }
        scheduleAutoSave(updated)
    }

    /** Auto-save with 800ms debounce to avoid excessive DB writes. */
    private fun scheduleAutoSave(note: Note) {
        autoSaveJob?.cancel()
        autoSaveJob = viewModelScope.launch {
            delay(800)
            _state.update { it.copy(isSaving = true) }
            saveNote(note.copy(updatedAt = System.currentTimeMillis()))
            _state.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    /** Immediate save — call on back press. */
    fun saveImmediately() {
        autoSaveJob?.cancel()
        val note = _state.value.note ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true) }
            saveNote(note.copy(updatedAt = System.currentTimeMillis()))
            _state.update { it.copy(isSaving = false, isSaved = true) }
        }
    }

    fun deleteCurrentNote() {
        val id = _state.value.note?.id ?: return
        viewModelScope.launch { deleteNote(id) }
    }

    override fun onCleared() {
        super.onCleared()
        autoSaveJob?.cancel()
        // Final save on ViewModel cleared
        val note = _state.value.note ?: return
        if (!_state.value.isSaved) {
            viewModelScope.launch { saveNote(note.copy(updatedAt = System.currentTimeMillis())) }
        }
    }
}
