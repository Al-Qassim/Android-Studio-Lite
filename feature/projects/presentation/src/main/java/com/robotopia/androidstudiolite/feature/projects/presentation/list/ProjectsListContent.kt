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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarTitleAction(
            title = "Projects",
            actionLabel = "+ New",
            onActionClick = onCreateProject,
            onSettingsClick = onOpenSettings,
        )
        ProjectsListBody(
            state = state,
            onOpenClick = onOpenClick,
            onMenuOpen = onMenuOpen,
            onMenuDismiss = onMenuDismiss,
            onRunMenuClick = onRunMenuClick,
            onDeleteMenuClick = onDeleteMenuClick,
        )
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
    if (state.projects.isEmpty()) {
        ProjectsListEmpty()
    } else {
        ProjectsList(
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
private fun ProjectsListEmpty() {
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
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
        }
        item { ProjectsListFooterHint() }
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
        ProjectCard(
            name = project.name,
            packageName = project.packageName,
            meta = formatOpenedMeta(project.lastOpenedAt),
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
        )
    }
}

@Composable
private fun ProjectsListFooterHint() {
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

private val previewProjects = listOf(
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
    Project(
        id = ProjectId("3"),
        name = "Notes",
        packageName = "com.example.notes",
        rootPath = "/projects/3",
        lastOpenedAt = System.currentTimeMillis() - 86_400_000,
    ),
)

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "List · empty",
)
@Composable
private fun ProjectsListEmptyPreview() {
    ProjectsListContent(
        state = ProjectsListUiState(),
        onCreateProject = {},
        onOpenSettings = {},
        onOpenClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onRunMenuClick = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "List · with projects",
)
@Composable
private fun ProjectsListFilledPreview() {
    ProjectsListContent(
        state = ProjectsListUiState(projects = previewProjects),
        onCreateProject = {},
        onOpenSettings = {},
        onOpenClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onRunMenuClick = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "List · menu open",
)
@Composable
private fun ProjectsListMenuPreview() {
    ProjectsListContent(
        state = ProjectsListUiState(
            projects = previewProjects,
            menuProject = previewProjects.first(),
        ),
        onCreateProject = {},
        onOpenSettings = {},
        onOpenClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onRunMenuClick = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "List · delete confirm",
)
@Composable
private fun ProjectsListDeleteConfirmPreview() {
    ProjectsListContent(
        state = ProjectsListUiState(
            projects = previewProjects,
            pendingDelete = previewProjects.first(),
        ),
        onCreateProject = {},
        onOpenSettings = {},
        onOpenClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onRunMenuClick = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "List · action error",
)
@Composable
private fun ProjectsListActionErrorPreview() {
    ProjectsListContent(
        state = ProjectsListUiState(
            projects = previewProjects,
            actionError = "Project not found",
        ),
        onCreateProject = {},
        onOpenSettings = {},
        onOpenClick = {},
        onMenuOpen = {},
        onMenuDismiss = {},
        onRunMenuClick = {},
        onDeleteMenuClick = {},
        onDeleteCancel = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
}
