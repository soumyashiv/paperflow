package com.paperflow.app.data.local.database.dao

import androidx.room.*
import com.paperflow.app.data.local.database.entity.OCRIndexEntity

@Dao
interface OCRIndexDao {

    /**
     * Full-text search across all indexed content.
     * Caller must filter by document visibility at the repository layer (SR-SEARCH-002).
     *
     * The FTS MATCH syntax supports: word, word*, "exact phrase"
     * Special chars are sanitized before calling this (SR-SEARCH-004).
     */
    @Query("""
        SELECT rowid, document_id, extracted_text
        FROM ocr_index
        WHERE ocr_index MATCH :query
        LIMIT 100
    """)
    suspend fun search(query: String): List<OCRIndexEntity>

    /**
     * Search filtered by visible documents only (excludes locked/hidden).
     * Use this as the primary search path.
     */
    @Query("""
        SELECT oi.rowid, oi.document_id, oi.extracted_text
        FROM ocr_index oi
        INNER JOIN documents d ON oi.document_id = d.id
        WHERE ocr_index MATCH :query
          AND d.is_hidden = 0
          AND d.is_locked = 0
        LIMIT 50
    """)
    suspend fun searchVisible(query: String): List<OCRIndexEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun index(entry: OCRIndexEntity)

    @Query("DELETE FROM ocr_index WHERE rowid = :pageId")
    suspend fun deleteForPage(pageId: Long)

    @Query("DELETE FROM ocr_index WHERE document_id = :documentId")
    suspend fun deleteForDocument(documentId: Long)

    @Query("SELECT COUNT(*) FROM ocr_index WHERE document_id = :documentId")
    suspend fun countIndexedPages(documentId: Long): Int
}
