package com.robotopia.androidstudiolite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.integration.navigation.IdeNavHost
import com.robotopia.androidstudiolite.ui.theme.AndroidStudioLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val scrim = Colors.Canvas.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrim),
            navigationBarStyle = SystemBarStyle.dark(scrim),
        )
        setContent {
            AndroidStudioLiteTheme(darkTheme = true, dynamicColor = false) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Colors.Canvas),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.systemBars),
                    ) {
                        IdeNavHost()
                    }
                }
            }
        }
    }
}
