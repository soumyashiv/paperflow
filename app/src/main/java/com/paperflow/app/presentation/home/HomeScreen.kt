package com.paperflow.app.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
        containerColor = KiwiBg,
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
            // ── Greeting ─────────────────────────────────────────────────
            item {
                HomeGreetingSection(
                    userName             = state.userName,
                    notificationCount    = state.notificationCount,
                    onNotificationsClick = onNotificationsClick,
                )
            }

            // ── Search Bar ───────────────────────────────────────────────
            item {
                KiwiSearchBarWithDecor(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .clickable { onSearchClick() },
                )
            }

            // ── Quick Actions ────────────────────────────────────────────
            item { SectionHeader(title = "Quick Actions") }
            item {
                QuickActionsGrid(
                    onScanClick      = onScanClick,
                    onUploadClick    = onUploadClick,
                    onNoteClick      = { navController.navigate(com.paperflow.app.core.navigation.Routes.NoteEditor.newNote()) },
                    onConvertClick   = onConvertClick,
                    onAiChatClick    = onAiChatClick,
                    onDocumentsClick = { navController.navigate(com.paperflow.app.core.navigation.Routes.Workspace.route) },
                )
            }

            // ── Folders ───────────────────────────────────────────────────
            if (state.folders.isNotEmpty()) {
                item { SectionHeader(title = "Folders") }
                item {
                    FoldersRow(
                        folders       = state.folders,
                        onFolderClick = { folder ->
                            navController.navigate(com.paperflow.app.core.navigation.Routes.FolderContents.withId(folder.id))
                        },
                    )
                }
            }

            // ── Recent Documents ──────────────────────────────────────────
            item {
                SectionHeader(
                    title  = "Recent Documents",
                    action = {
                        TextButton(
                            onClick = { navController.navigate(com.paperflow.app.core.navigation.Routes.Workspace.route) },
                        ) {
                            Text(
                                text       = "See all",
                                color      = KiwiPrimary,
                                fontFamily = InterFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 13.sp,
                            )
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint     = KiwiPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    },
                )
            }

            when {
                state.isLoading -> {
                    items(5) { index ->
                        AnimatedListItem(index = index) { DocumentItemSkeleton() }
                    }
                }
                state.recentDocuments.isEmpty() -> {
                    item {
                        EmptyState(
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(KiwiRadius.LargeCard))
                                        .background(KiwiLight),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        Icons.Outlined.FolderOpen,
                                        contentDescription = null,
                                        tint     = KiwiPrimary,
                                        modifier = Modifier.size(40.dp),
                                    )
                                }
                            },
                            title    = "No documents yet",
                            subtitle = "Tap the scan button below to add your first document",
                            action   = {
                                KiwiPrimaryButton(
                                    text    = "Scan Now",
                                    icon    = Icons.Outlined.CameraAlt,
                                    onClick = onScanClick,
                                )
                            },
                        )
                    }
                }
                else -> {
                    itemsIndexed(
                        items = state.recentDocuments,
                        key   = { _, doc -> doc.id },
                    ) { index, doc ->
                        AnimatedListItem(index = index) {
                            RecentDocumentItem(
                                document        = doc,
                                onClick         = { onDocumentClick(doc.id) },
                                onFavoriteClick = { viewModel.toggleFavorite(doc) },
                                onMenuClick     = { /* context menu */ },
                            )
                        }
                    }
                }
            }

            // ── Storage ───────────────────────────────────────────────────
            item { StorageBar(storageInfo = state.storageInfo) }
        }
    }
}

// ─── Greeting Section ─────────────────────────────────────────────────────────
@Composable
private fun HomeGreetingSection(
    userName: String,
    notificationCount: Int,
    onNotificationsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Column {
            val greeting = when (java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
                in 5..11  -> "Good morning"
                in 12..17 -> "Good afternoon"
                else      -> "Good evening"
            }
            // "Good evening, shivv 👋" — name in KiwiPrimary
            val displayName = if (userName.isNotBlank()) userName else "there"
            Text(
                text = buildAnnotatedString {
                    append("$greeting, ")
                    withStyle(SpanStyle(color = KiwiPrimary, fontWeight = FontWeight.Bold)) {
                        append(displayName)
                    }
                    append(" 👋")
                },
                style      = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color      = NearBlack,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = "All your documents in one place",
                style = MaterialTheme.typography.bodySmall,
                color = Gray,
            )
        }

        BadgedBox(badge = {
            if (notificationCount > 0) {
                Badge(containerColor = Error) {
                    Text(notificationCount.toString(), fontSize = 10.sp)
                }
            }
        }) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(KiwiRadius.Card))
                    .background(KiwiLight)
                    .clickable(onClick = onNotificationsClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications",
                    tint               = KiwiPrimary,
                    modifier           = Modifier.size(26.dp),
                )
            }
        }
    }
}

// ─── Search Bar With Kiwi Decor ───────────────────────────────────────────────
@Composable
private fun KiwiSearchBarWithDecor(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        // The pill search bar (non-interactive — clicking routes to search screen)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(
                    elevation    = 4.dp,
                    shape        = RoundedCornerShape(KiwiRadius.SearchBar),
                    spotColor    = KiwiPrimary.copy(alpha = 0.90f),
                    ambientColor = Color.Transparent,
                )
                .clip(RoundedCornerShape(KiwiRadius.SearchBar))
                .background(KiwiSurface)
                .border(2.dp, Border, RoundedCornerShape(KiwiRadius.SearchBar)),
            contentAlignment = Alignment.CenterStart,
        ) {
            Row(
                modifier             = Modifier.padding(horizontal = 16.dp),
                verticalAlignment    = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Search,
                    contentDescription = null,
                    tint               = GrayLight,
                    modifier           = Modifier.size(24.dp),
                )
                Text(
                    text   = "Search documents, notes...",
                    style  = MaterialTheme.typography.bodyMedium,
                    color  = GrayLight,
                )
            }
            // Kiwi slice decoration — trailing right side
            KiwiSliceDecor(
                modifier = Modifier
                    .size(25.dp)
                    .align(Alignment.CenterEnd),
                alpha = 0.90f,
                size  = 50.dp,
            )
        }
    }
}

// ─── Quick Actions Grid ───────────────────────────────────────────────────────
private data class QuickAction(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val iconBg: Color,
    val decor: @Composable (Modifier) -> Unit,
    val onClick: () -> Unit,
)

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
        QuickAction("Scan",      "Smart scanner",     Icons.Outlined.CameraAlt,    KiwiLighter,           { m -> LeafDecor(m, alpha = 0.5f, size = 50.dp) },    onScanClick),
        QuickAction("Upload",    "Import & files",    Icons.Outlined.Upload,        KiwiLighter,         { m -> KiwiSliceDecor(m, alpha = 0.9f, size = 45.dp) }, onUploadClick),
        QuickAction("Note",      "Create notes",      Icons.Outlined.EditNote,      KiwiLighter,         { m -> LeafDecor(m, mirrorX = true, alpha = 0.5f, size = 50.dp) }, onNoteClick),
        QuickAction("Convert",   "PDF, JPG, OCR",     Icons.Outlined.Transform,     KiwiLighter,         { m -> OrganicBlobDecor(m, alpha = 0.15f, size = 60.dp) }, onConvertClick),
        QuickAction("Ask AI",    "Summarize, explain",Icons.Outlined.AutoAwesome,   KiwiLighter,   { m -> SparkleDecor(m, alpha = 0.50f) }, onAiChatClick),
        QuickAction("Documents", "View all files",    Icons.Outlined.FolderOpen,    KiwiLighter,         { m -> KiwiSliceDecor(m, alpha = 0.9f, size = 40.dp) }, onDocumentsClick),
    )

    Column(modifier = Modifier.padding(horizontal = 18.dp)) {
        for (row in actions.chunked(3)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { action ->
                    KiwiQuickActionCard(action = action, modifier = Modifier.weight(1f))
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun KiwiQuickActionCard(action: QuickAction, modifier: Modifier = Modifier) {
    PressableCard(
        onClick        = action.onClick,
        modifier       = modifier.height(115.dp),
        containerColor = KiwiSurface,
        elevation      = KiwiElevation.Card,
        shape          = RoundedCornerShape(KiwiRadius.LargeCard),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative element in corner
            action.decor(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(52.dp),
            )
            // Content
            Column(
                modifier            = Modifier.padding(vertical = 14.dp, horizontal = 14.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(KiwiRadius.Small))
                        .background(action.iconBg),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = action.icon,
                        contentDescription = action.label,
                        tint               = KiwiPrimary,
                        modifier           = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text       = action.label,
                    fontSize   = 14.sp,
                    fontFamily = InterFamily,
                    fontWeight = FontWeight.Bold,
                    color      = NearBlack,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
                Text(
                    text       = action.subtitle,
                    fontSize   = 10.sp,
                    fontFamily = InterFamily,
                    color      = Gray,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

// ─── Folders Row ──────────────────────────────────────────────────────────────
@Composable
private fun FoldersRow(folders: List<Folder>, onFolderClick: (Folder) -> Unit) {
    Row(
        modifier              = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        folders.forEach { folder ->
            KiwiFolderCard(folder = folder, onClick = { onFolderClick(folder) })
        }
    }
}

@Composable
private fun KiwiFolderCard(folder: Folder, onClick: () -> Unit) {
    val (icon, iconBg) = when {
        folder.name.contains("Work",     ignoreCase = true) -> Icons.Outlined.Work          to KiwiLighter
        folder.name.contains("Study",    ignoreCase = true) -> Icons.Outlined.School        to KiwiLighter
        folder.name.contains("Personal", ignoreCase = true) -> Icons.Outlined.Person        to KiwiLighter
        folder.isLocked                                      -> Icons.Outlined.Lock          to KiwiLighter
        else                                                 -> Icons.Outlined.FolderOpen   to KiwiLighter
    }

    PressableCard(
        onClick        = onClick,
        modifier       = Modifier.width(115.dp),
        containerColor = KiwiSurface,
        shape          = RoundedCornerShape(KiwiRadius.LargeCard),
        elevation      = KiwiElevation.Card,
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(KiwiRadius.Small))
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, contentDescription = folder.name, tint = KiwiPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text       = folder.name,
                fontSize   = 13.sp,
                fontFamily = InterFamily,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                color      = NearBlack,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text       = if (folder.isLocked) "Locked" else "${folder.itemCount} items",
                fontSize   = 10.sp,
                fontFamily = InterFamily,
                color      = Gray,
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
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Star icon — kiwi tinted when favorited
        IconButton(onClick = onFavoriteClick, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector        = if (document.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Favorite",
                tint               = if (document.isFavorite) KiwiPrimary else GrayLight,
                modifier           = Modifier.size(20.dp),
            )
        }

        DocumentThumbnail(
            thumbnailPath = document.thumbnailPath,
            type          = document.type,
            modifier      = Modifier.size(width = 52.dp, height = 68.dp),
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = document.name,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis,
                color      = NearBlack,
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text   = "${document.type.name} • ${formatRelativeTime(document.updatedAt)} • ${formatSize(document.sizeBytes)}",
                style  = MaterialTheme.typography.bodySmall,
                color  = Gray,
                maxLines = 1,
            )
        }

        TypeBadge(document.type)

        IconButton(onClick = onMenuClick, modifier = Modifier.size(32.dp)) {
            Icon(
                Icons.Default.MoreVert,
                contentDescription = "More",
                tint     = GrayLight,
                modifier = Modifier.size(18.dp),
            )
        }
    }
    HorizontalDivider(
        color     = KiwiDivider,
        thickness = 0.75.dp,
        modifier  = Modifier.padding(horizontal = 20.dp),
    )
}

@Composable
private fun DocumentItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SkeletonBox(Modifier.size(32.dp), shape = RoundedCornerShape(KiwiRadius.Pill))
        SkeletonBox(Modifier.size(52.dp, 68.dp), shape = RoundedCornerShape(KiwiRadius.Card))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SkeletonBox(Modifier.fillMaxWidth(0.7f).height(14.dp))
            SkeletonBox(Modifier.fillMaxWidth(0.5f).height(10.dp))
        }
        SkeletonBox(Modifier.size(44.dp, 22.dp), shape = RoundedCornerShape(KiwiRadius.Pill))
    }
}

// ─── Storage Bar ─────────────────────────────────────────────────────────────
@Composable
private fun StorageBar(storageInfo: StorageInfo) {
    PressableCard(
        onClick  = {},
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        shape    = RoundedCornerShape(KiwiRadius.LargeCard),
    ) {
        Row(
            modifier              = Modifier.padding(18.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress    = { storageInfo.usedPercent },
                    modifier    = Modifier.size(52.dp),
                    color       = KiwiPrimary,
                    trackColor  = KiwiDivider,
                    strokeWidth = 4.dp,
                )
                Text(
                    text       = "${(storageInfo.usedPercent * 100).toInt()}%",
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = InterFamily,
                    color      = KiwiDark,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Storage", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = NearBlack)
                Text(
                    text  = "${formatSize(storageInfo.usedBytes)} of ${formatSize(storageInfo.totalBytes)} used",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray,
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress    = { storageInfo.usedPercent },
                    modifier    = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(KiwiRadius.Pill)),
                    color       = KiwiPrimary,
                    trackColor  = KiwiDivider,
                )
            }
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(KiwiLight),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = KiwiPrimary, modifier = Modifier.size(18.dp))
            }
        }
    }
}

// ─── Formatters ───────────────────────────────────────────────────────────────
private fun formatRelativeTime(ts: Long): String {
    val diff = System.currentTimeMillis() - ts
    return when {
        diff < 60_000      -> "Just now"
        diff < 3_600_000   -> "${diff / 60_000}m ago"
        diff < 86_400_000  -> "Today, ${java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault()).format(java.util.Date(ts))}"
        diff < 172_800_000 -> "Yesterday"
        else -> java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date(ts))
    }
}

private fun formatSize(bytes: Long): String = when {
    bytes < 1024      -> "$bytes B"
    bytes < 1_048_576 -> "${bytes / 1024} KB"
    else              -> String.format("%.1f MB", bytes / 1_048_576.0)
}
