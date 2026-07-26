package com.paperflow.app.data.repository

import com.paperflow.app.data.local.database.dao.DocumentDao
import com.paperflow.app.data.local.database.entity.DocumentEntity
import com.paperflow.app.domain.model.*
import com.paperflow.app.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DocumentRepositoryImpl @Inject constructor(
    private val dao: DocumentDao,
) : DocumentRepository {

    override fun getAllDocuments(): Flow<List<Document>> =
        dao.getAllDocuments().map { list -> list.map { it.toDomain() } }

    override fun getRecentDocuments(limit: Int): Flow<List<Document>> =
        dao.getRecentDocuments(limit).map { list -> list.map { it.toDomain() } }

    override fun getDocumentsByFolder(folderId: Long): Flow<List<Document>> =
        dao.getDocumentsByFolder(folderId).map { list -> list.map { it.toDomain() } }

    override fun getFavoriteDocuments(): Flow<List<Document>> =
        dao.getFavoriteDocuments().map { list -> list.map { it.toDomain() } }

    override fun getDocumentsByType(type: DocumentType): Flow<List<Document>> =
        dao.getDocumentsByType(type.ext).map { list -> list.map { it.toDomain() } }

    override suspend fun getDocumentById(id: Long): Document? =
        dao.getDocumentById(id)?.toDomain()

    override suspend fun searchByName(query: String): List<Document> =
        dao.searchByName(query).map { it.toDomain() }

    override suspend fun insertDocument(document: Document): Long =
        dao.insertDocument(document.toEntity())

    override suspend fun updateDocument(document: Document) =
        dao.updateDocument(document.toEntity())

    override suspend fun deleteDocument(documentId: Long) =
        dao.deleteById(documentId)

    override suspend fun setFavorite(documentId: Long, favorite: Boolean) =
        dao.setFavorite(documentId, favorite)

    override suspend fun updateLastReadPage(documentId: Long, page: Int) =
        dao.updateLastReadPage(documentId, page)

    override suspend fun updateOcrStatus(documentId: Long, status: OcrStatus) =
        dao.updateOcrStatus(documentId, status.name.lowercase())

    override suspend fun updateThumbnailPath(documentId: Long, path: String) =
        dao.updateThumbnailPath(documentId, path)

    override suspend fun getTotalStorageBytes(): Long =
        dao.getTotalSizeBytes() ?: 0L

    // ── Mappers ───────────────────────────────────────────────────────────────
    private fun DocumentEntity.toDomain() = Document(
        id = id, name = name, type = DocumentType.from(type),
        createdAt = createdAt, updatedAt = updatedAt,
        thumbnailPath = thumbnailPath, folderId = folderId,
        isLocked = isLocked, isHidden = isHidden,
        filePath = filePath, pageCount = pageCount, sizeBytes = sizeBytes,
        isFavorite = isFavorite,
        ocrStatus = OcrStatus.entries.firstOrNull { it.name.equals(ocrStatus, true) } ?: OcrStatus.PENDING,
        lastReadPage = lastReadPage,
    )

    private fun Document.toEntity() = DocumentEntity(
        id = id, name = name, type = type.ext,
        createdAt = createdAt, updatedAt = updatedAt,
        thumbnailPath = thumbnailPath, folderId = folderId,
        isLocked = isLocked, isHidden = isHidden,
        filePath = filePath, pageCount = pageCount, sizeBytes = sizeBytes,
        isFavorite = isFavorite, ocrStatus = ocrStatus.name.lowercase(),
        lastReadPage = lastReadPage,
    )
}
