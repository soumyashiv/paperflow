package com.paperflow.app.data.local.database.dao

import androidx.room.*
import com.paperflow.app.data.local.database.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivity(limit: Int = 50): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activity WHERE document_id = :documentId ORDER BY timestamp DESC")
    suspend fun getActivityForDocument(documentId: Long): List<ActivityEntity>

    @Insert
    suspend fun insert(activity: ActivityEntity)

    @Query("DELETE FROM activity WHERE timestamp < :olderThan")
    suspend fun cleanupOldActivity(olderThan: Long)

    @Query("DELETE FROM activity WHERE document_id = :documentId")
    suspend fun deleteForDocument(documentId: Long)
}
