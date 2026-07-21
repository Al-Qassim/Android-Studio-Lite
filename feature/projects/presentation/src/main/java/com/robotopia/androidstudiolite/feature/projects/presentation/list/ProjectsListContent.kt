package com.robotopia.androidstudiolite.feature.projects.presentation.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.component.ProjectRow
import com.robotopia.androidstudiolite.designsystem.component.ToastBottom
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.icon.IconAdd
import com.robotopia.androidstudiolite.designsystem.icon.IconCloud
import com.robotopia.androidstudiolite.designsystem.icon.IconFolder
import com.robotopia.androidstudiolite.designsystem.icon.IconRun
import com.robotopia.androidstudiolite.designsystem.icon.IconSave
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

@Composable
internal fun ProjectsListContent(
    state: ProjectsListUiState,
    onOpenSettings: () -> Unit,
    onHubMenuOpen: () -> Unit,
    onHubMenuDismiss: () -> Unit,
    onNewProject: () -> Unit,
    onCloneProject: () -> Unit,
    onImportProject: () -> Unit,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onRunMenuClick: (Project) -> Unit,
    onExportMenuClick: (Project) -> Unit,
    onBuildHistoryMenuClick: (Project) -> Unit,
    onDeleteMenuClick: (Project) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onExportDismiss: () -> Unit,
    onExportOpenFolder: () -> Unit,
    onExportShare: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarTitleAction(
                title = "Projects",
                onActionClick = onHubMenuOpen,
                onSettingsClick = onOpenSettings,
            )
        },
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            ProjectsListBody(
                state = state,
                onOpenClick = onOpenClick,
                onMenuOpen = onMenuOpen,
                onMenuDismiss = onMenuDismiss,
                onRunMenuClick = onRunMenuClick,
                onExportMenuClick = onExportMenuClick,
                onBuildHistoryMenuClick = onBuildHistoryMenuClick,
                onDeleteMenuClick = onDeleteMenuClick,
            )
            state.toastMessage?.let { message ->
                ToastBottom(message = message)
            }
        }
    }

    if (state.hubMenuOpen) {
        ProjectsHubOverflowMenu(
            onNewProject = onNewProject,
            onCloneProject = onCloneProject,
            onImportProject = onImportProject,
            onDismiss = onHubMenuDismiss,
        )
    }

    state.pendingDelete?.let { project ->
        DeleteProjectDialog(
            projectName = project.name,
            onCancel = onDeleteCancel,
            onConfirm = onDeleteConfirm,
        )
    }

    state.pendingExport?.let { export ->
        ExportProjectDialog(
            displayName = export.displayName,
            savedToDownloads = export.downloadsUri != null,
            onDismiss = onExportDismiss,
            onOpenFolder = onExportOpenFolder,
            onShare = onExportShare,
        )
    }

    state.actionError?.let { message ->
        ActionErrorDialog(
            message = message,
            onDismiss = onErrorDismiss,
        )
    }

    if (state.isBusy) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(label = "Working…")
            }
        }
    }
}

@Composable
private fun ProjectsListBody(
    state: ProjectsListUiState,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onRunMenuClick: (Project) -> Unit,
    onExportMenuClick: (Project) -> Unit,
    onBuildHistoryMenuClick: (Project) -> Unit,
    onDeleteMenuClick: (Project) -> Unit,
) {
    when {
        state.isLoading -> ProjectsListLoading()
        state.projects.isEmpty() -> ProjectsListEmpty()
        else -> ProjectsList(
            projects = state.projects,
            menuProjectId = state.menuProject?.id,
            onOpenClick = onOpenClick,
            onMenuOpen = onMenuOpen,
            onMenuDismiss = onMenuDismiss,
            onRunMenuClick = onRunMenuClick,
            onExportMenuClick = onExportMenuClick,
            onBuildHistoryMenuClick = onBuildHistoryMenuClick,
            onDeleteMenuClick = onDeleteMenuClick,
        )
    }
}

@Composable
private fun ProjectsListLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator(label = "Loading projects…")
    }
}

@Composable
private fun ProjectsListEmpty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = "No projects yet",
            hint = "Tap + to create or import a project.",
        )
    }
}

@Composable
private fun ProjectsList(
    projects: List<Project>,
    menuProjectId: ProjectId?,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onRunMenuClick: (Project) -> Unit,
    onExportMenuClick: (Project) -> Unit,
    onBuildHistoryMenuClick: (Project) -> Unit,
    onDeleteMenuClick: (Project) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(projects, key = { it.id.value }) { project ->
            ProjectListItem(
                project = project,
                menuOpen = menuProjectId == project.id,
                onOpenClick = onOpenClick,
                onMenuOpen = onMenuOpen,
                onMenuDismiss = onMenuDismiss,
                onRunMenuClick = onRunMenuClick,
                onExportMenuClick = onExportMenuClick,
                onBuildHistoryMenuClick = onBuildHistoryMenuClick,
                onDeleteMenuClick = onDeleteMenuClick,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp)
                    .height(1.dp)
                    .background(Theme.colors.MenuDivider),
            )
        }
    }
}

@Composable
private fun ProjectListItem(
    project: Project,
    menuOpen: Boolean,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onRunMenuClick: (Project) -> Unit,
    onExportMenuClick: (Project) -> Unit,
    onBuildHistoryMenuClick: (Project) -> Unit,
    onDeleteMenuClick: (Project) -> Unit,
) {
    Box {
        ProjectRow(
            name = project.name,
            packageName = project.packageName,
            meta = formatOpenedMeta(project.lastOpenedAt),
            selected = menuOpen,
            onClick = { onOpenClick(project) },
            onLongClick = { onMenuOpen(project) },
            onMenuClick = { onMenuOpen(project) },
        )
        if (menuOpen) {
            ProjectOverflowMenu(
                onOpen = { onOpenClick(project) },
                onRun = { onRunMenuClick(project) },
                onExport = { onExportMenuClick(project) },
                onBuildHistory = { onBuildHistoryMenuClick(project) },
                onDelete = { onDeleteMenuClick(project) },
                onDismiss = onMenuDismiss,
            )
        }
    }
}

/** Below ProjectRow's ⋮ control: inset 2 + pad 12 + centered 32.dp IconButton. */
private val ProjectOverflowMenuTopOffset = 62.dp
private val ProjectOverflowMenuEndOffset = 8.dp

/** Below the projects top-bar + control. */
private val HubMenuTopOffset = 48.dp
private val HubMenuEndOffset = 12.dp

@Composable
private fun ProjectOverflowMenu(
    onOpen: () -> Unit,
    onRun: () -> Unit,
    onExport: () -> Unit,
    onBuildHistory: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(
            top = ProjectOverflowMenuTopOffset,
            end = ProjectOverflowMenuEndOffset,
        ),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Menu(
            items = listOf(
                MenuItem.Button(
                    label = "Open",
                    onClick = onOpen,
                    icon = { tint, size -> IconFolder(tint = tint, size = size) },
                ),
                MenuItem.Divider,
                MenuItem.Button(
                    label = "Run",
                    onClick = onRun,
                    icon = { tint, size -> IconRun(tint = tint, size = size) },
                ),
                MenuItem.Button(
                    label = "Build history",
                    onClick = onBuildHistory,
                    icon = { tint, size -> IconCloud(tint = tint, size = size) },
                ),
                MenuItem.Divider,
                MenuItem.Button(
                    label = "Export…",
                    onClick = onExport,
                    icon = { tint, size -> IconSave(tint = tint, size = size) },
                ),
                MenuItem.Button(label = "Delete", onClick = onDelete, danger = true),
            ),
        )
    }
}

@Composable
private fun ProjectsHubOverflowMenu(
    onNewProject: () -> Unit,
    onCloneProject: () -> Unit,
    onImportProject: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        offset = topEndPopupOffset(
            top = HubMenuTopOffset,
            end = HubMenuEndOffset,
            includeStatusBars = true,
        ),
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Menu(
            items = listOf(
                MenuItem.Button(
                    label = "New project",
                    onClick = onNewProject,
                    icon = { tint, size -> IconAdd(tint = tint, size = size) },
                ),
                MenuItem.Button(
                    label = "Clone from GitHub",
                    onClick = onCloneProject,
                    icon = { tint, size -> IconCloud(tint = tint, size = size) },
                ),
                MenuItem.Button(
                    label = "Import project",
                    onClick = onImportProject,
                    icon = { tint, size -> IconFolder(tint = tint, size = size) },
                ),
            ),
        )
    }
}

@Composable
private fun DeleteProjectDialog(
    projectName: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        DialogMessageAction(
            title = "Delete project?",
            message = "$projectName and its files will be removed from this device. This cannot be undone.",
            actionLabel = "Delete",
            dangerAction = true,
            onCancel = onCancel,
            onAction = onConfirm,
        )
    }
}

@Composable
private fun ExportProjectDialog(
    displayName: String,
    savedToDownloads: Boolean,
    onDismiss: () -> Unit,
    onOpenFolder: () -> Unit,
    onShare: () -> Unit,
) {
    val message = if (savedToDownloads) {
        "Saved “$displayName” to Downloads/AndroidStudioLite. Open the folder or share it with another app."
    } else {
        "Packaged “$displayName”. Share it with another app, or open Downloads to look for a saved copy."
    }
    Dialog(onDismissRequest = onDismiss) {
        DialogMessageAction(
            title = "Project exported",
            message = message,
            cancelLabel = "Open folder",
            actionLabel = "Share",
            onCancel = onOpenFolder,
            onAction = onShare,
        )
    }
}

@Composable
private fun ActionErrorDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        DialogMessageAction(
            title = "Something went wrong",
            message = message,
            actionLabel = "OK",
            onCancel = onDismiss,
            onAction = onDismiss,
        )
    }
}
