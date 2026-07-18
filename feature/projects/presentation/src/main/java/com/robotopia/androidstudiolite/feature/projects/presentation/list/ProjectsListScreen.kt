package com.robotopia.androidstudiolite.feature.projects.presentation.list

import android.app.DownloadManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectExportResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.io.File

@Composable
internal fun ProjectsListScreen(
    projectService: ProjectService,
    onOpenProject: (Project) -> Unit,
    onRunProject: (Project) -> Unit,
    onCreateProject: () -> Unit,
    onOpenSettings: () -> Unit,
    onBuildHistory: (Project) -> Unit,
    viewModel: ProjectsListViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            importProject(
                projectService = projectService,
                uiState = viewModel.uiState,
                zipUri = uri.toString(),
            )
        }
    }

    LaunchedEffect(projectService) {
        collectProjects(projectService, viewModel.uiState)
    }

    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(TOAST_DURATION_MS)
            viewModel.uiState.update { it.copy(toastMessage = null) }
        }
    }

    ProjectsListContent(
        state = state,
        onOpenSettings = onOpenSettings,
        onHubMenuOpen = {
            viewModel.uiState.update {
                it.copy(hubMenuOpen = true, menuProject = null)
            }
        },
        onHubMenuDismiss = {
            viewModel.uiState.update { it.copy(hubMenuOpen = false) }
        },
        onNewProject = {
            viewModel.uiState.update { it.copy(hubMenuOpen = false) }
            onCreateProject()
        },
        onImportProject = {
            viewModel.uiState.update { it.copy(hubMenuOpen = false) }
            importLauncher.launch(arrayOf("application/zip", "application/x-zip-compressed"))
        },
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
            viewModel.uiState.update {
                it.copy(menuProject = project, hubMenuOpen = false)
            }
        },
        onMenuDismiss = {
            viewModel.uiState.update { it.copy(menuProject = null) }
        },
        onRunMenuClick = { project ->
            viewModel.uiState.update { it.copy(menuProject = null) }
            onRunProject(project)
        },
        onExportMenuClick = { project ->
            viewModel.uiState.update { it.copy(menuProject = null) }
            scope.launch {
                exportProject(
                    projectService = projectService,
                    uiState = viewModel.uiState,
                    project = project,
                )
            }
        },
        onBuildHistoryMenuClick = { project ->
            viewModel.uiState.update { it.copy(menuProject = null) }
            onBuildHistory(project)
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
        onExportDismiss = {
            viewModel.uiState.update { it.copy(pendingExport = null) }
        },
        onExportOpenFolder = {
            viewModel.uiState.update { it.copy(pendingExport = null) }
            openExportFolder(context)
        },
        onExportShare = {
            val export = viewModel.uiState.value.pendingExport ?: return@ProjectsListContent
            viewModel.uiState.update { it.copy(pendingExport = null) }
            shareProjectZip(context = context, export = export)
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
                isLoading = false,
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
    onOpenProject: (Project) -> Unit,
) {
    uiState.update { it.copy(menuProject = null) }
    runCatching { projectService.markOpened(project.id) }
        .onSuccess { onOpenProject(project) }
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

private suspend fun exportProject(
    projectService: ProjectService,
    uiState: MutableStateFlow<ProjectsListUiState>,
    project: Project,
) {
    uiState.update { it.copy(isBusy = true, actionError = null) }
    try {
        val result = projectService.exportProject(project.id)
        uiState.update {
            it.copy(
                isBusy = false,
                pendingExport = result,
            )
        }
    } catch (error: CancellationException) {
        uiState.update { it.copy(isBusy = false) }
        throw error
    } catch (error: Exception) {
        uiState.update {
            it.copy(
                isBusy = false,
                actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
            )
        }
    }
}

private suspend fun importProject(
    projectService: ProjectService,
    uiState: MutableStateFlow<ProjectsListUiState>,
    zipUri: String,
) {
    uiState.update { it.copy(isBusy = true, actionError = null) }
    try {
        val imported = projectService.importProject(zipUri)
        uiState.update {
            it.copy(
                isBusy = false,
                toastMessage = "Imported ${imported.name}",
            )
        }
    } catch (error: CancellationException) {
        uiState.update { it.copy(isBusy = false) }
        throw error
    } catch (error: Exception) {
        uiState.update {
            it.copy(
                isBusy = false,
                actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
            )
        }
    }
}

private fun shareProjectZip(
    context: Context,
    export: ProjectExportResult,
) {
    val zipFile = File(export.localZipPath)
    if (!zipFile.isFile) return
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        zipFile,
    )
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "application/zip"
        putExtra(Intent.EXTRA_STREAM, uri)
        clipData = ClipData.newUri(context.contentResolver, export.displayName, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    context.startActivity(
        Intent.createChooser(send, "Export project").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    )
}

private fun openExportFolder(context: Context) {
    val intent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching {
        context.startActivity(intent)
    }
}

private const val TAG = "ProjectsList"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"
private const val TOAST_DURATION_MS = 2_500L
