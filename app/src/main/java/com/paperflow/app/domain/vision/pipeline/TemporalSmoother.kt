package com.paperflow.app.domain.vision.pipeline

import android.graphics.PointF
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Stage 8 — Temporal Smoothing.
 *
 * Purpose: Prevent the detected quad from jittering between frames due to
 * minor pixel-level noise in the CV pipeline. A document held steady by the
 * user should produce a completely stable overlay, not a flickering one.
 *
 * Algorithm: Exponential Moving Average (EMA) on each of the 4 corners.
 *   smoothed = alpha * new + (1 - alpha) * previous
 *
 * Only corners that move more than [DocumentDetectionConfig.stabilityThresholdPx]
 * pixels trigger an update. This prevents sub-pixel noise from accumulating
 * in the EMA and eventually shifting the smoothed position.
 *
 * Stability Score: A value in [0, 1] that represents how long the detection
 * has been stable. It increases monotonically as the detector holds steady
 * and resets to 0 on significant movement. Used by the auto-capture logic.
 *
 * Input:  New 4 corner positions + confidence (or null = no detection this frame)
 * Output: Smoothed 4 corner positions, stability score [0, 1]
 *
 * Thread safety: NOT thread-safe. Must be called from a single thread.
 */
class TemporalSmoother(private val config: DocumentDetectionConfig) {

    private var smoothedCorners: Array<PointF>? = null
    private var stableFrameCount = 0
    private var consecutiveMissCount = 0
    private val MAX_MISS_FRAMES = 6 // Reset after 6 consecutive misses

    /**
     * Update the smoother with a new frame's detection result.
     *
     * @param newCorners 4 corners from the current frame (null = no detection).
     * @return [SmoothedResult] with the current smoothed corners and stability.
     */
    fun update(newCorners: Array<PointF>?): SmoothedResult {
        if (newCorners == null || newCorners.size != 4) {
            consecutiveMissCount++
            if (consecutiveMissCount >= MAX_MISS_FRAMES) {
                // Document is gone — reset everything
                smoothedCorners = null
                stableFrameCount = 0
                consecutiveMissCount = 0
            }
            // Return the last known smoothed position (helps during momentary misses)
            val current = smoothedCorners
            return SmoothedResult(
                corners = current,
                stability = if (current != null) computeStability() else 0f,
            )
        }

        consecutiveMissCount = 0

        val previous = smoothedCorners
        if (previous == null) {
            // First detection — seed the smoother
            smoothedCorners = newCorners.clone()
            stableFrameCount = 1
            return SmoothedResult(corners = smoothedCorners!!.clone(), stability = 0f)
        }

        // Check movement: average displacement across all 4 corners
        val avgMovement = (0..3).map { i ->
            distance(previous[i], newCorners[i])
        }.average().toFloat()

        if (avgMovement > config.stabilityThresholdPx) {
            // Document moved significantly — apply EMA update and reset stability
            stableFrameCount = 1
            for (i in 0..3) {
                val alpha = config.emaAlpha
                previous[i] = PointF(
                    alpha * newCorners[i].x + (1f - alpha) * previous[i].x,
                    alpha * newCorners[i].y + (1f - alpha) * previous[i].y,
                )
            }
        } else {
            // Stable — slow EMA to reduce jitter further
            stableFrameCount = (stableFrameCount + 1).coerceAtMost(config.stabilityFramesRequired * 2)
            val slowAlpha = config.emaAlpha * 0.3f
            for (i in 0..3) {
                previous[i] = PointF(
                    slowAlpha * newCorners[i].x + (1f - slowAlpha) * previous[i].x,
                    slowAlpha * newCorners[i].y + (1f - slowAlpha) * previous[i].y,
                )
            }
        }

        smoothedCorners = previous
        return SmoothedResult(
            corners = smoothedCorners!!.clone(),
            stability = computeStability(),
        )
    }

    /** Reset the smoother. Call when the camera session restarts. */
    fun reset() {
        smoothedCorners = null
        stableFrameCount = 0
        consecutiveMissCount = 0
    }

    /** Stability [0, 1] based on how many consecutive stable frames we have seen. */
    private fun computeStability(): Float {
        return (stableFrameCount.toFloat() / config.stabilityFramesRequired).coerceIn(0f, 1f)
    }

    private fun distance(a: PointF, b: PointF): Float {
        val dx = a.x - b.x; val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }

    /** Output of a single smoother update. */
    data class SmoothedResult(
        /** Smoothed corner positions in TL/TR/BR/BL order. Null if no document detected. */
        val corners: Array<PointF>?,
        /** Stability in [0, 1]. Reaches 1.0 after [DocumentDetectionConfig.stabilityFramesRequired] stable frames. */
        val stability: Float,
    ) {
        override fun equals(other: Any?) = other is SmoothedResult && corners.contentDeepEquals(other.corners)
        override fun hashCode() = corners.contentDeepHashCode()
    }
}
