package com.paperflow.app.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.*

// ─── Document Type Badge ──────────────────────────────────────────────────────
@Composable
fun TypeBadge(type: DocumentType, modifier: Modifier = Modifier) {
    val (bg, text) = when (type) {
        DocumentType.PDF -> PdfBadge to "PDF"
        DocumentType.JPG -> ImgBadge to "IMG"
        DocumentType.PNG -> ImgBadge to "PNG"
        DocumentType.NOTE -> NoteBadge to "NOTE"
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bg)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = InterFamily,
        )
    }
}

// ─── Pressable Card ───────────────────────────────────────────────────────────
/**
 * Card that scales down slightly on press — motion design micro-interaction.
 * Press scale: 0.97, spring stiffness: 400, damping: 30 (Premium archetype).
 */
@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(16.dp),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 2.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = spring(stiffness = 400f, dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_press_scale",
    )

    Card(
        onClick = onClick,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        enabled = enabled,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        interactionSource = interactionSource,
    ) {
        Column(content = content)
    }
}

// ─── Skeleton Loader ──────────────────────────────────────────────────────────
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
) {
    val shimmerAlpha by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer_alpha",
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = shimmerAlpha)),
    )
}

// ─── Empty State ──────────────────────────────────────────────────────────────
@Composable
fun EmptyState(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "empty_state_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "empty_state_offset"
    )

    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.graphicsLayer { translationY = offsetY }) {
            icon()
        }
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        action?.let { Spacer(Modifier.height(24.dp)); it() }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
        action?.invoke()
    }
}

// ─── Document Thumbnail ───────────────────────────────────────────────────────
@Composable
fun DocumentThumbnail(
    thumbnailPath: String?,
    type: DocumentType,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    if (thumbnailPath != null) {
        AsyncImage(
            model = thumbnailPath,
            contentDescription = null,
            modifier = modifier.clip(RoundedCornerShape(8.dp)),
            contentScale = contentScale,
        )
    } else {
        // Placeholder based on type
        val bg = when (type) {
            DocumentType.PDF -> PdfBadge.copy(alpha = 0.1f)
            DocumentType.NOTE -> NoteBadge.copy(alpha = 0.1f)
            else -> ImgBadge.copy(alpha = 0.1f)
        }
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            TypeBadge(type)
        }
    }
}

@Composable
fun PaperFlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search documents, folders, notes…",
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearch: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val elevation by animateDpAsState(
        targetValue = if (isFocused) 8.dp else 2.dp,
        animationSpec = tween(300),
        label = "search_elevation"
    )

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation, CircleShape, spotColor = Color.Black.copy(alpha = 0.05f), ambientColor = Color.Transparent),
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        trailingIcon = trailingIcon,
        singleLine = true,
        shape = CircleShape,
        interactionSource = interactionSource,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Amber,
            unfocusedBorderColor = Border.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        ),
    )
}

// ─── Amber FAB ────────────────────────────────────────────────────────────────
@Composable
fun AmberFAB(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "fab_scale",
    )
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 12.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "fab_elevation"
    )

    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        containerColor = Amber,
        contentColor = NearBlack,
        interactionSource = interactionSource,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = elevation, pressedElevation = elevation),
    ) {
        icon()
    }
}

// ─── Animated List Entrance ───────────────────────────────────────────────────
/**
 * Staggered entrance animation for list items.
 * Delay = index * 30ms (motion design token: stagger 30ms per item).
 */
@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(index * 30L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 4 },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow,
            ),
        ),
        modifier = modifier,
    ) {
        content()
    }
}

// ─── Filter Chip Row ──────────────────────────────────────────────────────────
@Composable
fun FilterChipRow(
    chips: List<String>,
    selectedChip: String,
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            val selected = chip == selectedChip
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            
            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.95f else 1f,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "chip_scale"
            )
            val bgColor by animateColorAsState(
                targetValue = if (selected) AmberLight else MaterialTheme.colorScheme.surface,
                animationSpec = tween(250),
                label = "chip_bg"
            )
            val textColor by animateColorAsState(
                targetValue = if (selected) NearBlack else MaterialTheme.colorScheme.onSurfaceVariant,
                animationSpec = tween(250),
                label = "chip_text"
            )
            val borderColor by animateColorAsState(
                targetValue = if (selected) Amber else Border.copy(alpha = 0.5f),
                animationSpec = tween(250),
                label = "chip_border"
            )

            Box(
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = interactionSource,
                        indication = androidx.compose.material3.ripple(),
                        onClick = { onChipSelected(chip) }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    chip,
                    fontFamily = InterFamily,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    color = textColor,
                    fontSize = 13.sp
                )
            }
        }
    }
}
