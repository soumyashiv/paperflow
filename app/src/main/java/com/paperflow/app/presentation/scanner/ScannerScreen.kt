package com.paperflow.app.presentation.scanner

import android.Manifest
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.paperflow.app.core.theme.*
import java.io.File
import java.util.concurrent.Executors

private const val TAG = "ScannerScreen"

// ─── Screen ───────────────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerScreen(
    onClose: () -> Unit,
    onScanComplete: (String) -> Unit,
    viewModel: ScannerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    val haptic = LocalHapticFeedback.current

    // Navigate out when a session is complete
    LaunchedEffect(state.sessionId) {
        state.sessionId?.let { onScanComplete(it) }
    }

    // Haptic: stable detection lock
    LaunchedEffect(state.isDetectionStable, state.detectedType) {
        if (state.isDetectionStable && state.detectedType != DocumentType.UNKNOWN) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    // Haptic: countdown tick
    LaunchedEffect(state.autoCaptureCountdown) {
        if (state.autoCaptureCountdown != null) {
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        }
    }
    // Haptic: shutter fired
    LaunchedEffect(state.isCapturing) {
        if (state.isCapturing) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    if (!cameraPermission.status.isGranted) {
        CameraPermissionRequest(
            onRequest = { cameraPermission.launchPermissionRequest() },
            onClose = onClose,
        )
        return
    }

    // Show a camera error if one was set by the ViewModel
    if (state.cameraError != null) {
        CameraErrorScreen(message = state.cameraError!!, onRetry = { viewModel.clearError() }, onClose = onClose)
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Camera preview (Edge to Edge) ──────────────────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            CameraPreview(
                onCameraReady = { imageCapture -> viewModel.setImageCapture(imageCapture) },
                onDetectionResult = { type, conf, blur, dark, corners, w, h ->
                    viewModel.onDetectionResult(type, conf, blur, dark, corners, w, h)
                },
                onError = { msg -> viewModel.onCameraError(msg) },
                modifier = Modifier.fillMaxSize(),
            )

            val corners = state.detectedCorners
            if (corners != null && state.frameWidth > 0 && state.frameHeight > 0) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val scaleX = size.width / state.frameWidth
                    val scaleY = size.height / state.frameHeight
                    val scale = maxOf(scaleX, scaleY) // CameraX FILL_CENTER uses max scale

                    // Calculate offset if the preview is cropped by FILL_CENTER
                    val scaledW = state.frameWidth * scale
                    val scaledH = state.frameHeight * scale
                    val offsetX = (size.width - scaledW) / 2f
                    val offsetY = (size.height - scaledH) / 2f

                    val path = Path().apply {
                        moveTo(corners.topLeft.x * scale + offsetX, corners.topLeft.y * scale + offsetY)
                        lineTo(corners.topRight.x * scale + offsetX, corners.topRight.y * scale + offsetY)
                        lineTo(corners.bottomRight.x * scale + offsetX, corners.bottomRight.y * scale + offsetY)
                        lineTo(corners.bottomLeft.x * scale + offsetX, corners.bottomLeft.y * scale + offsetY)
                        close()
                    }

                    drawPath(
                        path = path,
                        color = Amber.copy(alpha = 0.3f),
                        style = androidx.compose.ui.graphics.drawscope.Fill
                    )
                    drawPath(
                        path = path,
                        color = Amber,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 6f)
                    )
                }
            }
        }

        // ── Capture flash indicator ────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isCapturing,
            enter = fadeIn(animationSpec = tween(100)),
            exit = fadeOut(animationSpec = tween(500)),
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.8f)))
        }

        // ── Top Bar ────────────────────────────────────────────────────────
        TopBar(
            onClose = onClose,
            isAutoMode = state.isAutoMode,
            onToggleMode = { viewModel.toggleAutoMode() },
            flashEnabled = state.flashEnabled,
            onToggleFlash = { viewModel.toggleFlash() },
            detectedType = state.detectedType,
            isStable = state.isDetectionStable,
            isBlurry = state.isBlurry,
            isLowLight = state.isLowLight,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        // ── Bottom Panel ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            GuidancePill(message = state.guidanceMessage)
            Spacer(modifier = Modifier.height(32.dp))
            BottomControls(
                isCapturing = state.isCapturing,
                onCapture = { viewModel.captureImage() },
                capturedCount = state.capturedCount,
                latestImagePath = state.capturedPaths.lastOrNull(),
                onFinishSession = { viewModel.finishSession() },
            )
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────

@Composable
private fun TopBar(
    onClose: () -> Unit,
    isAutoMode: Boolean,
    onToggleMode: () -> Unit,
    flashEnabled: Boolean,
    onToggleFlash: () -> Unit,
    detectedType: DocumentType,
    isStable: Boolean,
    isBlurry: Boolean,
    isLowLight: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Cancel",
            color = Color.White,
            fontSize = 16.sp,
            fontFamily = InterFamily,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable(onClick = onClose)
                .padding(8.dp),
        )

        // Auto / Manual pill
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.clip(RoundedCornerShape(50)),
        ) {
            Row(modifier = Modifier.padding(4.dp)) {
                ModeTab("Auto", isAutoMode) { if (!isAutoMode) onToggleMode() }
                ModeTab("Manual", !isAutoMode) { if (isAutoMode) onToggleMode() }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Quality warning dot
            val dotColor by animateColorAsState(
                targetValue = when {
                    isBlurry   -> Color(0xFFFF5722) // red = blurry
                    isLowLight -> Color(0xFFFFEB3B) // yellow = low light
                    isStable && detectedType != DocumentType.UNKNOWN -> Color(0xFF4CAF50) // green = locked
                    detectedType != DocumentType.UNKNOWN -> Color(0xFFFFEB3B) // yellow = detecting
                    else -> Color.Transparent
                },
                animationSpec = tween(300),
                label = "status_dot_color",
            )
            if (dotColor != Color.Transparent) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
            }

            // Flash
            Icon(
                imageVector = if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                contentDescription = "Flash",
                tint = Color.White,
                modifier = Modifier.size(24.dp).clickable(onClick = onToggleFlash),
            )
        }
    }
}

@Composable
private fun ModeTab(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color.White.copy(alpha = 0.2f) else Color.Transparent,
        animationSpec = spring(),
        label = "tab_bg",
    )
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = Color.White, fontSize = 14.sp, fontFamily = InterFamily, fontWeight = FontWeight.Medium)
    }
}

// ─── Guidance Pill ───────────────────────────────────────────────────────────

@Composable
private fun GuidancePill(message: String) {
    AnimatedContent(
        targetState = message,
        transitionSpec = {
            (fadeIn() + slideInVertically { it / 2 })
                .togetherWith(fadeOut() + slideOutVertically { -it / 2 })
        },
        label = "guidance_pill",
    ) { msg ->
        Surface(
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.6f),
        ) {
            Text(
                text = msg,
                color = Color.White,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )
        }
    }
}

// ─── Bottom Controls ─────────────────────────────────────────────────────────

@Composable
private fun BottomControls(
    isCapturing: Boolean,
    onCapture: () -> Unit,
    capturedCount: Int,
    latestImagePath: String?,
    onFinishSession: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Gallery thumbnail
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            if (latestImagePath != null) {
                AnimatedContent(
                    targetState = latestImagePath,
                    transitionSpec = { scaleIn() + fadeIn() togetherWith fadeOut() },
                    label = "thumbnail",
                ) { path ->
                    AsyncImage(
                        model = File(path),
                        contentDescription = "Latest scan",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            } else {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White.copy(alpha = 0.7f))
            }
        }

        // Shutter button
        ShutterButton(isCapturing = isCapturing, onClick = onCapture)

        // Page counter / finish button
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(if (capturedCount > 0) Amber else Color.Transparent)
                .clickable(enabled = capturedCount > 0, onClick = onFinishSession),
            contentAlignment = Alignment.Center,
        ) {
            if (capturedCount > 0) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Check, contentDescription = "Finish", tint = NearBlack, modifier = Modifier.size(24.dp))
                    Text("$capturedCount", fontSize = 10.sp, color = NearBlack, fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                }
            }
        }
    }
}

// ─── Shutter Button ──────────────────────────────────────────────────────────

@Composable
private fun ShutterButton(isCapturing: Boolean, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isCapturing || isPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.6f),
        label = "shutter_scale",
    )
    Box(
        modifier = Modifier
            .size(80.dp)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isCapturing,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.size(80.dp).clip(CircleShape).border(4.dp, Color.White, CircleShape))
        Box(Modifier.size(64.dp).clip(CircleShape).background(Color.White))
    }
}

// ─── Camera Preview with ImageAnalysis ───────────────────────────────────────

/**
 * Fix #1: Explicitly set target resolution to 1280×720 for reliable OCR analysis.
 * Fix #2: Wrap executor in DisposableEffect to prevent thread leaks.
 * Fix #3: Move camera bind inside a LaunchedEffect on the lifecycle to handle re-entry.
 * Fix #4: Propagate bind errors to the ViewModel instead of swallowing them.
 * Fix #5: The `onDetectionResult` now includes `isBlurry` and `isLowLight` signals.
 */
@androidx.camera.core.ExperimentalGetImage
@Composable
private fun CameraPreview(
    onCameraReady: (ImageCapture) -> Unit,
    onDetectionResult: (DocumentType, Float, Boolean, Boolean, com.paperflow.app.domain.vision.DocumentDetector.Quad?, Int, Int) -> Unit,
    onError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Fix #2: Properly lifecycle-scoped executor
    val executor = remember { Executors.newSingleThreadExecutor() }
    DisposableEffect(Unit) {
        onDispose { executor.shutdown() }
    }

    val analyzer = remember {
        SmartDocumentAnalyzer(onResult = onDetectionResult)
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                // Fix #1: Explicit 1280×720 resolution for analysis frames
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
                    .build()
                    .also { it.setAnalyzer(executor, analyzer) }

                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .setTargetResolution(Size(3024, 4032)) // Full capture resolution
                    .build()
                onCameraReady(imageCapture)

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        imageCapture,
                        imageAnalysis,
                    )
                } catch (e: Exception) {
                    // Fix #4: Propagate error instead of swallowing
                    Log.e(TAG, "Camera bind failed: ${e.message}", e)
                    onError("Camera failed to start: ${e.message ?: "Unknown error"}")
                }
            }, ContextCompat.getMainExecutor(ctx))

            previewView
        },
        modifier = modifier,
    )
}

// ─── Error Screen ─────────────────────────────────────────────────────────────

@Composable
private fun CameraErrorScreen(message: String, onRetry: () -> Unit, onClose: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(NearBlack),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Icon(Icons.Default.ErrorOutline, null, tint = Color(0xFFFF5722), modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "Camera Error",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFamily,
            )
            Spacer(Modifier.height(8.dp))
            Text(message, color = GrayLight, fontFamily = InterFamily)
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Amber),
            ) {
                Text("Retry", color = NearBlack, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onClose) { Text("Cancel", color = GrayLight) }
        }
    }
}

// ─── Permission Request ───────────────────────────────────────────────────────

@Composable
private fun CameraPermissionRequest(onRequest: () -> Unit, onClose: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(NearBlack),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Icon(Icons.Default.CameraAlt, null, tint = Amber, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text(
                "Camera Access Needed",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = InterFamily,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "PaperFlow needs camera access to scan documents.",
                color = GrayLight,
                fontFamily = InterFamily,
            )
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = onRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Amber),
            ) {
                Text("Grant Permission", color = NearBlack, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onClose) { Text("Cancel", color = GrayLight) }
        }
    }
}
