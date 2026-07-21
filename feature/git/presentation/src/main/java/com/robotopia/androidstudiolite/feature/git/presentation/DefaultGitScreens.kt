package com.robotopia.androidstudiolite.feature.git.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.auth.api.AuthScreens
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitScreens
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectScreen
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreen
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.Project
import java.io.File

class DefaultGitScreens(
    private val gitService: GitService,
    private val gitHubClient: GitHubClient,
    private val projectService: ProjectService,
    private val authSession: AuthSession,
    private val authScreens: AuthScreens,
) : GitScreens {

    @Composable
    override fun CloneProject(
        onCreated: (Project) -> Unit,
        onCancel: () -> Unit,
    ) {
        CloneProjectScreen(
            gitService = gitService,
            projectService = projectService,
            authSession = authSession,
            onCreated = onCreated,
            onCancel = onCancel,
        )
    }

    @Composable
    override fun ProjectGit(
        projectRoot: File,
        projectName: String,
        onBack: () -> Unit,
        onOpenFile: (relativePath: String) -> Unit,
    ) {
        var showConnect by remember { mutableStateOf(false) }
        if (showConnect) {
            authScreens.ConnectAccount(
                onFinished = { showConnect = false },
                onCancel = { showConnect = false },
            )
        } else {
            ProjectGitScreen(
                gitService = gitService,
                gitHubClient = gitHubClient,
                authSession = authSession,
                projectRoot = projectRoot,
                projectName = projectName,
                onBack = onBack,
                onConnectAccount = { showConnect = true },
                onOpenFile = onOpenFile,
            )
        }
    }
}
