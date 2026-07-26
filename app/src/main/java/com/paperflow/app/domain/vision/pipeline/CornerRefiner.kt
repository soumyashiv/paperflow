package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Stage 7 — Sub-pixel Corner Refinement.
 *
 * Purpose: After the winning candidate is selected, improve the precision of
 * its 4 corner coordinates from integer pixel accuracy to sub-pixel accuracy
 * using OpenCV's [Imgproc.cornerSubPix].
 *
 * Why: Document crop quality is directly proportional to corner precision.
 * A 2–3 pixel error in a corner position becomes a visible skew or fringe in
 * the final cropped image. cornerSubPix minimises the error to sub-pixel level
 * by refining each corner towards the local intensity gradient minimum.
 *
 * Input:  4 ordered corner points + preprocessed grayscale [Mat]
 * Output: Same 4 corners with sub-pixel refined coordinates
 *
 * Failure cases:
 *   - If cornerSubPix fails (e.g. input is empty or corners are out of bounds),
 *     the original unrefined corners are returned unchanged.
 *
 * Complexity: O(4 × W² × iterations) where W = cornerSubPixWinSize.
 */
class CornerRefiner(private val config: DocumentDetectionConfig) {

    /**
     * Refine the given 4 corner points using sub-pixel optimisation.
     *
     * @param corners 4 corner points in clockwise order (from [CandidateScorer]).
     * @param gray The preprocessed grayscale [Mat] (output of [ImagePreprocessor]).
     * @return Refined corners (same ordering). Returned array is always length-4.
     */
    fun refine(corners: Array<Point>, gray: Mat): Array<Point> {
        if (gray.empty() || corners.size != 4) return corners

        return try {
            val corners2f = MatOfPoint2f(*corners)

            val winSize = Size(
                config.cornerSubPixWinSize.toDouble(),
                config.cornerSubPixWinSize.toDouble()
            )
            val zeroZone = Size(
                config.cornerSubPixZeroZone.toDouble(),
                config.cornerSubPixZeroZone.toDouble()
            )
            val criteria = TermCriteria(
                TermCriteria.EPS + TermCriteria.MAX_ITER,
                config.cornerSubPixMaxIter,
                config.cornerSubPixEpsilon
            )

            Imgproc.cornerSubPix(gray, corners2f, winSize, zeroZone, criteria)

            val refined = corners2f.toArray()
            corners2f.release()
            refined
        } catch (e: Exception) {
            // Sub-pixel refinement is a best-effort enhancement; never fail hard
            corners
        }
    }
}
