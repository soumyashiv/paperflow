package com.paperflow.app.domain.vision.pipeline

/**
 * Central configuration object for the entire document detection pipeline.
 *
 * All thresholds and parameters are collected here so that tuning never
 * requires hunting through individual classes.
 *
 * Every value has a comment explaining its purpose and the expected range.
 */
data class DocumentDetectionConfig(

    // ──────────────────────────────────────────────────────────────────────────
    // Preprocessing
    // ──────────────────────────────────────────────────────────────────────────

    /** Maximum dimension (px) of the scaled-down working image. Larger = slower but more accurate. */
    val processingMaxDim: Int = 800,

    /** CLAHE clip limit — how aggressively histogram is clipped. 2–4 is safe; higher = more contrast. */
    val claheClipLimit: Double = 3.0,

    /** CLAHE tile grid size. Smaller tiles = more local contrast enhancement. */
    val claheTileSize: Int = 8,

    /** Bilateral filter diameter. Larger preserves more edges but is slower. 0 = derive from sigma. */
    val bilateralD: Int = 9,

    /** Bilateral color sigma. Controls how much color variation is smoothed. */
    val bilateralSigmaColor: Double = 75.0,

    /** Bilateral space sigma. Controls how much spatial distance is smoothed. */
    val bilateralSigmaSpace: Double = 75.0,

    // ──────────────────────────────────────────────────────────────────────────
    // Edge Detection
    // ──────────────────────────────────────────────────────────────────────────

    /** Sigma used to compute dynamic Canny thresholds from image median. */
    val cannySigma: Double = 0.33,

    /** Weight assigned to the Canny edge map in the fused edge image. */
    val cannyWeight: Double = 0.5,

    /** Weight assigned to the adaptive threshold edge map in the fused edge image. */
    val adaptiveThreshWeight: Double = 0.3,

    /** Weight assigned to the morphological gradient map in the fused edge image. */
    val morphGradientWeight: Double = 0.2,

    /** Adaptive threshold block size (must be odd). */
    val adaptiveThreshBlockSize: Int = 15,

    /** Adaptive threshold constant subtracted from mean. */
    val adaptiveThreshC: Double = 4.0,

    // ──────────────────────────────────────────────────────────────────────────
    // Morphology
    // ──────────────────────────────────────────────────────────────────────────

    /** Kernel size for morphological closing on the fused edge map. Connects broken edges. */
    val morphCloseKernelSize: Int = 5,

    /** Kernel size for morphological opening (noise removal after closing). */
    val morphOpenKernelSize: Int = 3,

    // ──────────────────────────────────────────────────────────────────────────
    // Contour Evaluation
    // ──────────────────────────────────────────────────────────────────────────

    /** Maximum number of contours to evaluate per frame. Limits CPU cost. */
    val maxContoursToEvaluate: Int = 15,

    // ──────────────────────────────────────────────────────────────────────────
    // Candidate Validation
    // ──────────────────────────────────────────────────────────────────────────

    /** Minimum area of a candidate as a fraction of the frame area. [0, 1] */
    val minAreaFraction: Double = 0.05,

    /** Maximum area of a candidate as a fraction of the frame area. Rejects frame-filling noise. */
    val maxAreaFraction: Double = 0.97,

    /** Minimum convexity ratio (contour area / convex hull area). [0, 1] */
    val minConvexityRatio: Double = 0.80,

    /** Minimum allowed interior angle for any corner of the quad (degrees). */
    val minCornerAngleDeg: Double = 40.0,

    /** Maximum allowed interior angle for any corner of the quad (degrees). */
    val maxCornerAngleDeg: Double = 140.0,

    /** Minimum aspect ratio (short / long side). Rejects degenerate lines. */
    val minAspectRatio: Double = 0.15,

    /** Maximum aspect ratio (short / long side). Rejects very thin slabs. */
    val maxAspectRatio: Double = 1.0, // Always <= 1 since we take min/max

    /** Maximum allowed angle of any side from horizontal/vertical for a "rectangular" document (degrees). */
    val maxSkewDeg: Double = 45.0,

    // ──────────────────────────────────────────────────────────────────────────
    // Candidate Scoring Weights
    // ──────────────────────────────────────────────────────────────────────────

    /** Weight of the area coverage term in the final confidence score. */
    val wArea: Float = 0.25f,

    /** Weight of the rectangularity term. */
    val wRectangularity: Float = 0.20f,

    /** Weight of the convexity term. */
    val wConvexity: Float = 0.15f,

    /** Weight of the aspect ratio score. */
    val wAspect: Float = 0.10f,

    /** Weight of the corner angle consistency term. */
    val wCornerAngles: Float = 0.15f,

    /** Weight of the distance-from-borders penalty. */
    val wBorderDistance: Float = 0.10f,

    /** Weight of the perspective distortion penalty. */
    val wPerspective: Float = 0.05f,

    // ──────────────────────────────────────────────────────────────────────────
    // Corner Refinement
    // ──────────────────────────────────────────────────────────────────────────

    /** Half-size of the search window for `cornerSubPix`. */
    val cornerSubPixWinSize: Int = 5,

    /** Zero-zone size for `cornerSubPix`. -1 = no dead zone. */
    val cornerSubPixZeroZone: Int = -1,

    /** Maximum iterations for `cornerSubPix` refinement. */
    val cornerSubPixMaxIter: Int = 30,

    /** Minimum accuracy for `cornerSubPix` to terminate. */
    val cornerSubPixEpsilon: Double = 0.01,

    // ──────────────────────────────────────────────────────────────────────────
    // Temporal Smoothing
    // ──────────────────────────────────────────────────────────────────────────

    /** Exponential moving average alpha. 0 = no update, 1 = instant snap. */
    val emaAlpha: Float = 0.35f,

    /** Minimum pixel movement before accepting a new corner position. Prevents jitter. */
    val stabilityThresholdPx: Float = 8f,

    /** Number of frames a detection must be stable before `stability` score reaches 1.0. */
    val stabilityFramesRequired: Int = 8,

    // ──────────────────────────────────────────────────────────────────────────
    // Auto Capture
    // ──────────────────────────────────────────────────────────────────────────

    /** Minimum confidence score [0, 1] to consider a document detected. */
    val autoCaptureMinConfidence: Float = 0.82f,

    /** Minimum stability score [0, 1] required for auto capture. */
    val autoCaptureMinStability: Float = 0.90f,

    /** Minimum Laplacian variance (normalized, 0–1) required. Below = blurry. */
    val autoCaptureMinBlurScore: Float = 0.25f,

    /** Minimum document area coverage of the frame [0, 1] required for auto capture. */
    val autoCaptureMinCoverage: Float = 0.12f,

    /** Milliseconds the document must be stably detected before triggering auto capture. */
    val autoCaptureStableMs: Long = 500L,

    /** Maximum allowed Laplacian variance before treating as "too blurry." Higher = sharper. */
    val blurVarianceLaplacianThreshold: Double = 100.0,
) {
    companion object {
        /** Default configuration optimized for real-world desk scanning. */
        val DEFAULT = DocumentDetectionConfig()

        /** High-precision configuration for high-end devices with more time budget. */
        val HIGH_PRECISION = DocumentDetectionConfig(
            processingMaxDim = 1024,
            maxContoursToEvaluate = 20,
            emaAlpha = 0.25f,
            stabilityFramesRequired = 10,
        )
    }
}
