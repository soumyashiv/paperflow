package com.paperflow.app.domain.usecase

import android.content.Context
import androidx.work.WorkManager
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.PDFEngine
import com.paperflow.app.data.local.file.ScanFilterMode
import com.paperflow.app.data.local.file.ScanProcessor
import com.paperflow.app.data.workers.OCRWorker
import com.paperflow.app.data.workers.ThumbnailWorker
import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.model.Annotation
import com.paperflow.app.domain.repository.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.io.File
import java.util.UUID
import javax.inject.Inject

// ── Get Recent Documents ──────────────────────────────────────────────────────
class GetRecentDocumentsUseCase @Inject constructor(
    private val repo: DocumentRepository,
) {
    operator fun invoke(limit: Int = 20): Flow<List<Document>> = repo.getRecentDocuments(limit)
}

// ── Get All Documents ─────────────────────────────────────────────────────────
class GetAllDocumentsUseCase @Inject constructor(private val repo: DocumentRepository) {
    operator fun invoke(): Flow<List<Document>> = repo.getAllDocuments()
}

// ── Get Documents By Type ─────────────────────────────────────────────────────
class GetDocumentsByTypeUseCase @Inject constructor(private val repo: DocumentRepository) {
    operator fun invoke(type: DocumentType): Flow<List<Document>> = repo.getDocumentsByType(type)
}

// ── Get Favorite Documents ────────────────────────────────────────────────────
class GetFavoriteDocumentsUseCase @Inject constructor(private val repo: DocumentRepository) {
    operator fun invoke(): Flow<List<Document>> = repo.getFavoriteDocuments()
}

// ── Get Document By ID ────────────────────────────────────────────────────────
class GetDocumentByIdUseCase @Inject constructor(private val repo: DocumentRepository) {
    suspend operator fun invoke(id: Long): Document? = repo.getDocumentById(id)
}

// ── Toggle Favorite ───────────────────────────────────────────────────────────
class ToggleFavoriteUseCase @Inject constructor(private val repo: DocumentRepository) {
    suspend operator fun invoke(documentId: Long, currentValue: Boolean) =
        repo.setFavorite(documentId, !currentValue)
}

// ── Delete Document ───────────────────────────────────────────────────────────
class DeleteDocumentUseCase @Inject constructor(
    private val documentRepo: DocumentRepository,
    private val pageRepo: PageRepository,
    private val ocrRepo: OCRRepository,
    private val storage: FileStorage,
) {
    suspend operator fun invoke(document: Document) {
        // 1. Delete OCR index
        ocrRepo.deleteIndexForDocument(document.id)
        // 2. Delete all pages
        pageRepo.deleteAllPagesForDocument(document.id)
        // 3. Delete physical file
        val file = File(document.filePath)
        storage.secureDelete(file)
        // 4. Delete thumbnails
        storage.deleteDocumentArtefacts(document.id)
        // 5. Remove DB record (cascades annotations)
        documentRepo.deleteDocument(document.id)
    }
}

// ── Save Reading Position ─────────────────────────────────────────────────────
class SaveReadingPositionUseCase @Inject constructor(private val repo: DocumentRepository) {
    suspend operator fun invoke(documentId: Long, page: Int) =
        repo.updateLastReadPage(documentId, page)
}

// ── Get Folders ───────────────────────────────────────────────────────────────
class GetFoldersUseCase @Inject constructor(private val repo: FolderRepository) {
    operator fun invoke(): Flow<List<Folder>> = repo.getRootFolders()
}

// ── Get Folder Contents ───────────────────────────────────────────────────────
class GetFolderContentsUseCase @Inject constructor(
    private val documentRepo: DocumentRepository,
    private val folderRepo: FolderRepository,
) {
    fun documents(folderId: Long): Flow<List<Document>> = documentRepo.getDocumentsByFolder(folderId)
    fun subfolders(folderId: Long): Flow<List<Folder>> = folderRepo.getChildFolders(folderId)
}

// ── Create Folder ─────────────────────────────────────────────────────────────
class CreateFolderUseCase @Inject constructor(private val repo: FolderRepository) {
    suspend operator fun invoke(name: String, parentId: Long? = null, color: String = "#F5C842"): Long =
        repo.insertFolder(Folder(name = name, parentFolderId = parentId, colorHex = color))
}

// ── Search ────────────────────────────────────────────────────────────────────
class SearchDocumentsUseCase @Inject constructor(private val repo: SearchRepository) {
    suspend operator fun invoke(query: String): List<SearchResult> =
        repo.searchAll(query, includeHidden = false)
}

// ── Get All Notes ─────────────────────────────────────────────────────────────
class GetAllNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repo.getAllNotes()
}

// ── Create Note ───────────────────────────────────────────────────────────────
class CreateNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(title: String, folderId: Long? = null): Long =
        repo.insertNote(Note(title = title, folderId = folderId))
}

// ── Save Note ─────────────────────────────────────────────────────────────────
class SaveNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(note: Note) = repo.updateNote(note.copy(updatedAt = System.currentTimeMillis()))
}

// ── Auto-save Note Content ────────────────────────────────────────────────────
class AutoSaveNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(noteId: Long, content: String) =
        repo.updateContent(noteId, content)
}

// ── Delete Note ───────────────────────────────────────────────────────────────
class DeleteNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(noteId: Long) = repo.deleteNote(noteId)
}

// ── Convert Scan to Note ──────────────────────────────────────────────────────
class ConvertScanToNoteUseCase @Inject constructor(
    private val documentRepo: DocumentRepository,
    private val pageRepo: PageRepository,
    private val noteRepo: NoteRepository,
) {
    suspend operator fun invoke(documentId: Long): Long? {
        val doc = documentRepo.getDocumentById(documentId) ?: return null
        val pages = pageRepo.getPagesForDocumentSync(documentId)
        val combinedText = pages.mapNotNull { it.extractedText }.joinToString("\n\n")
        if (combinedText.isBlank()) return null
        return noteRepo.insertNote(
            Note(
                title = doc.name.substringBeforeLast("."),
                content = combinedText,
                linkedDocumentId = documentId,
            )
        )
    }
}

// ── Get Annotations For Page ──────────────────────────────────────────────────
class GetAnnotationsUseCase @Inject constructor(private val repo: AnnotationRepository) {
    operator fun invoke(pageId: Long): Flow<List<Annotation>> = repo.getAnnotationsForPage(pageId)
}

// ── Save Annotation ───────────────────────────────────────────────────────────
class SaveAnnotationUseCase @Inject constructor(private val repo: AnnotationRepository) {
    suspend operator fun invoke(annotation: Annotation): Long = repo.insertAnnotation(annotation)
    suspend fun update(annotation: Annotation) = repo.updateAnnotation(annotation)
    suspend fun delete(annotation: Annotation) = repo.deleteAnnotation(annotation)
}

// ── Save Scan as Document ─────────────────────────────────────────────────────
class SaveScanAsDocumentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentRepo: DocumentRepository,
    private val pageRepo: PageRepository,
    private val storage: FileStorage,
    private val pdfEngine: PDFEngine,
) {
    suspend operator fun invoke(
        imagePaths: List<String>,
        name: String,
        folderId: Long? = null,
        saveAsPdf: Boolean = true,
    ): Long? {
        val fileName = if (saveAsPdf) "$name.pdf" else "$name.jpg"
        val destFile = storage.documentFile(fileName)

        if (saveAsPdf) {
            val ok = pdfEngine.createPdfFromImages(imagePaths, destFile)
            if (!ok) return null
        } else {
            File(imagePaths.first()).copyTo(destFile, overwrite = true)
        }

        val type = if (saveAsPdf) DocumentType.PDF else DocumentType.JPG
        val docId = documentRepo.insertDocument(
            Document(
                name = name,
                type = type,
                filePath = destFile.absolutePath,
                sizeBytes = destFile.length(),
                pageCount = imagePaths.size,
                folderId = folderId,
            )
        )

        // Insert page records
        imagePaths.forEachIndexed { index, path ->
            pageRepo.insertPage(
                Page(
                    documentId = docId,
                    pageNumber = index,
                    previewPath = path,
                    orderIndex = index,
                )
            )
        }

        // Enqueue thumbnail + OCR workers
        val wm = WorkManager.getInstance(context)
        wm.enqueue(ThumbnailWorker.buildRequest(docId, destFile.absolutePath, type.ext))
        wm.enqueue(OCRWorker.buildRequest(docId))

        // Cleanup temp scan images
        imagePaths.forEach { path ->
            val f = File(path)
            if (f.absolutePath.startsWith(storage.scansDir.absolutePath)) {
                storage.secureDelete(f)
            }
        }

        return docId
    }
}

// ── Import Document ───────────────────────────────────────────────────────────
class ImportDocumentUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val documentRepo: DocumentRepository,
    private val pageRepo: PageRepository,
    private val storage: FileStorage,
    private val pdfEngine: PDFEngine,
) {
    suspend operator fun invoke(sourceUri: android.net.Uri, folderId: Long? = null): Long? {
        val contentResolver = context.contentResolver
        val mimeType = contentResolver.getType(sourceUri) ?: return null
        val name = getFileName(context, sourceUri) ?: "Document_${System.currentTimeMillis()}"
        val ext = if (mimeType.contains("pdf")) "pdf" else "jpg"
        val destFile = storage.documentFile("$name.$ext".replace("/", "_"))

        try {
            contentResolver.openInputStream(sourceUri)?.use { input ->
                destFile.outputStream().use { output -> input.copyTo(output) }
            } ?: return null
        } catch (e: Exception) { return null }

        val docType = if (ext == "pdf") DocumentType.PDF else DocumentType.JPG
        val pageCount = if (docType == DocumentType.PDF) pdfEngine.getPageCount(destFile) else 1

        val docId = documentRepo.insertDocument(
            Document(
                name = name, type = docType,
                filePath = destFile.absolutePath,
                sizeBytes = destFile.length(),
                pageCount = pageCount,
                folderId = folderId,
            )
        )

        for (i in 0 until pageCount) {
            pageRepo.insertPage(Page(documentId = docId, pageNumber = i, orderIndex = i))
        }

        val wm = WorkManager.getInstance(context)
        wm.enqueue(ThumbnailWorker.buildRequest(docId, destFile.absolutePath, docType.ext))
        wm.enqueue(OCRWorker.buildRequest(docId))

        return docId
    }

    private fun getFileName(context: Context, uri: android.net.Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idx = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (idx >= 0) result = cursor.getString(idx)
                }
            }
        }
        return result ?: uri.lastPathSegment
    }
}

// ── Reorder Pages ─────────────────────────────────────────────────────────────
class ReorderPagesUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pageRepo: PageRepository,
    private val documentRepo: DocumentRepository,
    private val pdfEngine: PDFEngine,
    private val storage: FileStorage,
) {
    suspend operator fun invoke(documentId: Long, orderedPageIds: List<Long>): Boolean {
        // 1. Update DB order
        pageRepo.reorderPages(documentId, orderedPageIds)

        // 2. Get document to rebuild PDF
        val doc = documentRepo.getDocumentById(documentId) ?: return false
        if (doc.type != DocumentType.PDF) return true // Image docs don't need PDF rebuild

        // 3. Rebuild PDF with new page order
        val pages = pageRepo.getPagesForDocumentSync(documentId)
        val orderedIndices = orderedPageIds.mapNotNull { id ->
            pages.indexOfFirst { it.id == id }.takeIf { it >= 0 }
        }

        val sourceFile = File(doc.filePath)
        val tempFile = storage.tempFile("rebuild_${documentId}_${System.currentTimeMillis()}.pdf")
        val ok = pdfEngine.rebuildPdfFromPageOrder(sourceFile, orderedIndices, tempFile)
        if (!ok) {
            storage.secureDelete(tempFile)
            return false
        }

        // 4. Swap files atomically
        val backupFile = storage.tempFile("backup_${documentId}.pdf")
        sourceFile.copyTo(backupFile, overwrite = true)
        tempFile.copyTo(sourceFile, overwrite = true)
        storage.secureDelete(tempFile)
        storage.secureDelete(backupFile)

        return true
    }
}

// ── Get Storage Info ──────────────────────────────────────────────────────────
class GetStorageInfoUseCase @Inject constructor(
    private val documentRepo: DocumentRepository,
    private val storage: FileStorage,
) {
    suspend operator fun invoke(): StorageInfo {
        val used = documentRepo.getTotalStorageBytes()
        val total = storage.documentsDir.totalSpace
        return StorageInfo(usedBytes = used, totalBytes = total)
    }
}
