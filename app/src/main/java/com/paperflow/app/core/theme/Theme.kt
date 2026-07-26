package com.paperflow.app.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Theme Mode ───────────────────────────────────────────────────────────────
enum class AppTheme { LIGHT, DARK, AMOLED }

val LocalAppTheme = staticCompositionLocalOf { AppTheme.LIGHT }

// ─── Light Color Scheme ───────────────────────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary = Amber,
    onPrimary = NearBlack,
    primaryContainer = AmberLight,
    onPrimaryContainer = NearBlack,
    secondary = NearBlack,
    onSecondary = White,
    secondaryContainer = Border,
    onSecondaryContainer = NearBlack,
    background = White,
    onBackground = NearBlack,
    surface = Cream,
    onSurface = NearBlack,
    surfaceVariant = Cream,
    onSurfaceVariant = Gray,
    outline = Border,
    outlineVariant = Border,
    error = Error,
    onError = White,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
)

// ─── Dark Color Scheme ────────────────────────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary = Amber,
    onPrimary = NearBlack,
    primaryContainer = AmberDark,
    onPrimaryContainer = White,
    secondary = White,
    onSecondary = NearBlack,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
    error = Error,
    onError = White,
    errorContainer = ErrorLight,
    onErrorContainer = Error,
)

// ─── AMOLED Color Scheme ──────────────────────────────────────────────────────
private val AmoledColorScheme = DarkColorScheme.copy(
    background = AmoledBackground,
    surface = AmoledSurface,
    surfaceVariant = AmoledSurface,
)

// ─── Motion Tokens (shared via CompositionLocal) ──────────────────────────────
data class MotionTokens(
    val durationShort: Int = 150,       // Micro-interactions
    val durationMedium: Int = 300,      // Card transitions
    val durationLong: Int = 500,        // Page transitions
    val durationDrag: Int = 350,        // Drag animations
)

val LocalMotionTokens = compositionLocalOf { MotionTokens() }

// ─── Root Theme Composable ────────────────────────────────────────────────────
@Composable
fun PaperFlowTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit,
) {
    val isDark = appTheme == AppTheme.DARK || appTheme == AppTheme.AMOLED
    val colorScheme = when (appTheme) {
        AppTheme.LIGHT -> LightColorScheme
        AppTheme.DARK -> DarkColorScheme
        AppTheme.AMOLED -> AmoledColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDark
                isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    CompositionLocalProvider(
        LocalAppTheme provides appTheme,
        LocalMotionTokens provides MotionTokens(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PaperFlowTypography,
            shapes = PaperFlowShapes,
            content = content,
        )
    }
}

// ─── Spacing Scale ─────────────────────────────────────────────────────────────
object Spacing {
    val xs = 4
    val sm = 8
    val md = 12
    val lg = 16
    val xl = 24
    val xxl = 32
    val xxxl = 48
}
