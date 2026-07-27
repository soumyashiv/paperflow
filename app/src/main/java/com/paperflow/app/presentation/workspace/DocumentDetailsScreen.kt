package com.paperflow.app.presentation.workspace

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.Document
import com.paperflow.app.domain.model.DocumentType
import com.paperflow.app.domain.model.OcrStatus
import com.paperflow.app.presentation.components.DocumentThumbnail
import com.paperflow.app.presentation.components.TypeBadge
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailsScreen(
    documentId: Long,
    onBack: () -> Unit,
    onOpen: () -> Unit,
    onAnnotate: () -> Unit,
    onShare: () -> Unit,
    onConvertToNote: () -> Unit,
    viewModel: DocumentDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showDeleteConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(documentId) { viewModel.loadDocument(documentId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Details", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (state.document?.isFavorite == true) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            "Favorite",
                            tint = if (state.document?.isFavorite == true) Amber else GrayLight,
                        )
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Outlined.Delete, "Delete", tint = Error)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { pv ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KiwiPrimary)
            }
        } else {
            val doc = state.document
            if (doc == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Document not found", color = Gray, fontFamily = InterFamily)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(pv).verticalScroll(rememberScrollState()),
                ) {
                    // Header card
                    DocumentHeaderCard(doc = doc)

                    // Action buttons
                    ActionButtonsRow(
                        onOpen = onOpen,
                        onAnnotate = onAnnotate,
                        onShare = onShare,
                        onConvertToNote = onConvertToNote,
                    )

                    // Metadata section
                    MetadataSection(doc = doc)

                    // OCR status section
                    OcrStatusSection(doc = doc)

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Document") },
            text = { Text("This action cannot be undone. The file and all its annotations will be permanently deleted.", fontFamily = InterFamily) },
            confirmButton = {
                Button(
                    onClick = { viewModel.deleteDocument(onBack); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Error),
                ) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun DocumentHeaderCard(doc: Document) {
    androidx.compose.material3.Surface(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
    ) {
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            DocumentThumbnail(doc.thumbnailPath, doc.type, Modifier.size(80.dp, 104.dp))
            Column(Modifier.weight(1f)) {
                Text(doc.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = InterFamily, maxLines = 2)
                Spacer(Modifier.height(6.dp))
                TypeBadge(doc.type)
                Spacer(Modifier.height(6.dp))
                Text("${doc.pageCount} page${if (doc.pageCount != 1) "s" else ""}", style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
                Text(formatSize(doc.sizeBytes), style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
            }
        }
    }
}

@Composable
private fun ActionButtonsRow(onOpen: () -> Unit, onAnnotate: () -> Unit, onShare: () -> Unit, onConvertToNote: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        data class Btn(val label: String, val icon: ImageVector, val primary: Boolean, val action: () -> Unit)
        listOf(
            Btn("Open", Icons.Default.OpenInNew, true, onOpen),
            Btn("Edit", Icons.Default.Edit, false, onAnnotate),
            Btn("Share", Icons.Default.Share, false, onShare),
            Btn("Note", Icons.Default.EditNote, false, onConvertToNote),
        ).forEach { btn ->
            if (btn.primary) {
                Button(onClick = btn.action, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = NearBlack), shape = RoundedCornerShape(12.dp)) {
                    Icon(btn.icon, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(btn.label, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold)
                }
            } else {
                OutlinedButton(onClick = btn.action, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Border)) {
                    Icon(btn.icon, null, modifier = Modifier.size(14.dp), tint = NearBlack)
                    Spacer(Modifier.width(2.dp))
                    Text(btn.label, fontFamily = InterFamily, fontSize = 11.sp, color = NearBlack)
                }
            }
        }
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun MetadataSection(doc: Document) {
    Text("File Info", style = MaterialTheme.typography.labelMedium, color = GrayLight, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp))
    Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
        Column(Modifier.padding(8.dp)) {
            val sdf = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
            MetaRow("Created", sdf.format(Date(doc.createdAt)))
            MetaRow("Modified", sdf.format(Date(doc.updatedAt)))
            MetaRow("File size", formatSize(doc.sizeBytes))
            MetaRow("Pages", doc.pageCount.toString())
            MetaRow("Type", doc.type.name)
            MetaRow("Last read page", if (doc.lastReadPage > 0) "Page ${doc.lastReadPage + 1}" else "Not started")
        }
    }
}

@Composable
private fun OcrStatusSection(doc: Document) {
    Spacer(Modifier.height(12.dp))
    Text("OCR Status", style = MaterialTheme.typography.labelMedium, color = GrayLight, modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp))
    Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val (icon, color, label) = when (doc.ocrStatus) {
                OcrStatus.COMPLETE -> Triple(Icons.Default.CheckCircle, Color(0xFF4CAF50), "Text extraction complete")
                OcrStatus.PROCESSING -> Triple(Icons.Default.Sync, Amber, "Processing…")
                OcrStatus.PENDING -> Triple(Icons.Default.Schedule, GrayLight, "Queued for text extraction")
                OcrStatus.FAILED -> Triple(Icons.Default.Error, Error, "Text extraction failed")
            }
            Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            Text(label, fontFamily = InterFamily, color = NearBlack)
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(label, color = Gray, fontFamily = InterFamily, style = MaterialTheme.typography.bodySmall)
        Text(value, fontFamily = InterFamily, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

private fun formatSize(bytes: Long) = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1_048_576 -> "${bytes / 1024} KB"
    else -> String.format("%.1f MB", bytes / 1_048_576.0)
}

