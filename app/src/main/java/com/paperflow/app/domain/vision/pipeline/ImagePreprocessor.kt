package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Stage 1 — Image Preprocessing.
 *
 * Purpose: Normalise the raw camera frame into a clean, contrast-enhanced,
 * noise-reduced grayscale image suitable for robust edge detection.
 *
 * Input:  RGBA [Mat] from the camera (arbitrary size)
 * Output: Grayscale [Mat] at [DocumentDetectionConfig.processingMaxDim] px (max dimension)
 *
 * Pipeline:
 *   RGBA → Resize → Grayscale → CLAHE → Bilateral Filter
 *
 * Failure cases:
 *   - Empty input Mat: returns a new empty Mat (safe no-op).
 *   - CLAHE failure: falls back to unenhanced grayscale.
 *
 * Complexity: O(W × H) — linear in pixel count.
 *
 * Reuse: Pass [preallocatedMat] from a previous frame to avoid repeated native
 * allocation. Caller is responsible for releasing the returned Mat.
 */
class ImagePreprocessor(private val config: DocumentDetectionConfig) {

    // Pre-allocated Mats reused across frames to reduce GC pressure
    private val rgbaMat = Mat()
    private val grayMat = Mat()
    private val claheMat = Mat()
    private val bilateralMat = Mat()

    // CLAHE instance — expensive to create, so we create once and reuse
    private val clahe: CLAHE = Imgproc.createCLAHE(
        config.claheClipLimit,
        Size(config.claheTileSize.toDouble(), config.claheTileSize.toDouble())
    )

    /**
     * Process the given RGBA [Mat] into a preprocessed grayscale [Mat].
     *
     * @param input RGBA Mat from camera (will NOT be released by this function).
     * @param scale Output parameter — the downscale factor applied (output = input * scale).
     * @return A grayscale, CLAHE-enhanced, bilateral-filtered Mat. Caller must release.
     */
    fun process(input: Mat): ProcessedFrame {
        if (input.empty()) return ProcessedFrame(Mat(), 1.0)

        val maxDim = config.processingMaxDim.toDouble()
        val scale = minOf(1.0, maxDim / maxOf(input.cols(), input.rows()))
        val newW = maxOf(1, (input.cols() * scale).toInt())
        val newH = maxOf(1, (input.rows() * scale).toInt())

        // 1. Resize (skip if no scaling needed)
        val resized = if (scale < 1.0) {
            val tmp = Mat()
            Imgproc.resize(input, tmp, Size(newW.toDouble(), newH.toDouble()), 0.0, 0.0, Imgproc.INTER_AREA)
            tmp
        } else {
            input // no copy — caller still owns it
        }

        // 2. Convert to grayscale
        Imgproc.cvtColor(resized, grayMat, Imgproc.COLOR_RGBA2GRAY)
        if (resized !== input) resized.release()

        // 3. Apply CLAHE for contrast normalisation
        //    Why: Corrects shadows, uneven lighting, white-on-white documents.
        //    Without this, low-contrast docs (white paper on white desk) produce
        //    almost no edge signal.
        try {
            clahe.apply(grayMat, claheMat)
        } catch (e: Exception) {
            grayMat.copyTo(claheMat) // Fallback: unenhanced
        }

        // 4. Bilateral filter — de-noise while preserving sharp edges
        //    Why: Gaussian blurs edges; bilateral smooths texture without hurting
        //    the document boundary, which is what contour detection needs.
        Imgproc.bilateralFilter(claheMat, bilateralMat,
            config.bilateralD,
            config.bilateralSigmaColor,
            config.bilateralSigmaSpace)

        // Return a fresh copy so callers can safely release without destroying
        // our pre-allocated buffers
        val output = Mat()
        bilateralMat.copyTo(output)
        return ProcessedFrame(output, scale)
    }

    /**
     * Release all pre-allocated native Mats.
     * Call this when the detector is shut down (e.g. camera session ends).
     */
    fun release() {
        rgbaMat.release()
        grayMat.release()
        claheMat.release()
        bilateralMat.release()
    }

    /** Carries the preprocessed Mat plus the scale used to downsize the image. */
    data class ProcessedFrame(val mat: Mat, val scale: Double)
}
