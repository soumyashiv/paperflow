package com.paperflow.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.paperflow.app.data.local.database.dao.*
import com.paperflow.app.data.local.database.entity.*

@Database(
    entities = [
        DocumentEntity::class,
        PageEntity::class,
        OCRIndexEntity::class,
        NoteEntity::class,
        AnnotationEntity::class,
        FolderEntity::class,
        TagEntity::class,
        DocumentTagEntity::class,
        ActivityEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
    abstract fun pageDao(): PageDao
    abstract fun ocrIndexDao(): OCRIndexDao
    abstract fun noteDao(): NoteDao
    abstract fun annotationDao(): AnnotationDao
    abstract fun folderDao(): FolderDao
    abstract fun activityDao(): ActivityDao

    companion object {
        const val DATABASE_NAME = "paperflow.db"
    }
}
