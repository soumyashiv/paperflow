package com.paperflow.app.presentation.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import androidx.navigation.NavController
import com.paperflow.app.core.navigation.Routes
import com.paperflow.app.core.theme.*

@Composable
fun SettingsScreen(
    navController: NavController,
    onScanClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = { com.paperflow.app.presentation.components.BottomNavBar(navController, onScanClick) },
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Profile Card ──────────────────────────────────────────────
            ProfileCard(
                name = state.userName.ifBlank { "Your Name" },
                documentCount = state.documentCount,
                storageUsed = state.storageUsed,
                onEditName = { viewModel.promptNameEdit() },
                onAvatarClick = { /* pick avatar */ },
            )
            Spacer(Modifier.height(8.dp))

            // ── Document Tools ────────────────────────────────────────────
            SettingsGroup(title = "Document Tools") {
                SettingsRow(icon = Icons.Outlined.CameraAlt, label = "Scanner", subtitle = "Auto-crop, filters, multi-page") { navController.navigate(Routes.SettingsScanner.route) }
                SettingsRow(icon = Icons.Outlined.PictureAsPdf, label = "PDF Reader", subtitle = "View mode, reading direction") { navController.navigate(Routes.SettingsPDFReader.route) }
                SettingsRow(icon = Icons.Outlined.TextFormat, label = "OCR & Text", subtitle = "Languages, handwriting") { navController.navigate(Routes.SettingsOCR.route) }
                SettingsRow(icon = Icons.Outlined.EditNote, label = "Notes", subtitle = "Font size, autosave") { navController.navigate(Routes.SettingsNotes.route) }
                SettingsRow(icon = Icons.Outlined.Search, label = "Search", subtitle = "History, OCR search") { navController.navigate(Routes.SettingsSearch.route) }
                SettingsRow(icon = Icons.Outlined.Folder, label = "Workspace", subtitle = "Layout, sorting, default folder") { navController.navigate(Routes.SettingsWorkspace.route) }
            }

            // ── Preferences ───────────────────────────────────────────────
            SettingsGroup(title = "Preferences") {
                SettingsRow(icon = Icons.Outlined.Palette, label = "Themes & Appearance", subtitle = "Dark mode, accent color") { navController.navigate(Routes.SettingsThemes.route) }
                SettingsRow(icon = Icons.Outlined.Notifications, label = "Notifications", subtitle = "Alerts, reminders") { navController.navigate(Routes.SettingsNotifications.route) }
                SettingsRow(icon = Icons.Outlined.Accessibility, label = "Accessibility", subtitle = "Text size, contrast") { navController.navigate(Routes.SettingsAccessibility.route) }
            }

            // ── Privacy, Security & Data ──────────────────────────────────
            SettingsGroup(title = "Privacy & Data") {
                SettingsRow(icon = Icons.Outlined.Lock, label = "Security", subtitle = "App lock, vault, biometrics") { navController.navigate(Routes.SettingsSecurity.route) }
                SettingsRow(icon = Icons.Outlined.Storage, label = "Storage", subtitle = "Cache, usage details") { navController.navigate(Routes.SettingsStorage.route) }
                SettingsRow(icon = Icons.Outlined.Backup, label = "Backup & Restore", subtitle = "Export data, import") { navController.navigate(Routes.SettingsBackup.route) }
            }

            // ── About ─────────────────────────────────────────────────────
            SettingsGroup(title = "About") {
                SettingsRow(icon = Icons.Outlined.Info, label = "About PaperFlow", subtitle = "Version, licenses") { navController.navigate(Routes.SettingsAbout.route) }
                SettingsRow(icon = Icons.Outlined.HelpOutline, label = "Help Center") { navController.navigate(Routes.HelpCenter.route) }
                
                // Note: Developer settings is unlocked via taps inside AboutScreen
                // But if unlocked, it can appear here. Assuming it's hidden by default.
            }

            Spacer(Modifier.height(32.dp))
        }
    }

    if (state.showNameEdit) {
        NameEditDialog(
            initial = state.userName,
            onConfirm = { viewModel.saveName(it) },
            onDismiss = { viewModel.dismissNameEdit() },
        )
    }
}

@Composable
private fun ProfileCard(name: String, documentCount: Int, storageUsed: String, onEditName: () -> Unit, onAvatarClick: () -> Unit) {
    com.paperflow.app.presentation.components.PressableCard(
        onClick = onEditName,
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Amber).clickable(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = name.firstOrNull()?.uppercase() ?: "U",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = NearBlack,
                        fontFamily = InterFamily,
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(name, fontWeight = FontWeight.Bold, fontSize = 20.sp, fontFamily = InterFamily)
                    Text("Tap to edit profile", style = MaterialTheme.typography.bodyMedium, color = Gray, fontFamily = InterFamily)
                }
                Icon(Icons.Default.ChevronRight, null, tint = GrayLight)
            }
            
            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = Border.copy(alpha = 0.5f))
            Spacer(Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ProfileStat(value = documentCount.toString(), label = "Documents")
                ProfileStat(value = "0", label = "Notes") // Placeholder until Notes DB is queried
                ProfileStat(value = storageUsed, label = "Storage")
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, fontFamily = InterFamily, color = NearBlack)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
    }
}

@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = GrayLight,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
    )
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsRow(icon: ImageVector, label: String, subtitle: String = "", onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(AmberLight.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Amber, modifier = Modifier.size(22.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, fontFamily = InterFamily, color = NearBlack)
            if (subtitle.isNotBlank()) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = GrayLight, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun NameEditDialog(initial: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Name", fontFamily = InterFamily, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(text) }, colors = ButtonDefaults.buttonColors(containerColor = Amber, contentColor = NearBlack)) {
                Text("Save", fontFamily = InterFamily, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Gray) }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp)
    )
}
