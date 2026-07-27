package com.paperflow.app.presentation.notes

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

// ─── Kiwi note card color palette — green pastels only ───────────────────────
private val KiwiNoteColors = listOf(
    Color(0xFFEAF9C8), // KiwiLight
    Color(0xFFDDF5AF), // Lighter kiwi
    Color(0xFFEFF9D8), // Between
    Color(0xFFF0FBE0), // Pale kiwi
    Color(0xFFE6F7D0), // Warm kiwi
    Color(0xFFF5FDE8), // Almost-white kiwi
)

// Selects which decor type appears on each card
private enum class KiwiDecorType { SLICE, LEAF, LEAF_MIRROR, SPARKLE, BLOB, NONE }
private fun noteDecorType(id: Long): KiwiDecorType = KiwiDecorType.entries[((id % 5).toInt())]

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
        containerColor = KiwiBg,
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = {
                    Text(
                        "Notes",
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        color      = NearBlack,
                    )
                },
                actions = {
                    // Grid icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(KiwiRadius.Card))
                            .background(KiwiLight)
                            .clickable { /* toggle grid/list */ },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.GridView, null, tint = KiwiPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    // Search icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(KiwiRadius.Card))
                            .background(KiwiLight)
                            .clickable {
                                navController.navigate(com.paperflow.app.core.navigation.Routes.Search.withQuery())
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Outlined.Search, "Search", tint = KiwiPrimary, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBg),
            )
        },
        bottomBar = { BottomNavBar(navController = navController, onScanClick = onScanClick) },
        floatingActionButton = {
            // Kiwi extended FAB — capsule with gradient
            ExtendedFloatingActionButton(
                onClick          = onNewNote,
                icon             = {
                    Icon(Icons.Outlined.Add, null, tint = KiwiDark)
                },
                text             = {
                    Text(
                        "New Note",
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.Bold,
                        color      = KiwiDark,
                    )
                },
                containerColor   = KiwiAccent,
                shape            = RoundedCornerShape(KiwiRadius.Button),
                elevation        = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 8.dp,
                ),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier       = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 100.dp),
        ) {
            // ── Filter chips ─────────────────────────────────────────────
            item {
                FilterChipRow(
                    chips            = NoteFilter.entries.map { it.label() },
                    selectedChip     = state.filter.label(),
                    onChipSelected   = { chip ->
                        NoteFilter.entries.firstOrNull { it.label() == chip }?.let { viewModel.setFilter(it) }
                    },
                    modifier         = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }

            // ── Content ───────────────────────────────────────────────────
            when {
                state.isLoading -> {
                    items(3) { idx ->
                        AnimatedListItem(index = idx) {
                            Row(
                                modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SkeletonBox(Modifier.weight(1f).height(120.dp), RoundedCornerShape(KiwiRadius.LargeCard))
                                SkeletonBox(Modifier.weight(1f).height(160.dp), RoundedCornerShape(KiwiRadius.LargeCard))
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
                state.notes.isEmpty() -> {
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
                                        Icons.Outlined.EditNote,
                                        null,
                                        tint     = KiwiPrimary,
                                        modifier = Modifier.size(40.dp),
                                    )
                                }
                            },
                            title    = "No notes yet",
                            subtitle = "Tap the button below to create your first note",
                            action   = {
                                KiwiPrimaryButton(
                                    text    = "New Note",
                                    icon    = Icons.Outlined.Add,
                                    onClick = onNewNote,
                                )
                            },
                        )
                    }
                }
                else -> {
                    // 2-column staggered grid
                    items(state.notes.chunked(2), key = { it.first().id }) { pair ->
                        AnimatedListItem(index = state.notes.indexOf(pair.first()) / 2) {
                            Row(
                                modifier              = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment     = Alignment.Top,
                            ) {
                                pair.forEachIndexed { i, note ->
                                    KiwiNoteCard(
                                        note            = note,
                                        backgroundColor = KiwiNoteColors[(note.id % KiwiNoteColors.size).toInt()],
                                        decorType       = noteDecorType(note.id),
                                        onClick         = { onNoteClick(note.id) },
                                        onDelete        = { viewModel.deleteNote(note) },
                                        modifier        = Modifier.weight(1f),
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
    }
}

// ─── Kiwi Note Card ───────────────────────────────────────────────────────────
// Each card has a slightly different organic Kiwi decoration in the bottom-right
@Composable
private fun KiwiNoteCard(
    note: Note,
    backgroundColor: Color,
    decorType: KiwiDecorType,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }

    PressableCard(
        onClick        = onClick,
        modifier       = modifier,
        containerColor = backgroundColor,
        elevation      = KiwiElevation.None,
        shape          = RoundedCornerShape(KiwiRadius.LargeCard),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            // Organic corner decoration
            when (decorType) {
                KiwiDecorType.SLICE        -> KiwiSliceDecor(
                    modifier = Modifier.align(Alignment.BottomEnd).size(56.dp),
                    tint     = KiwiPrimary,
                    alpha    = 0.22f,
                )
                KiwiDecorType.LEAF         -> LeafDecor(
                    modifier = Modifier.align(Alignment.BottomEnd).size(48.dp),
                    alpha    = 0.28f,
                )
                KiwiDecorType.LEAF_MIRROR  -> LeafDecor(
                    modifier = Modifier.align(Alignment.BottomEnd).size(48.dp),
                    mirrorX  = true,
                    alpha    = 0.28f,
                )
                KiwiDecorType.SPARKLE      -> SparkleDecor(
                    modifier = Modifier.align(Alignment.BottomEnd).size(36.dp),
                    alpha    = 0.35f,
                )
                KiwiDecorType.BLOB         -> OrganicBlobDecor(
                    modifier = Modifier.align(Alignment.BottomEnd).size(60.dp),
                    alpha    = 0.18f,
                )
                KiwiDecorType.NONE         -> { /* no decoration */ }
            }

            // Card content
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.Top,
                ) {
                    Text(
                        text       = note.title,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFamily,
                        fontSize   = 14.sp,
                        maxLines   = 2,
                        overflow   = TextOverflow.Ellipsis,
                        color      = NearBlack,
                        modifier   = Modifier.weight(1f),
                    )
                    Box {
                        IconButton(
                            onClick  = { showMenu = true },
                            modifier = Modifier.size(28.dp),
                        ) {
                            Icon(Icons.Default.MoreVert, null, tint = Gray, modifier = Modifier.size(14.dp))
                        }
                        DropdownMenu(
                            expanded          = showMenu,
                            onDismissRequest  = { showMenu = false },
                        ) {
                            DropdownMenuItem(
                                text        = { Text("Delete", color = Error) },
                                onClick     = { onDelete(); showMenu = false },
                                leadingIcon = { Icon(Icons.Outlined.Delete, null, tint = Error) },
                            )
                        }
                    }
                }

                if (note.content.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text       = note.content.take(120),
                        fontSize   = 12.sp,
                        fontFamily = InterFamily,
                        color      = NearBlack.copy(alpha = 0.65f),
                        maxLines   = 4,
                        overflow   = TextOverflow.Ellipsis,
                    )
                }

                Spacer(Modifier.height(32.dp)) // Room for decoration
                Text(
                    text       = SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(note.updatedAt)),
                    fontSize   = 10.sp,
                    fontFamily = InterFamily,
                    color      = KiwiDark.copy(alpha = 0.6f),
                )
            }
        }
    }
}

private fun NoteFilter.label() = when (this) {
    NoteFilter.ALL         -> "All"
    NoteFilter.TYPED       -> "Typed"
    NoteFilter.HANDWRITTEN -> "Handwritten"
    NoteFilter.STARRED     -> "Starred"
}
