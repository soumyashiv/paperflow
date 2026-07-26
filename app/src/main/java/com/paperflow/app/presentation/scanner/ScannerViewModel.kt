package com.paperflow.app.presentation.scanner

import android.content.Context
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.data.local.file.FileStorage
import com.paperflow.app.data.local.file.ScanFilterMode
import com.paperflow.app.data.local.file.ScanProcessor
import com.paperflow.app.domain.vision.DocumentDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import java.util.concurrent.Executors
import javax.inject.Inject

private const val TAG = "ScannerViewModel"

data class ScannerUiState(
    val capturedCount: Int = 0,
    val capturedPaths: List<String> = emptyList(),
    val selectedFilter: Int = 0,               // 0=Color, 1=Gray, 2=B&W, 3=Original
    val flashEnabled: Boolean = false,
    val isCapturing: Boolean = false,
    val sessionId: String? = null,             // Set when the session is complete
    // ── Smart detection ──────────────────────────────────────────────────
    val detectedType: DocumentType = DocumentType.UNKNOWN,
    val isDetectionStable: Boolean = false,
    val isAutoMode: Boolean = true,
    val autoCaptureCountdown: Int? = null,
    // ── Live Corners Overlay ──────────────────────────────────────────────
    val detectedCorners: DocumentDetector.Quad? = null,
    val frameWidth: Int = 0,
    val frameHeight: Int = 0,
    // ── Image quality ─────────────────────────────────────────────────────
    val isBlurry: Boolean = false,
    val isLowLight: Boolean = false,
    // ── Error state ───────────────────────────────────────────────────────
    val cameraError: String? = null,
) {
    val guidanceMessage: String
        get() = when {
            cameraError != null          -> "Camera error — tap Retry"
            isLowLight                   -> "Too dark — find better lighting"
            isBlurry                     -> "Hold still — image is blurry"
            autoCaptureCountdown != null -> "Hold steady... $autoCaptureCountdown"
            detectedCorners == null      -> "Position document in view"
            detectedType == DocumentType.UNKNOWN -> "Scanning document..."
            !isDetectionStable           -> "Hold steady"
            else                         -> "${detectedType.displayName} detected ✓"
        }
}

@HiltViewModel
class ScannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val storage: FileStorage,
    private val scanProcessor: ScanProcessor,
) : ViewModel() {

    private val _state = MutableStateFlow(ScannerUiState())
    val state: StateFlow<ScannerUiState> = _state.asStateFlow()

    private var imageCapture: ImageCapture? = null
    private val sessionId = UUID.randomUUID().toString()
    private val executor = Executors.newSingleThreadExecutor()

    private val STABLE_MS = 600L
    private var stableJob: Job? = null
    private var countdownJob: Job? = null

    fun setImageCapture(capture: ImageCapture) {
        imageCapture = capture
    }

    fun toggleFlash() {
        val newFlash = !_state.value.flashEnabled
        imageCapture?.flashMode = if (newFlash) ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
        _state.update { it.copy(flashEnabled = newFlash) }
    }

    fun setFilter(index: Int) = _state.update { it.copy(selectedFilter = index) }

    fun onCameraError(message: String) {
        Log.e(TAG, "Camera error: $message")
        _state.update { it.copy(cameraError = message) }
    }

    fun clearError() {
        _state.update { it.copy(cameraError = null) }
    }

    fun toggleAutoMode() {
        val newMode = !_state.value.isAutoMode
        _state.update { it.copy(isAutoMode = newMode, autoCaptureCountdown = null) }
        countdownJob?.cancel()
    }

    fun onDetectionResult(
        type: DocumentType,
        confidence: Float,
        isBlurry: Boolean,
        isLowLight: Boolean,
        corners: DocumentDetector.Quad?,
        imageWidth: Int,
        imageHeight: Int
    ) {
        Log.d(TAG, "onDetectionResult: type=$type conf=$confidence blur=$isBlurry dark=$isLowLight corners=${if (corners != null) "FOUND" else "null"}")
        _state.update {
            it.copy(
                isBlurry = isBlurry,
                isLowLight = isLowLight,
                detectedCorners = corners,
                frameWidth = imageWidth,
                frameHeight = imageHeight,
            )
        }

        if (countdownJob?.isActive == true && (corners == null || isBlurry || isLowLight)) {
            cancelCountdown()
        }

        if (type != _state.value.detectedType) {
            stableJob?.cancel()
            _state.update { it.copy(detectedType = type, isDetectionStable = false) }

            if (type != DocumentType.UNKNOWN || corners != null) {
                stableJob = viewModelScope.launch {
                    delay(STABLE_MS)
                    _state.update { it.copy(isDetectionStable = true) }
                    startAutoCaptureIfNeeded()
                }
            } else {
                cancelCountdown()
            }
        } else if (corners != null && !_state.value.isDetectionStable && stableJob?.isActive != true) {
            stableJob = viewModelScope.launch {
                delay(STABLE_MS)
                _state.update { it.copy(isDetectionStable = true) }
                startAutoCaptureIfNeeded()
            }
        }
    }

    private fun startAutoCaptureIfNeeded() {
        val s = _state.value
        if (!s.isAutoMode || s.isCapturing || s.isBlurry || s.isLowLight || s.detectedCorners == null) return

        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            for (i in 3 downTo 1) {
                val current = _state.value
                if (current.isBlurry || current.isLowLight || current.detectedCorners == null) {
                    cancelCountdown()
                    return@launch
                }
                _state.update { it.copy(autoCaptureCountdown = i) }
                delay(800)
            }
            _state.update { it.copy(autoCaptureCountdown = null) }
            val final = _state.value
            if (final.isDetectionStable && !final.isBlurry && !final.isLowLight && final.detectedCorners != null) {
                captureImage()
            }
        }
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        _state.update { it.copy(autoCaptureCountdown = null) }
    }

    fun captureImage() {
        val capture = imageCapture ?: run {
            Log.e(TAG, "captureImage: imageCapture is null — cannot capture")
            return
        }
        _state.update { it.copy(isCapturing = true) }
        val index = _state.value.capturedCount
        val outputFile = storage.scanFile(sessionId, index)
        Log.d(TAG, "captureImage: saving raw frame to ${outputFile.absolutePath}")

        val options = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        capture.takePicture(options, executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                Log.d(TAG, "✅ onImageSaved: raw file size=${outputFile.length()} bytes")
                viewModelScope.launch(Dispatchers.IO) {
                    processAndSave(outputFile)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e(TAG, "❌ onError: capture failed — ${exception.message}", exception)
                _state.update { it.copy(isCapturing = false) }
            }
        })
    }

    private suspend fun processAndSave(outputFile: File) {
        Log.d(TAG, "processAndSave: BEGIN")
        val rawBitmap = scanProcessor.autoRotateFromExif(outputFile)
        if (rawBitmap == null) {
            Log.e(TAG, "processAndSave: autoRotateFromExif returned null — skipping processing")
            _state.update { it.copy(isCapturing = false) }
            return
        }
        Log.d(TAG, "processAndSave: bitmap loaded ${rawBitmap.width}x${rawBitmap.height}")

        // ── Stage A: Document boundary detection on full-res image ──────────
        Log.d(TAG, "processAndSave: running DocumentDetector on full-res bitmap...")
        val preciseCorners = DocumentDetector.detectDocument(rawBitmap)

        // ── Stage B: Perspective correction / crop ───────────────────────────
        val croppedBitmap = if (preciseCorners != null) {
            Log.d(TAG, "processAndSave: corners FOUND → running perspective correction")
            Log.d(TAG, "  TL=${preciseCorners.topLeft}  TR=${preciseCorners.topRight}  BR=${preciseCorners.bottomRight}  BL=${preciseCorners.bottomLeft}")
            val pts = arrayOf(
                preciseCorners.topLeft,
                preciseCorners.topRight,
                preciseCorners.bottomRight,
                preciseCorners.bottomLeft,
            )
            val corrected = scanProcessor.correctPerspective(rawBitmap, pts)
            if (corrected != null) {
                Log.d(TAG, "✅ processAndSave: perspective corrected → ${corrected.width}x${corrected.height}")
                corrected
            } else {
                Log.w(TAG, "processAndSave: correctPerspective returned null — using raw bitmap")
                rawBitmap
            }
        } else {
            Log.w(TAG, "processAndSave: DocumentDetector returned null — NO CROP APPLIED. Raw image will be used.")
            rawBitmap
        }

        // ── Stage C: Color filter ─────────────────────────────────────────────
        val filterMode = ScanFilterMode.entries[_state.value.selectedFilter]
        Log.d(TAG, "processAndSave: applying filter $filterMode")
        val filteredBitmap = scanProcessor.applyFilter(croppedBitmap, filterMode)

        // ── Stage D: Save ─────────────────────────────────────────────────────
        val saveOk = scanProcessor.saveBitmap(filteredBitmap, outputFile)
        Log.d(TAG, "processAndSave: save result=$saveOk  file size=${outputFile.length()} bytes  path=${outputFile.absolutePath}")

        if (filteredBitmap !== croppedBitmap) filteredBitmap.recycle()
        if (croppedBitmap !== rawBitmap) croppedBitmap.recycle()
        rawBitmap.recycle()

        Log.d(TAG, "processAndSave: END — updating state capturedCount=${_state.value.capturedCount + 1}")
        _state.update { s ->
            s.copy(
                capturedCount = s.capturedCount + 1,
                capturedPaths = s.capturedPaths + outputFile.absolutePath,
                isCapturing = false,
            )
        }
    }

    fun finishSession() {
        if (_state.value.capturedPaths.isEmpty()) {
            Log.w(TAG, "finishSession: no captured paths — ignoring")
            return
        }
        Log.d(TAG, "finishSession: sessionId=$sessionId  paths=${_state.value.capturedPaths}")
        _state.update { it.copy(sessionId = sessionId) }
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
        stableJob?.cancel()
        countdownJob?.cancel()
        if (_state.value.sessionId == null) {
            _state.value.capturedPaths.forEach { storage.secureDelete(File(it)) }
        }
    }
}
