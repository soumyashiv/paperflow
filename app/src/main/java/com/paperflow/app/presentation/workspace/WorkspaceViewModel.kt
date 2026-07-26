package com.paperflow.app.presentation.workspace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SortBy { DATE_DESC, DATE_ASC, NAME_ASC, NAME_DESC, SIZE_DESC }
enum class ViewMode { LIST, GRID, LARGE_GRID }
enum class FilterType { ALL, PDF, IMAGES, NOTES, OCR, STARRED }

data class WorkspaceUiState(
    val folderId: Long? = null,
    val folderName: String = "Workspace",
    val documents: List<Document> = emptyList(),
    val subFolders: List<Folder> = emptyList(),
    val rootFolders: List<Folder> = emptyList(),
    val filter: FilterType = FilterType.ALL,
    val sortBy: SortBy = SortBy.DATE_DESC,
    val viewMode: ViewMode = ViewMode.LIST,
    val searchQuery: String = "",
    val totalStorageBytes: Long = 0,
    val isLoading: Boolean = true,
)

@HiltViewModel
class WorkspaceViewModel @Inject constructor(
    private val getAllDocuments: GetAllDocumentsUseCase,
    private val getFolderContents: GetFolderContentsUseCase,
    private val getFolders: GetFoldersUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val deleteDocument: DeleteDocumentUseCase,
    private val createFolder: CreateFolderUseCase,
    private val getDocumentsByType: GetDocumentsByTypeUseCase,
    private val getFavoriteDocuments: GetFavoriteDocumentsUseCase,
    private val getStorageInfo: GetStorageInfoUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(WorkspaceUiState())
    val state: StateFlow<WorkspaceUiState> = _state.asStateFlow()

    fun init(folderId: Long?) {
        _state.update { it.copy(folderId = folderId, isLoading = true) }
        viewModelScope.launch {
            val storage = getStorageInfo()
            _state.update { it.copy(totalStorageBytes = storage.usedBytes) }
        }
        loadDocuments(folderId)
        if (folderId == null) loadRootFolders()
    }

    private fun loadDocuments(folderId: Long?) {
        viewModelScope.launch {
            val flow = if (folderId != null) {
                getFolderContents.documents(folderId)
            } else {
                getAllDocuments()
            }
            flow.collect { docs ->
                _state.update { state ->
                    state.copy(
                        documents = applyFilter(docs, state.filter, state.searchQuery),
                        isLoading = false,
                    )
                }
            }
        }
        if (folderId != null) {
            viewModelScope.launch {
                getFolderContents.subfolders(folderId).collect { sub ->
                    _state.update { it.copy(subFolders = sub) }
                }
            }
        }
    }

    private fun loadRootFolders() {
        viewModelScope.launch {
            getFolders().collect { folders ->
                _state.update { it.copy(rootFolders = folders) }
            }
        }
    }

    fun setFilter(filter: FilterType) {
        _state.update { state ->
            state.copy(
                filter = filter,
                documents = applyFilter(state.documents, filter, state.searchQuery),
            )
        }
        reloadForFilter(filter)
    }

    private fun reloadForFilter(filter: FilterType) {
        viewModelScope.launch {
            val flow: Flow<List<Document>> = when (filter) {
                FilterType.PDF -> getDocumentsByType(DocumentType.PDF)
                FilterType.IMAGES -> getDocumentsByType(DocumentType.JPG)
                FilterType.NOTES -> getDocumentsByType(DocumentType.NOTE)
                FilterType.STARRED -> getFavoriteDocuments()
                FilterType.ALL, FilterType.OCR -> getAllDocuments()
            }
            flow.first().let { docs ->
                _state.update { state ->
                    state.copy(
                        documents = applyFilter(docs, filter, state.searchQuery),
                        isLoading = false,
                    )
                }
            }
        }
    }

    fun setSortBy(sort: SortBy) {
        _state.update { state ->
            state.copy(sortBy = sort, documents = sortDocuments(state.documents, sort))
        }
    }

    fun setViewMode(mode: ViewMode) = _state.update { it.copy(viewMode = mode) }

    fun setSearchQuery(query: String) {
        _state.update { state ->
            state.copy(
                searchQuery = query,
                documents = applyFilter(state.documents, state.filter, query),
            )
        }
    }

    fun toggleFavorite(document: Document) {
        viewModelScope.launch { toggleFavorite(document.id, document.isFavorite) }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch { deleteDocument.invoke(document) }
    }

    fun createNewFolder(name: String) {
        viewModelScope.launch {
            createFolder(name, parentId = _state.value.folderId)
        }
    }

    private fun applyFilter(docs: List<Document>, filter: FilterType, query: String): List<Document> {
        var result = docs
        result = when (filter) {
            FilterType.ALL -> result
            FilterType.PDF -> result.filter { it.type == DocumentType.PDF }
            FilterType.IMAGES -> result.filter { it.type == DocumentType.JPG || it.type == DocumentType.PNG }
            FilterType.NOTES -> result.filter { it.type == DocumentType.NOTE }
            FilterType.OCR -> result.filter { it.ocrStatus == OcrStatus.COMPLETE }
            FilterType.STARRED -> result.filter { it.isFavorite }
        }
        if (query.isNotBlank()) {
            result = result.filter { it.name.contains(query, ignoreCase = true) }
        }
        return result
    }

    private fun sortDocuments(docs: List<Document>, sort: SortBy): List<Document> = when (sort) {
        SortBy.DATE_DESC -> docs.sortedByDescending { it.updatedAt }
        SortBy.DATE_ASC -> docs.sortedBy { it.updatedAt }
        SortBy.NAME_ASC -> docs.sortedBy { it.name.lowercase() }
        SortBy.NAME_DESC -> docs.sortedByDescending { it.name.lowercase() }
        SortBy.SIZE_DESC -> docs.sortedByDescending { it.sizeBytes }
    }
}
