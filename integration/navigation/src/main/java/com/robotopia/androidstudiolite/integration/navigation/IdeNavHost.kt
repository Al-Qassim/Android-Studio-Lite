package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import com.robotopia.androidstudiolite.feature.settings.api.SettingsScreens
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

/**
 * IDE root: switches between feature sub-navigations.
 * Feature-internal routes stay inside each feature’s [NavHost].
 */
@Composable
fun IdeNavHost() {
    val projectsScreens: ProjectsScreens = koinInject()
    val filesScreens: FilesScreens = koinInject()
    val editorScreens: EditorScreens = koinInject()
    val buildScreens: BuildScreens = koinInject()
    val settingsScreens: SettingsScreens = koinInject()
    val apkInstaller: ApkInstaller = koinInject()
    val editorSession: EditorSession = koinInject()
    val projectService: ProjectService = koinInject()
    var route by remember { mutableStateOf<IdeRoute>(IdeRoute.Projects) }
    val scope = rememberCoroutineScope()

    // Force-close editor when the open document's project is deleted.
    LaunchedEffect(Unit) {
        projectService.observeProjects().collect { projects ->
            val open = editorSession.document.value ?: return@collect
            if (projects.none { it.id == open.id.projectId }) {
                editorSession.close()
                if (route is IdeRoute.Editor) {
                    route = IdeRoute.Projects
                }
            }
        }
    }

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
                onRunProject = { projectId ->
                    scope.launch {
                        openBuildForProjectId(
                            projectService = projectService,
                            projectId = projectId,
                            returnTo = IdeRoute.Projects,
                            setRoute = { route = it },
                        )
                    }
                },
                onOpenSettings = {
                    route = IdeRoute.Settings
                },
            )
        }

        IdeRoute.Settings -> {
            settingsScreens.Settings(
                onDismiss = { route = IdeRoute.Projects },
            )
        }

        is IdeRoute.Files -> {
            filesScreens.NavHost(
                root = ProjectRoot(current.project.rootPath),
                projectName = current.project.name,
                initialRelativePath = "",
                onOpenFile = { relativePath ->
                    route = IdeRoute.Editor(
                        project = current.project,
                        documentId = DocumentId(current.project.id, relativePath),
                    )
                },
                onNavigateBack = { route = IdeRoute.Projects },
                onRun = {
                    route = IdeRoute.Build(
                        project = current.project,
                        returnTo = current,
                    )
                },
            )
        }

        is IdeRoute.Editor -> {
            editorScreens.NavHost(
                documentId = current.documentId,
                root = ProjectRoot(current.project.rootPath),
                onNavigateBack = { route = IdeRoute.Files(current.project) },
                onRun = {
                    route = IdeRoute.Build(
                        project = current.project,
                        returnTo = current,
                    )
                },
            )
        }

        is IdeRoute.Build -> {
            buildScreens.NavHost(
                request = BuildRequest(
                    projectId = current.project.id,
                    projectRoot = ProjectRoot(current.project.rootPath),
                    projectName = current.project.name,
                    packageName = current.project.packageName,
                ),
                onReadyToInstall = { apkPath ->
                    apkInstaller.requestInstall(apkPath)
                },
                onDismiss = { route = current.returnTo },
            )
        }
    }
}

private suspend fun openBuildForProjectId(
    projectService: ProjectService,
    projectId: ProjectId,
    returnTo: IdeRoute,
    setRoute: (IdeRoute) -> Unit,
) {
    val project = projectService.getProject(projectId) ?: return
    setRoute(
        IdeRoute.Build(
            project = project,
            returnTo = returnTo,
        ),
    )
}

private sealed interface IdeRoute {
    data object Projects : IdeRoute
    data object Settings : IdeRoute
    data class Files(val project: Project) : IdeRoute
    data class Editor(
        val project: Project,
        val documentId: DocumentId,
    ) : IdeRoute
    data class Build(
        val project: Project,
        val returnTo: IdeRoute,
    ) : IdeRoute
}
