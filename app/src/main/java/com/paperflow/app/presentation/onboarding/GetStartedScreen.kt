package com.paperflow.app.presentation.onboarding

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.paperflow.app.core.theme.*
import kotlinx.coroutines.delay

@Composable
fun GetStartedScreen(
    onGetStarted: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    // ── Check first launch — navigate immediately if returning user ────────
    LaunchedEffect(Unit) {
        viewModel.checkFirstLaunch { onGetStarted() }
    }

    // ── Entrance animations ────────────────────────────────────────────────
    var logoVisible by remember { mutableStateOf(false) }
    var titleVisible by remember { mutableStateOf(false) }
    var subtitleVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200); logoVisible = true
        delay(300); titleVisible = true
        delay(200); subtitleVisible = true
        delay(250); buttonVisible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Cream),
    ) {
        // ── Decorative wave background ─────────────────────────────────────
        WaveBackground(modifier = Modifier.align(Alignment.BottomCenter))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // ── Logo ──────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = logoVisible,
                enter = scaleIn(
                    initialScale = 0.6f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                ) + fadeIn(tween(400)),
            ) {
                PaperFlowLogo(size = 120.dp)
            }

            Spacer(Modifier.height(32.dp))

            // ── App Name ──────────────────────────────────────────────────
            AnimatedVisibility(
                visible = titleVisible,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 4 }),
            ) {
                Text(
                    text = "PaperFlow",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                    color = NearBlack,
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Tagline ───────────────────────────────────────────────────
            AnimatedVisibility(
                visible = subtitleVisible,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 4 }),
            ) {
                Text(
                    text = "Scan. Organize. Annotate.\nAll in one workspace.",
                    fontSize = 16.sp,
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Normal,
                    color = Gray,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp,
                )
            }

            Spacer(Modifier.height(64.dp))

            // ── Get Started Button ─────────────────────────────────────────
            AnimatedVisibility(
                visible = buttonVisible,
                enter = fadeIn(tween(400)) + slideInVertically(initialOffsetY = { it / 3 }),
            ) {
                Button(
                    onClick = {
                        viewModel.markFirstLaunchDone()
                        onGetStarted()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NearBlack,
                        contentColor = Color.White,
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = InterFamily,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // ── Privacy note ──────────────────────────────────────────────
            AnimatedVisibility(visible = buttonVisible, enter = fadeIn(tween(500))) {
                Text(
                    text = "All your documents stay on your device.\nNo cloud. No account required.",
                    fontSize = 12.sp,
                    fontFamily = InterFamily,
                    color = GrayLight,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                )
            }
        }
    }
}

@Composable
private fun PaperFlowLogo(size: androidx.compose.ui.unit.Dp) {
    // Amber rounded square with "P" lettermark — matches Stitch design
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.22f))
            .background(Amber),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "P",
            fontSize = (size.value * 0.55f).sp,
            fontWeight = FontWeight.Bold,
            fontFamily = InterFamily,
            color = NearBlack,
        )
    }
}

@Composable
private fun WaveBackground(modifier: Modifier = Modifier) {
    // Simple gradient wave suggestion at bottom of screen
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Border.copy(alpha = 0.3f)),
                )
            )
    )
}
