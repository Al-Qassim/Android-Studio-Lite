package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.designsystem.animation.navFade
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.git.api.GitScreens
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.presentation.create.CreateProjectScreen
import com.robotopia.androidstudiolite.feature.projects.presentation.list.ProjectsListScreen

class DefaultProjectsScreens(
    private val projectService: ProjectService,
    private val buildScreens: BuildScreens,
    private val gitScreens: GitScreens,
) : ProjectsScreens {

    @Composable
    override fun NavHost(
        onOpenProject: (Project) -> Unit,
        onRunProject: (Project) -> Unit,
        onOpenSettings: () -> Unit,
    ) {
        ProjectsNavHost(
            projectService = projectService,
            buildScreens = buildScreens,
            gitScreens = gitScreens,
            onOpenProject = onOpenProject,
            onRunProject = onRunProject,
            onOpenSettings = onOpenSettings,
        )
    }
}

private enum class ProjectsRoute {
    List,
    Create,
    Clone,
    BuildHistory,
}

@Composable
private fun ProjectsNavHost(
    projectService: ProjectService,
    buildScreens: BuildScreens,
    gitScreens: GitScreens,
    onOpenProject: (Project) -> Unit,
    onRunProject: (Project) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var route by rememberSaveable { mutableStateOf(ProjectsRoute.List) }
    var historyProjectId by rememberSaveable { mutableStateOf("") }

    AnimatedContent(
        targetState = route,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = { navFade() },
        label = "projectsNav",
    ) { current ->
        when (current) {
            ProjectsRoute.List -> {
                ProjectsListScreen(
                    projectService = projectService,
                    onOpenProject = onOpenProject,
                    onRunProject = onRunProject,
                    onCreateProject = { route = ProjectsRoute.Create },
                    onCloneProject = { route = ProjectsRoute.Clone },
                    onOpenSettings = onOpenSettings,
                    onBuildHistory = { project ->
                        historyProjectId = project.id.value
                        route = ProjectsRoute.BuildHistory
                    },
                )
            }

            ProjectsRoute.Create -> {
                CreateProjectScreen(
                    projectService = projectService,
                    onCreated = { route = ProjectsRoute.List },
                    onCancel = { route = ProjectsRoute.List },
                )
            }

            ProjectsRoute.Clone -> {
                gitScreens.CloneProject(
                    onCreated = { project ->
                        route = ProjectsRoute.List
                        onOpenProject(project)
                    },
                    onCancel = { route = ProjectsRoute.List },
                )
            }

            ProjectsRoute.BuildHistory -> {
                buildScreens.History(
                    projectIdFilter = historyProjectId,
                    onDismiss = { route = ProjectsRoute.List },
                )
            }
        }
    }
}
