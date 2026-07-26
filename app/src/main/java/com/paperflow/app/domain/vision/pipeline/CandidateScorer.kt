package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.*

/**
 * Stage 6 — Multi-Criteria Candidate Scoring.
 *
 * Purpose: Assign a confidence score [0, 1] to each validated candidate quad.
 * The highest-scoring candidate wins. Unlike "pick the largest contour",
 * scoring considers multiple independent quality signals.
 *
 * Score = weighted sum of normalized sub-scores, each in [0, 1]:
 *
 *   Term              | Weight | Why it matters
 *   ─────────────────────────────────────────────────────────────────────
 *   Area Coverage     | 0.25   | Larger documents are more likely true positives
 *   Rectangularity    | 0.20   | Real documents are more rectangular than noise
 *   Convexity         | 0.15   | Real documents have convex boundaries
 *   Aspect Ratio      | 0.10   | Document aspect ratios cluster around known values
 *   Corner Angles     | 0.15   | Real rectangles have ~90° corners
 *   Border Distance   | 0.10   | Document should not be right at the image edge
 *   Perspective       | 0.05   | Penalise extreme distortion
 *
 * All weights sum to 1.0 (per config defaults).
 *
 * Input:  Validated [CandidateGenerator.CandidateQuad] + frame dimensions
 * Output: [ScoredCandidate] with ordered corners and a confidence [Float]
 */
class CandidateScorer(private val config: DocumentDetectionConfig) {

    data class ScoredCandidate(
        /** Quad corners in clockwise order: TL, TR, BR, BL */
        val points: Array<Point>,
        val confidence: Float,
    ) {
        override fun equals(other: Any?) = other is ScoredCandidate && points.contentEquals(other.points)
        override fun hashCode() = points.contentHashCode()
    }

    /**
     * Score a validated candidate quad.
     *
     * @param candidate A validated quad (passed [CandidateValidator]).
     * @param frameW Width of the working (scaled-down) image in px.
     * @param frameH Height of the working (scaled-down) image in px.
     * @return [ScoredCandidate] with the ordered corners and confidence score.
     */
    fun score(candidate: CandidateGenerator.CandidateQuad, frameW: Int, frameH: Int): ScoredCandidate {
        val pts = candidate.points
        val frameArea = frameW * frameH.toDouble()
        val coverage = candidate.area / frameArea

        // ── Sub-score 1: Area Coverage ────────────────────────────────────────
        // Prefer documents that fill 15–90% of the frame. Peak score at 60%.
        val areaNorm = when {
            coverage < 0.05 -> 0f
            coverage > 0.95 -> 0.1f
            else -> (1.0 - abs(coverage - 0.60) / 0.55).coerceIn(0.0, 1.0).toFloat()
        }

        // ── Sub-score 2: Rectangularity (area vs bounding box area) ──────────
        // A perfect rectangle has area == width × height.
        val sides = (0..3).map { distance(pts[it], pts[(it + 1) % 4]) }
        val avgW = (sides[0] + sides[2]) / 2.0
        val avgH = (sides[1] + sides[3]) / 2.0
        val bboxArea = avgW * avgH
        val rectScore = if (bboxArea > 0) (candidate.area / bboxArea).coerceIn(0.0, 1.0).toFloat() else 0f

        // ── Sub-score 3: Convexity ────────────────────────────────────────────
        val matPts = MatOfPoint(*pts)
        val hullIdx = MatOfInt()
        Imgproc.convexHull(matPts, hullIdx)
        val hullPts = hullIdx.toArray().map { pts[it] }.toTypedArray()
        val hullArea = Imgproc.contourArea(MatOfPoint(*hullPts))
        hullIdx.release()
        matPts.release()
        val convexityScore = if (hullArea > 0) (candidate.area / hullArea).coerceIn(0.0, 1.0).toFloat() else 0f

        // ── Sub-score 4: Aspect Ratio ─────────────────────────────────────────
        // Known document ratios: A4=1.414, Letter=1.294, ID=1.586, Passport=1.42
        // Reward ratios close to any known document standard.
        val aspect = if (avgH > 0) avgW / avgH else 1.0
        val aspectScore = scoreAspectRatio(aspect).toFloat()

        // ── Sub-score 5: Corner Angle Consistency ─────────────────────────────
        // A perfect rectangle has all interior angles = 90°. We penalise
        // deviation from 90° for each corner.
        var totalAnglePenalty = 0f
        for (i in 0..3) {
            val angle = interiorAngleDeg(pts[i], pts[(i + 1) % 4], pts[(i + 2) % 4])
            totalAnglePenalty += abs(angle - 90.0).toFloat()
        }
        val cornerScore = (1f - (totalAnglePenalty / (4f * 45f))).coerceIn(0f, 1f)

        // ── Sub-score 6: Distance from Borders ───────────────────────────────
        // A valid document should not have corners pressed right to the image
        // border. Documents at the very edge are likely partially out of frame.
        val margin = 0.03 // 3% border zone
        val marginPx = maxOf(frameW, frameH) * margin
        val borderPenalty = pts.count { p ->
            p.x < marginPx || p.y < marginPx ||
                    p.x > frameW - marginPx || p.y > frameH - marginPx
        } / 4.0f
        val borderScore = (1f - borderPenalty).coerceIn(0f, 1f)

        // ── Sub-score 7: Perspective Distortion Penalty ───────────────────────
        // Compute the ratio of min side / max side. Extreme distortion (a very
        // "squished" trapezoid) should score lower.
        val minSide = sides.minOrNull() ?: 0.0
        val maxSide = sides.maxOrNull() ?: 1.0
        val perspScore = if (maxSide > 0) (minSide / maxSide).coerceIn(0.0, 1.0).toFloat() else 0f

        // ── Weighted Sum ─────────────────────────────────────────────────────
        val confidence = (
            config.wArea * areaNorm +
            config.wRectangularity * rectScore +
            config.wConvexity * convexityScore +
            config.wAspect * aspectScore +
            config.wCornerAngles * cornerScore +
            config.wBorderDistance * borderScore +
            config.wPerspective * perspScore
        ).coerceIn(0f, 1f)

        // Order corners clockwise: TL, TR, BR, BL
        val ordered = orderCornersClockwise(pts)

        return ScoredCandidate(ordered, confidence)
    }

    // ─── Aspect Ratio Scoring ─────────────────────────────────────────────────

    /** Known document aspect ratios (landscape orientation, long/short). */
    private val knownAspects = listOf(
        1.414,  // A4
        1.294,  // US Letter
        1.586,  // Standard ID (CR-80)
        1.417,  // Passport
        1.0,    // Square (some business cards)
        1.35,   // Receipt (approximate)
        1.5,    // Book / notebook
        1.189,  // A5
    )

    /**
     * Score an aspect ratio against known document proportions.
     * Returns the best match score in [0, 1].
     */
    private fun scoreAspectRatio(aspect: Double): Double {
        // Normalise to > 1
        val a = if (aspect < 1.0) 1.0 / aspect else aspect
        return knownAspects.maxOf { known ->
            val diff = abs(a - known)
            (1.0 - diff / 0.5).coerceIn(0.0, 1.0) // within 0.5 tolerance = good
        }
    }

    // ─── Geometry Helpers ─────────────────────────────────────────────────────

    /**
     * Order 4 corner points clockwise starting from top-left.
     *
     * Uses centroid + atan2 clockwise sorting. Works for arbitrary rotations,
     * including 90°, 180°, extreme perspective, and upside-down.
     */
    fun orderCornersClockwise(pts: Array<Point>): Array<Point> {
        // Compute centroid
        val cx = pts.sumOf { it.x } / 4.0
        val cy = pts.sumOf { it.y } / 4.0

        // Sort clockwise by angle from centroid (atan2 is counterclockwise in math,
        // but image Y is flipped, so negating Y gives clockwise sort)
        val sorted = pts.sortedBy { p ->
            atan2(p.y - cy, p.x - cx)
        }

        // After atan2 sort, order is: left-top-ish, right-top-ish, right-bottom-ish, left-bottom-ish
        // Find the top-left (smallest x+y), then cycle the array so TL is first
        val sortedArr = sorted.toTypedArray()
        var tlIdx = 0
        var minSum = Double.MAX_VALUE
        for (i in sortedArr.indices) {
            val s = sortedArr[i].x + sortedArr[i].y
            if (s < minSum) { minSum = s; tlIdx = i }
        }
        // Rotate array so TL is at index 0
        return Array(4) { sortedArr[(tlIdx + it) % 4] }
    }

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
}
