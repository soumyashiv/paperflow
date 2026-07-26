package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Stage 2 — Multi-Source Edge Detection with Weighted Fusion.
 *
 * Purpose: Generate a single, high-quality binary edge map by fusing
 * three complementary edge detectors. A single detector (e.g. pure Canny)
 * is insufficient for all document types:
 *
 *   - Canny: excellent for sharp, high-contrast edges (A4 on dark desk).
 *   - Adaptive Threshold: excellent for low-contrast / shadow cases (white on white).
 *   - Morphological Gradient: excellent for thick, soft edges (books, notebooks).
 *
 * Input:  Preprocessed grayscale [Mat]
 * Output: Single binary edge [Mat] (CV_8U, 0 or 255)
 *
 * Fusion pipeline:
 *   Gray
 *   ├──► Canny (dynamic thresholds from median pixel value)
 *   ├──► Adaptive Threshold → edge extraction via subtraction
 *   └──► Morphological Gradient (Dilate − Erode)
 *       ↓
 *   Weighted additive merge → normalize → threshold → binary edges
 *       ↓
 *   Morphological Close (connect broken edges)
 *       ↓
 *   Morphological Open (remove isolated noise)
 *
 * Failure cases: If input is empty, returns an empty Mat.
 * Complexity: O(W × H) — linear in pixel count.
 */
class EdgeDetector(private val config: DocumentDetectionConfig) {

    // Reusable Mats
    private val cannyMat = Mat()
    private val adaptiveMat = Mat()
    private val morphGradMat = Mat()
    private val fusedMat = Mat()
    private val tmp1 = Mat()
    private val tmp2 = Mat()

    // Reusable kernels — created once to avoid repeated allocation
    private val closeKernel: Mat = Imgproc.getStructuringElement(
        Imgproc.MORPH_RECT,
        Size(config.morphCloseKernelSize.toDouble(), config.morphCloseKernelSize.toDouble())
    )
    private val openKernel: Mat = Imgproc.getStructuringElement(
        Imgproc.MORPH_RECT,
        Size(config.morphOpenKernelSize.toDouble(), config.morphOpenKernelSize.toDouble())
    )
    private val gradKernel: Mat = Imgproc.getStructuringElement(
        Imgproc.MORPH_RECT, Size(3.0, 3.0)
    )

    /**
     * Compute the fused edge map from the given preprocessed grayscale [Mat].
     *
     * @param gray Preprocessed grayscale Mat (output of [ImagePreprocessor]). NOT released here.
     * @return Binary edge Mat (caller must release).
     */
    fun detect(gray: Mat): Mat {
        if (gray.empty()) return Mat()

        // ── A. Dynamic Canny ─────────────────────────────────────────────────
        // Compute image median to set Canny thresholds adaptively.
        // Why: Fixed Canny thresholds fail in diverse lighting.
        // A sigma of 0.33 around the median is a widely used rule-of-thumb.
        val median = computeMedian(gray)
        val sigma = config.cannySigma
        val lower = maxOf(0.0, (1.0 - sigma) * median)
        val upper = minOf(255.0, (1.0 + sigma) * median)
        Imgproc.Canny(gray, cannyMat, lower, upper)

        // ── B. Adaptive Threshold Edges ──────────────────────────────────────
        // Why: Works when global contrast is low (e.g. white paper on white desk).
        // We threshold locally, then invert so white = edges.
        Imgproc.adaptiveThreshold(
            gray, adaptiveMat, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            config.adaptiveThreshBlockSize,
            config.adaptiveThreshC
        )

        // ── C. Morphological Gradient ─────────────────────────────────────────
        // Gradient = Dilate − Erode. Highlights transitions = edges.
        // Why: More robust to wide, soft edges (book spines, rounded corners).
        Imgproc.morphologyEx(gray, morphGradMat, Imgproc.MORPH_GRADIENT, gradKernel)
        // Threshold to binary
        Core.threshold(morphGradMat, morphGradMat, 30.0, 255.0, Core.THRESH_BINARY)

        // ── D. Weighted Fusion ────────────────────────────────────────────────
        // Combine three maps with weights from config.
        // Using addWeighted twice (only 2 operands supported per call).
        Core.addWeighted(cannyMat, config.cannyWeight, adaptiveMat, config.adaptiveThreshWeight, 0.0, tmp1)
        Core.addWeighted(tmp1, 1.0, morphGradMat, config.morphGradientWeight, 0.0, fusedMat)

        // Normalize to 0-255 and threshold to binary
        Core.normalize(fusedMat, fusedMat, 0.0, 255.0, Core.NORM_MINMAX)
        Core.threshold(fusedMat, fusedMat, 50.0, 255.0, Core.THRESH_BINARY)

        // ── E. Morphological Closing ──────────────────────────────────────────
        // Connect edges that have small gaps (due to shadows, highlights, text overlay).
        Imgproc.morphologyEx(fusedMat, tmp1, Imgproc.MORPH_CLOSE, closeKernel)

        // ── F. Morphological Opening ──────────────────────────────────────────
        // Remove small isolated noise pixels that would generate false contours.
        Imgproc.morphologyEx(tmp1, tmp2, Imgproc.MORPH_OPEN, openKernel)

        val output = Mat()
        tmp2.copyTo(output)
        return output
    }

    /** Compute the median pixel value of a single-channel [Mat] via histogram. */
    private fun computeMedian(mat: Mat): Double {
        val hist = Mat()
        Imgproc.calcHist(
            listOf(mat), MatOfInt(0), Mat(), hist,
            MatOfInt(256), MatOfFloat(0f, 256f)
        )
        val total = mat.rows() * mat.cols()
        val histData = FloatArray(256)
        hist.get(0, 0, histData)
        hist.release()

        var cumsum = 0f
        for (i in 0..255) {
            cumsum += histData[i]
            if (cumsum >= total / 2.0f) return i.toDouble()
        }
        return 127.0
    }

    /** Release all pre-allocated native resources. */
    fun release() {
        cannyMat.release()
        adaptiveMat.release()
        morphGradMat.release()
        fusedMat.release()
        tmp1.release()
        tmp2.release()
        closeKernel.release()
        openKernel.release()
        gradKernel.release()
    }
}
