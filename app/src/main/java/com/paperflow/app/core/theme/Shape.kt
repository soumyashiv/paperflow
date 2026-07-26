package com.paperflow.app.core.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// PaperFlow shape system — warm, approachable rounding
val PaperFlowShapes = Shapes(
    // Small: badges, chips, text fields
    small = RoundedCornerShape(8.dp),
    // Medium: cards, dialogs, small bottom sheets
    medium = RoundedCornerShape(16.dp),
    // Large: bottom sheets, modals, large cards
    large = RoundedCornerShape(24.dp),
    // ExtraLarge: full-screen sheets, onboarding panels
    extraLarge = RoundedCornerShape(32.dp),
)

// Additional custom shape constants used throughout the app
object PaperFlowRadius {
    val XSmall = 4.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val XLarge = 24.dp
    val Full = 100.dp   // Pills / FAB
}
