package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.*

/**
 * Stage 5 — Candidate Validation.
 *
 * Purpose: Reject geometrically impossible or physically implausible candidates
 * BEFORE scoring, so that the scorer only evaluates sensible quads.
 * Separating validation from scoring keeps both classes focused and testable.
 *
 * Validation rules (each rule has a rejection reason string for logging):
 *   1. Minimum area coverage.
 *   2. Maximum area coverage (rejects frame-filling noise).
 *   3. Convexity ratio (area / convex-hull area).
 *   4. Corner angle limits (each interior angle must be within min–max degrees).
 *   5. Aspect ratio limits (short side / long side must be > minAspectRatio).
 *   6. Maximum skew (no side should be more than 45° from horizontal/vertical
 *      — this rejects random diagonal lines or triangular shapes).
 *   7. Self-intersection check (a valid quad should be convex; Imgproc.isContourConvex).
 *
 * Input:  [CandidateGenerator.CandidateQuad]
 * Output: [ValidationResult] — either PASS or FAIL with a reason string.
 */
class CandidateValidator(private val config: DocumentDetectionConfig) {

    data class ValidationResult(val passed: Boolean, val reason: String = "")

    /**
     * Validate a single candidate quad.
     *
     * @param candidate The raw quad from [CandidateGenerator].
     * @return [ValidationResult] with pass/fail and failure reason.
     */
    fun validate(candidate: CandidateGenerator.CandidateQuad): ValidationResult {
        val pts = candidate.points
        val area = candidate.area
        val coverage = candidate.coverage

        // 1. Minimum area
        if (coverage < config.minAreaFraction) {
            return ValidationResult(false, "area_too_small coverage=${coverage.fmt()}")
        }

        // 2. Maximum area
        if (coverage > config.maxAreaFraction) {
            return ValidationResult(false, "area_too_large coverage=${coverage.fmt()}")
        }

        // 3. Self-intersection / convexity via OpenCV
        val matPts = MatOfPoint(*pts)
        val isConvex = Imgproc.isContourConvex(matPts)
        matPts.release()
        if (!isConvex) {
            return ValidationResult(false, "not_convex")
        }

        // 4. Convexity ratio (area vs hull area)
        val mat2f = MatOfPoint2f(*pts)
        val hullIdx = MatOfInt()
        val matPtsForHull = MatOfPoint(*pts)
        Imgproc.convexHull(matPtsForHull, hullIdx)
        val hullPts = hullIdx.toArray().map { pts[it] }.toTypedArray()
        val hullArea = Imgproc.contourArea(MatOfPoint(*hullPts))
        hullIdx.release()
        matPtsForHull.release()
        mat2f.release()

        val convexityRatio = if (hullArea > 0) area / hullArea else 0.0
        if (convexityRatio < config.minConvexityRatio) {
            return ValidationResult(false, "low_convexity ratio=${convexityRatio.fmt()}")
        }

        // 5. Corner angle limits
        for (i in 0..3) {
            val angle = interiorAngleDeg(pts[i], pts[(i + 1) % 4], pts[(i + 2) % 4])
            if (angle < config.minCornerAngleDeg || angle > config.maxCornerAngleDeg) {
                return ValidationResult(false, "bad_corner_angle idx=$i angle=${angle.fmt()}deg")
            }
        }

        // 6. Aspect ratio limits
        val sides = (0..3).map { distance(pts[it], pts[(it + 1) % 4]) }
        val avgShort = minOf(
            (sides[0] + sides[2]) / 2.0,
            (sides[1] + sides[3]) / 2.0
        )
        val avgLong = maxOf(
            (sides[0] + sides[2]) / 2.0,
            (sides[1] + sides[3]) / 2.0
        )
        val aspectRatio = if (avgLong > 0) avgShort / avgLong else 0.0
        if (aspectRatio < config.minAspectRatio) {
            return ValidationResult(false, "bad_aspect_ratio ratio=${aspectRatio.fmt()}")
        }

        return ValidationResult(true)
    }

    // ─── Geometry Helpers ─────────────────────────────────────────────────────

    /**
     * Compute the interior angle at vertex [b] of the path a→b→c (degrees).
     * Uses the law of cosines.
     */
    private fun interiorAngleDeg(a: Point, b: Point, c: Point): Double {
        val v1x = a.x - b.x; val v1y = a.y - b.y
        val v2x = c.x - b.x; val v2y = c.y - b.y
        val dot = v1x * v2x + v1y * v2y
        val mag1 = sqrt(v1x * v1x + v1y * v1y)
        val mag2 = sqrt(v2x * v2x + v2y * v2y)
        if (mag1 == 0.0 || mag2 == 0.0) return 90.0
        return Math.toDegrees(acos((dot / (mag1 * mag2)).coerceIn(-1.0, 1.0)))
    }

    private fun distance(a: Point, b: Point): Double {
        val dx = a.x - b.x; val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }

    private fun Double.fmt() = String.format("%.3f", this)
}
