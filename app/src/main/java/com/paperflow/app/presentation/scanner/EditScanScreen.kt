package com.paperflow.app.presentation.scanner

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.paperflow.app.core.theme.*
import com.paperflow.app.presentation.components.FilterChipRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScanScreen(
    sessionId: String,
    onClose: () -> Unit,
    onSaved: (Long) -> Unit,
    viewModel: EditScanViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var documentName by remember { mutableStateOf("Scan_${System.currentTimeMillis() / 1000}") }
    var showSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) { viewModel.loadSession(sessionId) }
    LaunchedEffect(state.savedDocumentId) {
        state.savedDocumentId?.let { onSaved(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Scan", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onClose) { Icon(Icons.Default.Close, "Close") } },
                actions = {
                    TextButton(
                        onClick = { showSaveDialog = true },
                        enabled = state.pages.isNotEmpty(),
                    ) {
                        Text("Save", color = Amber, fontWeight = FontWeight.Bold, fontFamily = InterFamily, fontSize = 16.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, tonalElevation = 2.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    EditActionButton(Icons.Default.Rotate90DegreesCw, "Rotate") { viewModel.rotatePage(state.selectedPage) }
                    EditActionButton(Icons.Default.Crop, "Crop") { /* crop UI */ }
                    EditActionButton(Icons.Default.FilterBAndW, "Filter") { /* filter picker */ }
                    EditActionButton(Icons.Default.Delete, "Delete") { viewModel.deletePage(state.selectedPage) }
                    Spacer(Modifier.weight(1f))
                    EditActionButton(Icons.Default.AddPhotoAlternate, "Add Page") { /* re-open camera */ }
                    EditActionButton(Icons.Default.Reorder, "Reorder") { viewModel.toggleReorderMode() }
                }
            }
        },
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Filter chips row
            FilterChipRow(
                chips = listOf("Color", "Grayscale", "B&W", "Original"),
                selectedChip = listOf("Color", "Grayscale", "B&W", "Original")[state.selectedFilter],
                onChipSelected = { chip ->
                    val idx = listOf("Color", "Grayscale", "B&W", "Original").indexOf(chip)
                    if (idx >= 0) viewModel.applyFilterToAll(idx)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            )

            // Page count indicator
            Text(
                text = "${state.pages.size} page${if (state.pages.size != 1) "s" else ""}",
                style = MaterialTheme.typography.bodySmall,
                color = Gray,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(8.dp))

            // Main page preview
            if (state.pages.isNotEmpty()) {
                val selectedPath = state.pages.getOrNull(state.selectedPage)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    if (selectedPath != null) {
                        AsyncImage(
                            model = selectedPath,
                            contentDescription = "Page ${state.selectedPage + 1}",
                            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Page strip
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(state.pages, key = { _, path -> path }) { index, path ->
                    Box(
                        modifier = Modifier
                            .size(64.dp, 84.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                width = if (index == state.selectedPage) 2.dp else 0.dp,
                                color = if (index == state.selectedPage) Amber else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable { viewModel.selectPage(index) },
                    ) {
                        AsyncImage(
                            model = path,
                            contentDescription = "Page ${index + 1}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                        )
                        // Page number badge
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(NearBlack.copy(alpha = 0.7f))
                                .padding(horizontal = 4.dp, vertical = 1.dp),
                        ) {
                            Text("${index + 1}", fontSize = 9.sp, color = androidx.compose.ui.graphics.Color.White, fontFamily = InterFamily)
                        }
                    }
                }
                // Add page button
                item {
                    Box(
                        modifier = Modifier
                            .size(64.dp, 84.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, Border, RoundedCornerShape(8.dp))
                            .clickable { /* open camera again */ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Add, "Add page", tint = Gray)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }

    // Save dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save Document") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = documentName,
                        onValueChange = { documentName = it },
                        label = { Text("Document name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Amber),
                    )
                    var asPdf by remember { mutableStateOf(true) }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = asPdf, onCheckedChange = { asPdf = it }, colors = CheckboxDefaults.colors(checkedColor = Amber))
                        Text("Save as PDF", fontFamily = InterFamily)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveDocument(documentName)
                        showSaveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NearBlack),
                    enabled = documentName.isNotBlank(),
                ) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun EditActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 6.dp, vertical = 4.dp),
    ) {
        Icon(icon, label, tint = Gray, modifier = Modifier.size(20.dp))
        Text(label, fontSize = 9.sp, color = Gray, fontFamily = InterFamily)
    }
}
