package com.paperflow.app.presentation.vault

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*

@Composable
fun VaultAuthScreen(
    onAuthenticated: () -> Unit,
    onBack: () -> Unit,
    viewModel: VaultViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current as? FragmentActivity

    // Navigate on success
    LaunchedEffect(state.authState) {
        if (state.authState == VaultAuthState.SUCCESS) onAuthenticated()
    }

    // Auto-launch biometric on entry
    LaunchedEffect(state.biometricAvailable) {
        if (state.biometricAvailable && context != null) {
            viewModel.authenticate(context)
        }
    }

    val lockScale by animateFloatAsState(
        targetValue = if (state.authState == VaultAuthState.AUTHENTICATING) 0.9f else 1f,
        animationSpec = spring(stiffness = 400f, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "lock_scale",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E), Color(0xFF0F3460)),
                )
            ),
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.TopStart).statusBarsPadding().padding(8.dp),
        ) {
            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier.align(Alignment.Center).padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Lock icon with pulse animation
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(lockScale)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = when (state.authState) {
                        VaultAuthState.SUCCESS -> Icons.Default.LockOpen
                        VaultAuthState.FAILED -> Icons.Default.LockReset
                        else -> Icons.Default.Lock
                    },
                    contentDescription = "Vault lock",
                    tint = when (state.authState) {
                        VaultAuthState.SUCCESS -> Color(0xFF4CAF50)
                        VaultAuthState.FAILED -> Error
                        else -> Amber
                    },
                    modifier = Modifier.size(48.dp),
                )
            }

            Spacer(Modifier.height(32.dp))

            Text(
                "Secure Vault",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = InterFamily,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Your protected documents are locked.\nAuthenticate to continue.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = InterFamily,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
            )

            // Error message
            AnimatedVisibility(visible = state.errorMessage != null) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        color = Error.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text(
                            text = state.errorMessage ?: "",
                            color = Color(0xFFFF8A80),
                            fontFamily = InterFamily,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            when {
                state.authState == VaultAuthState.NOT_ENROLLED -> {
                    Text(
                        "No biometrics enrolled on this device.\nPlease set up fingerprint or face unlock in Settings.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = InterFamily,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                    )
                }
                else -> {
                    Button(
                        onClick = { context?.let { viewModel.authenticate(it) } },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = KiwiPrimary),
                        enabled = state.authState != VaultAuthState.AUTHENTICATING,
                    ) {
                        if (state.authState == VaultAuthState.AUTHENTICATING) {
                            CircularProgressIndicator(Modifier.size(20.dp), color = NearBlack, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Fingerprint, null, tint = NearBlack)
                            Spacer(Modifier.width(8.dp))
                            Text("Authenticate", color = NearBlack, fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VaultContentsScreen(
    navController: androidx.navigation.NavController,
    onDocumentClick: (Long) -> Unit,
    onBack: () -> Unit,
) {
    // Vault contents reuse WorkspaceScreen with vault filter
    // FLAG_SECURE is set via DisposableEffect on the Window
    val context = LocalContext.current as? FragmentActivity
    DisposableEffect(Unit) {
        // SR-VAULT-001: Prevent screenshots in vault
        (context as? android.app.Activity)?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        onDispose {
            (context as? android.app.Activity)?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_SECURE)
        }
    }

    com.paperflow.app.presentation.workspace.WorkspaceScreen(
        navController = navController,
        onDocumentClick = onDocumentClick,
        onDocumentDetails = {},
        onFolderClick = {},
        onVaultClick = {},
        onScanClick = {},
    )
}

