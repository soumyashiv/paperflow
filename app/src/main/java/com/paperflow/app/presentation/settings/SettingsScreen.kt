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
import androidx.compose.ui.graphics.Brush
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
        containerColor = KiwiBg,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color      = NearBlack,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBg),
            )
        },
        bottomBar = {
            com.paperflow.app.presentation.components.BottomNavBar(navController, onScanClick)
        },
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(rememberScrollState()),
        ) {
            // ── Profile Card ──────────────────────────────────────────────
            KiwiProfileCard(
                name          = state.userName.ifBlank { "Your Name" },
                documentCount = state.documentCount,
                storageUsed   = state.storageUsed,
                onEditName    = { viewModel.promptNameEdit() },
                onAvatarClick = { /* pick avatar */ },
            )

            Spacer(Modifier.height(8.dp))

            // ── Document Tools ────────────────────────────────────────────
            KiwiSettingsGroup(title = "Document Tools") {
                KiwiSettingsRow(Icons.Outlined.CameraAlt,     "Scanner",        "Auto-crop, filters, multi-page")     { navController.navigate(Routes.SettingsScanner.route) }
                KiwiSettingsRow(Icons.Outlined.PictureAsPdf,  "PDF Reader",     "View mode, reading direction")       { navController.navigate(Routes.SettingsPDFReader.route) }
                KiwiSettingsRow(Icons.Outlined.TextFormat,    "OCR & Text",     "Languages, handwriting")             { navController.navigate(Routes.SettingsOCR.route) }
                KiwiSettingsRow(Icons.Outlined.EditNote,      "Notes",          "Font size, autosave")                { navController.navigate(Routes.SettingsNotes.route) }
                KiwiSettingsRow(Icons.Outlined.Search,        "Search",         "History, OCR search")                { navController.navigate(Routes.SettingsSearch.route) }
                KiwiSettingsRow(Icons.Outlined.Folder,        "Workspace",      "Layout, sorting, default folder")    { navController.navigate(Routes.SettingsWorkspace.route) }
            }

            // ── Preferences ───────────────────────────────────────────────
            KiwiSettingsGroup(title = "Preferences") {
                KiwiSettingsRow(Icons.Outlined.Palette,        "Themes & Appearance", "Dark mode, Kiwi / Dynamic Color") { navController.navigate(Routes.SettingsThemes.route) }
                KiwiSettingsRow(Icons.Outlined.Notifications,  "Notifications",        "Alerts, reminders")              { navController.navigate(Routes.SettingsNotifications.route) }
                KiwiSettingsRow(Icons.Outlined.Accessibility,  "Accessibility",        "Text size, contrast")            { navController.navigate(Routes.SettingsAccessibility.route) }
            }

            // ── Privacy, Security & Data ──────────────────────────────────
            KiwiSettingsGroup(title = "Privacy & Data") {
                KiwiSettingsRow(Icons.Outlined.Lock,    "Security",         "App lock, vault, biometrics") { navController.navigate(Routes.SettingsSecurity.route) }
                KiwiSettingsRow(Icons.Outlined.Storage, "Storage",          "Cache, usage details")        { navController.navigate(Routes.SettingsStorage.route) }
                KiwiSettingsRow(Icons.Outlined.Backup,  "Backup & Restore", "Export data, import")         { navController.navigate(Routes.SettingsBackup.route) }
            }

            // ── About ─────────────────────────────────────────────────────
            KiwiSettingsGroup(title = "About") {
                KiwiSettingsRow(Icons.Outlined.Info,        "About PaperFlow", "Version, licenses") { navController.navigate(Routes.SettingsAbout.route) }
                KiwiSettingsRow(Icons.Outlined.HelpOutline, "Help Center")                         { navController.navigate(Routes.HelpCenter.route) }
            }

            Spacer(Modifier.height(40.dp))
        }
    }

    if (state.showNameEdit) {
        KiwiNameEditDialog(
            initial   = state.userName,
            onConfirm = { viewModel.saveName(it) },
            onDismiss = { viewModel.dismissNameEdit() },
        )
    }
}

// ─── Profile Card (Kiwi) ──────────────────────────────────────────────────────
@Composable
private fun KiwiProfileCard(
    name: String,
    documentCount: Int,
    storageUsed: String,
    onEditName: () -> Unit,
    onAvatarClick: () -> Unit,
) {
    com.paperflow.app.presentation.components.PressableCard(
        onClick  = onEditName,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
        shape    = RoundedCornerShape(KiwiRadius.LargeCard),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                // Avatar — gradient circle with initial
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(KiwiAccent, KiwiPrimary)
                            )
                        )
                        .clickable(onClick = onAvatarClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text       = name.firstOrNull()?.uppercase() ?: "U",
                        fontSize   = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color      = White,
                        fontFamily = InterFamily,
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        name,
                        fontWeight = FontWeight.Bold,
                        fontSize   = 20.sp,
                        fontFamily = InterFamily,
                        color      = NearBlack,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Tap to edit profile",
                        style      = MaterialTheme.typography.bodyMedium,
                        color      = Gray,
                        fontFamily = InterFamily,
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(KiwiLight),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Default.ChevronRight, null, tint = KiwiPrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = KiwiDivider)
            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                KiwiProfileStat(value = documentCount.toString(), label = "Documents")
                KiwiProfileStat(value = "0",          label = "Notes")
                KiwiProfileStat(value = storageUsed,  label = "Storage")
            }
        }
    }
}

@Composable
private fun KiwiProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            fontWeight = FontWeight.Bold,
            fontSize   = 20.sp,
            fontFamily = InterFamily,
            color      = KiwiPrimary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style  = MaterialTheme.typography.bodySmall,
            color  = Gray,
            fontFamily = InterFamily,
        )
    }
}

// ─── Settings Group Card ──────────────────────────────────────────────────────
@Composable
fun KiwiSettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        text       = title.uppercase(),
        style      = MaterialTheme.typography.labelSmall,
        color      = KiwiPrimary,
        fontWeight = FontWeight.Bold,
        letterSpacing = androidx.compose.ui.unit.TextUnit(1.5f, androidx.compose.ui.unit.TextUnitType.Sp),
        modifier   = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
    )
    Surface(
        modifier       = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape          = RoundedCornerShape(KiwiRadius.LargeCard),
        color          = KiwiSurface,
        shadowElevation = KiwiElevation.Card,
        border         = androidx.compose.foundation.BorderStroke(1.dp, KiwiDivider),
    ) {
        Column(content = content)
    }
}

// Backward-compat alias used by existing sub-screens
@Composable
fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) =
    KiwiSettingsGroup(title, content)

// ─── Settings Row ─────────────────────────────────────────────────────────────
@Composable
fun KiwiSettingsRow(
    icon: ImageVector,
    label: String,
    subtitle: String = "",
    showDivider: Boolean = true,
    onClick: () -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Kiwi-tinted icon badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(KiwiRadius.Small))
                    .background(KiwiLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = KiwiPrimary,
                    modifier           = Modifier.size(22.dp),
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize   = 15.sp,
                    fontFamily = InterFamily,
                    color      = NearBlack,
                )
                if (subtitle.isNotBlank()) {
                    Spacer(Modifier.height(1.dp))
                    Text(
                        subtitle,
                        style      = MaterialTheme.typography.bodySmall,
                        color      = Gray,
                        fontFamily = InterFamily,
                    )
                }
            }

            Icon(
                Icons.Default.ChevronRight,
                null,
                tint     = GrayLight,
                modifier = Modifier.size(18.dp),
            )
        }
        if (showDivider) {
            HorizontalDivider(
                color    = KiwiDivider,
                modifier = Modifier.padding(start = 72.dp, end = 18.dp),
            )
        }
    }
}

// Backward-compat alias used by existing sub-screens
@Composable
fun SettingsRow(icon: ImageVector, label: String, subtitle: String = "", onClick: () -> Unit) =
    KiwiSettingsRow(icon, label, subtitle, showDivider = true, onClick)

// ─── Name Edit Dialog ─────────────────────────────────────────────────────────
@Composable
fun KiwiNameEditDialog(
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Edit Name",
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold,
                color      = NearBlack,
            )
        },
        text = {
            OutlinedTextField(
                value         = text,
                onValueChange = { text = it },
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(KiwiRadius.Card),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = KiwiPrimary,
                    unfocusedBorderColor = KiwiDivider,
                    cursorColor          = KiwiPrimary,
                ),
            )
        },
        confirmButton = {
            com.paperflow.app.presentation.components.KiwiPrimaryButton(
                text    = "Save",
                onClick = { onConfirm(text) },
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Gray, fontFamily = InterFamily)
            }
        },
        containerColor = KiwiSurface,
        shape          = RoundedCornerShape(KiwiRadius.Dialog),
    )
}

// Backward-compat alias
@Composable
fun NameEditDialog(initial: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) =
    KiwiNameEditDialog(initial, onConfirm, onDismiss)
