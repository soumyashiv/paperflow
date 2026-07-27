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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*



// ─── Security Settings ────────────────────────────────────────────────────────
@Composable
fun SecuritySettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showKeyInput by remember { mutableStateOf(false) }
    var apiKey by remember { mutableStateOf(state.geminiApiKey) }

    SettingsSubScreen("Security", onBack) {
        SettingSectionLabel("Biometrics")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Biometric Unlock", "Use fingerprint or face to unlock vault", state.biometricEnabled, viewModel::setBiometric)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Screenshot Protection", "Prevent screenshots in vault & secure screens (SR-VAULT-001)", state.screenshotProtection, viewModel::setScreenshotProtection)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                Column(Modifier.padding(16.dp)) {
                    Text("Vault Auto-Lock", fontFamily = InterFamily, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(8.dp))
                    listOf(5 to "5 min", 10 to "10 min", 15 to "15 min", 30 to "30 min").forEach { (mins, label) ->
                        Row(Modifier.fillMaxWidth().clickable { viewModel.setVaultTimeout(mins) }.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(selected = state.vaultTimeoutMinutes == mins, onClick = { viewModel.setVaultTimeout(mins) }, colors = RadioButtonDefaults.colors(selectedColor = KiwiPrimary))
                            Text(label, fontFamily = InterFamily)
                        }
                    }
                }
            }
        }

        SettingSectionLabel("AI Integration (Optional)")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp)) {
                Text("Gemini API Key", fontFamily = InterFamily, fontWeight = FontWeight.Medium)
                Text("Required only for online AI features. Stored locally, never uploaded.", style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.setGeminiKey(apiKey); showKeyInput = false },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors(containerColor = KiwiPrimary, contentColor = White)
                ) {
                    Text("Save Key", fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                }
            }
        }
    }
}

// ─── Storage Settings ─────────────────────────────────────────────────────────
@Composable
fun StorageSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Storage", onBack) {
        SettingSectionLabel("Usage")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp)) {
                Text("Total Space Used: ${state.storageUsed}", fontFamily = InterFamily, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { state.storagePercent },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = KiwiPrimary, trackColor = GrayLight.copy(alpha = 0.3f)
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = { /* clear cache */ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear Cache", fontFamily = InterFamily, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Backup & Restore ─────────────────────────────────────────────────────────
@Composable
fun BackupRestoreScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Backup & Restore", onBack) {
        SettingSectionLabel("Data Sync")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Backup Documents", "Include PDFs and scans", state.backupDocuments, viewModel::setBackupDocuments)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Backup Notes", "Include rich text notes", state.backupNotes, viewModel::setBackupNotes)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Backup Settings", "Include app preferences", state.backupSettings, viewModel::setBackupSettings)
            }
        }
    }
}

// ─── Notifications ────────────────────────────────────────────────────────────
@Composable
fun NotificationsSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Notifications", onBack) {
        SettingSectionLabel("Alerts")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Scan Complete", "Notify when multi-page scan is saved", state.notifScanComplete, viewModel::setNotifScanComplete)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("OCR Complete", "Notify when text extraction finishes", state.notifOcrComplete, viewModel::setNotifOcrComplete)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Reminders", "Notify for scheduled tasks", state.notifReminders, viewModel::setNotifReminders)
            }
        }
    }
}


