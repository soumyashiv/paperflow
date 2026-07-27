package com.paperflow.app.presentation.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*



// ─── Scanner Settings ─────────────────────────────────────────────────────────
@Composable
fun ScannerSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Scanner", onBack) {
        SettingSectionLabel("Capture")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Auto Crop", "Automatically detect and crop edges", state.scannerAutoCrop, viewModel::setScannerAutoCrop)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Perspective Correction", "Fix skewed documents automatically", state.scannerPerspectiveCorrection, viewModel::setScannerPerspectiveCorrection)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Smart Rotation", "Rotate upside-down pages", state.scannerSmartRotation, viewModel::setScannerSmartRotation)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Haptic Feedback", "Vibrate when scan succeeds", state.scannerHapticFeedback, viewModel::setScannerHapticFeedback)
            }
        }
    }
}

// ─── PDF Reader Settings ──────────────────────────────────────────────────────
@Composable
fun PDFReaderSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("PDF Reader", onBack) {
        SettingSectionLabel("Viewer")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                Column(Modifier.padding(16.dp)) {
                    Text("Default View Mode", fontFamily = InterFamily, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    listOf("single" to "Single Page", "scroll" to "Continuous Scroll").forEach { (key, label) ->
                        Row(Modifier.fillMaxWidth().clickable { viewModel.setPdfView(key) }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = state.pdfDefaultView == key, onClick = { viewModel.setPdfView(key) }, colors = RadioButtonDefaults.colors(selectedColor = KiwiPrimary))
                            Text(label, fontFamily = InterFamily)
                        }
                    }
                }
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Page Turn Sound", "Play a sound when turning pages", state.pdfPageSound, viewModel::setPdfPageSound)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Remember Last Page", "Start where you left off", state.pdfRememberLastPage, viewModel::setPdfRememberLastPage)
            }
        }
    }
}

// ─── OCR Settings ─────────────────────────────────────────────────────────────
@Composable
fun OCRSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("OCR & Text", onBack) {
        SettingSectionLabel("Text Recognition")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Enable OCR", "Extract text from new scans", state.ocrEnabled, viewModel::setOcrEnabled)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Offline Mode", "Use on-device models only", state.ocrOfflineMode, viewModel::setOcrOfflineMode)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Auto-Index for Search", "Make extracted text searchable", state.ocrAutoIndex, viewModel::setOcrAutoIndex)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Background Processing", "Run OCR in the background", state.ocrBackgroundProc, viewModel::setOcrBackgroundProc)
            }
        }
    }
}

// ─── Notes Settings ───────────────────────────────────────────────────────────
@Composable
fun NotesSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Notes", onBack) {
        SettingSectionLabel("Editor")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                Column(Modifier.padding(16.dp)) {
                    Text("Font Size: ${state.notesFontSize}sp", fontFamily = InterFamily, fontWeight = FontWeight.Medium)
                    Slider(
                        value = state.notesFontSize.toFloat(),
                        onValueChange = { viewModel.setNotesFontSize(it.toInt()) },
                        valueRange = 12f..24f,
                        steps = 5,
                        colors = SliderDefaults.colors(thumbColor = KiwiPrimary, activeTrackColor = KiwiPrimary),
                    )
                }
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Auto-Save", "Save drafts automatically", state.notesAutoSave, viewModel::setNotesAutoSave)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Spell Check", "Highlight misspelled words", state.notesSpellCheck, viewModel::setNotesSpellCheck)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Markdown Support", "Enable basic markdown rendering", state.notesMarkdown, viewModel::setNotesMarkdown)
            }
        }
    }
}


