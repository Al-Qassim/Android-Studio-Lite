package com.robotopia.androidstudiolite.feature.projects.presentation.list

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.ProjectCard
import com.robotopia.androidstudiolite.designsystem.component.ProjectMenu
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun ProjectsListScreen(
    projectService: ProjectService,
    onOpenProject: (projectId: ProjectId) -> Unit,
    onCreateProject: () -> Unit,
    viewModel: ProjectsListViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(projectService) {
        collectProjects(projectService, viewModel.uiState)
    }

    ProjectsListContent(
        state = state,
        onCreateProject = onCreateProject,
        onOpenClick = { project ->
            scope.launch {
                openProject(
                    projectService = projectService,
                    uiState = viewModel.uiState,
                    project = project,
                    onOpenProject = onOpenProject,
                )
            }
        },
        onMenuOpen = { project ->
            viewModel.uiState.update { it.copy(menuProject = project) }
        },
        onMenuDismiss = {
            viewModel.uiState.update { it.copy(menuProject = null) }
        },
        onDeleteMenuClick = { project ->
            viewModel.uiState.update {
                it.copy(menuProject = null, pendingDelete = project)
            }
        },
        onDeleteCancel = {
            viewModel.uiState.update { it.copy(pendingDelete = null) }
        },
        onDeleteConfirm = {
            val toDelete = viewModel.uiState.value.pendingDelete ?: return@ProjectsListContent
            viewModel.uiState.update { it.copy(pendingDelete = null) }
            scope.launch {
                deleteProject(
                    projectService = projectService,
                    uiState = viewModel.uiState,
                    project = toDelete,
                )
            }
        },
        onErrorDismiss = {
            viewModel.uiState.update { it.copy(actionError = null) }
        },
    )
}

@Composable
internal fun ProjectsListContent(
    state: ProjectsListUiState,
    onCreateProject: () -> Unit,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onDeleteMenuClick: (Project) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
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
            state.projects.isEmpty() -> {
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
                    items(state.projects, key = { it.id.value }) { project ->
                        Box {
                            ProjectCard(
                                name = project.name,
                                packageName = project.packageName,
                                meta = formatOpenedMeta(project.lastOpenedAt),
                                onClick = { onOpenClick(project) },
                                onLongClick = { onMenuOpen(project) },
                                onMenuClick = { onMenuOpen(project) },
                            )
                            if (state.menuProject?.id == project.id) {
                                Popup(
                                    alignment = Alignment.TopEnd,
                                    onDismissRequest = onMenuDismiss,
                                    properties = PopupProperties(focusable = true),
                                ) {
                                    ProjectMenu(
                                        onOpen = { onOpenClick(project) },
                                        onDelete = { onDeleteMenuClick(project) },
                                    )
                                }
                            }
                        }
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
            }
        }
    }

    state.pendingDelete?.let { project ->
        Dialog(onDismissRequest = onDeleteCancel) {
            DialogMessageAction(
                title = "Delete project?",
                message = "${project.name} and its files will be removed from this device. This cannot be undone.",
                actionLabel = "Delete",
                dangerAction = true,
                onCancel = onDeleteCancel,
                onAction = onDeleteConfirm,
            )
        }
    }

    state.actionError?.let { message ->
        Dialog(onDismissRequest = onErrorDismiss) {
            DialogMessageAction(
                title = "Something went wrong",
                message = message,
                actionLabel = "OK",
                onCancel = onErrorDismiss,
                onAction = onErrorDismiss,
            )
        }
    }
}

private suspend fun collectProjects(
    projectService: ProjectService,
    uiState: MutableStateFlow<ProjectsListUiState>,
) {
    projectService.observeProjects().collect { projects ->
        uiState.update { state ->
            state.copy(
                projects = projects,
                menuProject = state.menuProject?.takeIf { menu ->
                    projects.any { it.id == menu.id }
                },
                pendingDelete = state.pendingDelete?.takeIf { pending ->
                    projects.any { it.id == pending.id }
                },
            )
        }
    }
}

private suspend fun openProject(
    projectService: ProjectService,
    uiState: MutableStateFlow<ProjectsListUiState>,
    project: Project,
    onOpenProject: (ProjectId) -> Unit,
) {
    uiState.update { it.copy(menuProject = null) }
    runCatching { projectService.markOpened(project.id) }
        .onSuccess { onOpenProject(project.id) }
        .onFailure { error ->
            uiState.update {
                it.copy(actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE)
            }
        }
}

private suspend fun deleteProject(
    projectService: ProjectService,
    uiState: MutableStateFlow<ProjectsListUiState>,
    project: Project,
) {
    runCatching { projectService.deleteProject(project.id) }
        .onFailure { error ->
            uiState.update {
                it.copy(actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE)
            }
        }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun ProjectsListScreenPreview() {
    ProjectsListContent(
        state = ProjectsListUiState(
            projects = listOf(
                Project(
                    id = ProjectId("1"),
                    name = "MyApp",
                    packageName = "com.example.myapp",
                    rootPath = "/projects/1",
                    lastOpenedAt = System.currentTimeMillis() - 3_600_000,
                ),
                Project(
                    id = ProjectId("2"),
                    name = "Demo",
                    packageName = "com.example.demo",
                    rootPath = "/projects/2",
                    lastOpenedAt = null,
                ),
            ),
        ),
        onCreateProject = {},
        onOpenClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
}

private const val TAG = "ProjectsList"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"
