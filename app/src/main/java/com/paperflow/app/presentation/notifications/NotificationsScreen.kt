package com.paperflow.app.presentation.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
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
import com.paperflow.app.core.theme.*
import com.paperflow.app.presentation.components.EmptyState
import java.text.SimpleDateFormat
import java.util.*

private data class AppNotification(
    val id: Int,
    val title: String,
    val body: String,
    val timestamp: Long,
    val type: NotifType,
    val isRead: Boolean = false,
)

private enum class NotifType { OCR_DONE, SCAN_SAVED, IMPORT_COMPLETE, BACKUP, SYSTEM }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    // Static demo notifications — in production, driven by WorkManager result flows
    val notifications = remember {
        mutableStateListOf(
            AppNotification(1, "OCR Complete", "Text extraction finished for 'Invoice_March.pdf'", System.currentTimeMillis() - 300_000, NotifType.OCR_DONE),
            AppNotification(2, "Scan Saved", "5-page scan saved as 'Contract_2024.pdf'", System.currentTimeMillis() - 3_600_000, NotifType.SCAN_SAVED),
            AppNotification(3, "Import Complete", "'Research_Paper.pdf' (2.1 MB) imported successfully", System.currentTimeMillis() - 86_400_000, NotifType.IMPORT_COMPLETE, isRead = true),
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = { notifications.replaceAll { it.copy(isRead = true) } }) {
                            Text("Mark all read", color = Amber, fontFamily = InterFamily)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { pv ->
        if (notifications.isEmpty()) {
            EmptyState(
                icon = { Icon(Icons.Outlined.Notifications, null, Modifier.size(64.dp), tint = GrayLight) },
                title = "No notifications",
                subtitle = "You're all caught up!",
                modifier = Modifier.fillMaxSize().padding(pv),
            )
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(pv), contentPadding = PaddingValues(bottom = 24.dp)) {
                itemsIndexed(notifications, key = { _, n -> n.id }) { _, notif ->
                    NotificationRow(
                        notif = notif,
                        onClick = { notifications.replaceAll { if (it.id == notif.id) it.copy(isRead = true) else it } },
                        onDismiss = { notifications.removeAll { it.id == notif.id } },
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(notif: AppNotification, onClick: () -> Unit, onDismiss: () -> Unit) {
    val (icon, tint) = when (notif.type) {
        NotifType.OCR_DONE -> Icons.Default.TextSnippet to Color(0xFF7B1FA2)
        NotifType.SCAN_SAVED -> Icons.Default.CameraAlt to Amber
        NotifType.IMPORT_COMPLETE -> Icons.Default.FileDownload to Info
        NotifType.BACKUP -> Icons.Default.Backup to Color(0xFF4CAF50)
        NotifType.SYSTEM -> Icons.Default.Info to GrayLight
    }
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (notif.isRead) MaterialTheme.colorScheme.surface else AmberLight,
        tonalElevation = if (notif.isRead) 0.dp else 2.dp,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(16.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(40.dp).let {
                    it.clip(RoundedCornerShape(10.dp)).background(tint.copy(alpha = 0.1f))
                },
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
            }
            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(notif.title, fontWeight = FontWeight.SemiBold, fontFamily = InterFamily, fontSize = 14.sp)
                    if (!notif.isRead) Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Amber))
                }
                Spacer(Modifier.height(2.dp))
                Text(notif.body, style = MaterialTheme.typography.bodySmall, color = Gray, fontFamily = InterFamily, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(formatRelTime(notif.timestamp), fontSize = 10.sp, color = GrayLight, fontFamily = InterFamily)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.Close, "Dismiss", tint = GrayLight, modifier = Modifier.size(14.dp))
            }
        }
    }
}

private fun formatRelTime(ts: Long): String {
    val diff = System.currentTimeMillis() - ts
    return when {
        diff < 60_000 -> "Just now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(ts))
    }
}
