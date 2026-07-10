package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.ProjectCard
import com.robotopia.androidstudiolite.designsystem.component.ProjectMenu
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.math.max

class DefaultProjectsScreens(
    private val projectService: ProjectService,
) : ProjectsScreens {

    @Composable
    override fun ProjectsList(
        onOpenProject: (projectId: ProjectId) -> Unit,
        onCreateProject: () -> Unit,
    ) {
        ProjectsListScreen(
            projectService = projectService,
            onOpenProject = onOpenProject,
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

@Composable
private fun ProjectsListScreen(
    projectService: ProjectService,
    onOpenProject: (projectId: ProjectId) -> Unit,
    onCreateProject: () -> Unit,
) {
    val projects by projectService.observeProjects().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var menuProject by remember { mutableStateOf<Project?>(null) }
    var pendingDelete by remember { mutableStateOf<Project?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

    fun openProject(project: Project) {
        scope.launch {
            runCatching { projectService.markOpened(project.id) }
                .onSuccess { onOpenProject(project.id) }
                .onFailure {
                    actionError = it.message ?: "Could not open project"
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarTitleAction(
            title = "Projects",
            actionLabel = "+ New",
            onActionClick = onCreateProject,
        )

        when {
            projects.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        title = "No projects yet",
                        hint = "Tap + New to create your first project.",
                    )
                }
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(projects, key = { it.id.value }) { project ->
                            ProjectCard(
                                name = project.name,
                                packageName = project.packageName,
                                meta = formatOpenedMeta(project.lastOpenedAt),
                                onClick = { openProject(project) },
                                onLongClick = { menuProject = project },
                            )
                        }
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                BasicText(
                                    text = "Tap a project to open",
                                    style = Typography.Caption.copy(
                                        color = Colors.Muted2,
                                        textAlign = TextAlign.Center,
                                    ),
                                )
                                BasicText(
                                    text = "or + New to create one",
                                    style = Typography.Caption.copy(
                                        color = Colors.Muted2,
                                        textAlign = TextAlign.Center,
                                    ),
                                )
                            }
                        }
                    }

                    menuProject?.let { project ->
                        Popup(
                            alignment = Alignment.Center,
                            onDismissRequest = { menuProject = null },
                            properties = PopupProperties(focusable = true),
                        ) {
                            ProjectMenu(
                                onOpen = {
                                    menuProject = null
                                    openProject(project)
                                },
                                onDelete = {
                                    menuProject = null
                                    pendingDelete = project
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    pendingDelete?.let { project ->
        Dialog(onDismissRequest = { pendingDelete = null }) {
            DialogMessageAction(
                title = "Delete project?",
                message = "“${project.name}” and all of its files will be permanently deleted.",
                actionLabel = "Delete",
                dangerAction = true,
                onCancel = { pendingDelete = null },
                onAction = {
                    val toDelete = project
                    pendingDelete = null
                    scope.launch {
                        runCatching { projectService.deleteProject(toDelete.id) }
                            .onFailure {
                                actionError = it.message ?: "Could not delete project"
                            }
                    }
                },
            )
        }
    }

    actionError?.let { message ->
        Dialog(onDismissRequest = { actionError = null }) {
            DialogMessageAction(
                title = "Something went wrong",
                message = message,
                actionLabel = "OK",
                onCancel = { actionError = null },
                onAction = { actionError = null },
            )
        }
    }
}

internal fun formatOpenedMeta(lastOpenedAt: Long?, now: Long = System.currentTimeMillis()): String {
    if (lastOpenedAt == null) return "Never opened"
    val elapsedMs = max(0L, now - lastOpenedAt)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMs)
    val hours = TimeUnit.MILLISECONDS.toHours(elapsedMs)
    val days = TimeUnit.MILLISECONDS.toDays(elapsedMs)
    return when {
        minutes < 1 -> "Opened just now"
        minutes < 60 -> "Opened ${minutes}m ago"
        hours < 24 -> "Opened ${hours}h ago"
        days == 1L -> "Opened yesterday"
        else -> "Opened $days days ago"
    }
}
