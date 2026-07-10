package com.robotopia.androidstudiolite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.integration.database.AslDatabase
import com.robotopia.androidstudiolite.integration.navigation.IdeNavHost
import com.robotopia.androidstudiolite.ui.theme.AndroidStudioLiteTheme
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val database: AslDatabase = get()
        setContent {
            AndroidStudioLiteTheme {
                LaunchedEffect(database) {
                    // Smoke: open Room (empty schema) so DI + DB boot is exercised.
                    database.openHelper.writableDatabase
                }
                Surface(modifier = Modifier.fillMaxSize()) {
                    IdeNavHost()
                }
            }
        }
    }
}
