package com.paperflow.app.core.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ─── Kiwi Primary Brand ───────────────────────────────────────────────────────
val KiwiPrimary    = Color(0xFF4E8E18)   // Primary green
val KiwiDark       = Color(0xFF2E6411)   // Dark green / pressed state
val KiwiMid        = Color(0xFF5FA020)   // Mid-tone for hover
val KiwiAccent     = Color(0xFF95D63A)   // Accent green / highlights
val KiwiLight      = Color(0xFFEAF9C8)   // Light green surface / selection
val KiwiLighter    = Color(0xFFF0FBE0)   // Very light green tint
val KiwiPale       = Color(0xFFF5FDE8)   // Near-white green tint

// ─── Backgrounds ─────────────────────────────────────────────────────────────
val KiwiBg         = Color(0xFFFAFBF8)   // Main app background (nature-fresh off-white)
val KiwiSurface    = Color(0xFFFFFFFF)   // Cards / surfaces
val KiwiDivider    = Color(0xFFEDF3E4)   // Section dividers / borders

// ─── Text ────────────────────────────────────────────────────────────────────
val NearBlack      = Color(0xFF1E1E1E)   // Primary text
val Gray           = Color(0xFF6B7280)   // Secondary text / captions
val GrayLight      = Color(0xFFBFC7B3)   // Disabled / placeholder
val Border         = Color(0xFFEDF3E4)   // Dividers / borders (alias)

// Backward-compat aliases (used in screens we'll update later)
val White          = Color(0xFFFFFFFF)
val Cream          = Color(0xFFFAFBF8)   // Was warm cream, now kiwi-fresh

// ─── Amber backward-compat aliases ────────────────────────────────────────────
// These map the old Amber-based design tokens to their Kiwi equivalents.
// Screens that haven't been updated yet will compile and display green tones.
val Amber          = KiwiPrimary         // #4E8E18 — primary action / accent
val AmberDark      = KiwiDark            // #2E6411 — pressed / strong
val AmberLight     = KiwiLight           // #EAF9C8 — surfaces / chips / backgrounds

// ─── Gradients ────────────────────────────────────────────────────────────────
val ButtonGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFB8F34D), Color(0xFF8FD22B))
)
val FabGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFA8E73A), Color(0xFF7BC71D))
)
val SelectionGradient = Brush.linearGradient(
    colors = listOf(Color(0xFFEAF9C8), Color(0xFFDDF5AF))
)

// ─── Semantic ────────────────────────────────────────────────────────────────
val Success        = Color(0xFF67C23A)
val SuccessLight   = Color(0xFFE8F5E9)
val Warning        = Color(0xFFF6C343)
val WarningLight   = Color(0xFFFFF8E1)
val Error          = Color(0xFFE75A5A)
val ErrorLight     = Color(0xFFFFEBEB)
val Info           = Color(0xFF2196F3)

// ─── Document Type Badges (all green-tinted in Kiwi theme) ───────────────────
val PdfBadge       = Color(0xFFFFCDD2)   // Light rose for PDF (kept for contrast)
val NoteBadge      = Color(0xFFD4EDDA)   // Green-tinted for NOTE
val ImgBadge       = Color(0xFFCCE5FF)   // Blue-tinted for images
val XlsxBadge      = Color(0xFFD4EDDA)
val PptxBadge      = Color(0xFFFFE0B2)
val OcrBadge       = Color(0xFFE8D5F0)
val TxtBadge       = Color(0xFFE2F0CB)   // Kiwi-tinted for text files

// ─── Dark Mode (deep-green-on-dark Kiwi variant) ─────────────────────────────
val DarkBackground         = Color(0xFF0F1A0C)   // Very dark green-black
val DarkSurface            = Color(0xFF162110)   // Dark surface green tint
val DarkSurfaceElevated    = Color(0xFF1E2E16)   // Elevated dark surface
val DarkBorder             = Color(0xFF2A3D1F)   // Dark divider
val DarkTextPrimary        = Color(0xFFE8F5D9)   // Light green-white text
val DarkTextSecondary      = Color(0xFF8DB87A)   // Muted green secondary

// ─── AMOLED Mode (deep green-black) ─────────────────────────────────────────
val AmoledBackground       = Color(0xFF000000)
val AmoledSurface          = Color(0xFF0A1008)   // Near-black with green hint
