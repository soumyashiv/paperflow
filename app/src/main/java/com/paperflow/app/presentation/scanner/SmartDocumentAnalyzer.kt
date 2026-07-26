package com.paperflow.app.presentation.scanner

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.paperflow.app.domain.vision.DocumentDetector
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.abs

/**
 * CameraX [ImageAnalysis.Analyzer] that classifies the document type visible
 * in the viewfinder using ML Kit Text Recognition + Document Boundary Detection.
 *
 * The [DocumentDetector.detect] API is used, providing a full [DocumentDetector.DetectionResult]
 * including confidence, stability, blur score, and auto-capture recommendation.
 */
class SmartDocumentAnalyzer(
    private val onResult: (
        type: DocumentType,
        confidence: Float,
        isBlurry: Boolean,
        isLowLight: Boolean,
        detectionResult: DocumentDetector.DetectionResult,
        imageWidth: Int,
        imageHeight: Int,
    ) -> Unit,
) : ImageAnalysis.Analyzer {

    // One detector instance per analyzer (single-threaded use guaranteed by CameraX)
    private val detector = DocumentDetector()

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val isBusy = AtomicBoolean(false)
    private val lastAnalysisTs = AtomicLong(0L)

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val now = System.currentTimeMillis()
        val elapsed = now - lastAnalysisTs.get()

        // Throttle to MAX_FPS; drop frames when analyser is already running
        if (elapsed < FRAME_INTERVAL_MS || isBusy.getAndSet(true)) {
            imageProxy.close()
            return
        }
        lastAnalysisTs.set(now)

        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            isBusy.set(false)
            imageProxy.close()
            return
        }

        // ── 1. Image Quality ──────────────────────────────────────────────────
        val (isBlurry, isLowLight, lumaVariance) = analyzeImageQuality(imageProxy)
        
        // ── 2. Document Boundary Detection ────────────────────────────────────
        val detectionResult: DocumentDetector.DetectionResult
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        
        try {
            // Convert to bitmap for boundary detection
            val bitmap = imageProxy.toBitmap()
            // Rotate so corners match the display orientation
            val rotatedBitmap = if (rotationDegrees != 0) {
                val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else bitmap
            
            detectionResult = detector.detect(rotatedBitmap)
            if (rotatedBitmap !== bitmap) rotatedBitmap.recycle()
            bitmap.recycle()
        } catch (e: Exception) {
            android.util.Log.e("SmartDocAnalyzer", "DocumentDetector threw: ${e.javaClass.simpleName}: ${e.message}", e)
            isBusy.set(false)
            imageProxy.close()
            return
        }

        // Final dimensions based on rotation
        val isPortrait = rotationDegrees == 90 || rotationDegrees == 270
        val finalWidth = if (isPortrait) imageProxy.height else imageProxy.width
        val finalHeight = if (isPortrait) imageProxy.width else imageProxy.height

        // Use the blur score from the rich DetectionResult instead of re-computing
        val isBlurryFromDetector = detectionResult.blur < 0.25f

        // ── 3. OCR Text Classification ────────────────────────────────────────
        val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val (type, confidence) = if (visionText.text.isBlank() || visionText.textBlocks.isEmpty()) {
                    classifyFromImageSignals(lumaVariance, isLowLight)
                } else {
                    classify(
                        fullText = visionText.text,
                        blocks = visionText.textBlocks,
                        imageWidth = finalWidth.toFloat(),
                        imageHeight = finalHeight.toFloat(),
                    )
                }
                onResult(type, confidence, isBlurry || isBlurryFromDetector, isLowLight, detectionResult, finalWidth, finalHeight)
            }
            .addOnCompleteListener {
                isBusy.set(false)
                imageProxy.close()
            }
    }

    // ─── Image Quality Analysis ───────────────────────────────────────────────

    private data class ImageQuality(val isBlurry: Boolean, val isLowLight: Boolean, val lumaVariance: Double)

    private fun analyzeImageQuality(imageProxy: ImageProxy): ImageQuality {
        return try {
            val yPlane = imageProxy.planes[0]
            val buffer = yPlane.buffer.duplicate()
            val rowStride = yPlane.rowStride
            val width = imageProxy.width
            val height = imageProxy.height

            val step = 8
            var sum = 0.0
            var count = 0

            for (y in 0 until height step step) {
                for (x in 0 until width step step) {
                    val index = y * rowStride + x
                    if (index < buffer.limit()) {
                        val luma = (buffer.get(index).toInt() and 0xFF).toDouble()
                        sum += luma
                        count++
                    }
                }
            }

            if (count == 0) return ImageQuality(false, false, 0.0)

            val mean = sum / count
            val isLowLight = mean < LOW_LIGHT_THRESHOLD

            var lapSum = 0.0
            var lapCount = 0
            for (y in step until height - step step step) {
                for (x in step until width - step step step) {
                    val c = (buffer.get(y * rowStride + x).toInt() and 0xFF).toDouble()
                    val n = (buffer.get((y - step) * rowStride + x).toInt() and 0xFF).toDouble()
                    val s = (buffer.get((y + step) * rowStride + x).toInt() and 0xFF).toDouble()
                    val lapVal = abs(2 * c - n - s)
                    lapSum += lapVal * lapVal
                    lapCount++
                }
            }
            val lumaVariance = if (lapCount > 0) lapSum / lapCount else 0.0
            val isBlurry = lumaVariance < BLUR_THRESHOLD

            ImageQuality(isBlurry, isLowLight, lumaVariance)
        } catch (e: Exception) {
            ImageQuality(false, false, 0.0)
        }
    }

    private fun classifyFromImageSignals(lumaVariance: Double, isLowLight: Boolean): ClassResult {
        return when {
            isLowLight -> ClassResult(DocumentType.UNKNOWN, 0f)
            lumaVariance > 200.0 -> ClassResult(DocumentType.DOCUMENT, 0.55f)
            lumaVariance > 80.0 -> ClassResult(DocumentType.DOCUMENT, 0.40f)
            else -> ClassResult(DocumentType.UNKNOWN, 0f)
        }
    }

    // ─── Classification ───────────────────────────────────────────────────────

    private data class ClassResult(val type: DocumentType, val confidence: Float)

    private fun classify(
        fullText: String,
        blocks: List<com.google.mlkit.vision.text.Text.TextBlock>,
        imageWidth: Float,
        imageHeight: Float,
    ): ClassResult {
        if (fullText.isBlank() || blocks.isEmpty()) return ClassResult(DocumentType.UNKNOWN, 0f)

        val upper = fullText.uppercase()
        val wordCount = fullText.split(Regex("\\s+")).size
        val blockCount = blocks.size

        if (AADHAAR_PATTERN.containsMatchIn(fullText)) return ClassResult(DocumentType.ID_CARD_AADHAAR, 0.95f)
        if (PAN_PATTERN.containsMatchIn(fullText))     return ClassResult(DocumentType.ID_CARD_PAN, 0.95f)

        for ((keywords, type, confidence) in KEYWORD_RULES) {
            if (keywords.any { upper.contains(it) }) return ClassResult(type, confidence)
        }

        val textAspect = combinedTextAspect(blocks)
        if (textAspect != null && textAspect in 1.2f..2.2f && blockCount <= 6) {
            return if (wordCount < 20) ClassResult(DocumentType.ID_CARD_GENERIC, 0.72f)
                   else ClassResult(DocumentType.CERTIFICATE, 0.65f)
        }

        val imageAspect = imageWidth / imageHeight
        if (textAspect != null && textAspect > 1.5f && imageAspect < 1f && blockCount > 6) {
            return ClassResult(DocumentType.OPEN_BOOK, 0.70f)
        }

        val charCount = fullText.replace(Regex("\\s"), "").length
        val density = charCount.toFloat() / (imageWidth * imageHeight) * 1_000_000f

        return when {
            density < 0.3f && blockCount <= 4 -> ClassResult(DocumentType.HANDWRITTEN_NOTE, 0.60f)
            density < 1.0f && blockCount <= 8  -> ClassResult(DocumentType.NOTEBOOK, 0.55f)
            density > 4.0f && blockCount > 10  -> ClassResult(DocumentType.BOOK, 0.60f)
            else                               -> ClassResult(DocumentType.DOCUMENT, 0.65f)
        }
    }

    private fun combinedTextAspect(blocks: List<com.google.mlkit.vision.text.Text.TextBlock>): Float? {
        var minX = Int.MAX_VALUE; var maxX = Int.MIN_VALUE
        var minY = Int.MAX_VALUE; var maxY = Int.MIN_VALUE
        for (block in blocks) {
            val bb = block.boundingBox ?: continue
            minX = minOf(minX, bb.left);  maxX = maxOf(maxX, bb.right)
            minY = minOf(minY, bb.top);   maxY = maxOf(maxY, bb.bottom)
        }
        val w = (maxX - minX).toFloat()
        val h = (maxY - minY).toFloat()
        return if (w > 0 && h > 0) w / h else null
    }

    companion object {
        private const val MAX_FPS = 5 // Slightly higher FPS for smooth bounding box
        private const val FRAME_INTERVAL_MS = 1000L / MAX_FPS

        private const val BLUR_THRESHOLD = 30.0
        private const val LOW_LIGHT_THRESHOLD = 60.0

        private val AADHAAR_PATTERN = Regex("""(?<!\d)\d{4}\s\d{4}\s\d{4}(?!\d)""")
        private val PAN_PATTERN     = Regex("""[A-Z]{5}\d{4}[A-Z]""")

        private val KEYWORD_RULES: List<Triple<List<String>, DocumentType, Float>> = listOf(
            Triple(listOf("RECEIPT", "SUBTOTAL", "THANK YOU FOR YOUR PURCHASE", "CASH REGISTER"), DocumentType.RECEIPT, 0.90f),
            Triple(listOf("INVOICE", "INVOICE NO", "INVOICE NUMBER", "TAX INVOICE", "BILL TO", "SHIP TO", "GST", "GSTIN"), DocumentType.INVOICE, 0.90f),
            Triple(listOf("ELECTRICITY BILL", "WATER BILL", "GAS BILL", "UTILITY BILL", "AMOUNT DUE", "DUE DATE"), DocumentType.BILL, 0.85f),
            Triple(listOf("PASSPORT", "REPUBLIC OF INDIA", "DATE OF ISSUE", "NATIONALITY"), DocumentType.ID_CARD_PASSPORT, 0.92f),
            Triple(listOf("DRIVING LICENCE", "DRIVING LICENSE", "D/L NO", "DL NO", "VEHICLE CLASS"), DocumentType.ID_CARD_DRIVING_LICENCE, 0.92f),
            Triple(listOf("ELECTION COMMISSION", "VOTER", "ELECTOR", "EPIC NO"), DocumentType.ID_CARD_VOTER, 0.92f),
            Triple(listOf("STUDENT ID", "STUDENT CARD", "ENROLLMENT NO", "ROLL NO", "UNIVERSITY", "COLLEGE", "SCHOOL ID", "REG NO"), DocumentType.ID_CARD_STUDENT, 0.85f),
            Triple(listOf("EMPLOYEE ID", "EMP ID", "STAFF ID", "EMPLOYEE NO", "DEPARTMENT"), DocumentType.ID_CARD_EMPLOYEE, 0.85f),
            Triple(listOf("CEO", "CTO", "CFO", "DIRECTOR", "MANAGER", "PHONE:", "EMAIL:", "WWW.", "HTTP", "@"), DocumentType.BUSINESS_CARD, 0.75f),
            Triple(listOf("CERTIFICATE", "HEREBY CERTIFY", "AWARDED TO", "THIS IS TO CERTIFY", "IN RECOGNITION"), DocumentType.CERTIFICATE, 0.88f),
            Triple(listOf("LICENSE NO", "LICENCE NO", "PERMIT NO"), DocumentType.LICENCE, 0.82f),
            Triple(listOf("AGREEMENT", "CONTRACT", "TERMS AND CONDITIONS", "WHEREAS", "HEREINAFTER", "PARTY OF THE FIRST"), DocumentType.CONTRACT, 0.85f),
            Triple(listOf("DEAR SIR", "DEAR MADAM", "TO WHOM IT MAY CONCERN", "YOURS SINCERELY", "YOURS FAITHFULLY"), DocumentType.LETTER, 0.80f),
            Triple(listOf("FORM NO", "PLEASE FILL", "APPLICATION FORM", "TICK THE", "SIGNATURE OF APPLICANT"), DocumentType.FORM, 0.82f),
        )
    }
}
