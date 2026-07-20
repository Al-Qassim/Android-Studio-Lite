package com.robotopia.androidstudiolite.feature.git.presentation

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitScreens
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectScreen
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreen
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.Project
import java.io.File

class DefaultGitScreens(
    private val gitService: GitService,
    private val projectService: ProjectService,
    private val authSession: AuthSession,
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
    ) {
        ProjectGitScreen(
            gitService = gitService,
            authSession = authSession,
            projectRoot = projectRoot,
            projectName = projectName,
            onBack = onBack,
        )
    }
}
