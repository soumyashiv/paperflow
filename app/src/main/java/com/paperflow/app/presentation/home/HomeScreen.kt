package com.paperflow.app.presentation.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.*
import com.paperflow.app.presentation.components.*

@Composable
fun HomeScreen(
    navController: NavController,
    onScanClick: () -> Unit,
    onDocumentClick: (Long) -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAiChatClick: () -> Unit,
    onUploadClick: () -> Unit,
    onConvertClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            BottomNavBar(navController = navController, onScanClick = onScanClick)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            // ── Top Bar ──────────────────────────────────────────────────
            item {
                HomeTopBar(
                    userName = state.userName,
                    notificationCount = state.notificationCount,
                    onNotificationsClick = onNotificationsClick,
                )
            }

            // ── Search Bar ───────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Clean search field — no trailing mic/AI button
                    PaperFlowSearchBar(
                        query = "",
                        onQueryChange = {},
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSearchClick() },
                        placeholder = "Search documents, notes...",
                    )
                }
            }

            // ── Quick Actions Grid ────────────────────────────────────────
            item {
                SectionHeader(title = "Quick Actions")
            }

            item {
                QuickActionsGrid(
                    onScanClick = onScanClick,
                    onUploadClick = onUploadClick,
                    onNoteClick = { navController.navigate(com.paperflow.app.core.navigation.Routes.NoteEditor.newNote()) },
                    onConvertClick = onConvertClick,
                    onAiChatClick = onAiChatClick,
                    onDocumentsClick = { navController.navigate(com.paperflow.app.core.navigation.Routes.Workspace.route) },
                )
            }

            // ── Folders ───────────────────────────────────────────────────
            if (state.folders.isNotEmpty()) {
                item { SectionHeader(title = "Folders") }
                item {
                    FoldersRow(
                        folders = state.folders,
                        onFolderClick = { folder ->
                            navController.navigate(com.paperflow.app.core.navigation.Routes.FolderContents.withId(folder.id))
                        },
                    )
                }
            }

            // ── Recent Documents ──────────────────────────────────────────
            item {
                SectionHeader(
                    title = "Recent Documents",
                    action = {
                        TextButton(onClick = { navController.navigate(com.paperflow.app.core.navigation.Routes.Workspace.route) }) {
                            Text("See all", color = Amber, fontFamily = InterFamily)
                        }
                    },
                )
            }

            if (state.isLoading) {
                items(5) { index ->
                    AnimatedListItem(index = index) {
                        DocumentItemSkeleton()
                    }
                }
            } else if (state.recentDocuments.isEmpty()) {
                item {
                    EmptyState(
                        icon = { Icon(Icons.Outlined.FolderOpen, contentDescription = null, modifier = Modifier.size(64.dp), tint = GrayLight) },
                        title = "No documents yet",
                        subtitle = "Tap the scan button below to add your first document",
                    )
                }
            } else {
                itemsIndexed(
                    items = state.recentDocuments,
                    key = { _, doc -> doc.id },
                ) { index, doc ->
                    AnimatedListItem(index = index) {
                        RecentDocumentItem(
                            document = doc,
                            onClick = { onDocumentClick(doc.id) },
                            onFavoriteClick = { viewModel.toggleFavorite(doc) },
                            onMenuClick = { /* context menu */ },
                        )
                    }
                }
            }

            // ── Storage Indicator ─────────────────────────────────────────
            item {
                StorageBar(storageInfo = state.storageInfo)
            }
        }
    }
}

// ─── Top Bar ─────────────────────────────────────────────────────────────────
@Composable
private fun HomeTopBar(
    userName: String,
    notificationCount: Int,
    onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
                in 5..11 -> "Good morning"
                in 12..17 -> "Good afternoon"
                else -> "Good evening"
            }
            Text(
                text = if (userName.isNotBlank()) "$greeting, $userName 👋" else "Hello! 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = NearBlack,
            )
            Text(
                text = "All your documents in one place",
                style = MaterialTheme.typography.bodySmall,
                color = Gray,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BadgedBox(badge = {
                if (notificationCount > 0) {
                    Badge(containerColor = Error) {
                        Text(notificationCount.toString(), fontSize = 10.sp)
                    }
                }
            }) {
                IconButton(onClick = onNotificationsClick) {
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications")
                }
            }
        }
    }
}

// ─── Quick Actions Grid ───────────────────────────────────────────────────────

private data class QuickAction(val label: String, val icon: ImageVector, val color: Color, val onClick: () -> Unit)

/**
 * 6-action grid in 2 rows of 3:
 *
 *  [ Scan ]  [ Upload ]  [ Note ]
 *  [ Convert ] [ Ask AI ] [ Documents ]
 *
 * Import and ID Card have been removed — Upload covers file import, and the
 * Scanner now detects ID cards automatically.
 */
@Composable
private fun QuickActionsGrid(
    onScanClick: () -> Unit,
    onUploadClick: () -> Unit,
    onNoteClick: () -> Unit,
    onConvertClick: () -> Unit,
    onAiChatClick: () -> Unit,
    onDocumentsClick: () -> Unit,
) {
    val actions = listOf(
        QuickAction("Scan",      Icons.Default.CameraAlt,   Color(0xFFFFE8CC), onScanClick),
        QuickAction("Upload",    Icons.Default.Upload,       Color(0xFFF0F0F0), onUploadClick),
        QuickAction("Note",      Icons.Default.EditNote,     Color(0xFFF0F0F0), onNoteClick),
        QuickAction("Convert",   Icons.Default.Transform,    Color(0xFFF0F0F0), onConvertClick),
        QuickAction("Ask AI",    Icons.Default.SmartToy,     Color(0xFFFFF0D0), onAiChatClick),
        QuickAction("Documents", Icons.Default.FolderOpen,   Color(0xFFF0F0F0), onDocumentsClick),
    )

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (row in actions.chunked(3)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { action ->
                    QuickActionCard(
                        action = action,
                        modifier = Modifier.weight(1f),
                    )
                }
                // Fill any trailing gap (shouldn't happen with 6 items / 3 cols, but defensive)
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction, modifier: Modifier = Modifier) {
    PressableCard(
        onClick = action.onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        elevation = 0.dp,
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(action.color),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.label,
                    tint = NearBlack,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = action.label,
                fontSize = 12.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                color = NearBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val subtitle = when (action.label) {
                "Scan"      -> "Smart scanner"
                "Upload"    -> "Import & files"
                "Note"      -> "Create notes"
                "Convert"   -> "PDF, JPG, OCR"
                "Ask AI"    -> "Summarize, explain"
                "Documents" -> "View all files"
                else        -> ""
            }
            if (subtitle.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    fontFamily = InterFamily,
                    color = Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ─── Folders Row ──────────────────────────────────────────────────────────────
@Composable
private fun FoldersRow(folders: List<Folder>, onFolderClick: (Folder) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        folders.forEach { folder ->
            FolderCard(
                folder = folder,
                onClick = { onFolderClick(folder) },
            )
        }
    }
}

@Composable
private fun FolderCard(folder: Folder, onClick: () -> Unit) {
    val (icon, bgColor) = when {
        folder.name.contains("Work", ignoreCase = true)     -> Icons.Default.Folder     to Color(0xFFFFE8CC)
        folder.name.contains("Study", ignoreCase = true)    -> Icons.Default.School     to Color(0xFFE8F5E9)
        folder.name.contains("Personal", ignoreCase = true) -> Icons.Default.Person     to Color(0xFFE8F5E9)
        folder.name.contains("Vault", ignoreCase = true) || folder.isLocked
                                                            -> Icons.Default.Lock       to Color(0xFFFFF0D0)
        else                                                -> Icons.Default.Folder     to Color(0xFFF0F0F0)
    }

    PressableCard(
        onClick = onClick,
        modifier = Modifier.width(100.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(20.dp),
        elevation = 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = folder.name,
                    tint = NearBlack,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = folder.name,
                fontSize = 12.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = NearBlack,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = if (folder.isLocked) "Locked" else "${folder.itemCount} items",
                fontSize = 10.sp,
                fontFamily = InterFamily,
                color = Gray,
            )
        }
    }
}

// ─── Recent Document Item ─────────────────────────────────────────────────────
@Composable
private fun RecentDocumentItem(
    document: Document,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconButton(onClick = onFavoriteClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = if (document.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Favorite",
                tint = if (document.isFavorite) Amber else GrayLight,
                modifier = Modifier.size(20.dp),
            )
        }

        DocumentThumbnail(
            thumbnailPath = document.thumbnailPath,
            type = document.type,
            modifier = Modifier.size(width = 56.dp, height = 72.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = document.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = NearBlack,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${document.type.name} • ${formatRelativeTime(document.updatedAt)} • ${formatSize(document.sizeBytes)}",
                style = MaterialTheme.typography.bodySmall,
                color = Gray,
                maxLines = 1,
            )
        }

        TypeBadge(document.type)

        IconButton(onClick = onMenuClick, modifier = Modifier.size(32.dp)) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Gray, modifier = Modifier.size(18.dp))
        }
    }
    HorizontalDivider(color = Border.copy(alpha = 0.5f), thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun DocumentItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkeletonBox(Modifier.size(32.dp), shape = RoundedCornerShape(50))
        SkeletonBox(Modifier.size(56.dp, 72.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonBox(Modifier.fillMaxWidth(0.7f).height(14.dp))
            SkeletonBox(Modifier.fillMaxWidth(0.5f).height(10.dp))
        }
        SkeletonBox(Modifier.size(40.dp, 20.dp))
    }
}

// ─── Storage Bar ─────────────────────────────────────────────────────────────
@Composable
private fun StorageBar(storageInfo: StorageInfo) {
    PressableCard(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { storageInfo.usedPercent },
                    modifier = Modifier.size(48.dp),
                    color = Amber,
                    trackColor = Border,
                    strokeWidth = 4.dp,
                )
                Text(
                    text = "${(storageInfo.usedPercent * 100).toInt()}%",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Storage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Text(
                    text = "${formatSize(storageInfo.usedBytes)} of ${formatSize(storageInfo.totalBytes)} used",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray,
                )
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { storageInfo.usedPercent },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Amber,
                    trackColor = Border,
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Gray)
        }
    }
}

// ─── Formatters ───────────────────────────────────────────────────────────────
private fun formatRelativeTime(ts: Long): String {
    val diff = System.currentTimeMillis() - ts
    return when {
        diff < 60_000     -> "Just now"
        diff < 3_600_000  -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "Today, ${java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(ts))}"
        diff < 172_800_000 -> "Yesterday"
        else -> java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(ts))
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes < 1024       -> "$bytes B"
    bytes < 1_048_576  -> "${bytes / 1024} KB"
    else               -> String.format("%.1f MB", bytes / 1_048_576.0)
}
