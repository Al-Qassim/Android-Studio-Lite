package com.robotopia.androidstudiolite.designsystem.preview.projects

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.ProjectMenu
import com.robotopia.androidstudiolite.designsystem.component.ProjectRow
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.preview.ExamplePreviewBackground
import com.robotopia.androidstudiolite.designsystem.preview.PreviewScrim
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class ProjectsExampleCase {
    Empty,
    List,
    Menu,
    DeleteConfirm,
    NewProjectForm,
}

private class ProjectsExampleCaseProvider : PreviewParameterProvider<ProjectsExampleCase> {
    override val values = ProjectsExampleCase.entries.asSequence()
    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

@Preview(
    name = "Projects",
    showBackground = true,
    backgroundColor = ExamplePreviewBackground,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun ProjectsExamplePreview(
    @PreviewParameter(ProjectsExampleCaseProvider::class) case: ProjectsExampleCase,
) {
    when (case) {
        ProjectsExampleCase.Empty -> ProjectsEmptyScreen()
        ProjectsExampleCase.List -> ProjectsListScreen()
        ProjectsExampleCase.Menu -> ProjectsMenuScreen()
        ProjectsExampleCase.DeleteConfirm -> ProjectsDeleteConfirmScreen()
        ProjectsExampleCase.NewProjectForm -> NewProjectFormScreen()
    }
}

@Composable
private fun ProjectsEmptyScreen() {
    IslandScaffold(
        topBar = { TopBarTitleAction(title = "Projects", onSettingsClick = {}) },
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState(
                title = "No projects yet",
                hint = "Tap + to create your first project.",
            )
        }
    }
}

@Composable
private fun ProjectsListScreen() {
    IslandScaffold(
        topBar = { TopBarTitleAction(title = "Projects", onSettingsClick = {}) },
    ) {
        ProjectRow(
            name = "HelloCompose",
            packageName = "com.example.hellocompose",
            meta = "Opened just now",
            onMenuClick = {},
        )
        ProjectRow(
            name = "TodoApp",
            packageName = "com.example.todo",
            meta = "Opened yesterday",
            onMenuClick = {},
        )
        ProjectRow(
            name = "WeatherDemo",
            packageName = "com.example.weather",
            meta = "Opened 3 days ago",
            onMenuClick = {},
        )
        Spacer(modifier = Modifier.weight(1f))
        BasicText(
            text = "Tap a project to open",
            style = Typography.Caption.copy(color = Colors.Muted2),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
        )
        BasicText(
            text = "or + to create one",
            style = Typography.Caption.copy(color = Colors.Muted2),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun ProjectsMenuScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        ProjectsListScreen()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, end = 12.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            ProjectMenu()
        }
    }
}

@Composable
private fun ProjectsDeleteConfirmScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        ProjectsListScreen()
        PreviewScrim {
            DialogMessageAction(
                title = "Delete project?",
                message = "TodoApp and its files will be removed from this device. This cannot be undone.",
                actionLabel = "Delete",
                dangerAction = true,
            )
        }
    }
}

@Composable
private fun NewProjectFormScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "New project") },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(
                text = "Single Activity + Compose",
                style = Typography.Caption.copy(color = Colors.Muted),
            )
            TextField(
                value = "HelloCompose",
                onValueChange = {},
                placeholder = "App name",
                variant = TextFieldVariant.Form,
            )
            TextField(
                value = "com.example.hellocompose",
                onValueChange = {},
                placeholder = "Package name",
                variant = TextFieldVariant.Form,
            )
            TextField(
                value = "26",
                onValueChange = {},
                placeholder = "Min SDK",
                variant = TextFieldVariant.Form,
            )
            Button(
                label = "Create project",
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
            )
            BasicText(
                text = "Copies template into app storage",
                style = Typography.Caption.copy(color = Colors.Muted2),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
