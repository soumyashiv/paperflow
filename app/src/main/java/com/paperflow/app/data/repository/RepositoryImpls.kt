package com.paperflow.app.data.repository

import com.paperflow.app.data.local.database.dao.*
import com.paperflow.app.data.local.database.entity.*
import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.model.Annotation
import com.paperflow.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// ─── Page Repository ──────────────────────────────────────────────────────────
@Singleton
class PageRepositoryImpl @Inject constructor(private val dao: PageDao) : PageRepository {

    override fun getPagesForDocument(documentId: Long) =
        dao.getPagesForDocument(documentId).map { list -> list.map { it.toDomain() } }

    override suspend fun getPagesForDocumentSync(documentId: Long) =
        dao.getPagesForDocumentSync(documentId).map { it.toDomain() }

    override suspend fun getPageById(pageId: Long) = dao.getPageById(pageId)?.toDomain()

    override suspend fun insertPage(page: Page) = dao.insertPage(page.toEntity())

    override suspend fun insertPages(pages: List<Page>) =
        dao.insertPages(pages.map { it.toEntity() })

    override suspend fun updatePage(page: Page) = dao.updatePage(page.toEntity())

    override suspend fun updateExtractedText(pageId: Long, text: String) =
        dao.updateExtractedText(pageId, text)

    override suspend fun updateThumbnail(pageId: Long, path: String) =
        dao.updateThumbnail(pageId, path)

    override suspend fun reorderPages(documentId: Long, orderedPageIds: List<Long>) =
        dao.reorderPages(documentId, orderedPageIds)

    override suspend fun deletePage(pageId: Long) {
        val page = dao.getPageById(pageId) ?: return
        dao.deletePage(page)
    }

    override suspend fun deleteAllPagesForDocument(documentId: Long) =
        dao.deleteAllPagesForDocument(documentId)

    private fun PageEntity.toDomain() = Page(
        id = id, documentId = documentId, pageNumber = pageNumber,
        thumbnailPath = thumbnailPath, previewPath = previewPath,
        extractedText = extractedText, orderIndex = orderIndex,
    )

    private fun Page.toEntity() = PageEntity(
        id = id, documentId = documentId, pageNumber = pageNumber,
        thumbnailPath = thumbnailPath, previewPath = previewPath,
        extractedText = extractedText, orderIndex = orderIndex,
    )
}

// ─── Note Repository ──────────────────────────────────────────────────────────
@Singleton
class NoteRepositoryImpl @Inject constructor(private val dao: NoteDao) : NoteRepository {

    override fun getAllNotes() = dao.getAllNotes().map { it.map { e -> e.toDomain() } }

    override fun getNotesByType(handwritten: Boolean) =
        dao.getNotesByType(handwritten).map { it.map { e -> e.toDomain() } }

    override fun getFavoriteNotes() = dao.getFavoriteNotes().map { it.map { e -> e.toDomain() } }

    override fun getNotesByFolder(folderId: Long) =
        dao.getNotesByFolder(folderId).map { it.map { e -> e.toDomain() } }

    override suspend fun getNoteById(id: Long) = dao.getNoteById(id)?.toDomain()

    override suspend fun searchNotes(query: String) = dao.searchNotes(query).map { it.toDomain() }

    override suspend fun insertNote(note: Note) = dao.insertNote(note.toEntity())

    override suspend fun updateNote(note: Note) = dao.updateNote(note.toEntity())

    override suspend fun updateContent(noteId: Long, content: String) =
        dao.updateContent(noteId, content)

    override suspend fun setFavorite(noteId: Long, favorite: Boolean) =
        dao.setFavorite(noteId, favorite)

    override suspend fun deleteNote(noteId: Long) = dao.deleteById(noteId)

    private fun NoteEntity.toDomain() = Note(
        id = id, title = title, content = content,
        linkedDocumentId = linkedDocumentId, folderId = folderId,
        createdAt = createdAt, updatedAt = updatedAt,
        isHandwritten = isHandwritten, isFavorite = isFavorite,
        thumbnailPath = thumbnailPath,
    )

    private fun Note.toEntity() = NoteEntity(
        id = id, title = title, content = content,
        linkedDocumentId = linkedDocumentId, folderId = folderId,
        createdAt = createdAt, updatedAt = updatedAt,
        isHandwritten = isHandwritten, isFavorite = isFavorite,
        thumbnailPath = thumbnailPath,
    )
}

// ─── Annotation Repository ────────────────────────────────────────────────────
@Singleton
class AnnotationRepositoryImpl @Inject constructor(private val dao: AnnotationDao) : AnnotationRepository {

    override fun getAnnotationsForPage(pageId: Long) =
        dao.getAnnotationsForPage(pageId).map { it.map { e -> e.toDomain() } }

    override suspend fun getAnnotationsForPageSync(pageId: Long) =
        dao.getAnnotationsForPageSync(pageId).map { it.toDomain() }

    override suspend fun getAnnotationsForDocument(documentId: Long) =
        dao.getAnnotationsForDocument(documentId).map { it.toDomain() }

    override suspend fun insertAnnotation(annotation: Annotation) =
        dao.insertAnnotation(annotation.toEntity())

    override suspend fun updateAnnotation(annotation: Annotation) =
        dao.updateAnnotation(annotation.toEntity())

    override suspend fun deleteAnnotation(annotation: Annotation) =
        dao.deleteAnnotation(annotation.toEntity())

    override suspend fun deleteAllForPage(pageId: Long) = dao.deleteAllForPage(pageId)

    override suspend fun saveStrokes(
        documentId: Long,
        pageIndex: Int,
        strokes: List<List<androidx.compose.ui.geometry.Offset>>,
        color: androidx.compose.ui.graphics.Color,
    ) {
        if (strokes.isEmpty()) return
        // Serialize strokes to a compact JSON string for positionData
        val strokesJson = buildString {
            append("[")
            strokes.forEachIndexed { si, stroke ->
                if (si > 0) append(",")
                append("[")
                stroke.forEachIndexed { pi, pt ->
                    if (pi > 0) append(",")
                    append("{\"x\":${pt.x},\"y\":${pt.y}}")
                }
                append("]")
            }
            append("]")
        }
        val colorHex = "#%02X%02X%02X%02X".format(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt(),
        )
        // Find the page entity for this document + pageIndex to get the pageId
        val pages = dao.getPageIdForDocumentAndIndex(documentId, pageIndex)
        val pageId = pages ?: return
        dao.insertAnnotation(
            AnnotationEntity(
                pageId = pageId,
                type = "freehand",
                positionData = strokesJson,
                color = colorHex,
                content = null,
                createdAt = System.currentTimeMillis(),
            )
        )
    }

    private fun AnnotationEntity.toDomain() = Annotation(
        id = id, pageId = pageId,
        type = AnnotationType.from(type),
        positionData = positionData, color = color,
        content = content, createdAt = createdAt,
    )

    private fun Annotation.toEntity() = AnnotationEntity(
        id = id, pageId = pageId,
        type = type.name.lowercase(),
        positionData = positionData, color = color,
        content = content, createdAt = createdAt,
    )
}

// ─── Folder Repository ────────────────────────────────────────────────────────
@Singleton
class FolderRepositoryImpl @Inject constructor(
    private val folderDao: FolderDao,
    private val documentDao: DocumentDao,
) : FolderRepository {

    override fun getRootFolders(): Flow<List<Folder>> =
        folderDao.getRootFolders().map { list ->
            list.map { it.toDomain() }
        }

    override fun getChildFolders(parentId: Long) =
        folderDao.getChildFolders(parentId).map { list -> list.map { it.toDomain() } }

    override suspend fun getFolderById(id: Long) = folderDao.getFolderById(id)?.toDomain()

    override suspend fun searchFolders(query: String) =
        folderDao.searchFolders(query).map { it.toDomain() }

    override suspend fun insertFolder(folder: Folder) = folderDao.insertFolder(folder.toEntity())

    override suspend fun updateFolder(folder: Folder) = folderDao.updateFolder(folder.toEntity())

    override suspend fun setLocked(folderId: Long, locked: Boolean) =
        folderDao.setLocked(folderId, locked)

    override suspend fun deleteFolder(folderId: Long) = folderDao.deleteById(folderId)

    private fun FolderEntity.toDomain() = Folder(
        id = id, name = name, parentFolderId = parentFolderId,
        isLocked = isLocked, isHidden = isHidden,
        colorHex = colorHex, iconName = iconName,
        createdAt = createdAt,
    )

    private fun Folder.toEntity() = FolderEntity(
        id = id, name = name, parentFolderId = parentFolderId,
        isLocked = isLocked, isHidden = isHidden,
        colorHex = colorHex, iconName = iconName,
        createdAt = createdAt,
    )
}

// ─── OCR Repository ───────────────────────────────────────────────────────────
@Singleton
class OCRRepositoryImpl @Inject constructor(private val dao: OCRIndexDao) : OCRRepository {

    override suspend fun indexPage(pageId: Long, documentId: Long, text: String) =
        dao.index(OCRIndexEntity(pageId = pageId, documentId = documentId, extractedText = text))

    override suspend fun deleteIndexForDocument(documentId: Long) =
        dao.deleteForDocument(documentId)

    override suspend fun countIndexedPages(documentId: Long) =
        dao.countIndexedPages(documentId)
}

// ─── Activity Repository ──────────────────────────────────────────────────────
@Singleton
class ActivityRepositoryImpl @Inject constructor(private val dao: ActivityDao) : ActivityRepository {

    override fun getRecentActivity(limit: Int) =
        dao.getRecentActivity(limit).map { list ->
            list.map { ActivityItem(id = it.id, documentId = it.documentId, action = it.action, timestamp = it.timestamp) }
        }

    override suspend fun logActivity(documentId: Long, action: String) =
        dao.insert(ActivityEntity(documentId = documentId, action = action))

    override suspend fun cleanupOldActivity(olderThanDays: Int) {
        val cutoff = System.currentTimeMillis() - (olderThanDays * 24 * 60 * 60 * 1000L)
        dao.cleanupOldActivity(cutoff)
    }
}
