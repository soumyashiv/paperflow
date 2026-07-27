package com.paperflow.app.presentation.aichat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.paperflow.app.core.theme.*
import com.paperflow.app.domain.model.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    documentId: Long?,
    onBack: () -> Unit,
    viewModel: AIChatViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) { viewModel.init(documentId) }
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(listOf(KiwiAccent, KiwiPrimary))), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, null, tint = NearBlack, modifier = Modifier.size(20.dp))
                        }
                        Column {
                            Text("Ask AI", fontWeight = FontWeight.Bold, fontFamily = InterFamily, fontSize = 16.sp)
                            Text(
                                if (state.mode == AIMode.ONLINE) "● Online" else "○ Offline",
                                fontSize = 11.sp,
                                color = if (state.mode == AIMode.ONLINE) Color(0xFF4CAF50) else GrayLight,
                                fontFamily = InterFamily,
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearConversation() }) { Icon(Icons.Outlined.DeleteSweep, "Clear") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KiwiBg),
            )
        },
        bottomBar = {
            Surface(color = KiwiSurface, tonalElevation = 0.dp, shadowElevation = 8.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = state.inputText,
                        onValueChange = { viewModel.setInput(it) },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ask about your documents…", fontFamily = InterFamily, color = GrayLight) },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = KiwiPrimary, unfocusedBorderColor = Border),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.sendMessage() }),
                    )
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(if (state.inputText.isNotBlank() && !state.isThinking) KiwiPrimary else GrayLight).clickable(enabled = state.inputText.isNotBlank() && !state.isThinking) { viewModel.sendMessage() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Default.Send, "Send", tint = if (state.inputText.isNotBlank()) NearBlack else Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Context badge
            state.contextDocumentName?.let { name ->
                item {
                    Surface(color = KiwiLight, shape = RoundedCornerShape(KiwiRadius.Card), modifier = Modifier.fillMaxWidth()) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PictureAsPdf, null, tint = KiwiPrimary, modifier = Modifier.size(16.dp))
                            Text("Context: $name", fontSize = 12.sp, fontFamily = InterFamily, color = NearBlack)
                        }
                    }
                }
            }

            itemsIndexed(state.messages, key = { _, m -> m.id }) { index, message ->
                AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })) {
                    ChatBubble(message = message)
                }
            }

            // Thinking indicator
            if (state.isThinking) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(KiwiAccent, KiwiPrimary))), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.SmartToy, null, tint = NearBlack, modifier = Modifier.size(16.dp))
                        }
                        ThinkingDots()
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    if (message.isUser) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Surface(
                color = NearBlack,
                shape = RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp),
                modifier = Modifier.widthIn(max = 280.dp),
            ) {
                Text(message.text, color = Color.White, fontFamily = InterFamily, modifier = Modifier.padding(12.dp, 10.dp), lineHeight = 22.sp)
            }
        }
    } else {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
            Box(Modifier.size(32.dp).clip(CircleShape).background(Brush.linearGradient(listOf(KiwiAccent, KiwiPrimary))), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.SmartToy, null, tint = NearBlack, modifier = Modifier.size(16.dp))
            }
            Column(modifier = Modifier.widthIn(max = 280.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp),
                ) {
                    Text(message.text, fontFamily = InterFamily, modifier = Modifier.padding(12.dp, 10.dp), lineHeight = 22.sp)
                }
                if (message.sourceDocuments.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Source: ${message.sourceDocuments.joinToString(", ")}",
                        fontSize = 10.sp, color = GrayLight, fontFamily = InterFamily,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ThinkingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val dot1 by infiniteTransition.animateFloat(0.3f, 1f, animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse, initialStartOffset = StartOffset(0)), label = "d1")
    val dot2 by infiniteTransition.animateFloat(0.3f, 1f, animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse, initialStartOffset = StartOffset(200)), label = "d2")
    val dot3 by infiniteTransition.animateFloat(0.3f, 1f, animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse, initialStartOffset = StartOffset(400)), label = "d3")
    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp)) {
        Row(Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf(dot1, dot2, dot3).forEach { alpha ->
                Box(Modifier.size(8.dp).clip(CircleShape).background(Gray.copy(alpha = alpha)))
            }
        }
    }
}
