package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Stage 4 — Candidate Polygon Generation.
 *
 * Purpose: Convert raw contours into ordered 4-corner quadrilateral candidates.
 * Documents are quadrilaterals in the physical world; this stage extracts the
 * best 4-corner representation from each contour using a fallback chain.
 *
 * Fallback chain (never returns null for a large-enough contour):
 *   1. approxPolyDP with epsilon sweep → if 4 points found, done.
 *   2. Convex hull of the contour → try approxPolyDP on hull.
 *   3. minAreaRect → guaranteed 4 corners from OpenCV.
 *
 * Input:  List of [MatOfPoint] contours (from [ContourFinder])
 * Output: List of [CandidateQuad] with raw (unordered) corners in image coordinates
 *
 * Failure cases:
 *   - Contour too small: skipped.
 *   - approxPolyDP cannot converge to 4: fallback to minAreaRect.
 *
 * Complexity: O(N × P) where N = contour count, P = contour perimeter.
 */
class CandidateGenerator {

    /**
     * Generate candidate quads from the given contours.
     *
     * @param contours Contours sorted by area descending (from [ContourFinder]).
     * @param frameArea Total pixel area of the working image (width × height).
     * @param minAreaFraction Minimum area fraction to consider.
     * @param maxAreaFraction Maximum area fraction to consider.
     * @return List of raw candidate quads. Does NOT release input contours.
     */
    fun generate(
        contours: List<MatOfPoint>,
        frameArea: Double,
        minAreaFraction: Double,
        maxAreaFraction: Double,
    ): List<CandidateQuad> {
        val candidates = ArrayList<CandidateQuad>(contours.size)

        for (contour in contours) {
            val area = Imgproc.contourArea(contour)
            val coverage = area / frameArea
            if (coverage < minAreaFraction) break // Sorted descending — all smaller too
            if (coverage > maxAreaFraction) continue // Reject frame-filling noise

            val quad = extractQuad(contour, area, frameArea) ?: continue
            candidates.add(quad)
        }

        return candidates
    }

    /**
     * Extract a 4-point quad from a single contour using the fallback chain.
     */
    private fun extractQuad(contour: MatOfPoint, area: Double, frameArea: Double): CandidateQuad? {
        val contour2f = MatOfPoint2f(*contour.toArray())
        val perimeter = Imgproc.arcLength(contour2f, true)

        // ── Step 1: approxPolyDP with epsilon sweep ──────────────────────────
        // Try progressively larger epsilons until we get exactly 4 points.
        // Why sweep: a fixed epsilon often gives 5–8 points for slightly curved
        // edges (IDs, books). We sweep from tight (1%) to loose (8%).
        val approx = MatOfPoint2f()
        var result: Array<Point>? = null

        for (epsilonPct in listOf(0.02, 0.03, 0.04, 0.05, 0.06, 0.08)) {
            val epsilon = epsilonPct * perimeter
            Imgproc.approxPolyDP(contour2f, approx, epsilon, true)
            val pts = approx.toArray()
            if (pts.size == 4) {
                result = pts
                break
            }
        }

        // ── Step 2: Convex Hull fallback ─────────────────────────────────────
        // If approxPolyDP couldn't give us 4 points, try on the convex hull.
        if (result == null) {
            val hullIndices = MatOfInt()
            Imgproc.convexHull(contour, hullIndices)
            val hullPts = hullIndices.toArray().map { contour.toArray()[it] }.toTypedArray()
            hullIndices.release()

            val hull2f = MatOfPoint2f(*hullPts)
            val hullPerimeter = Imgproc.arcLength(hull2f, true)

            for (epsilonPct in listOf(0.02, 0.04, 0.06, 0.08, 0.10)) {
                val epsilon = epsilonPct * hullPerimeter
                Imgproc.approxPolyDP(hull2f, approx, epsilon, true)
                val pts = approx.toArray()
                if (pts.size == 4) {
                    result = pts
                    break
                }
            }
            hull2f.release()
        }

        // ── Step 3: minAreaRect ultimate fallback ─────────────────────────────
        // OpenCV's minAreaRect always returns exactly 4 corners. This is the
        // last resort — it gives the tightest bounding rectangle, which is a
        // reasonable approximation for any rectangular document.
        if (result == null) {
            val rect = Imgproc.minAreaRect(contour2f)
            val boxPts = Array(4) { Point() }
            rect.points(boxPts)
            result = boxPts
        }

        contour2f.release()
        approx.release()

        return result?.let {
            CandidateQuad(
                points = it,
                area = area,
                coverage = area / frameArea,
            )
        }
    }

    /** Raw candidate quad before validation and scoring. */
    data class CandidateQuad(
        /** 4 corner points in image pixel coordinates (unordered). */
        val points: Array<Point>,
        /** Area of the originating contour in pixels². */
        val area: Double,
        /** Coverage fraction of the frame [0, 1]. */
        val coverage: Double,
    ) {
        override fun equals(other: Any?) = other is CandidateQuad && points.contentEquals(other.points)
        override fun hashCode() = points.contentHashCode()
    }
}
