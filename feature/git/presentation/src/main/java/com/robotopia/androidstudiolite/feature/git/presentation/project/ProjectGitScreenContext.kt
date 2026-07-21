package com.robotopia.androidstudiolite.feature.git.presentation.project

import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import java.io.File
import kotlinx.coroutines.CoroutineScope

class ProjectGitScreenContext(
    val updateState: (ProjectGitUiState.() -> ProjectGitUiState) -> Unit,
    val gitService: GitService,
    val gitHubClient: GitHubClient,
    val authSession: AuthSession,
    val projectRoot: File,
    val onBack: () -> Unit,
    val onConnectAccount: () -> Unit,
    val onOpenFile: (relativePath: String) -> Unit,
    val openUrl: (String) -> Unit,
    val scope: CoroutineScope,
)
