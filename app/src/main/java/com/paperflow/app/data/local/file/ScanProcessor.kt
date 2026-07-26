package com.paperflow.app.data.local.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import androidx.exifinterface.media.ExifInterface
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Image processing pipeline for scanned documents.
 * Handles: auto-rotate, perspective correction, filtering (color/grayscale/B&W).
 */
@Singleton
class ScanProcessor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FileStorage,
) {
    /**
     * Apply a scan filter to a bitmap.
     * Returns a new Bitmap — caller is responsible for recycling old bitmap.
     */
    fun applyFilter(source: Bitmap, filter: ScanFilterMode): Bitmap {
        return when (filter) {
            ScanFilterMode.COLOR -> source // No-op
            ScanFilterMode.GRAYSCALE -> toGrayscale(source)
            ScanFilterMode.BLACK_WHITE -> toBlackAndWhite(source)
            ScanFilterMode.ORIGINAL -> source
        }
    }

    private fun toGrayscale(src: Bitmap): Bitmap {
        return try {
            val srcMat = org.opencv.core.Mat()
            org.opencv.android.Utils.bitmapToMat(src, srcMat)
            val grayMat = org.opencv.core.Mat()
            org.opencv.imgproc.Imgproc.cvtColor(srcMat, grayMat, org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY)
            val result = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
            org.opencv.android.Utils.matToBitmap(grayMat, result)
            srcMat.release()
            grayMat.release()
            result
        } catch (e: Exception) {
            src
        }
    }

    private fun toBlackAndWhite(src: Bitmap): Bitmap {
        return try {
            val srcMat = org.opencv.core.Mat()
            org.opencv.android.Utils.bitmapToMat(src, srcMat)
            
            // Convert to grayscale
            val grayMat = org.opencv.core.Mat()
            org.opencv.imgproc.Imgproc.cvtColor(srcMat, grayMat, org.opencv.imgproc.Imgproc.COLOR_RGBA2GRAY)
            
            // Apply OpenCV Adaptive Thresholding for the signature CamScanner crisp B&W look
            val bwMat = org.opencv.core.Mat()
            org.opencv.imgproc.Imgproc.adaptiveThreshold(
                grayMat, bwMat, 255.0, 
                org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 
                org.opencv.imgproc.Imgproc.THRESH_BINARY, 
                21, 15.0
            )
            
            val result = Bitmap.createBitmap(src.width, src.height, Bitmap.Config.ARGB_8888)
            org.opencv.android.Utils.matToBitmap(bwMat, result)
            
            srcMat.release()
            grayMat.release()
            bwMat.release()
            
            result
        } catch (e: Exception) {
            src
        }
    }

    /**
     * Auto-rotate bitmap based on EXIF data from the source file.
     * Camera images are often rotated incorrectly without this step.
     */
    suspend fun autoRotateFromExif(file: File): Bitmap? = withContext(Dispatchers.IO) {
        val bitmap = android.graphics.BitmapFactory.decodeFile(file.absolutePath) ?: return@withContext null
        try {
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )
            val rotation = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
            if (rotation == 0f) return@withContext bitmap
            val matrix = Matrix().apply { postRotate(rotation) }
            val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            rotated
        } catch (e: Exception) {
            bitmap
        }
    }

    /**
     * Apply mathematically perfect perspective correction using OpenCV.
     * [corners] are in order: TL, TR, BR, BL.
     * Returns corrected bitmap, or null if correction fails.
     */
    fun correctPerspective(source: Bitmap, corners: Array<PointF>): Bitmap? {
        if (corners.size != 4) return source
        // Calculate output dimensions
        val width = maxOf(
            distance(corners[0], corners[1]),
            distance(corners[3], corners[2])
        ).toInt()
        val height = maxOf(
            distance(corners[0], corners[3]),
            distance(corners[1], corners[2])
        ).toInt()
        
        if (width <= 0 || height <= 0) return source

        return try {
            val srcMat = org.opencv.core.Mat()
            org.opencv.android.Utils.bitmapToMat(source, srcMat)

            val srcPts = org.opencv.core.MatOfPoint2f(
                org.opencv.core.Point(corners[0].x.toDouble(), corners[0].y.toDouble()),
                org.opencv.core.Point(corners[1].x.toDouble(), corners[1].y.toDouble()),
                org.opencv.core.Point(corners[2].x.toDouble(), corners[2].y.toDouble()),
                org.opencv.core.Point(corners[3].x.toDouble(), corners[3].y.toDouble())
            )

            val dstPts = org.opencv.core.MatOfPoint2f(
                org.opencv.core.Point(0.0, 0.0),
                org.opencv.core.Point(width.toDouble(), 0.0),
                org.opencv.core.Point(width.toDouble(), height.toDouble()),
                org.opencv.core.Point(0.0, height.toDouble())
            )

            val perspectiveTransform = org.opencv.imgproc.Imgproc.getPerspectiveTransform(srcPts, dstPts)
            val dstMat = org.opencv.core.Mat()
            org.opencv.imgproc.Imgproc.warpPerspective(
                srcMat, dstMat, perspectiveTransform, 
                org.opencv.core.Size(width.toDouble(), height.toDouble()),
                org.opencv.imgproc.Imgproc.INTER_CUBIC
            )

            val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            org.opencv.android.Utils.matToBitmap(dstMat, result)

            // Native memory cleanup
            srcMat.release()
            srcPts.release()
            dstPts.release()
            perspectiveTransform.release()
            dstMat.release()

            result
        } catch (e: Exception) {
            android.util.Log.e("ScanProcessor", "OpenCV Perspective Transform failed", e)
            source
        }
    }

    private fun distance(a: PointF, b: PointF): Float {
        val dx = b.x - a.x
        val dy = b.y - a.y
        return kotlin.math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
    }

    /** Save bitmap to JPEG file. Returns file on success, null on failure. */
    suspend fun saveBitmap(bitmap: Bitmap, dest: File, quality: Int = 90): Boolean =
        withContext(Dispatchers.IO) {
            try {
                dest.parentFile?.mkdirs()
                dest.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
                }
                true
            } catch (e: Exception) { false }
        }

}

enum class ScanFilterMode { COLOR, GRAYSCALE, BLACK_WHITE, ORIGINAL }
