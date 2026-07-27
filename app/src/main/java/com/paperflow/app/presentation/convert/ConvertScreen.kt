package com.paperflow.app.presentation.convert

import android.content.Context
import android.content.Intent
import android.print.PrintManager
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.domain.model.Document
import com.paperflow.app.domain.usecase.GetDocumentByIdUseCase
import com.paperflow.app.domain.usecase.ConvertScanToNoteUseCase
import com.paperflow.app.core.theme.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── ViewModel ───────────────────────────────────────────────────────────────
data class ConvertUiState(
    val document: Document? = null,
    val isConverting: Boolean = false,
    val convertedNoteId: Long? = null,
    val error: String? = null,
)

@HiltViewModel
class ConvertViewModel @Inject constructor(
    private val getDocument: GetDocumentByIdUseCase,
    private val convertToNote: ConvertScanToNoteUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(ConvertUiState())
    val state: StateFlow<ConvertUiState> = _state.asStateFlow()

    fun load(id: Long) = viewModelScope.launch {
        _state.update { it.copy(document = getDocument(id)) }
    }

    fun convertToNote() = viewModelScope.launch {
        val doc = _state.value.document ?: return@launch
        if (doc.ocrStatus != com.paperflow.app.domain.model.OcrStatus.COMPLETE) {
            _state.update { it.copy(error = "OCR must complete before converting to note. Please wait.") }
            return@launch
        }
        _state.update { it.copy(isConverting = true) }
        val noteId = convertToNote(doc.id)
        _state.update { it.copy(isConverting = false, convertedNoteId = noteId, error = if (noteId == null) "No text found in document." else null) }
    }
}

// ─── Screen ──────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvertScreen(
    documentId: Long,
    onBack: () -> Unit,
    viewModel: ConvertViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(documentId) { viewModel.load(documentId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Convert", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { pv ->
        Column(
            modifier = Modifier.fillMaxSize().padding(pv).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(state.document?.name ?: "Document", fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = InterFamily)
            Text("Choose how to convert this document.", color = Gray, fontFamily = InterFamily)

            state.error?.let { err ->
                Surface(color = ErrorLight, shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Warning, null, tint = Error, modifier = Modifier.size(18.dp))
                        Text(err, color = Error, fontFamily = InterFamily, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            state.convertedNoteId?.let {
                Surface(color = Color(0xFFE8F5E9), shape = RoundedCornerShape(12.dp)) {
                    Row(Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(18.dp))
                        Text("Note created successfully!", color = Color(0xFF2E7D32), fontFamily = InterFamily)
                    }
                }
            }

            // Conversion options
            ConvertOption(
                icon = Icons.Default.EditNote,
                title = "Convert to Note",
                subtitle = "Extract all text (OCR) into an editable note",
                enabled = state.document?.ocrStatus == com.paperflow.app.domain.model.OcrStatus.COMPLETE,
                isLoading = state.isConverting,
                onClick = { viewModel.convertToNote() },
            )
            ConvertOption(
                icon = Icons.Default.Image,
                title = "Extract Images",
                subtitle = "Save each page as a PNG image",
                onClick = { /* extract images */ },
            )
            ConvertOption(
                icon = Icons.Default.TableChart,
                title = "Export as CSV",
                subtitle = "Extract tables into spreadsheet format",
                onClick = { /* export CSV */ },
            )
        }
    }
}

@Composable
private fun ConvertOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (enabled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        tonalElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled && !isLoading, onClick = onClick)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(KiwiPrimary.copy(alpha = if (enabled) 0.15f else 0.05f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = if (enabled) Amber else GrayLight, modifier = Modifier.size(24.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontFamily = InterFamily, color = if (enabled) NearBlack else GrayLight)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
            }
            if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = KiwiPrimary, strokeWidth = 2.dp)
            else Icon(Icons.Default.ChevronRight, null, tint = if (enabled) GrayLight else Border.copy(alpha = 0.5f))
        }
    }
}

