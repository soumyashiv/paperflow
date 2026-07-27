package com.paperflow.app.presentation.share

import android.content.Intent
import android.print.PrintManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.Document
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase
import com.paperflow.app.core.theme.*
import com.paperflow.app.core.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// ─── Shared ViewModel for both Share & Print screens ─────────────────────────
@HiltViewModel
class SharePrintViewModel @Inject constructor(
    private val getDocument: GetDocumentByIdUseCase,
) : ViewModel() {
    private val _document = MutableStateFlow<Document?>(null)
    val document: StateFlow<Document?> = _document.asStateFlow()

    fun load(id: Long) = viewModelScope.launch {
        _document.value = getDocument(id)
    }
}

// ─── Share Options Screen ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareOptionsScreen(
    documentId: Long,
    onBack: () -> Unit,
    viewModel: SharePrintViewModel = hiltViewModel(),
) {
    val doc by viewModel.document.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(documentId) { viewModel.load(documentId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Share", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { pv ->
        Column(
            Modifier.fillMaxSize().padding(pv).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            doc?.let { d ->
                Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, null, tint = PdfBadge, modifier = Modifier.size(32.dp))
                        Column {
                            Text(d.name, fontWeight = FontWeight.SemiBold, fontFamily = InterFamily)
                            Text(formatSize(d.sizeBytes), style = MaterialTheme.typography.bodySmall, color = Gray)
                        }
                    }
                }

                ShareOption(Icons.Default.Share, "Share File", "Send via any app (WhatsApp, Email, Drive…)") {
                    val file = File(d.filePath)
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    context.startActivity(Intent.createChooser(
                        Intent(Intent.ACTION_SEND).apply {
                            type = if (d.type == com.paperflow.app.domain.model.DocumentType.PDF) "application/pdf" else "image/*"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }, "Share ${d.name}"
                    ))
                }
                ShareOption(Icons.Default.Email, "Send via Email", "Attach to a new email message") {
                    val file = File(d.filePath)
                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                    context.startActivity(Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822"
                        putExtra(Intent.EXTRA_SUBJECT, d.name)
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    })
                }
                ShareOption(Icons.Default.ContentCopy, "Copy File Path", "Copy location to clipboard") {
                    val cm = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    cm.setPrimaryClip(android.content.ClipData.newPlainText("path", d.filePath))
                }
            }
        }
    }
}

// ─── Print Options Screen ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrintOptionsScreen(
    documentId: Long,
    onBack: () -> Unit,
    viewModel: SharePrintViewModel = hiltViewModel(),
) {
    val doc by viewModel.document.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var pageRange by remember { mutableStateOf("All") }
    var copies by remember { mutableIntStateOf(1) }
    var colorMode by remember { mutableStateOf("Color") }

    LaunchedEffect(documentId) { viewModel.load(documentId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Print", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                Button(
                    onClick = {
                        val pm = context.getSystemService(android.content.Context.PRINT_SERVICE) as PrintManager
                        // Android print framework — creates a print adapter
                        // Real implementation would use PdfDocument print adapter
                    },
                    modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp).navigationBarsPadding(),
                    colors = ButtonDefaults.buttonColors(containerColor = NearBlack),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Icon(Icons.Default.Print, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Print", fontFamily = InterFamily, fontWeight = FontWeight.Bold)
                }
            }
        },
    ) { pv ->
        Column(Modifier.fillMaxSize().padding(pv).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Print Settings", fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = InterFamily)

            Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(8.dp)) {
                    // Copies
                    Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Copies", fontFamily = InterFamily)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            IconButton(onClick = { if (copies > 1) copies-- }) { Icon(Icons.Default.Remove, null) }
                            Text("$copies", fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                            IconButton(onClick = { copies++ }) { Icon(Icons.Default.Add, null) }
                        }
                    }
                    HorizontalDivider(color = Border.copy(alpha = 0.5f))
                    // Page range
                    Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                        Text("Page Range", fontFamily = InterFamily)
                        Spacer(Modifier.height(8.dp))
                        listOf("All", "Current Page", "Custom Range").forEach { opt ->
                            Row(Modifier.clickable { pageRange = opt }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(selected = pageRange == opt, onClick = { pageRange = opt }, colors = RadioButtonDefaults.colors(selectedColor = KiwiPrimary))
                                Text(opt, fontFamily = InterFamily)
                            }
                        }
                    }
                    HorizontalDivider(color = Border.copy(alpha = 0.5f))
                    // Color mode
                    Column(Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                        Text("Color Mode", fontFamily = InterFamily)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Color", "Grayscale").forEach { opt ->
                                FilterChip(selected = colorMode == opt, onClick = { colorMode = opt }, label = { Text(opt, fontFamily = InterFamily) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = KiwiLight))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShareOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.clickable(onClick = onClick).padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(KiwiPrimary.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = KiwiPrimary, modifier = Modifier.size(22.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontFamily = InterFamily)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
            }
            Icon(Icons.Default.ChevronRight, null, tint = GrayLight)
        }
    }
}

private fun formatSize(bytes: Long) = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1_048_576 -> "${bytes / 1024} KB"
    else -> String.format("%.1f MB", bytes / 1_048_576.0)
}


