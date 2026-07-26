package com.paperflow.app.data.local.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * PDF operations: rendering pages to Bitmap, generating PDFs from images.
 * Uses Android's built-in PdfRenderer — no external library needed.
 *
 * Rendering strategy: lazy — only render the requested page.
 * Caller manages bitmap lifecycle (don't hold large bitmaps in memory).
 */
@Singleton
class PDFEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FileStorage,
) {
    companion object {
        private const val THUMBNAIL_WIDTH = 200
        private const val PREVIEW_WIDTH = 800
        private const val JPEG_QUALITY = 85
    }

    /**
     * Render a single PDF page to a Bitmap.
     * @param pdfFile The PDF file
     * @param pageIndex 0-based page index
     * @param width Target width in pixels (height calculated from aspect ratio)
     */
    suspend fun renderPage(pdfFile: File, pageIndex: Int, width: Int = PREVIEW_WIDTH): Bitmap? =
        withContext(Dispatchers.IO) {
            if (!pdfFile.exists()) return@withContext null
            try {
                val fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                fd.use {
                    val renderer = PdfRenderer(it)
                    renderer.use { r ->
                        if (pageIndex >= r.pageCount) return@withContext null
                        r.openPage(pageIndex).use { page ->
                            val height = (width.toFloat() / page.width * page.height).toInt()
                            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                            // Fill white background (PDF pages are transparent by default)
                            val canvas = Canvas(bitmap)
                            canvas.drawColor(Color.WHITE)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                            bitmap
                        }
                    }
                }
            } catch (e: Exception) {
                null // SR-ERROR-002: corrupted files fail gracefully
            }
        }

    /** Get total page count of a PDF file. Returns 0 on error. */
    suspend fun getPageCount(pdfFile: File): Int = withContext(Dispatchers.IO) {
        if (!pdfFile.exists()) return@withContext 0
        try {
            ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { renderer -> renderer.pageCount }
            }
        } catch (e: Exception) { 0 }
    }

    /**
     * Generate thumbnail JPG for a specific page.
     * Returns the saved thumbnail file path, or null on failure.
     */
    suspend fun generateThumbnail(pdfFile: File, documentId: Long, pageIndex: Int): String? =
        withContext(Dispatchers.IO) {
            val bitmap = renderPage(pdfFile, pageIndex, THUMBNAIL_WIDTH) ?: return@withContext null
            val thumbFile = storage.thumbnailFile(documentId, pageIndex)
            try {
                thumbFile.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)
                }
                bitmap.recycle()
                thumbFile.absolutePath
            } catch (e: Exception) {
                bitmap.recycle()
                null
            }
        }

    /**
     * Create a PDF from a list of image files (one image per page).
     * Used after scanning and for page reordering.
     */
    suspend fun createPdfFromImages(imagePaths: List<String>, outputFile: File): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val document = PdfDocument()
                imagePaths.forEachIndexed { index, path ->
                    val imgFile = File(path)
                    if (!imgFile.exists()) return@forEachIndexed
                    val options = android.graphics.BitmapFactory.Options().apply {
                        inSampleSize = 1
                    }
                    val bitmap = android.graphics.BitmapFactory.decodeFile(path, options)
                        ?: return@forEachIndexed
                    val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                    val page = document.startPage(pageInfo)
                    page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                    document.finishPage(page)
                    bitmap.recycle()
                }
                outputFile.outputStream().use { out -> document.writeTo(out) }
                document.close()
                true
            } catch (e: Exception) {
                false
            }
        }

    /**
     * Regenerate a PDF from a reordered list of page image paths.
     * Never mutates the original PDF — creates a new file (TRD §11 safety strategy).
     */
    suspend fun rebuildPdfFromPageOrder(
        originalPdf: File,
        orderedPageIndices: List<Int>,
        outputFile: File,
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val document = PdfDocument()
            ParcelFileDescriptor.open(originalPdf, ParcelFileDescriptor.MODE_READ_ONLY).use { fd ->
                PdfRenderer(fd).use { renderer ->
                    orderedPageIndices.forEachIndexed { newIndex, originalIndex ->
                        if (originalIndex >= renderer.pageCount) return@forEachIndexed
                        renderer.openPage(originalIndex).use { page ->
                            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                            Canvas(bitmap).drawColor(Color.WHITE)
                            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
                            val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, newIndex + 1).create()
                            val pdfPage = document.startPage(pageInfo)
                            pdfPage.canvas.drawBitmap(bitmap, 0f, 0f, null)
                            document.finishPage(pdfPage)
                            bitmap.recycle()
                        }
                    }
                }
            }
            outputFile.outputStream().use { document.writeTo(it) }
            document.close()
            true
        } catch (e: Exception) {
            false
        }
    }
}
