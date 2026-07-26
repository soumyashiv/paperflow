package com.paperflow.app.data.local.database.dao

import androidx.room.*
import com.paperflow.app.data.local.database.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY updated_at DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_handwritten = :handwritten ORDER BY updated_at DESC")
    fun getNotesByType(handwritten: Boolean): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE is_favorite = 1 ORDER BY updated_at DESC")
    fun getFavoriteNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE folder_id = :folderId ORDER BY updated_at DESC")
    fun getNotesByFolder(folderId: Long): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE linked_document_id = :documentId")
    suspend fun getNotesForDocument(documentId: Long): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE (title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%') ORDER BY updated_at DESC")
    suspend fun searchNotes(query: String): List<NoteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("UPDATE notes SET is_favorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE notes SET content = :content, updated_at = :ts WHERE id = :id")
    suspend fun updateContent(id: Long, content: String, ts: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
}
