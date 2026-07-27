package com.paperflow.app.presentation.notes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditorScreen(
    noteId: Long,
    onBack: () -> Unit,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val contentFocus = remember { FocusRequester() }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    // Format toolbar states
    var boldActive by remember { mutableStateOf(false) }
    var italicActive by remember { mutableStateOf(false) }
    var underlineActive by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = KiwiBg,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        viewModel.saveImmediately()
                        onBack()
                    }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                title = {
                    // Save status indicator
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AnimatedVisibility(visible = state.isSaving, enter = fadeIn(), exit = fadeOut()) {
                            CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 2.dp, color = Gray)
                        }
                        AnimatedVisibility(visible = state.isSaved && !state.isSaving, enter = fadeIn(), exit = fadeOut()) {
                            Icon(Icons.Default.Cloud, null, modifier = Modifier.size(14.dp), tint = GrayLight)
                        }
                        Text(
                            text = if (state.isSaving) "Saving…" else "Saved",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayLight,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* share */ }) { Icon(Icons.Outlined.Share, "Share") }
                    IconButton(onClick = { viewModel.deleteCurrentNote(); onBack() }) {
                        Icon(Icons.Outlined.Delete, "Delete", tint = Error)
                    }
                    IconButton(onClick = { /* more */ }) { Icon(Icons.Default.MoreVert, "More") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBg),
            )
        },
        bottomBar = {
            // Format toolbar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FormatButton(icon = Icons.Default.FormatBold, active = boldActive, label = "Bold") { boldActive = !boldActive }
                    FormatButton(icon = Icons.Default.FormatItalic, active = italicActive, label = "Italic") { italicActive = !italicActive }
                    FormatButton(icon = Icons.Default.FormatUnderlined, active = underlineActive, label = "Underline") { underlineActive = !underlineActive }
                    VerticalDivider(modifier = Modifier.height(24.dp), color = Border)
                    FormatButton(icon = Icons.Default.FormatListBulleted, active = false, label = "Bullet list") {}
                    FormatButton(icon = Icons.Default.FormatListNumbered, active = false, label = "Numbered list") {}
                    VerticalDivider(modifier = Modifier.height(24.dp), color = Border)
                    FormatButton(icon = Icons.Default.Link, active = false, label = "Insert link") {}
                    FormatButton(icon = Icons.Default.Image, active = false, label = "Insert image") {}
                    Spacer(Modifier.weight(1f))
                    FormatButton(icon = Icons.Default.KeyboardHide, active = false, label = "Dismiss keyboard") {}
                }
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KiwiPrimary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                // Date chip
                val dateStr = state.note?.updatedAt?.let {
                    SimpleDateFormat("MMMM d, yyyy · h:mm a", Locale.getDefault()).format(Date(it))
                } ?: ""
                Text(dateStr, style = MaterialTheme.typography.bodySmall, color = GrayLight)
                Spacer(Modifier.height(12.dp))

                // Title
                BasicTextField(
                    value = state.note?.title ?: "",
                    onValueChange = { viewModel.updateTitle(it) },
                    textStyle = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = NearBlack,
                    ),
                    cursorBrush = SolidColor(KiwiPrimary),
                    decorationBox = { inner ->
                        if ((state.note?.title ?: "").isEmpty()) {
                            Text("Title", fontFamily = InterFamily, fontWeight = FontWeight.Bold, fontSize = 26.sp, color = GrayLight)
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(16.dp))
                HorizontalDivider(color = KiwiDivider)
                Spacer(Modifier.height(16.dp))

                // Content
                BasicTextField(
                    value = state.note?.content ?: "",
                    onValueChange = { viewModel.updateContent(it) },
                    textStyle = TextStyle(
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 16.sp,
                        color = NearBlack,
                        lineHeight = 26.sp,
                    ),
                    cursorBrush = SolidColor(KiwiPrimary),
                    decorationBox = { inner ->
                        if ((state.note?.content ?: "").isEmpty()) {
                            Text(
                                "Start typing your note…",
                                fontFamily = InterFamily,
                                fontSize = 16.sp,
                                color = GrayLight,
                                lineHeight = 26.sp,
                            )
                        }
                        inner()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 300.dp)
                        .focusRequester(contentFocus),
                )

                Spacer(Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun FormatButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    active: Boolean,
    label: String,
    onClick: () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(36.dp)
            .background(
                color = if (active) KiwiLight else Color.Transparent,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(KiwiRadius.XSmall),
            ),
    ) {
        Icon(icon, label, tint = if (active) KiwiPrimary else Gray, modifier = Modifier.size(18.dp))
    }
}
