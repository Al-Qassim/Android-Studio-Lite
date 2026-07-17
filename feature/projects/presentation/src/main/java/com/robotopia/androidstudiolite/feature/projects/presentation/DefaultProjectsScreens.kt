package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import com.robotopia.androidstudiolite.feature.projects.presentation.create.CreateProjectScreen
import com.robotopia.androidstudiolite.feature.projects.presentation.list.ProjectsListScreen

class DefaultProjectsScreens(
    private val projectService: ProjectService,
) : ProjectsScreens {

    @Composable
    override fun NavHost(
        onOpenProject: (Project) -> Unit,
        onRunProject: (Project) -> Unit,
        onOpenSettings: () -> Unit,
    ) {
        ProjectsNavHost(
            projectService = projectService,
            onOpenProject = onOpenProject,
            onRunProject = onRunProject,
            onOpenSettings = onOpenSettings,
        )
    }

    @Composable
    override fun ProjectsList(
        onOpenProject: (Project) -> Unit,
        onRunProject: (Project) -> Unit,
        onCreateProject: () -> Unit,
        onOpenSettings: () -> Unit,
    ) {
        ProjectsListScreen(
            projectService = projectService,
            onOpenProject = onOpenProject,
            onRunProject = onRunProject,
            onCreateProject = onCreateProject,
            onOpenSettings = onOpenSettings,
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

private enum class ProjectsRoute {
    List,
    Create,
}

/**
 * Projects-owned sub-navigation: list ↔ create.
 * Cross-feature exits: [onOpenProject], [onRunProject], [onOpenSettings].
 */
@Composable
private fun ProjectsNavHost(
    projectService: ProjectService,
    onOpenProject: (Project) -> Unit,
    onRunProject: (Project) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var route by rememberSaveable { mutableStateOf(ProjectsRoute.List) }

    when (route) {
        ProjectsRoute.List -> {
            ProjectsListScreen(
                projectService = projectService,
                onOpenProject = onOpenProject,
                onRunProject = onRunProject,
                onCreateProject = { route = ProjectsRoute.Create },
                onOpenSettings = onOpenSettings,
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
