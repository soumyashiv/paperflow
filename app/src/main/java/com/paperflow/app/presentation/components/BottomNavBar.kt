package com.paperflow.app.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.paperflow.app.core.navigation.Routes
import com.paperflow.app.core.theme.InterFamily

// Design tokens
private val NavBarBackground = Color(0xFFFFFCF7) // Off-white / cream
private val ActiveTabBackground = Color(0xFFFFF2D4) // Soft warm yellow
private val InactiveIconColor = Color.Black.copy(alpha = 0.9f)
private val ActiveIconColor = Color.Black

private data class NavDestination(
    val label: String,
    val icon: ImageVector,
    val route: String
)

private val leftNavItems = listOf(
    NavDestination("Home", Icons.Outlined.Home, Routes.Home.route),
    NavDestination("Workspace", Icons.Outlined.Layers, Routes.Workspace.route)
)

private val rightNavItems = listOf(
    NavDestination("Notes", Icons.Outlined.TextSnippet, Routes.Notes.route),
    NavDestination("Settings", Icons.Outlined.Settings, Routes.Settings.route)
)

@Composable
fun BottomNavBar(
    navController: NavController,
    onScanClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating pill bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(76.dp),
            shape = RoundedCornerShape(38.dp),
            color = NavBarBackground,
            shadowElevation = 12.dp,
            tonalElevation = 0.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    leftNavItems.forEach { dest ->
                        NavigationItem(
                            item = dest,
                            selected = currentRoute == dest.route,
                            onClick = { navigateTo(navController, dest.route) }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(80.dp)) // Space for FAB

                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    rightNavItems.forEach { dest ->
                        NavigationItem(
                            item = dest,
                            selected = currentRoute == dest.route,
                            onClick = { navigateTo(navController, dest.route) }
                        )
                    }
                }
            }
        }

        // Center FAB
        FloatingScanButton(
            onClick = onScanClick,
            modifier = Modifier.offset(y = (-16).dp)
        )
    }
}

private fun navigateTo(navController: NavController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(navController.graph.startDestinationId) { saveState = true }
    }
}

@Composable
private fun NavigationItem(
    item: NavDestination,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Motion parameters
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "nav_scale"
    )
    val bgColor by animateColorAsState(
        targetValue = if (selected) ActiveTabBackground else Color.Transparent,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "nav_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) ActiveIconColor else InactiveIconColor,
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "nav_content_color"
    )

    Box(
        modifier = Modifier
            .graphicsLayer { 
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(32.dp))
            .background(bgColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Custom scale animation provides interaction feedback
                role = Role.Tab,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
                tint = contentColor,
                modifier = Modifier.size(26.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = item.label,
                fontSize = 11.sp,
                fontFamily = InterFamily,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

@Composable
private fun FloatingScanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, 
            stiffness = Spring.StiffnessLow
        ),
        label = "fab_scale"
    )
    
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 16.dp,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "fab_elevation"
    )

    Box(
        modifier = modifier
            .graphicsLayer { 
                scaleX = scale
                scaleY = scale
            }
            .size(88.dp) // Total size including halo
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                spotColor = Color.Black.copy(alpha = 0.25f),
                ambientColor = Color.Black.copy(alpha = 0.1f)
            )
            .background(NavBarBackground, CircleShape) // The subtle white halo
            .padding(8.dp) // Halo thickness
            .clip(CircleShape)
            .background(Color.Black)
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = Color.White),
                role = Role.Button,
                onClick = onClick
            )
            .padding(16.dp), // Padding inside the black circle
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.FilterCenterFocus,
            contentDescription = "Scan Document",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}
