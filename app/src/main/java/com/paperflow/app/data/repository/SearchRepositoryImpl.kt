package com.paperflow.app.data.repository

import com.paperflow.app.data.local.database.dao.DocumentDao
import com.paperflow.app.data.local.database.dao.FolderDao
import com.paperflow.app.data.local.database.dao.NoteDao
import com.paperflow.app.data.local.database.dao.OCRIndexDao
import com.paperflow.app.domain.model.SearchResult
import com.paperflow.app.domain.model.SearchResultType
import com.paperflow.app.domain.repository.SearchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val ocrDao: OCRIndexDao,
    private val documentDao: DocumentDao,
    private val noteDao: NoteDao,
    private val folderDao: FolderDao,
) : SearchRepository {

    override suspend fun searchAll(query: String, includeHidden: Boolean): List<SearchResult> {
        if (query.isBlank()) return emptyList()
        // Sanitize query for FTS — strip special FTS characters (SR-SEARCH-004)
        val sanitized = sanitizeFtsQuery(query)
        val results = mutableListOf<SearchResult>()
        results += searchOcr(sanitized)
        results += searchNotes(sanitized)
        results += searchFolders(sanitized)
        results += searchDocumentNames(sanitized)
        // Deduplicate by documentId + pageId
        return results.distinctBy { "${it.documentId}-${it.pageId}" }
    }

    override suspend fun searchOcr(query: String): List<SearchResult> {
        val sanitized = sanitizeFtsQuery(query)
        return try {
            ocrDao.searchVisible(sanitized).mapNotNull { ocr ->
                val doc = documentDao.getDocumentById(ocr.documentId) ?: return@mapNotNull null
                SearchResult(
                    documentId = ocr.documentId,
                    documentName = doc.name,
                    thumbnailPath = doc.thumbnailPath,
                    pageId = ocr.pageId,
                    pageNumber = null, // resolved by viewer
                    matchSnippet = extractSnippet(ocr.extractedText, query),
                    resultType = SearchResultType.PAGE_TEXT,
                )
            }
        } catch (e: Exception) {
            // FTS query syntax error — fall through to empty
            emptyList()
        }
    }

    override suspend fun searchNotes(query: String): List<SearchResult> =
        noteDao.searchNotes(query).map { note ->
            SearchResult(
                documentId = note.linkedDocumentId ?: -1L,
                documentName = note.title,
                thumbnailPath = note.thumbnailPath,
                pageId = null,
                pageNumber = null,
                matchSnippet = extractSnippet(note.content, query),
                resultType = SearchResultType.NOTE,
            )
        }

    override suspend fun searchFolders(query: String): List<SearchResult> =
        folderDao.searchFolders(query).map { folder ->
            SearchResult(
                documentId = folder.id,
                documentName = folder.name,
                thumbnailPath = null,
                pageId = null,
                pageNumber = null,
                matchSnippet = "Folder",
                resultType = SearchResultType.FOLDER,
            )
        }

    override suspend fun searchDocumentNames(query: String): List<SearchResult> =
        documentDao.searchByName(query).map { doc ->
            SearchResult(
                documentId = doc.id,
                documentName = doc.name,
                thumbnailPath = doc.thumbnailPath,
                pageId = null,
                pageNumber = null,
                matchSnippet = doc.type,
                resultType = SearchResultType.DOCUMENT,
            )
        }

    /** Extract a short snippet around the first match occurrence. */
    private fun extractSnippet(text: String, query: String, maxLen: Int = 120): String {
        val idx = text.indexOf(query, ignoreCase = true)
        if (idx < 0) return text.take(maxLen)
        val start = maxOf(0, idx - 30)
        val end = minOf(text.length, idx + query.length + 60)
        return (if (start > 0) "…" else "") + text.substring(start, end).trim() + (if (end < text.length) "…" else "")
    }

    /**
     * Sanitize FTS query to prevent syntax errors (SR-SEARCH-004).
     * Removes FTS special operators when not intended by user.
     */
    private fun sanitizeFtsQuery(query: String): String {
        // Wrap in quotes for exact phrase match, escape internal quotes
        val escaped = query.trim().replace("\"", "")
        return if (escaped.contains(' ')) "\"$escaped\"" else "$escaped*"
    }
}
