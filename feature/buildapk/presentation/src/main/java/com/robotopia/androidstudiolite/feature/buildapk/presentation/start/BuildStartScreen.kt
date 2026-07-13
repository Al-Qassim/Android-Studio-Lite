package com.robotopia.androidstudiolite.feature.buildapk.presentation.start

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

@Composable
internal fun BuildStartScreen(
    projectName: String,
    packageName: String,
    signedIn: Boolean,
    providerDisplayName: String,
    onBackClick: () -> Unit,
    onStartBuild: suspend () -> Unit,
    onConnectAccountClick: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var starting by remember { mutableStateOf(false) }

    BuildStartContent(
        projectName = projectName,
        packageName = packageName,
        starting = starting,
        signedIn = signedIn,
        providerDisplayName = providerDisplayName,
        onBackClick = onBackClick,
        onStartBuildClick = {
            if (starting) return@BuildStartContent
            starting = true
            scope.launch {
                try {
                    onStartBuild()
                } finally {
                    starting = false
                }
            }
        },
        onConnectAccountClick = onConnectAccountClick,
    )
}
