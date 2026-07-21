package com.robotopia.androidstudiolite.integration.navigation

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
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingScreens
import com.robotopia.androidstudiolite.feature.onboarding.api.OnboardingStore
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import com.robotopia.androidstudiolite.feature.settings.api.SettingsScreens
import org.koin.compose.koinInject

/**
 * IDE root: switches between feature sub-navigations with [navFade].
 * Feature-internal multi-step hosts use the same fade.
 *
 * Cross-feature [IdeRoute] is [rememberSaveable] so Activity recreation
 * (theme / config / process death) restores the same screen. Deep routes
 * carry project fields so destinations render without a host-side fetch.
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
    var route by rememberSaveable(stateSaver = IdeRouteSaver) {
        mutableStateOf(
            if (onboardingStore.isCompleted()) IdeRoute.Projects else IdeRoute.Onboarding,
        )
    }

    AnimatedContent(
        targetState = route,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = { navFade() },
        label = "ideNav",
    ) { current ->
        when (current) {
            IdeRoute.Onboarding -> {
                onboardingScreens.Onboarding(
                    onFinished = { route = IdeRoute.Projects },
                )
            }

            IdeRoute.Projects -> {
                projectsScreens.NavHost(
                    onOpenProject = { project ->
                        route = project.toFilesRoute()
                    },
                    onRunProject = { project ->
                        route = project.toBuildRoute(returnTo = IdeRoute.Projects)
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
                    root = ProjectRoot(current.rootPath),
                    projectName = current.projectName,
                    initialRelativePath = "",
                    onOpenFile = { relativePath ->
                        route = current.toEditor(relativePath)
                    },
                    onNavigateBack = { route = IdeRoute.Projects },
                    onRun = {
                        route = current.toBuild()
                    },
                    showGit = current.showGit,
                    onShowGitChange = { showGit ->
                        route = current.copy(showGit = showGit)
                    },
                )
            }

            is IdeRoute.Editor -> {
                editorScreens.NavHost(
                    documentId = current.documentId(),
                    root = ProjectRoot(current.rootPath),
                    onNavigateBack = { route = current.toFiles() },
                    onRun = {
                        route = current.toBuild()
                    },
                )
            }

            is IdeRoute.Build -> {
                buildScreens.NavHost(
                    request = BuildRequest(
                        projectId = ProjectId(current.projectId),
                        projectRoot = ProjectRoot(current.rootPath),
                        projectName = current.projectName,
                        packageName = current.packageName,
                    ),
                    onDismiss = { route = current.returnTo },
                )
            }
        }
    }
}
