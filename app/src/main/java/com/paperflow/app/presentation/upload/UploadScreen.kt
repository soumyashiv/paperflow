package com.paperflow.app.presentation.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.usecase.ImportDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── ViewModel ────────────────────────────────────────────────────────────────
data class UploadItem(val uri: Uri, val name: String, val isImporting: Boolean = false, val done: Boolean = false, val error: String? = null)

data class UploadUiState(val items: List<UploadItem> = emptyList(), val allDone: Boolean = false)

@HiltViewModel
class UploadViewModel @Inject constructor(private val importDocument: ImportDocumentUseCase) : ViewModel() {
    private val _state = MutableStateFlow(UploadUiState())
    val state: StateFlow<UploadUiState> = _state.asStateFlow()

    fun addUris(uris: List<Uri>, names: List<String>) {
        val items = uris.zip(names).map { (uri, name) -> UploadItem(uri, name) }
        _state.update { it.copy(items = it.items + items) }
    }

    fun importAll(folderId: Long? = null) {
        viewModelScope.launch {
            _state.value.items.forEachIndexed { index, item ->
                if (item.done) return@forEachIndexed
                _state.update { s -> s.copy(items = s.items.toMutableList().also { it[index] = item.copy(isImporting = true) }) }
                val result = runCatching { importDocument(item.uri, folderId) }
                _state.update { s ->
                    val updated = s.items.toMutableList()
                    updated[index] = if (result.isSuccess && result.getOrNull() != null) item.copy(done = true, isImporting = false)
                    else item.copy(error = "Import failed", isImporting = false)
                    s.copy(items = updated, allDone = updated.all { it.done })
                }
            }
        }
    }

    fun removeItem(index: Int) = _state.update { it.copy(items = it.items.toMutableList().also { l -> l.removeAt(index) }) }
}

// ─── Screen ───────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(onBack: () -> Unit, onDone: () -> Unit, viewModel: UploadViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        viewModel.addUris(uris, uris.map { it.lastPathSegment ?: "Document" })
    }

    LaunchedEffect(state.allDone) { if (state.allDone) onDone() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(title = { Text("Import Files", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
        },
        bottomBar = {
            if (state.items.isNotEmpty()) {
                Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 4.dp) {
                    Button(onClick = { viewModel.importAll() },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).height(52.dp).navigationBarsPadding(),
                        colors = ButtonDefaults.buttonColors(containerColor = NearBlack), shape = RoundedCornerShape(14.dp)) {
                        Icon(Icons.Default.CloudUpload, null); Spacer(Modifier.width(8.dp))
                        Text("Import ${state.items.size} file${if (state.items.size != 1) "s" else ""}", fontFamily = InterFamily, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
    ) { pv ->
        Column(Modifier.fillMaxSize().padding(pv).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Drop zone
            Surface(modifier = Modifier.fillMaxWidth().height(160.dp).clickable { launcher.launch("*/*") },
                shape = RoundedCornerShape(20.dp), color = Amber.copy(alpha = 0.06f),
                border = BorderStroke(2.dp, Amber.copy(alpha = 0.4f))) {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.UploadFile, null, tint = Amber, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Tap to browse files", fontWeight = FontWeight.SemiBold, fontFamily = InterFamily)
                    Text("PDF, JPG, PNG supported", style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
                }
            }
            // File list
            state.items.forEachIndexed { i, item ->
                Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(if (item.done) Icons.Default.CheckCircle else Icons.Default.InsertDriveFile,
                            null, tint = if (item.done) Color(0xFF4CAF50) else Amber, modifier = Modifier.size(32.dp))
                        Text(item.name, Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis, fontFamily = InterFamily)
                        when {
                            item.isImporting -> CircularProgressIndicator(Modifier.size(20.dp), color = Amber, strokeWidth = 2.dp)
                            item.error != null -> Icon(Icons.Default.Error, null, tint = Error)
                            !item.done -> IconButton(onClick = { viewModel.removeItem(i) }, Modifier.size(28.dp)) { Icon(Icons.Default.Close, null, Modifier.size(14.dp), tint = GrayLight) }
                        }
                    }
                }
            }
        }
    }
}
