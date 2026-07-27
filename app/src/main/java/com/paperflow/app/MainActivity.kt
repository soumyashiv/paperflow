package com.paperflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.paperflow.app.core.navigation.AppNavGraph
import com.paperflow.app.core.theme.AppTheme
import com.paperflow.app.core.theme.PaperFlowTheme
import com.paperflow.app.data.local.datastore.PreferencesDataStore
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var prefs: PreferencesDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeString by prefs.appTheme.collectAsState(initial = "LIGHT")
            val appTheme = when (themeString) {
                "DARK" -> AppTheme.DARK
                "AMOLED" -> AppTheme.AMOLED
                "DYNAMIC" -> AppTheme.DYNAMIC
                else -> AppTheme.LIGHT
            }
            PaperFlowTheme(appTheme = appTheme) {
                AppNavGraph()
            }
        }
    }
}
