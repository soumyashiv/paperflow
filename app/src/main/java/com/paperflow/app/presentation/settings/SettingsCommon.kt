package com.paperflow.app.presentation.settings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.paperflow.app.core.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSubScreen(title: String, onBack: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold, fontFamily = InterFamily) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { pv ->
        Column(modifier = Modifier.fillMaxSize().padding(pv).verticalScroll(rememberScrollState()), content = content)
    }
}

@Composable
fun SettingToggle(label: String, subtitle: String = "", checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, fontFamily = InterFamily, fontWeight = FontWeight.Medium)
            if (subtitle.isNotBlank()) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Gray)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = NearBlack, checkedTrackColor = Amber),
        )
    }
}

@Composable
fun SettingSectionLabel(text: String) = Text(
    text, style = MaterialTheme.typography.labelMedium, color = GrayLight,
    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp),
)
