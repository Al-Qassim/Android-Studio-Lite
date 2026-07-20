package com.robotopia.androidstudiolite.feature.git.presentation.project

import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitService
import java.io.File
import kotlinx.coroutines.CoroutineScope

class ProjectGitScreenContext(
    val updateState: (ProjectGitUiState.() -> ProjectGitUiState) -> Unit,
    val gitService: GitService,
    val authSession: AuthSession,
    val projectRoot: File,
    val onBack: () -> Unit,
    val scope: CoroutineScope,
)
