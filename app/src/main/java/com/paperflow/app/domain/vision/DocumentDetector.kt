package com.paperflow.app.domain.vision

import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.min

/**
 * Production-grade document corner detection using OpenCV for Android.
 */
object DocumentDetector {

    private const val TAG = "DocDetector"

    data class Quad(
        val topLeft: PointF,
        val topRight: PointF,
        val bottomRight: PointF,
        val bottomLeft: PointF,
    )

    fun detectDocument(bitmap: Bitmap): Quad? {
        val origW = bitmap.width
        val origH = bitmap.height
        Log.d(TAG,"━━━ detectDocument (OPENCV) ━━━ input=${origW}x${origH}")

        // 1. Downscale for faster processing
        val maxDim = 800.0 // Higher resolution for OpenCV for better accuracy
        val scale = min(1.0, maxDim / max(origW, origH))
        val w = max(1, (origW * scale).toInt())
        val h = max(1, (origH * scale).toInt())

        val scaledBmp = if (scale < 1.0) Bitmap.createScaledBitmap(bitmap, w, h, true) else bitmap

        val srcMat = Mat()
        Utils.bitmapToMat(scaledBmp, srcMat)
        if (scaledBmp !== bitmap) scaledBmp.recycle()

        // 2. Grayscale
        val grayMat = Mat()
        Imgproc.cvtColor(srcMat, grayMat, Imgproc.COLOR_RGBA2GRAY)

        // 3. Blur to reduce noise
        val blurredMat = Mat()
        Imgproc.GaussianBlur(grayMat, blurredMat, Size(5.0, 5.0), 0.0)
        Imgproc.medianBlur(blurredMat, blurredMat, 3)

        // 4. Adaptive Edge Detection (Canny)
        val edgesMat = Mat()
        // Compute median to dynamically adjust Canny thresholds
        val median = computeMedian(blurredMat)
        val sigma = 0.33
        val lowerThresh = max(0.0, (1.0 - sigma) * median)
        val upperThresh = min(255.0, (1.0 + sigma) * median)
        Imgproc.Canny(blurredMat, edgesMat, lowerThresh, upperThresh)
        
        Log.d(TAG, "[1] OpenCV Canny: median=$median thresholds=($lowerThresh, $upperThresh)")

        // 5. Morphological Closing (Dilate + Erode) to connect fragmented edges
        val closedMat = Mat()
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
        Imgproc.morphologyEx(edgesMat, closedMat, Imgproc.MORPH_CLOSE, kernel)

        // 6. Find Contours
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(closedMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE)
        Log.d(TAG, "[2] Found ${contours.size} contours")

        // 7. Sort by area descending
        contours.sortByDescending { Imgproc.contourArea(it) }

        var bestQuad: Quad? = null
        var maxArea = 0.0
        val frameArea = w * h.toDouble()

        // 8. Approximate polygons
        for (i in 0 until min(10, contours.size)) {
            val contour = contours[i]
            val area = Imgproc.contourArea(contour)
            val coverage = area / frameArea * 100.0

            if (coverage < 8.0) break // Too small

            val contour2f = MatOfPoint2f(*contour.toArray())
            val approx2f = MatOfPoint2f()
            
            // Douglas-Peucker epsilon: 2% of contour perimeter
            val perimeter = Imgproc.arcLength(contour2f, true)
            val epsilon = 0.02 * perimeter
            Imgproc.approxPolyDP(contour2f, approx2f, epsilon, true)

            val points = approx2f.toArray()
            
            if (points.size == 4 && Imgproc.isContourConvex(MatOfPoint(*points))) {
                if (area > maxArea) {
                    maxArea = area
                    
                    val ordered = orderCorners(points)
                    
                    // Scale back up to original bitmap coordinates
                    fun Point.scaleUp() = PointF((x / scale).toFloat(), (y / scale).toFloat())
                    
                    bestQuad = Quad(
                        topLeft = ordered[0].scaleUp(),
                        topRight = ordered[1].scaleUp(),
                        bottomRight = ordered[2].scaleUp(),
                        bottomLeft = ordered[3].scaleUp()
                    )
                }
            }
        }

        if (bestQuad != null) {
            Log.d(TAG, "✅ [3] DETECTED DOCUMENT: area=${maxArea.toInt()} (${String.format("%.1f", maxArea/frameArea*100)}%)")
        } else {
            Log.w(TAG, "❌ [3] REJECT: No OpenCV contours formed a valid convex quad > 8% area.")
        }

        // Cleanup native memory
        srcMat.release()
        grayMat.release()
        blurredMat.release()
        edgesMat.release()
        closedMat.release()
        hierarchy.release()
        kernel.release()
        contours.forEach { it.release() }

        return bestQuad
    }

    private fun computeMedian(mat: Mat): Double {
        val hist = Mat()
        Imgproc.calcHist(
            listOf(mat),
            MatOfInt(0),
            Mat(),
            hist,
            MatOfInt(256),
            MatOfFloat(0f, 256f)
        )
        
        val total = mat.rows() * mat.cols()
        var currentSum = 0f
        val histData = FloatArray(256)
        hist.get(0, 0, histData)
        
        for (i in 0..255) {
            currentSum += histData[i]
            if (currentSum >= total / 2.0f) {
                hist.release()
                return i.toDouble()
            }
        }
        hist.release()
        return 127.0
    }

    private fun orderCorners(pts: Array<Point>): List<Point> {
        // Sort corners into TL, TR, BR, BL based on their sum/diff of coordinates
        // Top-Left has smallest sum, Bottom-Right has largest sum
        // Top-Right has largest diff (x-y), Bottom-Left has smallest diff
        
        var tl = pts[0]; var minSum = Double.MAX_VALUE
        var br = pts[0]; var maxSum = -Double.MAX_VALUE
        var tr = pts[0]; var maxDiff = -Double.MAX_VALUE
        var bl = pts[0]; var minDiff = Double.MAX_VALUE

        for (p in pts) {
            val sum = p.x + p.y
            val diff = p.x - p.y
            if (sum < minSum) { minSum = sum; tl = p }
            if (sum > maxSum) { maxSum = sum; br = p }
            if (diff > maxDiff) { maxDiff = diff; tr = p }
            if (diff < minDiff) { minDiff = diff; bl = p }
        }
        
        return listOf(tl, tr, br, bl)
    }
}
