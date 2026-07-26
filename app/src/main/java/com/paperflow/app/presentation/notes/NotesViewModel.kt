package com.paperflow.app.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.Note
import com.paperflow.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val filter: NoteFilter = NoteFilter.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
)

enum class NoteFilter { ALL, TYPED, HANDWRITTEN, STARRED }

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getAllNotes: GetAllNotesUseCase,
    private val createNote: CreateNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val getNotesByType: GetAllNotesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(NotesUiState())
    val state: StateFlow<NotesUiState> = _state.asStateFlow()

    init { loadNotes() }

    private fun loadNotes() {
        viewModelScope.launch {
            getAllNotes().collect { notes ->
                _state.update { state ->
                    state.copy(
                        notes = applyFilter(notes, state.filter, state.searchQuery),
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun setFilter(filter: NoteFilter) {
        _state.update { state ->
            state.copy(filter = filter, notes = applyFilter(state.notes, filter, state.searchQuery))
        }
    }

    fun setSearchQuery(query: String) {
        _state.update { state ->
            state.copy(searchQuery = query, notes = applyFilter(state.notes, state.filter, query))
        }
    }

    fun deleteNote(note: Note) = viewModelScope.launch { deleteNote.invoke(note.id) }

    fun createNewNote(): Long? {
        var newId: Long? = null
        viewModelScope.launch {
            newId = createNote("Untitled Note")
        }
        return newId
    }

    private fun applyFilter(notes: List<Note>, filter: NoteFilter, query: String): List<Note> {
        var result = when (filter) {
            NoteFilter.ALL -> notes
            NoteFilter.TYPED -> notes.filter { !it.isHandwritten }
            NoteFilter.HANDWRITTEN -> notes.filter { it.isHandwritten }
            NoteFilter.STARRED -> notes.filter { it.isFavorite }
        }
        if (query.isNotBlank()) result = result.filter {
            it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
        }
        return result
    }
}
