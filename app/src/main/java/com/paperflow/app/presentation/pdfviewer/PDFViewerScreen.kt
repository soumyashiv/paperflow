package com.paperflow.app.presentation.pdfviewer

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*
import com.paperflow.app.presentation.components.SkeletonBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PDFViewerScreen(
    documentId: Long,
    initialPage: Int = 0,
    onBack: () -> Unit,
    onAnnotate: (Int) -> Unit,
    onShare: () -> Unit,
    onPrint: () -> Unit,
    onConvert: () -> Unit,
    onDetails: () -> Unit,
    viewModel: PDFViewerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var showBrightnessSlider by remember { mutableStateOf(false) }
    var showMore by remember { mutableStateOf(false) }

    LaunchedEffect(documentId) {
        viewModel.loadDocument(documentId, initialPage)
    }

    // Sync pager with ViewModel page state
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { state.totalPages.coerceAtLeast(1) },
    )

    LaunchedEffect(pagerState.currentPage) {
        viewModel.goToPage(pagerState.currentPage)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A))) {
        // ── Page content ─────────────────────────────────────────────────
        if (state.viewMode == PDFViewMode.SINGLE) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().clickable { viewModel.toggleToolbar() },
                pageSpacing = 16.dp,
            ) { pageIndex ->
                PDFPageContent(
                    bitmap = state.pageBitmaps[pageIndex],
                    isLoading = state.isLoading,
                    zoomLevel = state.zoomLevel,
                    brightness = state.brightness,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        } else {
            // Scroll mode — lazy column of pages
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize().clickable { viewModel.toggleToolbar() },
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(state.totalPages) { pageIndex ->
                    PDFPageContent(
                        bitmap = state.pageBitmaps[pageIndex],
                        isLoading = false,
                        zoomLevel = state.zoomLevel,
                        brightness = state.brightness,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    )
                }
            }
        }

        // ── Error overlay ────────────────────────────────────────────────
        if (state.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, null, tint = Error, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(state.error!!, color = Color.White, fontFamily = InterFamily)
                }
            }
        }

        // ── Top toolbar ──────────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isToolbarVisible,
            enter = fadeIn(tween(200)) + slideInVertically(initialOffsetY = { -it }),
            exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { -it }),
            modifier = Modifier.align(Alignment.TopCenter),
        ) {
            Surface(
                color = NearBlack.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        text = state.document?.name ?: "",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontFamily = InterFamily,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    )
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            if (state.document?.isFavorite == true) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            "Favorite",
                            tint = if (state.document?.isFavorite == true) Amber else Color.White,
                        )
                    }
                    IconButton(onClick = onShare) { Icon(Icons.Outlined.Share, "Share", tint = Color.White) }
                    Box {
                        IconButton(onClick = { showMore = true }) { Icon(Icons.Default.MoreVert, "More", tint = Color.White) }
                        DropdownMenu(
                            expanded = showMore,
                            onDismissRequest = { showMore = false },
                        ) {
                            DropdownMenuItem(text = { Text("Annotate") }, onClick = { onAnnotate(pagerState.currentPage); showMore = false }, leadingIcon = { Icon(Icons.Default.Edit, null) })
                            DropdownMenuItem(text = { Text("Details") }, onClick = { onDetails(); showMore = false }, leadingIcon = { Icon(Icons.Default.Info, null) })
                            DropdownMenuItem(text = { Text("Print") }, onClick = { onPrint(); showMore = false }, leadingIcon = { Icon(Icons.Default.Print, null) })
                            DropdownMenuItem(text = { Text("Convert") }, onClick = { onConvert(); showMore = false }, leadingIcon = { Icon(Icons.Default.Transform, null) })
                        }
                    }
                }
            }
        }

        // ── Bottom toolbar ───────────────────────────────────────────────
        AnimatedVisibility(
            visible = state.isToolbarVisible,
            enter = fadeIn(tween(200)) + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut(tween(200)) + slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter),
        ) {
            Surface(
                color = NearBlack.copy(alpha = 0.95f),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.navigationBarsPadding()) {
                    // Brightness slider (collapsible)
                    AnimatedVisibility(visible = showBrightnessSlider) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Icon(Icons.Default.BrightnessLow, null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Slider(
                                value = state.brightness,
                                onValueChange = { viewModel.setBrightness(it) },
                                valueRange = 0.2f..1.5f,
                                modifier = Modifier.weight(1f),
                                colors = SliderDefaults.colors(thumbColor = Amber, activeTrackColor = Amber),
                            )
                            Icon(Icons.Default.BrightnessHigh, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Page navigation
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = {
                                if (pagerState.currentPage > 0) {
                                    val newPage = pagerState.currentPage - 1
                                    scope.launch { pagerState.animateScrollToPage(newPage) }
                                }
                            }) { Icon(Icons.Default.ChevronLeft, "Previous", tint = Color.White) }

                            Text(
                                "${state.currentPage + 1} / ${state.totalPages}",
                                color = Color.White,
                                fontFamily = InterFamily,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                            )

                            IconButton(onClick = {
                                if (pagerState.currentPage < state.totalPages - 1) {
                                    val newPage = pagerState.currentPage + 1
                                    scope.launch { pagerState.animateScrollToPage(newPage) }
                                }
                            }) { Icon(Icons.Default.ChevronRight, "Next", tint = Color.White) }
                        }

                        // Right tools
                        Row {
                            IconButton(onClick = { showBrightnessSlider = !showBrightnessSlider }) {
                                Icon(Icons.Default.Brightness6, "Brightness", tint = if (showBrightnessSlider) Amber else Color.White)
                            }
                            IconButton(onClick = { viewModel.setViewMode(if (state.viewMode == PDFViewMode.SINGLE) PDFViewMode.SCROLL else PDFViewMode.SINGLE) }) {
                                Icon(
                                    if (state.viewMode == PDFViewMode.SINGLE) Icons.Default.ViewDay else Icons.Default.ViewStream,
                                    "View mode", tint = Color.White,
                                )
                            }
                            IconButton(onClick = { onAnnotate(pagerState.currentPage) }) {
                                Icon(Icons.Default.Edit, "Annotate", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PDFPageContent(
    bitmap: Bitmap?,
    isLoading: Boolean,
    zoomLevel: Float,
    brightness: Float,
    modifier: Modifier = Modifier,
) {
    if (isLoading || bitmap == null) {
        SkeletonBox(modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = 400.dp))
        return
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .graphicsLayer(
                    scaleX = zoomLevel,
                    scaleY = zoomLevel,
                    alpha = brightness.coerceIn(0f, 1f),
                ),
            contentScale = ContentScale.FillWidth,
        )
    }
}

// Expose viewModelScope for use in composable (workaround for pager scroll)
private val PDFViewerViewModel.viewModelScope: CoroutineScope
    get() = viewModelScope
