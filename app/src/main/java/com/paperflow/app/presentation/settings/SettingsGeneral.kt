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



// ─── Workspace Settings ───────────────────────────────────────────────────────
@Composable
fun WorkspaceSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Workspace", onBack) {
        SettingSectionLabel("Layout & View")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Show Recent Documents", "Display recent documents at the top", state.workspaceShowRecent, viewModel::setWorkspaceShowRecent)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Show Hidden Folders", "Display folders marked as hidden", state.workspaceShowHidden, viewModel::setWorkspaceShowHidden)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Timeline View", "Group documents by date", state.workspaceTimelineView, viewModel::setWorkspaceTimelineView)
            }
        }
    }
}

// ─── Themes & Appearance Settings ─────────────────────────────────────────────
@Composable
fun ThemesSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Themes & Appearance", onBack) {
        SettingSectionLabel("Theme")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                listOf("LIGHT" to "Light", "DARK" to "Dark", "AMOLED" to "AMOLED (Battery Saver)").forEach { (key, label) ->
                    Row(Modifier.fillMaxWidth().clickable { viewModel.setTheme(key) }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(label, Modifier.weight(1f), fontFamily = InterFamily)
                        RadioButton(selected = state.appTheme == key, onClick = { viewModel.setTheme(key) }, colors = RadioButtonDefaults.colors(selectedColor = Amber))
                    }
                }
            }
        }
        SettingSectionLabel("Animations")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Enable Animations", "Smooth transitions and micro-interactions", state.themeAnimations, viewModel::setThemeAnimations)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Reduce Motion", "Minimize motion effects for accessibility", state.themeReduceMotion, viewModel::setThemeReduceMotion)
            }
        }
    }
}

// ─── Search Settings ──────────────────────────────────────────────────────────
@Composable
fun SearchSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Search", onBack) {
        SettingSectionLabel("Search Scope")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("OCR Text", "Include extracted text from scans", state.searchOcrText, viewModel::setSearchOcrText)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Notes & Annotations", "Search inside your personal notes", state.searchNotes, viewModel::setSearchNotes)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("Search History", "Save past searches", state.searchHistoryEnabled, viewModel::setSearchHistoryEnabled)
            }
        }
    }
}

// ─── Accessibility Settings ───────────────────────────────────────────────────
@Composable
fun AccessibilitySettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsSubScreen("Accessibility", onBack) {
        SettingSectionLabel("Visuals")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column {
                SettingToggle("Larger Text", "Increase global font sizes", state.accessibilityLargerText, viewModel::setAccessibilityLargerText)
                HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
                SettingToggle("High Contrast", "Enhance UI contrast", state.accessibilityHighContrast, viewModel::setAccessibilityHighContrast)
            }
        }
    }
}

// ─── General Settings (Legacy mapping) ────────────────────────────────────────
@Composable
fun GeneralSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    ThemesSettingsScreen(onBack, viewModel)
}
