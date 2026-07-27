package com.paperflow.app.presentation.workspace

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
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
import com.paperflow.app.core.navigation.Routes
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.*
import com.paperflow.app.presentation.components.*

@Composable
fun WorkspaceScreen(
    navController: NavController,
    folderId: Long? = null,
    onDocumentClick: (Long) -> Unit,
    onDocumentDetails: (Long) -> Unit,
    onFolderClick: (Long) -> Unit,
    onVaultClick: () -> Unit,
    onScanClick: () -> Unit,
    viewModel: WorkspaceViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreateFolder by remember { mutableStateOf(false) }
    var newFolderName by remember { mutableStateOf("") }
    var showSortMenu by remember { mutableStateOf(false) }

    LaunchedEffect(folderId) {
        viewModel.init(folderId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            WorkspaceTopBar(
                title = if (folderId == null) "Workspace" else state.folderName,
                isRoot = folderId == null,
                viewMode = state.viewMode,
                onBack = { navController.popBackStack() },
                onSearch = { navController.navigate(Routes.Search.withQuery()) },
                onNotifications = { navController.navigate(Routes.Notifications.route) },
                onViewModeChange = { viewModel.setViewMode(it) },
                onMenuClick = {},
            )
        },
        bottomBar = {
            if (folderId == null) {
                BottomNavBar(navController = navController, onScanClick = onScanClick)
            }
        },
        floatingActionButton = {
            KiwiFAB(
                onClick = { showCreateFolder = true },
                icon    = { Icon(Icons.Default.Add, "New", tint = KiwiDark, modifier = Modifier.size(24.dp)) },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp),
        ) {
            // Search + Sort row
            item {
                AnimatedListItem(index = 0) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PaperFlowSearchBar(
                            query = state.searchQuery,
                            onQueryChange = { viewModel.setSearchQuery(it) },
                            modifier = Modifier.weight(1f),
                        )
                    Box {
                        OutlinedButton(
                            onClick = { showSortMenu = true },
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Border),
                        ) {
                            Icon(Icons.Default.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Sort", fontFamily = InterFamily)
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            SortBy.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = { Text(sort.label()) },
                                    onClick = { viewModel.setSortBy(sort); showSortMenu = false },
                                    trailingIcon = if (state.sortBy == sort) {
                                        { Icon(Icons.Default.Check, null, tint = KiwiPrimary) }
                                    } else null,
                                )
                            }
                        }
                    }
                }
            }
        }

            // Filter chips
            item {
                AnimatedListItem(index = 1) {
                    FilterChipRow(
                        chips = FilterType.entries.map { it.label() },
                        selectedChip = state.filter.label(),
                        onChipSelected = { chip ->
                            FilterType.entries.firstOrNull { it.label() == chip }
                                ?.let { viewModel.setFilter(it) }
                        },
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp),
                    )
                }
            }

            // Root folders grid (only on root workspace)
            if (folderId == null && state.rootFolders.isNotEmpty()) {
                item {
                    AnimatedListItem(index = 2) {
                        Column {
                            SectionHeader(
                                title = "Folders",
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            SpecialFoldersGrid(
                                folders = state.rootFolders,
                                onFolderClick = onFolderClick,
                                onVaultClick = onVaultClick,
                                onNewFolderClick = { showCreateFolder = true },
                            )
                            Spacer(Modifier.height(32.dp))
                        }
                    }
                }
            }

            // Documents section header
            item {
                AnimatedListItem(index = 3) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                    Text(
                        "Recent Documents",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ViewModeButton(Icons.Default.ViewList, state.viewMode == ViewMode.LIST) { viewModel.setViewMode(ViewMode.LIST) }
                        ViewModeButton(Icons.Default.GridView, state.viewMode == ViewMode.GRID) { viewModel.setViewMode(ViewMode.GRID) }
                        ViewModeButton(Icons.Default.GridOn, state.viewMode == ViewMode.LARGE_GRID) { viewModel.setViewMode(ViewMode.LARGE_GRID) }
                    }
                }
            }
        }

            // Documents
            when (state.viewMode) {
                ViewMode.LIST -> {
                    itemsIndexed(state.documents, key = { _, doc -> doc.id }) { idx, doc ->
                        AnimatedListItem(index = 4 + idx) {
                            WorkspaceDocumentListItem(
                                document = doc,
                                onClick = { onDocumentClick(doc.id) },
                                onDetails = { onDocumentDetails(doc.id) },
                                onFavorite = { viewModel.toggleFavorite(doc) },
                            )
                        }
                    }
                }
                ViewMode.GRID, ViewMode.LARGE_GRID -> {
                    // Grid uses a nested lazy grid (manual chunking for LazyColumn compat)
                    val cols = if (state.viewMode == ViewMode.GRID) 3 else 2
                    itemsIndexed(state.documents.chunked(cols), key = { _, chunk -> chunk.first().id }) { idx, chunk ->
                        AnimatedListItem(index = 4 + idx) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                            chunk.forEach { doc ->
                                WorkspaceDocumentGridItem(
                                    document = doc,
                                    onClick = { onDocumentClick(doc.id) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                                repeat(cols - chunk.size) { Spacer(Modifier.weight(1f)) }
                            }
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }

            if (state.documents.isEmpty() && !state.isLoading) {
                item {
                    AnimatedListItem(index = 4) {
                        EmptyState(
                            icon = { Icon(Icons.Outlined.FolderOpen, null, Modifier.size(64.dp), tint = GrayLight) },
                            title = "No documents",
                            subtitle = "Scan or import a document to get started",
                        )
                    }
                }
            }
        }
    }

    // Create folder dialog
    if (showCreateFolder) {
        AlertDialog(
            onDismissRequest = { showCreateFolder = false; newFolderName = "" },
            title = { Text("New Folder") },
            text = {
                OutlinedTextField(
                    value = newFolderName,
                    onValueChange = { newFolderName = it },
                    label = { Text("Folder name") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KiwiPrimary),
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newFolderName.isNotBlank()) {
                            viewModel.createNewFolder(newFolderName)
                            showCreateFolder = false; newFolderName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = KiwiPrimary, contentColor = White),
                ) { Text("Create", fontFamily = InterFamily, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateFolder = false; newFolderName = "" }) { Text("Cancel") }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkspaceTopBar(
    title: String,
    isRoot: Boolean,
    viewMode: ViewMode,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onMenuClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(title, fontWeight = FontWeight.Bold, fontFamily = InterFamily)
                if (isRoot) Text("All your documents in one place", style = MaterialTheme.typography.bodySmall, color = Gray)
            }
        },
        navigationIcon = if (!isRoot) {
            { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } }
        } else { {} },
        actions = {
            IconButton(onClick = onSearch) { Icon(Icons.Outlined.Search, "Search") }
            IconButton(onClick = onNotifications) { Icon(Icons.Outlined.Notifications, "Notifications") }
            IconButton(onClick = onMenuClick) { Icon(Icons.Default.MoreVert, "Menu") }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBg),
    )
}

@Composable
private fun SpecialFoldersGrid(
    folders: List<Folder>,
    onFolderClick: (Long) -> Unit,
    onVaultClick: () -> Unit,
    onNewFolderClick: () -> Unit,
) {
    data class GridItem(val name: String, val sub: String, val icon: ImageVector, val color: Color, val action: () -> Unit)
    val baseItems = folders.map { f ->
        GridItem(f.name, "${f.itemCount} items", Icons.Default.Folder, Color(android.graphics.Color.parseColor(f.colorHex)), { onFolderClick(f.id) })
    }
    val extras = listOf(
        GridItem("Vault", "Locked", Icons.Default.Lock, Color(0xFF607D8B), onVaultClick),
        GridItem("New Folder", "", Icons.Default.Add, GrayLight, onNewFolderClick),
    )
    val all = baseItems + extras

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        all.chunked(4).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { item ->
                    PressableCard(
                        onClick = item.action,
                        modifier = Modifier.weight(1f).aspectRatio(1f),
                        shape = RoundedCornerShape(KiwiRadius.LargeCard),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Icon(item.icon, item.name, tint = item.color, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.height(4.dp))
                            Text(item.name, fontSize = 11.sp, fontFamily = InterFamily, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            if (item.sub.isNotEmpty()) Text(item.sub, fontSize = 9.sp, color = Gray, fontFamily = InterFamily)
                        }
                    }
                }
                repeat(4 - row.size) { Spacer(Modifier.weight(1f)) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun WorkspaceDocumentListItem(
    document: Document,
    onClick: () -> Unit,
    onDetails: () -> Unit,
    onFavorite: () -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        IconButton(onClick = onFavorite, modifier = Modifier.size(28.dp)) {
            Icon(
                if (document.isFavorite) Icons.Filled.Star else Icons.Outlined.StarOutline,
                null, tint = if (document.isFavorite) KiwiPrimary else GrayLight,
                modifier = Modifier.size(18.dp),
            )
        }
        DocumentThumbnail(document.thumbnailPath, document.type, Modifier.size(52.dp, 68.dp))
        Column(Modifier.weight(1f)) {
            Text(document.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = NearBlack)
            Text("${document.pageCount}p • ${formatSize(document.sizeBytes)}", style = MaterialTheme.typography.bodySmall, color = Gray)
        }
        TypeBadge(document.type)
        Box {
            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Default.MoreVert, null, tint = Gray, modifier = Modifier.size(16.dp))
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                DropdownMenuItem(text = { Text("Details") }, onClick = { onDetails(); showMenu = false }, leadingIcon = { Icon(Icons.Default.Info, null) })
                DropdownMenuItem(text = { Text("Share") }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Default.Share, null) })
                DropdownMenuItem(text = { Text("Delete", color = Error) }, onClick = { showMenu = false }, leadingIcon = { Icon(Icons.Default.Delete, null, tint = Error) })
            }
        }
    }
    HorizontalDivider(color = KiwiDivider, thickness = 0.75.dp, modifier = Modifier.padding(horizontal = 24.dp))
}

@Composable
private fun WorkspaceDocumentGridItem(document: Document, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PressableCard(onClick = onClick, modifier = modifier, shape = RoundedCornerShape(KiwiRadius.LargeCard)) {
        Column(modifier = Modifier.padding(8.dp)) {
            DocumentThumbnail(document.thumbnailPath, document.type, Modifier.fillMaxWidth().height(100.dp))
            Spacer(Modifier.height(6.dp))
            Text(document.name, fontSize = 11.sp, fontFamily = InterFamily, fontWeight = FontWeight.Medium, maxLines = 2, overflow = TextOverflow.Ellipsis, color = NearBlack)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                TypeBadge(document.type)
                Text(formatSize(document.sizeBytes), fontSize = 9.sp, color = Gray, fontFamily = InterFamily)
            }
        }
    }
}

@Composable
private fun ViewModeButton(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(32.dp)) {
        Icon(icon, null, tint = if (selected) KiwiPrimary else GrayLight, modifier = Modifier.size(18.dp))
    }
}

private fun FilterType.label() = when (this) {
    FilterType.ALL -> "All"
    FilterType.PDF -> "PDF"
    FilterType.IMAGES -> "Images"
    FilterType.NOTES -> "Notes"
    FilterType.OCR -> "OCR"
    FilterType.STARRED -> "Starred"
}

private fun SortBy.label() = when (this) {
    SortBy.DATE_DESC -> "Newest first"
    SortBy.DATE_ASC -> "Oldest first"
    SortBy.NAME_ASC -> "Name A–Z"
    SortBy.NAME_DESC -> "Name Z–A"
    SortBy.SIZE_DESC -> "Largest first"
}

private fun formatSize(bytes: Long) = when {
    bytes < 1024 -> "$bytes B"
    bytes < 1_048_576 -> "${bytes / 1024} KB"
    else -> String.format("%.1f MB", bytes / 1_048_576.0)
}
