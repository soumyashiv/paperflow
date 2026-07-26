package com.paperflow.app.core.di

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.paperflow.app.data.local.file.EncryptionManager
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.PDFEngine
import com.paperflow.app.data.local.file.ScanProcessor
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /** WorkManager configuration using HiltWorkerFactory for @AssistedInject workers. */
    @Provides
    @Singleton
    fun provideWorkManagerConfiguration(workerFactory: HiltWorkerFactory): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(android.util.Log.ERROR) // SR-APP-004: minimal logging
            .build()

    @Provides @Singleton
    fun provideFileStorage(@ApplicationContext context: Context) = FileStorage(context)

    @Provides @Singleton
    fun provideEncryptionManager(@ApplicationContext context: Context) = EncryptionManager(context)

    @Provides @Singleton
    fun providePDFEngine(@ApplicationContext context: Context, storage: FileStorage) = PDFEngine(context, storage)

    @Provides @Singleton
    fun provideScanProcessor(@ApplicationContext context: Context, storage: FileStorage) = ScanProcessor(context, storage)
}
