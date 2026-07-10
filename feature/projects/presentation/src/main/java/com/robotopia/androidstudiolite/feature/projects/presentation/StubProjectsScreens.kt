package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

/** Scaffold stub — real UI lands in #7. */
class StubProjectsScreens : ProjectsScreens {
    @Composable
    override fun ProjectsList(
        onOpenProject: (projectId: ProjectId) -> Unit,
        onCreateProject: () -> Unit,
    ) {
        Text("Projects (stub)")
    }

    @Composable
    override fun CreateProject(
        onCreated: (projectId: ProjectId) -> Unit,
        onCancel: () -> Unit,
    ) {
        Text("Create project (stub)")
    }
}
