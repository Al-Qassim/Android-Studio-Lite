package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import com.robotopia.androidstudiolite.feature.projects.presentation.create.CreateProjectScreen
import com.robotopia.androidstudiolite.feature.projects.presentation.list.ProjectsListScreen

class DefaultProjectsScreens(
    private val projectService: ProjectService,
) : ProjectsScreens {

    @Composable
    override fun NavHost(
        onOpenProject: (projectId: ProjectId) -> Unit,
        onRunProject: (projectId: ProjectId) -> Unit,
    ) {
        ProjectsNavHost(
            projectService = projectService,
            onOpenProject = onOpenProject,
            onRunProject = onRunProject,
        )
    }

    @Composable
    override fun ProjectsList(
        onOpenProject: (projectId: ProjectId) -> Unit,
        onRunProject: (projectId: ProjectId) -> Unit,
        onCreateProject: () -> Unit,
    ) {
        ProjectsListScreen(
            projectService = projectService,
            onOpenProject = onOpenProject,
            onRunProject = onRunProject,
            onCreateProject = onCreateProject,
        )
    }

    @Composable
    override fun CreateProject(
        onCreated: (projectId: ProjectId) -> Unit,
        onCancel: () -> Unit,
    ) {
        CreateProjectScreen(
            projectService = projectService,
            onCreated = onCreated,
            onCancel = onCancel,
        )
    }
}

private sealed interface ProjectsRoute {
    data object List : ProjectsRoute
    data object Create : ProjectsRoute
}

/**
 * Projects-owned sub-navigation: list ↔ create.
 * Cross-feature exits: [onOpenProject], [onRunProject].
 */
@Composable
private fun ProjectsNavHost(
    projectService: ProjectService,
    onOpenProject: (projectId: ProjectId) -> Unit,
    onRunProject: (projectId: ProjectId) -> Unit,
) {
    var route by remember { mutableStateOf<ProjectsRoute>(ProjectsRoute.List) }

    when (route) {
        ProjectsRoute.List -> {
            ProjectsListScreen(
                projectService = projectService,
                onOpenProject = onOpenProject,
                onRunProject = onRunProject,
                onCreateProject = { route = ProjectsRoute.Create },
            )
        }

        ProjectsRoute.Create -> {
            CreateProjectScreen(
                projectService = projectService,
                onCreated = { route = ProjectsRoute.List },
                onCancel = { route = ProjectsRoute.List },
            )
        }
    }
}
