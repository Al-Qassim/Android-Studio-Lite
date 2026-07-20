package com.robotopia.androidstudiolite.feature.git.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.projects.model.Project
import java.io.File

interface GitScreens {
    @Composable
    fun CloneProject(
        onCreated: (Project) -> Unit,
        onCancel: () -> Unit,
    )

    @Composable
    fun ProjectGit(
        projectRoot: File,
        projectName: String,
        onBack: () -> Unit,
    )
}
