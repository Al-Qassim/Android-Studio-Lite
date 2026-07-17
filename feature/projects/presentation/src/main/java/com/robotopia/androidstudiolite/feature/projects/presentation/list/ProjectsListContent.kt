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
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.ProjectMenu
import com.robotopia.androidstudiolite.designsystem.component.ProjectRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

@Composable
internal fun ProjectsListContent(
    state: ProjectsListUiState,
    onCreateProject: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onRunMenuClick: (Project) -> Unit,
    onDeleteMenuClick: (Project) -> Unit,
    onDeleteCancel: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarTitleAction(
                title = "Projects",
                onActionClick = onCreateProject,
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
                onDeleteMenuClick = onDeleteMenuClick,
            )
        }
    }

    state.pendingDelete?.let { project ->
        DeleteProjectDialog(
            projectName = project.name,
            onCancel = onDeleteCancel,
            onConfirm = onDeleteConfirm,
        )
    }

    state.actionError?.let { message ->
        ActionErrorDialog(
            message = message,
            onDismiss = onErrorDismiss,
        )
    }
}

@Composable
private fun ProjectsListBody(
    state: ProjectsListUiState,
    onOpenClick: (Project) -> Unit,
    onMenuOpen: (Project) -> Unit,
    onMenuDismiss: () -> Unit,
    onRunMenuClick: (Project) -> Unit,
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
            hint = "Tap + to create your first project.",
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
                onDeleteMenuClick = onDeleteMenuClick,
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 16.dp)
                    .height(1.dp)
                    .background(Colors.MenuDivider),
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
                onDelete = { onDeleteMenuClick(project) },
                onDismiss = onMenuDismiss,
            )
        }
    }
}

/** Below ProjectRow's ⋮ control: inset 2 + pad 12 + centered 32.dp IconButton. */
private val ProjectOverflowMenuTopOffset = 62.dp

@Composable
private fun ProjectOverflowMenu(
    onOpen: () -> Unit,
    onRun: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        ProjectMenu(
            onOpen = onOpen,
            onRun = onRun,
            onDelete = onDelete,
            modifier = Modifier.padding(
                top = ProjectOverflowMenuTopOffset,
                end = 8.dp,
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
