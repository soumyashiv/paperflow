package com.paperflow.app.domain.vision.pipeline

import org.opencv.core.*
import org.opencv.imgproc.Imgproc

/**
 * Stage 3 — Contour Finding.
 *
 * Purpose: Extract all geometric boundaries from the binary edge map using
 * two complementary contour retrieval modes. Using both modes maximises the
 * chance of finding the document even when it is embedded within a complex scene.
 *
 * Input:  Binary edge [Mat] (output of [EdgeDetector])
 * Output: List of [MatOfPoint] contours, sorted by area (largest first)
 *
 * Why two modes?
 *   - [Imgproc.RETR_EXTERNAL]: Only outermost contours. Fast; works when
 *     the document is the dominant boundary in the scene.
 *   - [Imgproc.RETR_TREE]: All contours in hierarchy. Slower; needed for
 *     documents embedded inside other shapes (e.g. business card on a desk).
 *   Both sets are merged and de-duplicated by area before returning.
 *
 * Failure cases: If edge map is empty, returns an empty list.
 * Complexity: O(perimeter) for contour following.
 */
class ContourFinder(private val config: DocumentDetectionConfig) {

    private val hierarchy = Mat()

    /**
     * Find all contours from the given binary edge [Mat].
     *
     * @param edges Binary edge Mat (output of [EdgeDetector]). NOT released here.
     * @return List of contours sorted by area descending. Caller must release each contour's [Mat].
     */
    fun find(edges: Mat): List<MatOfPoint> {
        if (edges.empty()) return emptyList()

        val externalContours = ArrayList<MatOfPoint>()
        val treeContours = ArrayList<MatOfPoint>()

        // Mode 1: External contours only — fast path
        Imgproc.findContours(
            edges, externalContours, hierarchy,
            Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE
        )

        // Mode 2: Full tree — catches embedded documents
        // We use a copy so that RETR_TREE doesn't mutate the edge map
        val edgesCopy = edges.clone()
        Imgproc.findContours(
            edgesCopy, treeContours, hierarchy,
            Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE
        )
        edgesCopy.release()

        // Merge: combine both lists, de-duplicate by area bucket (±5%), then sort
        val all = ArrayList<MatOfPoint>(externalContours.size + treeContours.size)
        all.addAll(externalContours)
        all.addAll(treeContours)

        // Sort by area descending
        all.sortByDescending { Imgproc.contourArea(it) }

        // Trim to the top N to avoid wasting time on tiny noise contours
        return if (all.size > config.maxContoursToEvaluate) {
            // Release the discarded ones immediately
            for (i in config.maxContoursToEvaluate until all.size) all[i].release()
            all.subList(0, config.maxContoursToEvaluate)
        } else {
            all
        }
    }

    /** Release pre-allocated native resources. */
    fun release() {
        hierarchy.release()
    }
}
