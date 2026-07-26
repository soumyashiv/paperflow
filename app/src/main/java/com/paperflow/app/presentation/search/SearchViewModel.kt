package com.paperflow.app.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.SearchResult
import com.paperflow.app.domain.usecase.SearchDocumentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val results: List<SearchResult> = emptyList(),
    val isSearching: Boolean = false,
    val recentQueries: List<String> = emptyList(),
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchDocuments: SearchDocumentsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun setQuery(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList(), isSearching = false) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // 300ms debounce
            _state.update { it.copy(isSearching = true) }
            val results = searchDocuments(query)
            _state.update { it.copy(results = results, isSearching = false) }
            // Save to recent if got results
            if (results.isNotEmpty()) addToRecent(query)
        }
    }

    private fun addToRecent(query: String) {
        val current = _state.value.recentQueries.filter { it != query }
        _state.update { it.copy(recentQueries = listOf(query) + current.take(7)) }
    }

    fun clearQuery() = setQuery("")
    fun useRecentQuery(q: String) = setQuery(q)
    fun removeRecent(q: String) = _state.update { it.copy(recentQueries = it.recentQueries - q) }
}
