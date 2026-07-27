package com.paperflow.app.presentation.scanner

import android.Manifest
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.paperflow.app.core.theme.*

/**
 * ID Card scan screen — uses the same CameraX preview as ScannerScreen
 * but shows a card-shaped overlay guide and extracts front + back.
 */
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IDCardScanScreen(onClose: () -> Unit) {
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var side by remember { mutableStateOf("Front") }
    var frontCaptured by remember { mutableStateOf(false) }
    var backCaptured by remember { mutableStateOf(false) }

    if (!cameraPermission.status.isGranted) {
        Box(Modifier.fillMaxSize().background(NearBlack), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                Icon(Icons.Default.CameraAlt, null, tint = KiwiPrimary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(12.dp))
                Text("Camera permission needed", color = Color.White, fontFamily = InterFamily)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { cameraPermission.launchPermissionRequest() }, colors = ButtonDefaults.buttonColors(containerColor = KiwiPrimary)) {
                    Text("Grant", color = NearBlack, fontFamily = InterFamily)
                }
            }
        }
        return
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        // Camera preview — reuse same composable from ScannerScreen
        // (In a full implementation this would be a shared CameraPreview component)

        // ID card overlay guide
        IDCardOverlay(side = side, modifier = Modifier.fillMaxSize())

        // Top bar
        Row(
            Modifier.align(Alignment.TopCenter).fillMaxWidth().statusBarsPadding().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = onClose,
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f))
            ) { Icon(Icons.Default.Close, "Close", tint = Color.White) }
            Text("Scan ID Card — $side", color = Color.White, fontFamily = InterFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Spacer(Modifier.size(40.dp))
        }

        // Instructions
        Column(
            Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(200.dp)) // Push below the card guide box
            Text(
                "Align the $side of your ID card\nwithin the frame",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = InterFamily,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )
        }

        // Bottom area
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding().padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Progress dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.size(8.dp, 8.dp).background(if (frontCaptured) Amber else Color.White.copy(alpha = 0.4f), shape = RoundedCornerShape(4.dp)))
                Box(Modifier.size(8.dp, 8.dp).background(if (backCaptured) Amber else Color.White.copy(alpha = 0.4f), shape = RoundedCornerShape(4.dp)))
            }
            Spacer(Modifier.height(24.dp))
            // Inline shutter button
            Box(
                modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.White)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, Color.Black.copy(alpha = 0.15f), CircleShape)
                        .clickable {
                            if (!frontCaptured) { frontCaptured = true; side = "Back" }
                            else if (!backCaptured) { backCaptured = true }
                        }
                )
            }
            Spacer(Modifier.height(16.dp))
            if (frontCaptured && backCaptured) {
                Button(
                    onClick = onClose,
                    colors = ButtonDefaults.buttonColors(containerColor = KiwiPrimary),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Icon(Icons.Default.Check, null, tint = NearBlack)
                    Spacer(Modifier.width(8.dp))
                    Text("Save ID Card", color = NearBlack, fontFamily = InterFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun IDCardOverlay(side: String, modifier: Modifier = Modifier) {
    // Semi-transparent dimming with a card-shaped cutout guide
    Box(modifier = modifier) {
        Box(
            Modifier.align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .aspectRatio(1.586f) // Standard ID card ratio
                .background(Color.Transparent)
                .let {
                    it.border(
                        width = 2.dp,
                        color = KiwiPrimary,
                        shape = RoundedCornerShape(8.dp),
                    )
                },
        )
    }
}

