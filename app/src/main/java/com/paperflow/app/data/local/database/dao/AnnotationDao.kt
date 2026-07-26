package com.paperflow.app.data.local.database.dao

import androidx.room.*
import com.paperflow.app.data.local.database.entity.AnnotationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnotationDao {

    @Query("SELECT * FROM annotations WHERE page_id = :pageId ORDER BY created_at ASC")
    fun getAnnotationsForPage(pageId: Long): Flow<List<AnnotationEntity>>

    @Query("SELECT * FROM annotations WHERE page_id = :pageId ORDER BY created_at ASC")
    suspend fun getAnnotationsForPageSync(pageId: Long): List<AnnotationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: AnnotationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotations(annotations: List<AnnotationEntity>)

    @Update
    suspend fun updateAnnotation(annotation: AnnotationEntity)

    @Delete
    suspend fun deleteAnnotation(annotation: AnnotationEntity)

    @Query("DELETE FROM annotations WHERE page_id = :pageId")
    suspend fun deleteAllForPage(pageId: Long)

    @Query("SELECT * FROM annotations WHERE page_id IN (SELECT id FROM pages WHERE document_id = :documentId)")
    suspend fun getAnnotationsForDocument(documentId: Long): List<AnnotationEntity>

    @Query("SELECT COUNT(*) FROM annotations WHERE page_id = :pageId")
    suspend fun countForPage(pageId: Long): Int

    /** Returns the page_id for a given document + zero-based page index, or null if not found. */
    @Query("SELECT id FROM pages WHERE document_id = :documentId AND page_number = :pageIndex LIMIT 1")
    suspend fun getPageIdForDocumentAndIndex(documentId: Long, pageIndex: Int): Long?
}
