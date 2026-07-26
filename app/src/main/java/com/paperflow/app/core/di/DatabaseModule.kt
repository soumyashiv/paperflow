package com.paperflow.app.core.di

import android.content.Context
import androidx.room.Room
import com.paperflow.app.data.local.database.AppDatabase
import com.paperflow.app.data.local.database.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigrationFrom() // Dev: safe in v1, add migrations in v2+
            .build()

    @Provides fun provideDocumentDao(db: AppDatabase): DocumentDao = db.documentDao()
    @Provides fun providePageDao(db: AppDatabase): PageDao = db.pageDao()
    @Provides fun provideOCRIndexDao(db: AppDatabase): OCRIndexDao = db.ocrIndexDao()
    @Provides fun provideNoteDao(db: AppDatabase): NoteDao = db.noteDao()
    @Provides fun provideAnnotationDao(db: AppDatabase): AnnotationDao = db.annotationDao()
    @Provides fun provideFolderDao(db: AppDatabase): FolderDao = db.folderDao()
    @Provides fun provideActivityDao(db: AppDatabase): ActivityDao = db.activityDao()
}
