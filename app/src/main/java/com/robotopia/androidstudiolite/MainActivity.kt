package com.robotopia.androidstudiolite

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.robotopia.androidstudiolite.integration.navigation.IdeNavHost
import com.robotopia.androidstudiolite.ui.theme.AndroidStudioLiteTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
        )
        setContent {
            AndroidStudioLiteTheme(darkTheme = true, dynamicColor = false) {
                IdeNavHost()
            }
        }
    }
}
