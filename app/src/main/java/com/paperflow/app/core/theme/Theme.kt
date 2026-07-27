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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.os.Build
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// ─── Theme Mode ───────────────────────────────────────────────────────────────
enum class AppTheme { LIGHT, DARK, AMOLED, DYNAMIC }

val LocalAppTheme = staticCompositionLocalOf { AppTheme.LIGHT }

// ─── Light Color Scheme (Kiwi Premium) ───────────────────────────────────────
private val LightColorScheme = lightColorScheme(
    primary              = KiwiPrimary,
    onPrimary            = White,
    primaryContainer     = KiwiLight,
    onPrimaryContainer   = KiwiDark,
    secondary            = KiwiDark,
    onSecondary          = White,
    secondaryContainer   = KiwiLighter,
    onSecondaryContainer = KiwiDark,
    tertiary             = KiwiAccent,
    onTertiary           = KiwiDark,
    tertiaryContainer    = KiwiPale,
    onTertiaryContainer  = KiwiDark,
    background           = KiwiBg,
    onBackground         = NearBlack,
    surface              = KiwiSurface,
    onSurface            = NearBlack,
    surfaceVariant       = KiwiLighter,
    onSurfaceVariant     = Gray,
    outline              = KiwiDivider,
    outlineVariant       = Border,
    error                = Error,
    onError              = White,
    errorContainer       = ErrorLight,
    onErrorContainer     = Error,
    inversePrimary       = KiwiAccent,
)

// ─── Dark Color Scheme (Deep Kiwi Green) ──────────────────────────────────────
private val DarkColorScheme = darkColorScheme(
    primary              = KiwiAccent,
    onPrimary            = DarkBackground,
    primaryContainer     = KiwiDark,
    onPrimaryContainer   = KiwiLight,
    secondary            = KiwiLight,
    onSecondary          = KiwiDark,
    secondaryContainer   = DarkSurfaceElevated,
    onSecondaryContainer = DarkTextPrimary,
    background           = DarkBackground,
    onBackground         = DarkTextPrimary,
    surface              = DarkSurface,
    onSurface            = DarkTextPrimary,
    surfaceVariant       = DarkSurfaceElevated,
    onSurfaceVariant     = DarkTextSecondary,
    outline              = DarkBorder,
    outlineVariant       = DarkBorder,
    error                = Error,
    onError              = White,
    errorContainer       = ErrorLight,
    onErrorContainer     = Error,
)

// ─── AMOLED Color Scheme ──────────────────────────────────────────────────────
private val AmoledColorScheme = DarkColorScheme.copy(
    background   = AmoledBackground,
    surface      = AmoledSurface,
    surfaceVariant = AmoledSurface,
)

// ─── Motion Tokens ────────────────────────────────────────────────────────────
data class MotionTokens(
    val durationShort: Int  = 150,    // Micro-interactions (chips, buttons)
    val durationMedium: Int = 300,    // Card transitions, sheet open
    val durationLong: Int   = 500,    // Page transitions, screen reveals
    val durationDrag: Int   = 350,    // Drag / swipe animations
    val durationSpring: Int = 400,    // Spring settle animations
)

val LocalMotionTokens = compositionLocalOf { MotionTokens() }

// ─── Elevation System (Kiwi: soft, large-blur, low-opacity) ──────────────────
object KiwiElevation {
    val None    : Dp = 0.dp
    val Subtle  : Dp = 1.dp    // Hairline — barely-there card lift
    val Card    : Dp = 2.dp    // Standard card elevation
    val Floating: Dp = 8.dp    // FABs, floating pills
    val Sheet   : Dp = 16.dp   // Bottom sheets
    val Modal   : Dp = 24.dp   // Dialogs / modals
}

// ─── Spacing Scale ────────────────────────────────────────────────────────────
object Spacing {
    val xs   = 4
    val sm   = 8
    val md   = 12
    val lg   = 16
    val xl   = 24
    val xxl  = 32
    val xxxl = 48
}

// ─── Root Theme Composable ────────────────────────────────────────────────────
@Composable
fun PaperFlowTheme(
    appTheme: AppTheme = AppTheme.LIGHT,
    content: @Composable () -> Unit,
) {
    val isDark = isSystemInDarkTheme() && appTheme == AppTheme.DYNAMIC || appTheme == AppTheme.DARK || appTheme == AppTheme.AMOLED
    val context = LocalContext.current
    val colorScheme = when {
        appTheme == AppTheme.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        appTheme == AppTheme.DARK -> DarkColorScheme
        appTheme == AppTheme.AMOLED -> AmoledColorScheme
        else -> LightColorScheme
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
            typography  = PaperFlowTypography,
            shapes      = PaperFlowShapes,
            content     = content,
        )
    }
}
