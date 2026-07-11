package com.robotopia.androidstudiolite.feature.buildapk.presentation.start

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.buildapk.api.GitHubTokenAuth
import kotlinx.coroutines.launch

@Composable
internal fun BuildStartScreen(
    projectName: String,
    packageName: String,
    tokenAuth: GitHubTokenAuth,
    onBackClick: () -> Unit,
    onStartBuild: suspend () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var starting by remember { mutableStateOf(false) }
    var tokenDraft by remember { mutableStateOf("") }
    var hasSavedToken by remember { mutableStateOf(tokenAuth.hasToken()) }

    BuildStartContent(
        projectName = projectName,
        packageName = packageName,
        tokenDraft = tokenDraft,
        hasSavedToken = hasSavedToken,
        starting = starting,
        onTokenDraftChange = { tokenDraft = it },
        onSaveToken = {
            tokenAuth.saveToken(tokenDraft)
            tokenDraft = ""
            hasSavedToken = tokenAuth.hasToken()
        },
        onClearToken = {
            tokenAuth.clearToken()
            tokenDraft = ""
            hasSavedToken = false
        },
        onBackClick = onBackClick,
        onStartBuildClick = {
            if (starting || !hasSavedToken) return@BuildStartContent
            starting = true
            scope.launch {
                try {
                    onStartBuild()
                } finally {
                    starting = false
                }
            }
        },
    )
}
