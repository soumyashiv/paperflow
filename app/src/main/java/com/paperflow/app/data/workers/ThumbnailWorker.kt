package com.paperflow.app.data.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.paperflow.app.data.local.database.dao.DocumentDao
import com.paperflow.app.data.local.database.dao.PageDao
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.PDFEngine
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Generates thumbnails for all pages of a document.
 * Runs after scan save or import. Non-expedited — can be deferred.
 */
@HiltWorker
class ThumbnailWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val documentDao: DocumentDao,
    private val pageDao: PageDao,
    private val pdfEngine: PDFEngine,
    private val fileStorage: FileStorage,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "ThumbnailWorker"
        const val KEY_DOCUMENT_ID = "document_id"
        const val KEY_FILE_PATH = "file_path"
        const val KEY_TYPE = "doc_type" // "pdf" or "image"

        fun buildRequest(documentId: Long, filePath: String, type: String): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<ThumbnailWorker>()
                .setInputData(workDataOf(
                    KEY_DOCUMENT_ID to documentId,
                    KEY_FILE_PATH to filePath,
                    KEY_TYPE to type,
                ))
                .addTag(TAG)
                .build()
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val documentId = inputData.getLong(KEY_DOCUMENT_ID, -1L)
        val filePath = inputData.getString(KEY_FILE_PATH) ?: return@withContext Result.failure()
        val type = inputData.getString(KEY_TYPE) ?: "pdf"
        val file = File(filePath)
        if (!file.exists()) return@withContext Result.failure()

        try {
            when (type) {
                "pdf" -> {
                    val pageCount = pdfEngine.getPageCount(file)
                    for (i in 0 until pageCount) {
                        val thumbPath = pdfEngine.generateThumbnail(file, documentId, i)
                        if (i == 0 && thumbPath != null) {
                            documentDao.updateThumbnailPath(documentId, thumbPath)
                        }
                        if (thumbPath != null) {
                            val page = pageDao.getPage(documentId, i)
                            if (page != null) {
                                pageDao.updateThumbnail(page.id, thumbPath)
                            }
                        }
                    }
                }
                "jpg", "png", "image" -> {
                    // Single image — thumbnail is a resized version
                    val bitmap = android.graphics.BitmapFactory.decodeFile(filePath) ?: return@withContext Result.failure()
                    val thumbFile = fileStorage.thumbnailFile(documentId, 0)
                    val scaled = android.graphics.Bitmap.createScaledBitmap(bitmap, 200, (200f / bitmap.width * bitmap.height).toInt(), true)
                    thumbFile.outputStream().use { scaled.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, it) }
                    bitmap.recycle()
                    scaled.recycle()
                    documentDao.updateThumbnailPath(documentId, thumbFile.absolutePath)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Thumbnail generation failed: ${e.message}")
            if (runAttemptCount < 2) Result.retry() else Result.failure()
        }
    }
}
