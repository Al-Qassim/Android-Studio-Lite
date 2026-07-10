package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * IDE root: switches between feature sub-navigations.
 * Feature-internal routes (e.g. projects list ↔ create) stay inside each feature’s [NavHost].
 * Full cross-feature graph (editor / build) lands in #11.
 */
@Composable
fun IdeNavHost() {
    val projectsScreens: ProjectsScreens = koinInject()
    val filesScreens: FilesScreens = koinInject()
    val projectService: ProjectService = koinInject()
    var route by remember { mutableStateOf<IdeRoute>(IdeRoute.Projects) }
    val scope = rememberCoroutineScope()

    when (val current = route) {
        IdeRoute.Projects -> {
            projectsScreens.NavHost(
                onOpenProject = { projectId ->
                    scope.launch {
                        val project = projectService.getProject(projectId)
                        if (project != null) {
                            route = IdeRoute.Files(project)
                        }
                    }
                },
            )
        }

        is IdeRoute.Files -> {
            filesScreens.NavHost(
                root = ProjectRoot(current.project.rootPath),
                projectName = current.project.name,
                initialRelativePath = "",
                onOpenFile = {
                    // Hand off to editor when #9 / #11 wire that route.
                },
                onNavigateBack = { route = IdeRoute.Projects },
            )
        }
    }
}

private sealed interface IdeRoute {
    data object Projects : IdeRoute
    data class Files(val project: Project) : IdeRoute
}
