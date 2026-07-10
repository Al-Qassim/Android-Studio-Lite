package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens

/** Scaffold stub — real UI lands in #7. */
class StubProjectsScreens : ProjectsScreens {
    @Composable
    override fun ProjectsList(
        onOpenProject: (projectId: String) -> Unit,
        onCreateProject: () -> Unit,
    ) {
        Text("Projects (stub)")
    }

    @Composable
    override fun CreateProject(
        onCreated: (projectId: String) -> Unit,
        onCancel: () -> Unit,
    ) {
        Text("Create project (stub)")
    }
}
