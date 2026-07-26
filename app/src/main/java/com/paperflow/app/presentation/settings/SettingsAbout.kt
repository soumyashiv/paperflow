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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*



// ─── About Settings ───────────────────────────────────────────────────────────
@Composable
fun AboutScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    SettingsSubScreen("About", onBack) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Info, contentDescription = null, tint = Amber, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("PaperFlow", fontWeight = FontWeight.Bold, fontSize = 24.sp, fontFamily = InterFamily)
            Text("Version 1.0.0", color = Gray, fontFamily = InterFamily)
            Spacer(Modifier.height(32.dp))
            Text("Crafted with care to help you manage your documents efficiently and beautifully.", 
                 style = MaterialTheme.typography.bodyMedium, color = Gray, 
                 modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}

// ─── Developer Settings ───────────────────────────────────────────────────────
@Composable
fun DeveloperSettingsScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    SettingsSubScreen("Developer Options", onBack) {
        SettingSectionLabel("Debug")
        Surface(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp), shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(Modifier.padding(16.dp)) {
                Text("Developer mode enabled.", fontFamily = InterFamily)
            }
        }
    }
}

// ─── Help Center ──────────────────────────────────────────────────────────────
@Composable
fun HelpCenterScreen(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    SettingsSubScreen("Help Center", onBack) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = Amber, modifier = Modifier.size(64.dp))
            Spacer(Modifier.height(16.dp))
            Text("Help & Support", fontWeight = FontWeight.Bold, fontSize = 24.sp, fontFamily = InterFamily)
            Spacer(Modifier.height(16.dp))
            Text("Documentation and support features coming soon.", 
                 style = MaterialTheme.typography.bodyMedium, color = Gray, 
                 modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
