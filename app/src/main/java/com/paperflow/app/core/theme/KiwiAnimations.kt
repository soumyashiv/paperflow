package com.paperflow.app.core.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─── Kiwi Motion Personality: Premium ────────────────────────────────────────
// Duration palette: Quick=150ms, Standard=300ms, Slow=500ms
// Easing: MD3 Emphasized for entrances (0.05,0.7,0.1,1), MD3 Accelerate for exits
// Overshoot: 0% (Premium archetype — no bounce on content)
// Exception: FAB and chips get 3-5% spring overshoot for playfulness

// ─── Spring Specs ─────────────────────────────────────────────────────────────

/** Standard spring for most UI elements — snappy, no overshoot */
val KiwiSpringSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioNoBouncy,
    stiffness    = Spring.StiffnessMedium,
)

/** Card press spring — slight softness, Premium feel */
val KiwiCardPressSpring = spring<Float>(
    dampingRatio = 0.75f,
    stiffness    = Spring.StiffnessMediumLow,
)

/** FAB spring — small overshoot (playful + premium) */
val KiwiFabSpring = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness    = Spring.StiffnessLow,
)

/** Chip selection spring — quick snap */
val KiwiChipSpring = spring<Float>(
    dampingRatio = 0.8f,
    stiffness    = Spring.StiffnessMedium,
)

// ─── Tween Specs ──────────────────────────────────────────────────────────────

/** Quick micro-interactions: button press, toggle, chip bg */
val KiwiTweenQuick = tween<Any>(
    durationMillis = 150,
    easing         = FastOutSlowInEasing,
)

/** Standard transitions: card bg, color change */
val KiwiTweenStandard = tween<Any>(
    durationMillis = 300,
    easing         = FastOutSlowInEasing,
)

/** Slow reveals: page fade, skeleton shimmer */
val KiwiTweenSlow = tween<Any>(
    durationMillis = 500,
    easing         = LinearOutSlowInEasing,
)

// ─── Enter / Exit Specs ───────────────────────────────────────────────────────

/** List item slide-up entrance: 20dp below, fade in */
val KiwiListEnterSpec: FiniteAnimationSpec<Float> = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness    = Spring.StiffnessMediumLow,
)

/** Page transition enter (slide in from right) */
val KiwiPageEnterSpec = tween<Any>(durationMillis = 400, easing = FastOutSlowInEasing)

/** Page transition exit (slide out to left, faster) */
val KiwiPageExitSpec = tween<Any>(durationMillis = 250, easing = LinearOutSlowInEasing)

// ─── Shimmer Spec (skeleton loader) ──────────────────────────────────────────
val KiwiShimmerSpec = infiniteRepeatable<Float>(
    animation  = tween(900, easing = FastOutSlowInEasing),
    repeatMode = RepeatMode.Reverse,
)

// ─── Stagger Delays ───────────────────────────────────────────────────────────
/** Micro cascade stagger for list items — 30ms per item, max 10 items */
fun kiwiListStaggerMs(index: Int) = (index * 30L).coerceAtMost(300L)

/** Card grid stagger — 40ms per card */
fun kiwiGridStaggerMs(index: Int) = (index * 40L).coerceAtMost(400L)

// ─── Reusable Press Scale Animation ──────────────────────────────────────────
/**
 * Returns a scale [State] that animates to [pressedScale] on press and back to 1f.
 * Backed by [KiwiCardPressSpring] — Premium, no bounce.
 *
 * Usage:
 * ```
 * val scale by rememberKiwiPressScale(interactionSource)
 * Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
 * ```
 */
@Composable
fun rememberKiwiPressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.97f,
): State<Float> {
    val isPressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue  = if (isPressed) pressedScale else 1f,
        animationSpec = KiwiCardPressSpring,
        label        = "kiwi_press_scale",
    )
}

/**
 * Returns a scale [State] for FABs — uses bouncy spring for satisfying tap feel.
 */
@Composable
fun rememberKiwiFabScale(interactionSource: MutableInteractionSource): State<Float> {
    val isPressed by interactionSource.collectIsPressedAsState()
    return animateFloatAsState(
        targetValue  = if (isPressed) 0.90f else 1f,
        animationSpec = KiwiFabSpring,
        label        = "kiwi_fab_scale",
    )
}

/**
 * Returns an elevation [Dp] that drops on press.
 */
@Composable
fun rememberKiwiPressElevation(
    interactionSource: MutableInteractionSource,
    defaultElevation: Dp = KiwiElevation.Floating,
    pressedElevation: Dp = KiwiElevation.Subtle,
): State<Dp> {
    val isPressed by interactionSource.collectIsPressedAsState()
    return animateDpAsState(
        targetValue   = if (isPressed) pressedElevation else defaultElevation,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label         = "kiwi_press_elevation",
    )
}
