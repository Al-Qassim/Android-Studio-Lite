package com.robotopia.androidstudiolite.feature.projects.presentation.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
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
    onRunProject: (projectId: ProjectId) -> Unit,
    onCreateProject: () -> Unit,
    onOpenSettings: () -> Unit,
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
        onOpenSettings = onOpenSettings,
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
        onRunMenuClick = { project ->
            viewModel.uiState.update { it.copy(menuProject = null) }
            onRunProject(project.id)
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

private const val TAG = "ProjectsList"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"
