package com.robotopia.androidstudiolite.feature.projects.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import com.robotopia.androidstudiolite.feature.projects.presentation.list.ProjectsListContent
import com.robotopia.androidstudiolite.feature.projects.presentation.list.ProjectsListUiState

internal data class ProjectsListPreviewCase(
    private val label: String,
    val state: ProjectsListUiState,
) {
    override fun toString(): String = label
}

internal val previewProjects = listOf(
    Project(
        id = ProjectId("1"),
        name = "HelloCompose",
        packageName = "com.example.hellocompose",
        rootPath = "/projects/1",
        lastOpenedAt = System.currentTimeMillis(),
    ),
    Project(
        id = ProjectId("2"),
        name = "TodoApp",
        packageName = "com.example.todo",
        rootPath = "/projects/2",
        lastOpenedAt = System.currentTimeMillis() - 3_600_000,
    ),
    Project(
        id = ProjectId("3"),
        name = "Notes",
        packageName = "com.example.notes",
        rootPath = "/projects/3",
        lastOpenedAt = System.currentTimeMillis() - 86_400_000,
    ),
)

internal class ProjectsListPreviewProvider : PreviewParameterProvider<ProjectsListPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        ProjectsListPreviewCase("empty", ProjectsListUiState()),
        ProjectsListPreviewCase(
            "with projects",
            ProjectsListUiState(projects = previewProjects),
        ),
        ProjectsListPreviewCase(
            "menu open",
            ProjectsListUiState(
                projects = previewProjects,
                menuProject = previewProjects.first(),
            ),
        ),
        ProjectsListPreviewCase(
            "delete confirm",
            ProjectsListUiState(
                projects = previewProjects,
                pendingDelete = previewProjects.first(),
            ),
        ),
        ProjectsListPreviewCase(
            "action error",
            ProjectsListUiState(
                projects = previewProjects,
                actionError = "Project not found",
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun ProjectsListPreview(
    @PreviewParameter(ProjectsListPreviewProvider::class) preview: ProjectsListPreviewCase,
) {
    ProjectsListContent(
        state = preview.state,
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
