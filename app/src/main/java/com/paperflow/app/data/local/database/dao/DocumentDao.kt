package com.paperflow.app.data.local.database.dao

import androidx.room.*
import com.paperflow.app.data.local.database.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Query("SELECT * FROM documents WHERE is_hidden = 0 ORDER BY updated_at DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE is_hidden = 0 AND folder_id IS NULL ORDER BY updated_at DESC LIMIT :limit")
    fun getRecentDocuments(limit: Int = 20): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE id = :id")
    suspend fun getDocumentById(id: Long): DocumentEntity?

    @Query("SELECT * FROM documents WHERE folder_id = :folderId AND is_hidden = 0 ORDER BY updated_at DESC")
    fun getDocumentsByFolder(folderId: Long): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE is_favorite = 1 AND is_hidden = 0 ORDER BY updated_at DESC")
    fun getFavoriteDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE type = :type AND is_hidden = 0 ORDER BY updated_at DESC")
    fun getDocumentsByType(type: String): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM documents WHERE name LIKE '%' || :query || '%' AND is_hidden = 0 ORDER BY updated_at DESC")
    suspend fun searchByName(query: String): List<DocumentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Update
    suspend fun updateDocument(document: DocumentEntity)

    @Query("UPDATE documents SET last_read_page = :page, updated_at = :ts WHERE id = :id")
    suspend fun updateLastReadPage(id: Long, page: Int, ts: Long = System.currentTimeMillis())

    @Query("UPDATE documents SET is_favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE documents SET ocr_status = :status WHERE id = :id")
    suspend fun updateOcrStatus(id: Long, status: String)

    @Query("UPDATE documents SET thumbnail_path = :path WHERE id = :id")
    suspend fun updateThumbnailPath(id: Long, path: String)

    @Query("UPDATE documents SET updated_at = :ts WHERE id = :id")
    suspend fun touch(id: Long, ts: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteDocument(document: DocumentEntity)

    @Query("DELETE FROM documents WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM documents WHERE is_hidden = 0")
    suspend fun countAll(): Int

    @Query("SELECT SUM(size_bytes) FROM documents")
    suspend fun getTotalSizeBytes(): Long?
}
