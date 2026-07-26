package com.paperflow.app.data.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.paperflow.app.data.local.database.dao.DocumentDao
import com.paperflow.app.data.local.database.dao.PageDao
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.PDFEngine
import com.paperflow.app.domain.repository.OCRRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

/**
 * WorkManager worker that performs ML Kit OCR on document pages.
 * Runs on IO dispatcher, retries 3 times with exponential backoff.
 *
 * SR-OCR-001: All OCR runs locally (ML Kit on-device).
 * SR-APP-004: Extracted text is never logged.
 */
@HiltWorker
class OCRWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val documentDao: DocumentDao,
    private val pageDao: PageDao,
    private val ocrRepository: OCRRepository,
    private val fileStorage: FileStorage,
    private val pdfEngine: PDFEngine,
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "OCRWorker"
        const val KEY_DOCUMENT_ID = "document_id"

        fun buildRequest(documentId: Long): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<OCRWorker>()
                .setInputData(workDataOf(KEY_DOCUMENT_ID to documentId))
                .setConstraints(Constraints.NONE)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 10_000, java.util.concurrent.TimeUnit.MILLISECONDS)
                .addTag(TAG)
                .build()
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val documentId = inputData.getLong(KEY_DOCUMENT_ID, -1L)
        if (documentId < 0) return@withContext Result.failure()

        try {
            documentDao.updateOcrStatus(documentId, "processing")
            
            // Fix #6: Read directly from the final document, not temporary scans
            val document = documentDao.getDocumentById(documentId)
            if (document == null) {
                documentDao.updateOcrStatus(documentId, "failed")
                return@withContext Result.failure()
            }
            
            val docFile = File(document.filePath)
            if (!docFile.exists()) {
                documentDao.updateOcrStatus(documentId, "failed")
                return@withContext Result.failure()
            }

            val pages = pageDao.getPagesForDocumentSync(documentId)
            if (pages.isEmpty()) {
                documentDao.updateOcrStatus(documentId, "complete")
                return@withContext Result.success()
            }

            for (page in pages) {
                // Bug #6 Fix: Extract bitmap from final saved PDF or image
                val bitmap = if (document.type.lowercase() == "pdf") {
                    pdfEngine.renderPage(docFile, page.pageNumber, 1200)
                } else {
                    BitmapFactory.decodeFile(docFile.absolutePath)
                }
                
                if (bitmap == null) continue

                // Bug #10 Fix: Correct EXIF rotation before OCR
                val rotation = if (document.type.lowercase() != "pdf") {
                    getExifRotation(docFile)
                } else 0

                val text = recognizeBitmap(bitmap, rotation)
                if (text != null) {
                    pageDao.updateExtractedText(page.id, text)
                    ocrRepository.indexPage(page.id, documentId, text)
                }
            }

            documentDao.updateOcrStatus(documentId, "complete")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "OCR failed for doc $documentId: ${e.javaClass.simpleName}")
            documentDao.updateOcrStatus(documentId, "failed")
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
    
    private fun getExifRotation(file: File): Int {
        return try {
            val exif = ExifInterface(file.absolutePath)
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun recognizeBitmap(bitmap: android.graphics.Bitmap, rotationDegrees: Int): String? {
        return try {
            val image = InputImage.fromBitmap(bitmap, rotationDegrees)
            val result = recognizer.process(image).await()
            bitmap.recycle()
            result.text.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            bitmap.recycle()
            null
        }
    }
}
