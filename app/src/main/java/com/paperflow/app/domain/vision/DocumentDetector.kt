package com.paperflow.app.domain.vision

import android.graphics.Bitmap
import android.graphics.PointF
import android.util.Log
import com.paperflow.app.domain.vision.pipeline.*
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.sqrt

/**
 * Production-grade document detection pipeline orchestrator.
 *
 * Composes the following stages into a single, end-to-end detection call:
 *
 *   Bitmap (RGBA)
 *     ↓  [ImagePreprocessor]   Resize → Grayscale → CLAHE → Bilateral
 *     ↓  [EdgeDetector]        Canny + AdaptiveThreshold + MorphGrad → Fused edges
 *     ↓  [ContourFinder]       RETR_EXTERNAL + RETR_TREE → merged contours
 *     ↓  [CandidateGenerator]  approxPolyDP → Hull → minAreaRect fallbacks
 *     ↓  [CandidateValidator]  Geometry validation (7 rules)
 *     ↓  [CandidateScorer]     7-term weighted confidence model
 *     ↓  [CornerRefiner]       cornerSubPix sub-pixel precision
 *     ↓  [TemporalSmoother]    EMA smoothing + stability score
 *     ↓  Blur estimation       Variance of Laplacian
 *     ↓  Auto-capture logic
 *     ↓  [DetectionResult]
 *
 * Memory: Every OpenCV [Mat] is allocated in local scope and released before
 * returning. Pre-allocated Mats inside sub-components are released via [release()].
 *
 * Thread safety: NOT thread-safe. Use one instance per analysis thread.
 *
 * Usage:
 *   val detector = DocumentDetector()
 *   val result = detector.detect(bitmap)
 *   detector.release() // call when camera session ends
 */
class DocumentDetector(
    private val config: DocumentDetectionConfig = DocumentDetectionConfig.DEFAULT,
) {
    // ─── Pipeline Components ──────────────────────────────────────────────────

    private val preprocessor = ImagePreprocessor(config)
    private val edgeDetector = EdgeDetector(config)
    private val contourFinder = ContourFinder(config)
    private val candidateGenerator = CandidateGenerator()
    private val candidateValidator = CandidateValidator(config)
    private val candidateScorer = CandidateScorer(config)
    private val cornerRefiner = CornerRefiner(config)
    private val temporalSmoother = TemporalSmoother(config)

    // ─── Auto Capture State ───────────────────────────────────────────────────
    private var stableDetectionStartMs: Long = 0L
    private var lastConfidence: Float = 0f

    // ─── Public API ───────────────────────────────────────────────────────────

    /**
     * Ordered 4-corner quadrilateral in image pixel coordinates.
     * Corners are in clockwise order: TL, TR, BR, BL.
     */
    data class Quad(
        val topLeft: PointF,
        val topRight: PointF,
        val bottomRight: PointF,
        val bottomLeft: PointF,
    )

    /**
     * Rich result returned on every analyzed frame.
     *
     * @param quad Smoothed document corners, or null if no document detected.
     * @param confidence Detection confidence [0, 1].
     * @param stability How long the detection has been stable [0, 1].
     * @param blur Blur score [0, 1]. Higher = sharper. Below 0.25 = too blurry.
     * @param shouldAutoCapture True when all auto-capture criteria are met.
     */
    data class DetectionResult(
        val quad: Quad?,
        val confidence: Float,
        val stability: Float,
        val blur: Float,
        val shouldAutoCapture: Boolean,
    )

    /**
     * Detect the document boundary in a [Bitmap] frame.
     *
     * This is the main entry point. The bitmap is NOT recycled by this call.
     *
     * @param bitmap ARGB/RGBA bitmap of the camera frame (any size).
     * @return [DetectionResult] with quad corners, confidence, stability, blur, and auto-capture flag.
     */
    fun detect(bitmap: Bitmap): DetectionResult {
        val t0 = System.currentTimeMillis()

        // Convert Bitmap to RGBA Mat
        val rgbaMat = Mat()
        Utils.bitmapToMat(bitmap, rgbaMat)

        val result = detectFromMat(rgbaMat, bitmap.width, bitmap.height, t0)
        rgbaMat.release()
        return result
    }

    // ─── Old API compatibility shim ───────────────────────────────────────────
    // SmartDocumentAnalyzer still calls detectDocument(), so we keep this entry.

    /** @deprecated Prefer [detect] which returns the full [DetectionResult]. */
    fun detectDocument(bitmap: Bitmap): Quad? = detect(bitmap).quad

    // ─── Private Pipeline Execution ───────────────────────────────────────────

    private fun detectFromMat(rgba: Mat, origW: Int, origH: Int, t0: Long): DetectionResult {
        // ── Stage 1: Preprocess ──────────────────────────────────────────────
        val preprocessed = preprocessor.process(rgba)
        val grayMat = preprocessed.mat
        val scale = preprocessed.scale

        // ── Blur Estimation (done on preprocessed gray) ──────────────────────
        // Variance of Laplacian: sharp image = high variance. Cheap and reliable.
        val blurScore = estimateBlur(grayMat)

        // ── Stage 2: Edge Detection ──────────────────────────────────────────
        val edgeMat = edgeDetector.detect(grayMat)

        // ── Stage 3: Find Contours ────────────────────────────────────────────
        val contours = contourFinder.find(edgeMat)
        edgeMat.release()

        val frameArea = (grayMat.cols() * grayMat.rows()).toDouble()
        val frameW = grayMat.cols()
        val frameH = grayMat.rows()

        // ── Stage 4: Generate Candidates ─────────────────────────────────────
        val candidates = candidateGenerator.generate(
            contours, frameArea,
            config.minAreaFraction, config.maxAreaFraction
        )
        contours.forEach { it.release() }

        // ── Stage 5: Validate + Stage 6: Score ───────────────────────────────
        var bestCandidate: CandidateScorer.ScoredCandidate? = null
        var bestConfidence = 0f
        var rejectLog = StringBuilder()

        for (candidate in candidates) {
            val validation = candidateValidator.validate(candidate)
            if (!validation.passed) {
                rejectLog.append("  REJECT(${validation.reason}) ")
                continue
            }
            val scored = candidateScorer.score(candidate, frameW, frameH)
            if (scored.confidence > bestConfidence) {
                bestConfidence = scored.confidence
                bestCandidate = scored
            }
        }

        if (rejectLog.isNotEmpty()) {
            Log.d(TAG, "Rejected candidates:$rejectLog")
        }

        // ── Stage 7: Corner Refinement ────────────────────────────────────────
        val refinedCorners: Array<org.opencv.core.Point>? = bestCandidate?.let {
            cornerRefiner.refine(it.points, grayMat)
        }
        grayMat.release()

        // ── Stage 8: Scale corners back to original image dimensions ──────────
        val scaledCorners: Array<PointF>? = refinedCorners?.let { pts ->
            Array(4) { i ->
                PointF((pts[i].x / scale).toFloat(), (pts[i].y / scale).toFloat())
            }
        }

        // ── Stage 9: Temporal Smoothing ───────────────────────────────────────
        val smoothed = temporalSmoother.update(scaledCorners)

        // ── Build Quad ────────────────────────────────────────────────────────
        val quad: Quad? = smoothed.corners?.let {
            Quad(topLeft = it[0], topRight = it[1], bottomRight = it[2], bottomLeft = it[3])
        }

        // ── Auto Capture Logic ────────────────────────────────────────────────
        val coverage = bestCandidate?.let { it.points.let { _ ->
            // Re-derive coverage using confidence-gated area
            candidates.firstOrNull()?.coverage?.toFloat() ?: 0f
        }} ?: 0f

        val shouldAutoCapture = evaluateAutoCapture(
            confidence = bestConfidence,
            stability = smoothed.stability,
            blurScore = blurScore,
            quad = quad,
            origW = origW,
            origH = origH,
        )

        val elapsed = System.currentTimeMillis() - t0
        Log.d(TAG, "detect: ${elapsed}ms | contours=${contours.size} | candidates=${candidates.size} | conf=${String.format("%.2f", bestConfidence)} | stability=${String.format("%.2f", smoothed.stability)} | blur=${String.format("%.2f", blurScore)} | quad=${if (quad != null) "✅" else "❌"} | autoCapture=$shouldAutoCapture")

        return DetectionResult(
            quad = quad,
            confidence = bestConfidence,
            stability = smoothed.stability,
            blur = blurScore,
            shouldAutoCapture = shouldAutoCapture,
        )
    }

    /**
     * Estimate image sharpness using variance of the Laplacian.
     *
     * Rationale: A blurry image has low-amplitude Laplacian values (no sharp transitions).
     * A sharp image has a high variance (many transitions with high amplitude).
     *
     * @return Normalized blur score in [0, 1]. 1 = sharp, 0 = very blurry.
     */
    private fun estimateBlur(gray: Mat): Float {
        if (gray.empty()) return 0f
        return try {
            val lap = Mat()
            Imgproc.Laplacian(gray, lap, CvType.CV_64F)
            val mean = MatOfDouble()
            val stddev = MatOfDouble()
            Core.meanStdDev(lap, mean, stddev)
            val variance = stddev.toArray()[0].let { it * it }
            lap.release()
            mean.release()
            stddev.release()
            // Normalize: variance of 500+ = fully sharp, 0 = blurry
            (variance / config.blurVarianceLaplacianThreshold).coerceIn(0.0, 1.0).toFloat()
        } catch (e: Exception) {
            0.5f // Unknown — assume acceptable
        }
    }

    /**
     * Evaluate whether all auto-capture preconditions are met.
     *
     * All conditions must be true simultaneously:
     *   1. Confidence ≥ threshold
     *   2. Stability ≥ threshold
     *   3. Blur score ≥ threshold (image is sharp)
     *   4. Document inside safe zone (not clipped by frame edge)
     *   5. Detection has been stable for ≥ autoCaptureStableMs
     */
    private fun evaluateAutoCapture(
        confidence: Float,
        stability: Float,
        blurScore: Float,
        quad: Quad?,
        origW: Int,
        origH: Int,
    ): Boolean {
        if (quad == null) {
            stableDetectionStartMs = 0L
            return false
        }

        val passesConfidence = confidence >= config.autoCaptureMinConfidence
        val passesStability = stability >= config.autoCaptureMinStability
        val passesBlur = blurScore >= config.autoCaptureMinBlurScore

        // Safe zone check: all corners must be inside the frame with a small margin
        val marginX = origW * 0.03f
        val marginY = origH * 0.03f
        val corners = listOf(quad.topLeft, quad.topRight, quad.bottomRight, quad.bottomLeft)
        val passesInsideFrame = corners.all { p ->
            p.x >= marginX && p.y >= marginY &&
                    p.x <= origW - marginX && p.y <= origH - marginY
        }

        val allPass = passesConfidence && passesStability && passesBlur && passesInsideFrame

        return if (allPass) {
            val now = System.currentTimeMillis()
            if (stableDetectionStartMs == 0L) stableDetectionStartMs = now
            (now - stableDetectionStartMs) >= config.autoCaptureStableMs
        } else {
            stableDetectionStartMs = 0L
            false
        }
    }

    /**
     * Release all native OpenCV resources held by pipeline components.
     * Call this when the camera session ends (e.g. in [ViewModel.onCleared]).
     */
    fun release() {
        preprocessor.release()
        edgeDetector.release()
        contourFinder.release()
        temporalSmoother.reset()
    }

    companion object {
        private const val TAG = "DocDetector"
    }
}
