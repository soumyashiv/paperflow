package com.paperflow.app.core.theme

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// ─── Kiwi Decorative Elements ────────────────────────────────────────────────
// All drawn in Canvas — no external assets required.
// Use on card corners, empty states, and backgrounds for organic feel.

/**
 * Draws a kiwi fruit cross-section (circle with radial segments and seeds)
 * in the bottom-right corner of its canvas.
 *
 * @param tint Primary color for the kiwi flesh (default: KiwiLight)
 * @param alpha Overall opacity (0.0 – 1.0); keep low (0.25–0.45) for subtlety
 * @param size Diameter of the kiwi in dp
 */
@Composable
fun KiwiSliceDecor(
    modifier: Modifier = Modifier,
    tint: Color = KiwiLight,
    alpha: Float = 0.35f,
    size: Dp = 56.dp,
) {
    Canvas(modifier = modifier) {
        val radiusPx = size.toPx() / 2f
        val cx = this.size.width - radiusPx * 0.6f
        val cy = this.size.height - radiusPx * 0.6f
        drawKiwiSlice(cx, cy, radiusPx, tint, alpha)
    }
}

/**
 * Draws a simple leaf shape — two varieties via [mirrorX] flag.
 *
 * @param tint Leaf color (default: KiwiAccent)
 * @param alpha Opacity — keep ≤ 0.35 on card backgrounds
 */
@Composable
fun LeafDecor(
    modifier: Modifier = Modifier,
    tint: Color = KiwiAccent,
    alpha: Float = 0.30f,
    mirrorX: Boolean = false,
    size: Dp = 48.dp,
) {
    Canvas(modifier = modifier) {
        val s = size.toPx()
        val cx = if (mirrorX) this.size.width - s * 0.2f else s * 0.2f
        val cy = this.size.height - s * 0.2f
        drawLeaf(cx, cy, s, tint, alpha, mirrorX)
    }
}

/**
 * Draws a tiny sparkle burst — used on "Ask AI" and premium feature cards.
 *
 * @param tint Sparkle color (default: KiwiAccent)
 * @param alpha Keep ≤ 0.5 for subtlety
 */
@Composable
fun SparkleDecor(
    modifier: Modifier = Modifier,
    tint: Color = KiwiAccent,
    alpha: Float = 0.45f,
    size: Dp = 32.dp,
) {
    Canvas(modifier = modifier) {
        val cx = this.size.width - size.toPx() * 0.5f
        val cy = size.toPx() * 0.5f
        drawSparkle(cx, cy, size.toPx() / 2f, tint, alpha)
    }
}

/**
 * Draws a subtle organic blob in the corner.
 * Used on empty states and scanner overlays.
 */
@Composable
fun OrganicBlobDecor(
    modifier: Modifier = Modifier,
    tint: Color = KiwiLight,
    alpha: Float = 0.20f,
    size: Dp = 80.dp,
) {
    Canvas(modifier = modifier) {
        val s = size.toPx()
        drawOrganicBlob(this.size.width - s * 0.3f, this.size.height - s * 0.3f, s, tint, alpha)
    }
}

// ─── Internal drawing primitives ─────────────────────────────────────────────

private fun DrawScope.drawKiwiSlice(
    cx: Float, cy: Float, radius: Float,
    tint: Color, alpha: Float,
) {
    // Outer skin ring
    drawCircle(
        color = KiwiDark.copy(alpha = alpha * 0.6f),
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = radius * 0.12f),
    )
    // Flesh fill
    drawCircle(
        color = tint.copy(alpha = alpha),
        radius = radius * 0.88f,
        center = Offset(cx, cy),
    )
    // Core circle
    drawCircle(
        color = KiwiDark.copy(alpha = alpha * 0.5f),
        radius = radius * 0.22f,
        center = Offset(cx, cy),
    )
    // Radial segments (8 lines from core to flesh)
    val segmentCount = 8
    for (i in 0 until segmentCount) {
        val angle = (i * 2 * PI / segmentCount).toFloat()
        val x1 = cx + cos(angle) * radius * 0.22f
        val y1 = cy + sin(angle) * radius * 0.22f
        val x2 = cx + cos(angle) * radius * 0.82f
        val y2 = cy + sin(angle) * radius * 0.82f
        drawLine(
            color = KiwiDark.copy(alpha = alpha * 0.35f),
            start = Offset(x1, y1),
            end   = Offset(x2, y2),
            strokeWidth = radius * 0.035f,
        )
    }
    // Seeds (small ovals between segments)
    val seedCount = 8
    for (i in 0 until seedCount) {
        val angle = ((i + 0.5f) * 2 * PI / seedCount).toFloat()
        val sx = cx + cos(angle) * radius * 0.55f
        val sy = cy + sin(angle) * radius * 0.55f
        drawCircle(
            color = KiwiDark.copy(alpha = alpha * 0.7f),
            radius = radius * 0.055f,
            center = Offset(sx, sy),
        )
    }
}

private fun DrawScope.drawLeaf(
    cx: Float, cy: Float, size: Float,
    tint: Color, alpha: Float, mirrorX: Boolean,
) {
    val flip = if (mirrorX) -1f else 1f
    val path = Path().apply {
        moveTo(cx, cy)
        cubicTo(
            cx + flip * size * 0.5f, cy - size * 0.6f,
            cx + flip * size * 0.9f, cy - size * 0.3f,
            cx + flip * size * 0.7f, cy,
        )
        cubicTo(
            cx + flip * size * 0.4f, cy + size * 0.1f,
            cx + flip * size * 0.1f, cy + size * 0.05f,
            cx, cy,
        )
        close()
    }
    drawPath(path, tint.copy(alpha = alpha))
    // Central vein
    drawLine(
        color = KiwiDark.copy(alpha = alpha * 0.5f),
        start = Offset(cx, cy),
        end   = Offset(cx + flip * size * 0.65f, cy - size * 0.22f),
        strokeWidth = size * 0.03f,
        cap   = StrokeCap.Round,
    )
}

private fun DrawScope.drawSparkle(
    cx: Float, cy: Float, radius: Float,
    tint: Color, alpha: Float,
) {
    val arms = 4
    for (i in 0 until arms) {
        val angle = (i * PI / arms).toFloat()
        drawLine(
            color = tint.copy(alpha = alpha),
            start = Offset(cx - cos(angle) * radius, cy - sin(angle) * radius),
            end   = Offset(cx + cos(angle) * radius, cy + sin(angle) * radius),
            strokeWidth = radius * 0.14f,
            cap   = StrokeCap.Round,
        )
    }
    // Center dot
    drawCircle(tint.copy(alpha = alpha), radius * 0.2f, Offset(cx, cy))
    // Small dots at arm tips
    for (i in 0 until 8) {
        val angle = (i * PI / 4).toFloat()
        val r = radius * 1.1f
        drawCircle(
            color  = tint.copy(alpha = alpha * 0.6f),
            radius = radius * 0.07f,
            center = Offset(cx + cos(angle) * r, cy + sin(angle) * r),
        )
    }
}

private fun DrawScope.drawOrganicBlob(
    cx: Float, cy: Float, size: Float,
    tint: Color, alpha: Float,
) {
    val path = Path().apply {
        moveTo(cx, cy - size * 0.45f)
        cubicTo(
            cx + size * 0.52f, cy - size * 0.48f,
            cx + size * 0.55f, cy + size * 0.1f,
            cx + size * 0.2f, cy + size * 0.42f,
        )
        cubicTo(
            cx - size * 0.15f, cy + size * 0.52f,
            cx - size * 0.55f, cy + size * 0.18f,
            cx - size * 0.45f, cy - size * 0.1f,
        )
        cubicTo(
            cx - size * 0.38f, cy - size * 0.44f,
            cx - size * 0.05f, cy - size * 0.42f,
            cx, cy - size * 0.45f,
        )
        close()
    }
    drawPath(path, tint.copy(alpha = alpha))
}
