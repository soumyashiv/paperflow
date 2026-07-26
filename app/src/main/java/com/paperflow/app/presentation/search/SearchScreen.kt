package com.paperflow.app.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.*
import com.paperflow.app.presentation.components.AnimatedListItem
import com.paperflow.app.presentation.components.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    initialQuery: String = "",
    onBack: () -> Unit,
    onResultClick: (SearchResult) -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val focus = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (initialQuery.isNotBlank()) viewModel.setQuery(initialQuery)
        focus.requestFocus()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                title = {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { viewModel.setQuery(it) },
                        placeholder = { Text("Search…", fontFamily = InterFamily, color = GrayLight) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().focusRequester(focus),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Amber,
                            unfocusedBorderColor = Border,
                        ),
                        trailingIcon = {
                            if (state.query.isNotBlank()) {
                                IconButton(onClick = { viewModel.clearQuery() }) {
                                    Icon(Icons.Default.Clear, "Clear", modifier = Modifier.size(18.dp))
                                }
                            }
                        },
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            if (state.query.isBlank()) {
                // Recent searches
                if (state.recentQueries.isNotEmpty()) {
                    item { SectionLabel("Recent Searches") }
                    itemsIndexed(state.recentQueries) { _, q ->
                        RecentQueryRow(
                            query = q,
                            onClick = { viewModel.useRecentQuery(q) },
                            onRemove = { viewModel.removeRecent(q) },
                        )
                    }
                } else {
                    item {
                        EmptyState(
                            icon = { Icon(Icons.Outlined.Search, null, Modifier.size(64.dp), tint = GrayLight) },
                            title = "Search anything",
                            subtitle = "Search across documents, notes, folders and OCR text",
                            modifier = Modifier.fillParentMaxHeight(0.5f),
                        )
                    }
                }
            } else if (state.isSearching) {
                items(5) { i ->
                    AnimatedListItem(index = i) {
                        SearchResultSkeleton()
                    }
                }
            } else if (state.results.isEmpty()) {
                item {
                    EmptyState(
                        icon = { Icon(Icons.Outlined.SearchOff, null, Modifier.size(64.dp), tint = GrayLight) },
                        title = "No results",
                        subtitle = "Try different keywords or check spelling",
                        modifier = Modifier.fillParentMaxHeight(0.5f),
                    )
                }
            } else {
                item { SectionLabel("${state.results.size} result${if (state.results.size != 1) "s" else ""} for \"${state.query}\"") }
                itemsIndexed(state.results, key = { i, r -> "${r.documentId}-${r.pageId}-$i" }) { index, result ->
                    AnimatedListItem(index = index) {
                        SearchResultRow(result = result, onClick = { onResultClick(result) })
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = GrayLight,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun RecentQueryRow(query: String, onClick: () -> Unit, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(Icons.Default.History, null, tint = GrayLight, modifier = Modifier.size(18.dp))
        Text(query, modifier = Modifier.weight(1f), fontFamily = InterFamily)
        IconButton(onClick = onRemove, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, "Remove", tint = GrayLight, modifier = Modifier.size(14.dp))
        }
    }
    HorizontalDivider(color = Border.copy(alpha = 0.5f), modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun SearchResultRow(result: SearchResult, onClick: () -> Unit) {
    val (icon, iconTint) = when (result.resultType) {
        SearchResultType.PAGE_TEXT -> Pair(Icons.Default.TextSnippet, Info)
        SearchResultType.DOCUMENT -> Pair(Icons.Default.PictureAsPdf, PdfBadge)
        SearchResultType.NOTE -> Pair(Icons.Default.EditNote, NoteBadge)
        SearchResultType.FOLDER -> Pair(Icons.Default.Folder, Warning)
        SearchResultType.ANNOTATION -> Pair(Icons.Default.Edit, OcrBadge)
    }
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(40.dp).let {
                it.clip(RoundedCornerShape(10.dp)).background(iconTint.copy(alpha = 0.1f))
            },
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(result.documentName, fontWeight = FontWeight.SemiBold, fontFamily = InterFamily, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (result.matchSnippet.isNotBlank()) {
                Text(result.matchSnippet, style = MaterialTheme.typography.bodySmall, color = Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = GrayLight, modifier = Modifier.size(18.dp))
    }
    HorizontalDivider(color = Border.copy(alpha = 0.4f), modifier = Modifier.padding(horizontal = 16.dp))
}

@Composable
private fun SearchResultSkeleton() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        com.paperflow.app.presentation.components.SkeletonBox(Modifier.size(40.dp), RoundedCornerShape(10.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            com.paperflow.app.presentation.components.SkeletonBox(Modifier.fillMaxWidth(0.6f).height(14.dp))
            com.paperflow.app.presentation.components.SkeletonBox(Modifier.fillMaxWidth(0.9f).height(10.dp))
        }
    }
}
