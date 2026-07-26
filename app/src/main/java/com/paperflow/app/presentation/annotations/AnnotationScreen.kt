package com.paperflow.app.presentation.annotations

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*

// Annotation tool types
enum class AnnotationTool { PEN, HIGHLIGHT, ERASER, TEXT, SHAPE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnotationScreen(
    documentId: Long,
    pageIndex: Int,
    onBack: () -> Unit,
    viewModel: AnnotationViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedTool by remember { mutableStateOf(AnnotationTool.PEN) }
    var selectedColor by remember { mutableStateOf(Color(0xFFFF6B6B)) }
    var strokeWidth by remember { mutableFloatStateOf(4f) }
    val currentStroke = remember { mutableStateListOf<Offset>() }
    val strokes = remember { mutableStateListOf<List<Offset>>() }

    LaunchedEffect(documentId, pageIndex) { viewModel.loadPage(documentId, pageIndex) }

    Scaffold(
        containerColor = Color(0xFF1A1A1A),
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) } },
                title = { Text("Annotate", fontWeight = FontWeight.Bold, color = Color.White, fontFamily = InterFamily) },
                actions = {
                    IconButton(onClick = { strokes.clear() }) { Icon(Icons.Default.Undo, "Undo", tint = Color.White) }
                    TextButton(onClick = { viewModel.saveAnnotations(strokes.toList(), selectedColor, documentId, pageIndex) }) {
                        Text("Done", color = Amber, fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1A1A1A)),
            )
        },
        bottomBar = {
            Surface(color = Color(0xFF242424)) {
                Column(Modifier.navigationBarsPadding().padding(8.dp)) {
                    // Color palette
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    ) {
                        listOf(
                            Color(0xFFFF6B6B), Color(0xFFFFFF00), Color(0xFF4CAF50),
                            Color(0xFF2196F3), Color(0xFFAB47BC), Color(0xFFFF9800), Color.White,
                        ).forEach { color ->
                            ColorDot(color = color, selected = color == selectedColor, onClick = { selectedColor = color })
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    // Tool row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                    ) {
                        listOf(
                            AnnotationTool.PEN to Icons.Default.Edit,
                            AnnotationTool.HIGHLIGHT to Icons.Default.BorderColor,
                            AnnotationTool.TEXT to Icons.Default.TextFormat,
                            AnnotationTool.SHAPE to Icons.Default.Crop32,
                            AnnotationTool.ERASER to Icons.Default.Remove,
                        ).forEach { (tool, icon) ->
                            ToolButton(icon = icon, selected = selectedTool == tool, onClick = { selectedTool = tool })
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    // Stroke width slider
                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Size", color = GrayLight, fontSize = 11.sp, fontFamily = InterFamily)
                        Slider(
                            value = strokeWidth,
                            onValueChange = { strokeWidth = it },
                            valueRange = 1f..20f,
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(thumbColor = Amber, activeTrackColor = Amber),
                        )
                    }
                }
            }
        },
    ) { pv ->
        // Drawing canvas
        Box(Modifier.fillMaxSize().padding(pv)) {
            // Page image background would be here (PDF page bitmap)
            if (state.pageBitmap != null) {
                androidx.compose.foundation.Image(
                    bitmap = state.pageBitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedTool) {
                        detectDragGestures(
                            onDragStart = { offset -> currentStroke.add(offset) },
                            onDrag = { change, _ ->
                                currentStroke.add(change.position)
                                change.consume()
                            },
                            onDragEnd = {
                                if (selectedTool == AnnotationTool.ERASER) {
                                    strokes.removeLastOrNull()
                                } else {
                                    strokes.add(currentStroke.toList())
                                }
                                currentStroke.clear()
                            },
                        )
                    },
            ) {
                val paint = Paint().apply {
                    color = when (selectedTool) {
                        AnnotationTool.HIGHLIGHT -> selectedColor.copy(alpha = 0.4f)
                        AnnotationTool.ERASER -> Color.Transparent
                        else -> selectedColor
                    }
                    style = PaintingStyle.Stroke
                    strokeWidth = strokeWidth
                    strokeCap = StrokeCap.Round
                    strokeJoin = StrokeJoin.Round
                    isAntiAlias = true
                }
                // Draw committed strokes
                strokes.forEach { stroke -> drawPath(strokeToPath(stroke), paint.color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
                // Draw current stroke
                drawPath(strokeToPath(currentStroke), paint.color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}

private fun strokeToPath(points: List<Offset>): Path {
    val path = Path()
    points.forEachIndexed { i, pt ->
        if (i == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
    }
    return path
}

@Composable
private fun ColorDot(color: Color, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(if (selected) 32.dp else 28.dp)
            .clip(CircleShape)
            .background(color)
            .then(if (selected) Modifier.padding(2.dp) else Modifier)
            .clip(CircleShape)
            .background(color)
            .let { if (selected) it.border(3.dp, Color.White, CircleShape) else it }
            .pointerInput(Unit) { detectDragGestures(onDragStart = { onClick() }, onDrag = { _, _ -> }) },
    ) { /* no content */ }
}

@Composable
private fun ToolButton(icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.background(if (selected) Amber.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(8.dp))) {
        Icon(icon, null, tint = if (selected) Amber else GrayLight)
    }
}
