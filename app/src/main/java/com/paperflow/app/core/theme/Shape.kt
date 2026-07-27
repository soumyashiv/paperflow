package com.paperflow.app.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ─── Kiwi Shape System — organic, rounded, premium ───────────────────────────
val PaperFlowShapes = Shapes(
    // Small: chips, badges, text fields, tiny pills
    small  = RoundedCornerShape(12.dp),
    // Medium: action cards, small dialogs
    medium = RoundedCornerShape(20.dp),
    // Large: document cards, bottom sheets (side radii)
    large  = RoundedCornerShape(28.dp),
    // ExtraLarge: full sheets, onboarding panels, search bar
    extraLarge = RoundedCornerShape(32.dp),
)

// ─── Kiwi Radius Constants — single source of truth ─────────────────────────
object KiwiRadius {
    val XSmall   = 8.dp
    val Small    = 12.dp
    val Medium   = 16.dp
    val Card     = 20.dp
    val LargeCard= 28.dp
    val SearchBar= 32.dp
    val Sheet    = 32.dp
    val Dialog   = 28.dp
    val Button   = 26.dp
    val FAB      = 36.dp
    val Pill     = 100.dp   // Full pill / capsule
}

// Backward-compat alias — screens using PaperFlowRadius still compile
@Deprecated("Use KiwiRadius", replaceWith = ReplaceWith("KiwiRadius"))
object PaperFlowRadius {
    val XSmall = KiwiRadius.XSmall
    val Small  = KiwiRadius.Small
    val Medium = KiwiRadius.Medium
    val Large  = KiwiRadius.LargeCard
    val XLarge = KiwiRadius.SearchBar
    val Full   = KiwiRadius.Pill
}
