package com.paperflow.app.domain.repository

import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.model.Annotation
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {
    fun getAllDocuments(): Flow<List<Document>>
    fun getRecentDocuments(limit: Int = 20): Flow<List<Document>>
    fun getDocumentsByFolder(folderId: Long): Flow<List<Document>>
    fun getFavoriteDocuments(): Flow<List<Document>>
    fun getDocumentsByType(type: DocumentType): Flow<List<Document>>
    suspend fun getDocumentById(id: Long): Document?
    suspend fun searchByName(query: String): List<Document>
    suspend fun insertDocument(document: Document): Long
    suspend fun updateDocument(document: Document)
    suspend fun deleteDocument(documentId: Long)
    suspend fun setFavorite(documentId: Long, favorite: Boolean)
    suspend fun updateLastReadPage(documentId: Long, page: Int)
    suspend fun updateOcrStatus(documentId: Long, status: OcrStatus)
    suspend fun updateThumbnailPath(documentId: Long, path: String)
    suspend fun getTotalStorageBytes(): Long
}

interface PageRepository {
    fun getPagesForDocument(documentId: Long): Flow<List<Page>>
    suspend fun getPagesForDocumentSync(documentId: Long): List<Page>
    suspend fun getPageById(pageId: Long): Page?
    suspend fun insertPage(page: Page): Long
    suspend fun insertPages(pages: List<Page>)
    suspend fun updatePage(page: Page)
    suspend fun updateExtractedText(pageId: Long, text: String)
    suspend fun updateThumbnail(pageId: Long, path: String)
    suspend fun reorderPages(documentId: Long, orderedPageIds: List<Long>)
    suspend fun deletePage(pageId: Long)
    suspend fun deleteAllPagesForDocument(documentId: Long)
}

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNotesByType(handwritten: Boolean): Flow<List<Note>>
    fun getFavoriteNotes(): Flow<List<Note>>
    fun getNotesByFolder(folderId: Long): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun searchNotes(query: String): List<Note>
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun updateContent(noteId: Long, content: String)
    suspend fun setFavorite(noteId: Long, favorite: Boolean)
    suspend fun deleteNote(noteId: Long)
}

interface AnnotationRepository {
    fun getAnnotationsForPage(pageId: Long): Flow<List<Annotation>>
    suspend fun getAnnotationsForPageSync(pageId: Long): List<Annotation>
    suspend fun getAnnotationsForDocument(documentId: Long): List<Annotation>
    suspend fun insertAnnotation(annotation: Annotation): Long
    suspend fun updateAnnotation(annotation: Annotation)
    suspend fun deleteAnnotation(annotation: Annotation)
    suspend fun deleteAllForPage(pageId: Long)
    /** Persist freehand strokes from the annotation canvas. */
    suspend fun saveStrokes(
        documentId: Long,
        pageIndex: Int,
        strokes: List<List<androidx.compose.ui.geometry.Offset>>,
        color: androidx.compose.ui.graphics.Color,
    )
}

interface FolderRepository {
    fun getRootFolders(): Flow<List<Folder>>
    fun getChildFolders(parentId: Long): Flow<List<Folder>>
    suspend fun getFolderById(id: Long): Folder?
    suspend fun searchFolders(query: String): List<Folder>
    suspend fun insertFolder(folder: Folder): Long
    suspend fun updateFolder(folder: Folder)
    suspend fun setLocked(folderId: Long, locked: Boolean)
    suspend fun deleteFolder(folderId: Long)
}

interface SearchRepository {
    suspend fun searchAll(query: String, includeHidden: Boolean = false): List<SearchResult>
    suspend fun searchOcr(query: String): List<SearchResult>
    suspend fun searchNotes(query: String): List<SearchResult>
    suspend fun searchFolders(query: String): List<SearchResult>
    suspend fun searchDocumentNames(query: String): List<SearchResult>
}

interface OCRRepository {
    suspend fun indexPage(pageId: Long, documentId: Long, text: String)
    suspend fun deleteIndexForDocument(documentId: Long)
    suspend fun countIndexedPages(documentId: Long): Int
}

interface ActivityRepository {
    fun getRecentActivity(limit: Int = 50): Flow<List<ActivityItem>>
    suspend fun logActivity(documentId: Long, action: String)
    suspend fun cleanupOldActivity(olderThanDays: Int = 30)
}
