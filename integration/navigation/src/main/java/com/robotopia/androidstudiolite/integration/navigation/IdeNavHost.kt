package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildScreens
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingScreens
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingStore
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import com.robotopia.androidstudiolite.feature.settings.api.SettingsScreens
import org.koin.compose.koinInject

/**
 * IDE root: switches between feature sub-navigations.
 * Feature-internal routes stay inside each feature’s [NavHost].
 *
 * Cross-feature [IdeRoute] is [rememberSaveable] so Activity recreation
 * (theme / config / process death) restores the same screen.
 */
@Composable
fun IdeNavHost() {
    val projectsScreens: ProjectsScreens = koinInject()
    val filesScreens: FilesScreens = koinInject()
    val editorScreens: EditorScreens = koinInject()
    val buildScreens: BuildScreens = koinInject()
    val settingsScreens: SettingsScreens = koinInject()
    val onboardingScreens: OnboardingScreens = koinInject()
    val onboardingStore: OnboardingStore = koinInject()
    val apkInstaller: ApkInstaller = koinInject()
    val editorSession: EditorSession = koinInject()
    val projectService: ProjectService = koinInject()
    var route by rememberSaveable(stateSaver = IdeRouteSaver) {
        mutableStateOf(
            if (onboardingStore.isCompleted()) IdeRoute.Projects else IdeRoute.Onboarding,
        )
    }

    // Drop deep links to deleted projects; also force-close editor session.
    LaunchedEffect(route) {
        val projectId = route.projectIdOrNull() ?: return@LaunchedEffect
        projectService.observeProjects().collect { projects ->
            if (projects.none { it.id == projectId }) {
                editorSession.close()
                route = IdeRoute.Projects
            }
        }
    }

    when (val current = route) {
        IdeRoute.Onboarding -> {
            onboardingScreens.Onboarding(
                onFinished = { route = IdeRoute.Projects },
            )
        }

        IdeRoute.Projects -> {
            projectsScreens.NavHost(
                onOpenProject = { projectId ->
                    route = IdeRoute.Files(projectId)
                },
                onRunProject = { projectId ->
                    route = IdeRoute.Build(
                        projectId = projectId,
                        returnTo = IdeRoute.Projects,
                    )
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
            RestoredProject(projectId = current.projectId, onMissing = { route = IdeRoute.Projects }) { project ->
                filesScreens.NavHost(
                    root = ProjectRoot(project.rootPath),
                    projectName = project.name,
                    initialRelativePath = "",
                    onOpenFile = { relativePath ->
                        route = IdeRoute.Editor(
                            documentId = DocumentId(project.id, relativePath),
                        )
                    },
                    onNavigateBack = { route = IdeRoute.Projects },
                    onRun = {
                        route = IdeRoute.Build(
                            projectId = project.id,
                            returnTo = current,
                        )
                    },
                )
            }
        }

        is IdeRoute.Editor -> {
            RestoredProject(
                projectId = current.documentId.projectId,
                onMissing = { route = IdeRoute.Projects },
            ) { project ->
                editorScreens.NavHost(
                    documentId = current.documentId,
                    root = ProjectRoot(project.rootPath),
                    onNavigateBack = { route = IdeRoute.Files(project.id) },
                    onRun = {
                        route = IdeRoute.Build(
                            projectId = project.id,
                            returnTo = current,
                        )
                    },
                )
            }
        }

        is IdeRoute.Build -> {
            RestoredProject(projectId = current.projectId, onMissing = { route = IdeRoute.Projects }) { project ->
                buildScreens.NavHost(
                    request = BuildRequest(
                        projectId = project.id,
                        projectRoot = ProjectRoot(project.rootPath),
                        projectName = project.name,
                        packageName = project.packageName,
                    ),
                    onReadyToInstall = { apkPath ->
                        apkInstaller.requestInstall(apkPath)
                    },
                    onDismiss = { route = current.returnTo },
                )
            }
        }
    }
}

@Composable
private fun RestoredProject(
    projectId: ProjectId,
    onMissing: () -> Unit,
    content: @Composable (Project) -> Unit,
) {
    val projectService: ProjectService = koinInject()
    var project by remember(projectId) { mutableStateOf<Project?>(null) }

    LaunchedEffect(projectId) {
        val loaded = projectService.getProject(projectId)
        if (loaded == null) {
            onMissing()
        } else {
            project = loaded
        }
    }

    project?.let { content(it) }
}
