package com.paperflow.app.presentation.notes

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.staggeredgrid.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.Note
import com.paperflow.app.presentation.components.*
import java.text.SimpleDateFormat
import java.util.*

// Note card pastel colors cycling
private val NoteColors = listOf(
    Color(0xFFFFF9C4), Color(0xFFC8E6C9), Color(0xFFBBDEFB),
    Color(0xFFFFCCBC), Color(0xFFE1BEE7), Color(0xFFB2EBF2),
)

@Composable
fun NotesScreen(
    navController: NavController,
    onNoteClick: (Long) -> Unit,
    onNewNote: () -> Unit,
    onScanClick: () -> Unit,
    viewModel: NotesViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text("Notes", fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                actions = {
                    IconButton(onClick = { /* toggle grid/list */ }) { Icon(Icons.Default.GridView, null) }
                    IconButton(onClick = { navController.navigate(com.paperflow.app.core.navigation.Routes.Search.withQuery()) }) { Icon(Icons.Outlined.Search, "Search") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
        bottomBar = { BottomNavBar(navController = navController, onScanClick = onScanClick) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNewNote,
                icon = { Icon(Icons.Default.Add, null, tint = NearBlack) },
                text = { Text("New Note", fontFamily = InterFamily, fontWeight = FontWeight.SemiBold, color = NearBlack) },
                containerColor = Amber,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp),
        ) {
            // Filter chips
            item {
                FilterChipRow(
                    chips = NoteFilter.entries.map { it.label() },
                    selectedChip = state.filter.label(),
                    onChipSelected = { chip ->
                        NoteFilter.entries.firstOrNull { it.label() == chip }?.let { viewModel.setFilter(it) }
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            }

            // Staggered notes grid using chunked rows
            if (state.isLoading) {
                items(3) { idx ->
                    AnimatedListItem(index = idx) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SkeletonBox(Modifier.weight(1f).height(120.dp), RoundedCornerShape(12.dp))
                            SkeletonBox(Modifier.weight(1f).height(160.dp), RoundedCornerShape(12.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            } else if (state.notes.isEmpty()) {
                item {
                    EmptyState(
                        icon = { Icon(Icons.Outlined.EditNote, null, Modifier.size(64.dp), tint = GrayLight) },
                        title = "No notes yet",
                        subtitle = "Tap the button below to create your first note",
                    )
                }
            } else {
                // 2-column staggered grid
                items(state.notes.chunked(2), key = { it.first().id }) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        pair.forEachIndexed { i, note ->
                            NoteCard(
                                note = note,
                                backgroundColor = NoteColors[(note.id % NoteColors.size).toInt()],
                                onClick = { onNoteClick(note.id) },
                                onDelete = { viewModel.deleteNote(note) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    backgroundColor: Color,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    PressableCard(
        onClick = onClick,
        modifier = modifier,
        containerColor = backgroundColor,
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Text(
                    text = note.title,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = InterFamily,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                Box {
                    IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.MoreVert, null, tint = Gray, modifier = Modifier.size(14.dp))
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(text = { Text("Delete", color = Error) }, onClick = { onDelete(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = Error) })
                    }
                }
            }
            if (note.content.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = note.content.take(120),
                    fontSize = 12.sp,
                    fontFamily = InterFamily,
                    color = NearBlack.copy(alpha = 0.7f),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(note.updatedAt)),
                fontSize = 10.sp,
                fontFamily = InterFamily,
                color = Gray,
            )
        }
    }
}

private fun NoteFilter.label() = when (this) {
    NoteFilter.ALL -> "All"
    NoteFilter.TYPED -> "Typed"
    NoteFilter.HANDWRITTEN -> "Handwritten"
    NoteFilter.STARRED -> "Starred"
}
