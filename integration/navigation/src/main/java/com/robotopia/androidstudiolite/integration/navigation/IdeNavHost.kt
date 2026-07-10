package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Toast
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

private sealed interface IdeRoute {
    data object ProjectsList : IdeRoute
    data object CreateProject : IdeRoute
}

/**
 * IDE navigation root. Temporary host for Projects (#7); full NavHost graph lands in #11.
 */
@Composable
fun IdeNavHost() {
    val projectsScreens: ProjectsScreens = koinInject()
    var route by remember { mutableStateOf<IdeRoute>(IdeRoute.ProjectsList) }
    var openedToast by remember { mutableStateOf<ProjectId?>(null) }

    LaunchedEffect(openedToast) {
        if (openedToast != null) {
            delay(2_500)
            openedToast = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        when (route) {
            IdeRoute.ProjectsList -> {
                projectsScreens.ProjectsList(
                    onOpenProject = { projectId ->
                        openedToast = projectId
                    },
                    onCreateProject = { route = IdeRoute.CreateProject },
                )
            }

            IdeRoute.CreateProject -> {
                projectsScreens.CreateProject(
                    onCreated = {
                        route = IdeRoute.ProjectsList
                    },
                    onCancel = { route = IdeRoute.ProjectsList },
                )
            }
        }

        openedToast?.let {
            Toast(
                message = "Project opened (files UI later)",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp),
            )
        }
    }
}
