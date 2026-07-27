package com.paperflow.app.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
// Kiwi style: rounded pill, green-tinted backgrounds, dark green text
@Composable
fun TypeBadge(type: DocumentType, modifier: Modifier = Modifier) {
    val (bg, label) = when (type) {
        DocumentType.PDF  -> PdfBadge  to "PDF"
        DocumentType.JPG  -> ImgBadge  to "IMG"
        DocumentType.PNG  -> ImgBadge  to "PNG"
        DocumentType.NOTE -> NoteBadge to "NOTE"
    }
    val textColor = when (type) {
        DocumentType.PDF  -> Color(0xFFB71C1C)
        DocumentType.NOTE -> KiwiDark
        else              -> Color(0xFF0D47A1)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(KiwiRadius.Pill))
            .background(bg)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text       = label,
            color      = textColor,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = InterFamily,
        )
    }
}

// ─── Pressable Card ───────────────────────────────────────────────────────────
// Kiwi style: 28dp radius, thin green border, soft elevation, spring press
@Composable
fun PressableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedCornerShape(KiwiRadius.LargeCard),
    containerColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = KiwiElevation.Card,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberKiwiPressScale(interactionSource)

    Card(
        onClick            = onClick,
        modifier           = modifier.graphicsLayer { scaleX = scale; scaleY = scale },
        enabled            = enabled,
        shape              = shape,
        colors             = CardDefaults.cardColors(containerColor = containerColor),
        elevation          = CardDefaults.cardElevation(defaultElevation = elevation),
        border             = BorderStroke(1.dp, Border),
        interactionSource  = interactionSource,
    ) {
        Column(content = content)
    }
}

// ─── Kiwi Primary Button ──────────────────────────────────────────────────────
// Gradient capsule, soft shadow, spring press scale
@Composable
fun KiwiPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue   = if (isPressed && enabled) 0.97f else 1f,
        animationSpec = KiwiCardPressSpring,
        label         = "kiwi_btn_scale",
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation     = if (isPressed) 2.dp else 8.dp,
                shape         = RoundedCornerShape(KiwiRadius.Button),
                spotColor     = KiwiAccent.copy(alpha = 0.35f),
                ambientColor  = KiwiPrimary.copy(alpha = 0.10f),
            )
            .clip(RoundedCornerShape(KiwiRadius.Button))
            .background(
                if (enabled) ButtonGradient
                else Brush.linearGradient(colors = listOf(GrayLight, GrayLight))
            )
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                enabled           = enabled,
                onClick           = onClick,
            )
            .padding(horizontal = 24.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment    = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            icon?.let {
                Icon(it, contentDescription = null, tint = KiwiDark, modifier = Modifier.size(20.dp))
            }
            Text(
                text       = text,
                color      = KiwiDark,
                fontFamily = InterFamily,
                fontWeight = FontWeight.Bold,
                fontSize   = 15.sp,
            )
        }
    }
}

// ─── Kiwi Outlined Button ─────────────────────────────────────────────────────
@Composable
fun KiwiOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(KiwiRadius.Button),
        border = BorderStroke(1.5.dp, KiwiPrimary),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = KiwiPrimary,
        ),
    ) {
        icon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
        }
        Text(text, fontFamily = InterFamily, fontWeight = FontWeight.SemiBold)
    }
}

// ─── Kiwi FAB ─────────────────────────────────────────────────────────────────
// Gradient fill, 36dp radius, bouncy spring press
@Composable
fun KiwiFAB(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
) {
    val interactionSource = remember { MutableInteractionSource() }
    val scale by rememberKiwiFabScale(interactionSource)
    val elev  by rememberKiwiPressElevation(interactionSource, defaultElevation = 12.dp, pressedElevation = 4.dp)

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(
                elevation    = elev,
                shape        = RoundedCornerShape(KiwiRadius.FAB),
                spotColor    = KiwiAccent.copy(alpha = 0.40f),
                ambientColor = KiwiPrimary.copy(alpha = 0.12f),
            )
            .clip(RoundedCornerShape(KiwiRadius.FAB))
            .background(FabGradient)
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick,
            )
            .padding(18.dp),
        contentAlignment = Alignment.Center,
    ) {
        icon()
    }
}

// Backward-compat alias used in existing screens
@Composable
fun AmberFAB(
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = "",
) = KiwiFAB(onClick, icon, modifier, contentDescription)

// ─── Skeleton Loader ──────────────────────────────────────────────────────────
// Kiwi: green-tinted shimmer
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(KiwiRadius.Small),
) {
    val shimmerAlpha by rememberInfiniteTransition(label = "shimmer").animateFloat(
        initialValue = 0.3f,
        targetValue  = 0.7f,
        animationSpec = KiwiShimmerSpec,
        label        = "shimmer_alpha",
    )
    Box(
        modifier = modifier
            .clip(shape)
            .background(KiwiDivider.copy(alpha = shimmerAlpha)),
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
    val infiniteTransition = rememberInfiniteTransition(label = "empty_float")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue  = 6f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "empty_float_y",
    )

    Column(
        modifier              = modifier.padding(40.dp),
        horizontalAlignment   = Alignment.CenterHorizontally,
        verticalArrangement   = Arrangement.Center,
    ) {
        Box(modifier = Modifier.graphicsLayer { translationY = offsetY }) {
            icon()
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color      = NearBlack,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text  = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Gray,
        )
        action?.let {
            Spacer(Modifier.height(28.dp))
            it()
        }
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
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically,
    ) {
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color      = NearBlack,
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
            model          = thumbnailPath,
            contentDescription = null,
            modifier       = modifier.clip(RoundedCornerShape(KiwiRadius.Card)),
            contentScale   = contentScale,
        )
    } else {
        val bg = when (type) {
            DocumentType.PDF  -> PdfBadge.copy(alpha = 0.15f)
            DocumentType.NOTE -> NoteBadge.copy(alpha = 0.15f)
            else              -> ImgBadge.copy(alpha = 0.15f)
        }
        val tint = when (type) {
            DocumentType.PDF  -> Color(0xFFB71C1C)
            DocumentType.NOTE -> KiwiDark
            else              -> Color(0xFF1565C0)
        }
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(KiwiRadius.Card))
                .background(bg),
            contentAlignment = Alignment.Center,
        ) {
            TypeBadge(type)
        }
    }
}

// ─── Kiwi Search Bar ──────────────────────────────────────────────────────────
// Large pill, floating, animated focus expansion, kiwi green focus border
@Composable
fun PaperFlowSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search documents, notes…",
    modifier: Modifier = Modifier,
    trailingContent: (@Composable () -> Unit)? = null,
    onSearch: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val elevation by animateDpAsState(
        targetValue   = if (isFocused) 12.dp else 3.dp,
        animationSpec = tween(300),
        label         = "search_elevation",
    )
    val borderColor by animateColorAsState(
        targetValue   = if (isFocused) KiwiPrimary else Border,
        animationSpec = tween(250),
        label         = "search_border",
    )

    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        modifier      = modifier
            .fillMaxWidth()
            .shadow(
                elevation    = elevation,
                shape        = RoundedCornerShape(KiwiRadius.SearchBar),
                spotColor    = KiwiPrimary.copy(alpha = 0.08f),
                ambientColor = Color.Transparent,
            ),
        placeholder = {
            Text(
                placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayLight,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = "Search",
                tint        = if (isFocused) KiwiPrimary else GrayLight,
                modifier    = Modifier.size(22.dp),
            )
        },
        trailingIcon   = trailingContent,
        singleLine     = true,
        shape          = RoundedCornerShape(KiwiRadius.SearchBar),
        interactionSource = interactionSource,
        colors         = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = KiwiPrimary,
            unfocusedBorderColor = Border,
            focusedContainerColor   = KiwiSurface,
            unfocusedContainerColor = KiwiSurface,
            cursorColor          = KiwiPrimary,
        ),
    )
}

// ─── Animated List Item ───────────────────────────────────────────────────────
@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(kiwiListStaggerMs(index))
        visible = true
    }
    AnimatedVisibility(
        visible  = visible,
        enter    = fadeIn(tween(300)) + slideInVertically(
            initialOffsetY = { it / 5 },
            animationSpec  = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness    = Spring.StiffnessMediumLow,
            ),
        ),
        modifier = modifier,
    ) {
        content()
    }
}

// ─── Filter Chip Row (Kiwi Style) ─────────────────────────────────────────────
// Selected: #EAF9C8 bg + KiwiDark text; Unselected: white + thin border; spring animation
@Composable
fun FilterChipRow(
    chips: List<String>,
    selectedChip: String,
    onChipSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier              = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        chips.forEach { chip ->
            KiwiFilterChip(
                label      = chip,
                selected   = chip == selectedChip,
                onClick    = { onChipSelected(chip) },
            )
        }
    }
}

@Composable
fun KiwiFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue   = if (isPressed) 0.95f else 1f,
        animationSpec = KiwiChipSpring,
        label         = "chip_scale",
    )
    val bgColor by animateColorAsState(
        targetValue   = if (selected) KiwiLight else KiwiSurface,
        animationSpec = tween(200),
        label         = "chip_bg",
    )
    val textColor by animateColorAsState(
        targetValue   = if (selected) KiwiDark else Gray,
        animationSpec = tween(200),
        label         = "chip_text",
    )
    val borderColor by animateColorAsState(
        targetValue   = if (selected) KiwiPrimary else Border,
        animationSpec = tween(200),
        label         = "chip_border",
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(KiwiRadius.Pill))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(KiwiRadius.Pill))
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick,
            )
            .padding(horizontal = 18.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text       = label,
            fontFamily = InterFamily,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color      = textColor,
            fontSize   = 13.sp,
        )
    }
}



