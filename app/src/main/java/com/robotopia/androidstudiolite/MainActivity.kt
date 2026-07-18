package com.robotopia.androidstudiolite

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.feature.settings.api.ThemePreferences
import com.robotopia.androidstudiolite.integration.navigation.IdeNavHost
import com.robotopia.androidstudiolite.ui.theme.AndroidStudioLiteTheme
import com.robotopia.androidstudiolite.ui.theme.usesLightSystemBars
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val themePreferences: ThemePreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appTheme by themePreferences.theme.collectAsStateWithLifecycle()
            SideEffect {
                val barStyle = if (appTheme.usesLightSystemBars) {
                    SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                } else {
                    SystemBarStyle.dark(Color.TRANSPARENT)
                }
                enableEdgeToEdge(
                    statusBarStyle = barStyle,
                    navigationBarStyle = barStyle,
                )
            }
            AndroidStudioLiteTheme(appTheme = appTheme) {
                IdeNavHost()
            }
        }
    }
}
