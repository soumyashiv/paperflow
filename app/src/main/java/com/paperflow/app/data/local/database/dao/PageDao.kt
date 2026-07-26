package com.paperflow.app.data.local.database.dao

import androidx.room.*
import com.paperflow.app.data.local.database.entity.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PageDao {

    @Query("SELECT * FROM pages WHERE document_id = :documentId ORDER BY order_index ASC")
    fun getPagesForDocument(documentId: Long): Flow<List<PageEntity>>

    @Query("SELECT * FROM pages WHERE document_id = :documentId ORDER BY order_index ASC")
    suspend fun getPagesForDocumentSync(documentId: Long): List<PageEntity>

    @Query("SELECT * FROM pages WHERE id = :pageId")
    suspend fun getPageById(pageId: Long): PageEntity?

    @Query("SELECT * FROM pages WHERE document_id = :documentId AND page_number = :pageNumber")
    suspend fun getPage(documentId: Long, pageNumber: Int): PageEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPage(page: PageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPages(pages: List<PageEntity>)

    @Update
    suspend fun updatePage(page: PageEntity)

    @Query("UPDATE pages SET extracted_text = :text WHERE id = :pageId")
    suspend fun updateExtractedText(pageId: Long, text: String)

    @Query("UPDATE pages SET thumbnail_path = :path WHERE id = :pageId")
    suspend fun updateThumbnail(pageId: Long, path: String)

    @Query("UPDATE pages SET order_index = :order WHERE id = :pageId")
    suspend fun updateOrder(pageId: Long, order: Int)

    @Transaction
    suspend fun reorderPages(documentId: Long, orderedPageIds: List<Long>) {
        orderedPageIds.forEachIndexed { index, pageId ->
            updateOrder(pageId, index)
        }
        // Also update page_number to match new order
        orderedPageIds.forEachIndexed { index, pageId ->
            updatePageNumber(pageId, index)
        }
    }

    @Query("UPDATE pages SET page_number = :pageNumber WHERE id = :pageId")
    suspend fun updatePageNumber(pageId: Long, pageNumber: Int)

    @Delete
    suspend fun deletePage(page: PageEntity)

    @Query("DELETE FROM pages WHERE document_id = :documentId")
    suspend fun deleteAllPagesForDocument(documentId: Long)

    @Query("SELECT COUNT(*) FROM pages WHERE document_id = :documentId")
    suspend fun countPagesForDocument(documentId: Long): Int
}
