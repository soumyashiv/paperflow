package com.paperflow.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.datastore.PreferencesDataStore
import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val recentDocuments: List<Document> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val storageInfo: StorageInfo = StorageInfo(0, 0),
    val notificationCount: Int = 0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRecentDocuments: GetRecentDocumentsUseCase,
    private val getFolders: GetFoldersUseCase,
    private val getStorageInfo: GetStorageInfoUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val deleteDocument: DeleteDocumentUseCase,
    private val prefs: PreferencesDataStore,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // Merge all flows into one state update
            combine(
                prefs.userName,
                getRecentDocuments(20),
                getFolders(),
            ) { name, docs, folders ->
                Triple(name, docs, folders)
            }.collect { (name, docs, folders) ->
                val storage = getStorageInfo()
                _state.update { state ->
                    state.copy(
                        userName = name,
                        recentDocuments = docs,
                        folders = folders,
                        storageInfo = storage,
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun toggleFavorite(document: Document) {
        viewModelScope.launch {
            toggleFavorite(document.id, document.isFavorite)
        }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch {
            deleteDocument.invoke(document)
        }
    }

    fun refresh() {
        _state.update { it.copy(isLoading = true) }
        loadData()
    }
}
