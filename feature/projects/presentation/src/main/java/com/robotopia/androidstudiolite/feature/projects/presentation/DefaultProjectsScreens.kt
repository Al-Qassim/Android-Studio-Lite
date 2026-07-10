package com.robotopia.androidstudiolite.feature.projects.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.ProjectCard
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date

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
    var pendingDelete by remember { mutableStateOf<Project?>(null) }
    var actionError by remember { mutableStateOf<String?>(null) }

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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    items(projects, key = { it.id.value }) { project ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            ProjectCard(
                                name = project.name,
                                packageName = project.packageName,
                                meta = formatLastOpened(project.lastOpenedAt),
                                onClick = {
                                    scope.launch {
                                        runCatching { projectService.markOpened(project.id) }
                                            .onSuccess { onOpenProject(project.id) }
                                            .onFailure {
                                                actionError = it.message ?: "Could not open project"
                                            }
                                    }
                                },
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                Button(
                                    label = "Delete",
                                    onClick = { pendingDelete = project },
                                    variant = ButtonVariant.DangerText,
                                )
                            }
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

private fun formatLastOpened(lastOpenedAt: Long?): String =
    if (lastOpenedAt == null) {
        "Never opened"
    } else {
        "Last opened · ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(lastOpenedAt))}"
    }
