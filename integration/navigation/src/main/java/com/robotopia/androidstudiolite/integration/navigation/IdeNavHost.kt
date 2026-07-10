package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * IDE navigation root. Full NavHost graph lands in #11.
 */
@Composable
fun IdeNavHost() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("IDE scaffold ready")
    }
}
