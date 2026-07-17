package com.robotopia.androidstudiolite.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.CodeSample
import com.robotopia.androidstudiolite.designsystem.component.ContextMenu
import com.robotopia.androidstudiolite.designsystem.component.CreateMenu
import com.robotopia.androidstudiolite.designsystem.component.DialogForm
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.FileRow
import com.robotopia.androidstudiolite.designsystem.component.FolderRow
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.MoveBar
import com.robotopia.androidstudiolite.designsystem.component.PathBar
import com.robotopia.androidstudiolite.designsystem.component.ProjectCard
import com.robotopia.androidstudiolite.designsystem.component.ProjectMenu
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.Toast
import com.robotopia.androidstudiolite.designsystem.component.ToastVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitleAdd
import com.robotopia.androidstudiolite.designsystem.component.TopBarEditorMore
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.component.TransferBarMode
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/**
 * Phone-sized example screens built only from `:designsystem` components.
 * Open this file in Android Studio Preview and flip cases in the preview picker.
 */
enum class ExampleScreenCase {
    ProjectsEmpty,
    ProjectsList,
    ProjectsMenu,
    ProjectsDeleteConfirm,
    NewProjectForm,
    FilesBrowse,
    FilesEmpty,
    FilesCreateMenu,
    FilesNewFileDialog,
    FilesMoveBar,
    EditorClean,
    EditorDirtyToast,
    Loading,
}

private class ExampleScreenCaseProvider : PreviewParameterProvider<ExampleScreenCase> {
    override val values: Sequence<ExampleScreenCase> = ExampleScreenCase.entries.asSequence()

    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

@Preview(
    name = "Example screens",
    showBackground = true,
    backgroundColor = 0xFF1E1F22,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun ExampleScreensPreview(
    @PreviewParameter(ExampleScreenCaseProvider::class) case: ExampleScreenCase,
) {
    ExamplePhoneShell {
        when (case) {
            ExampleScreenCase.ProjectsEmpty -> ProjectsEmptyScreen()
            ExampleScreenCase.ProjectsList -> ProjectsListScreen()
            ExampleScreenCase.ProjectsMenu -> ProjectsMenuScreen()
            ExampleScreenCase.ProjectsDeleteConfirm -> ProjectsDeleteConfirmScreen()
            ExampleScreenCase.NewProjectForm -> NewProjectFormScreen()
            ExampleScreenCase.FilesBrowse -> FilesBrowseScreen()
            ExampleScreenCase.FilesEmpty -> FilesEmptyScreen()
            ExampleScreenCase.FilesCreateMenu -> FilesCreateMenuScreen()
            ExampleScreenCase.FilesNewFileDialog -> FilesNewFileDialogScreen()
            ExampleScreenCase.FilesMoveBar -> FilesMoveBarScreen()
            ExampleScreenCase.EditorClean -> EditorCleanScreen()
            ExampleScreenCase.EditorDirtyToast -> EditorDirtyToastScreen()
            ExampleScreenCase.Loading -> LoadingScreen()
        }
    }
}

@Composable
private fun ExamplePhoneShell(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        content()
    }
}

@Composable
private fun Scrim(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f)),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun ProjectsEmptyScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarTitleAction(title = "Projects", onSettingsClick = {})
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
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarTitleAction(title = "Projects", onSettingsClick = {})
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProjectCard(
                name = "HelloCompose",
                packageName = "com.example.hellocompose",
                meta = "Opened just now",
                onMenuClick = {},
            )
            ProjectCard(
                name = "TodoApp",
                packageName = "com.example.todo",
                meta = "Opened yesterday",
                onMenuClick = {},
            )
            ProjectCard(
                name = "WeatherDemo",
                packageName = "com.example.weather",
                meta = "Opened 3 days ago",
                onMenuClick = {},
            )
        }
    }
}

@Composable
private fun ProjectsMenuScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        ProjectsListScreen()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 140.dp, end = 20.dp),
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
        Scrim {
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
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarBackTitle(title = "New project")
        Column(
            modifier = Modifier
                .fillMaxSize()
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
    }
}

@Composable
private fun FilesBrowseScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarBackTitleAdd(title = "MyApp", onRunClick = {})
        PathBar(segments = listOf("/", "app", "src", "main"))
        FolderRow(name = "java")
        FolderRow(name = "res")
        FileRow(name = "AndroidManifest.xml", showChevron = false)
        Spacer(modifier = Modifier.weight(1f))
        BasicText(
            text = "Tap a folder to open, a file to edit",
            style = Typography.Caption.copy(color = Colors.Muted2),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 8.dp),
        )
        BasicText(
            text = "Long-press for rename, move, copy, or delete",
            style = Typography.Caption.copy(color = Colors.Muted2),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun FilesEmptyScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarBackTitleAdd(title = "MyApp", onRunClick = {})
        PathBar(segments = listOf("/", "app", "empty"))
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            EmptyState(
                title = "Folder is empty",
                hint = "Tap + to create a file or folder.",
            )
        }
    }
}

@Composable
private fun FilesCreateMenuScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        FilesBrowseScreen()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, end = 12.dp),
            contentAlignment = Alignment.TopEnd,
        ) {
            CreateMenu()
        }
    }
}

@Composable
private fun FilesNewFileDialogScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        FilesBrowseScreen()
        Scrim {
            DialogForm(
                title = "New file",
                fieldValue = "MainActivity.kt",
                onFieldChange = {},
                primaryActionLabel = "Create",
                locationLabel = "Location: /app/src/main",
                fieldPlaceholder = "Name",
            )
        }
    }
}

@Composable
private fun FilesMoveBarScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBarBackTitleAdd(title = "MyApp", onRunClick = {})
            PathBar(segments = listOf("/"))
            FolderRow(name = "app", selected = true)
            FolderRow(name = "gradle")
            FileRow(name = "settings.gradle.kts", showChevron = false)
            Spacer(modifier = Modifier.weight(1f))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp, start = 72.dp),
            contentAlignment = Alignment.TopStart,
        ) {
            ContextMenu()
        }
        MoveBar(
            name = "build.gradle.kts",
            mode = TransferBarMode.Move,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun EditorCleanScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Editor),
    ) {
        TopBarEditorMore(fileName = "MainActivity.kt", onRunClick = {})
        CodeSample(gutter = "1", code = "package demo")
        CodeSample(gutter = "2", code = "")
        CodeSample(gutter = "3", code = "fun main() {")
        CodeSample(gutter = "4", code = "    println(\"Hello\")", stringHighlight = true)
        CodeSample(gutter = "5", code = "}")
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun EditorDirtyToastScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Colors.Editor),
        ) {
            TopBarEditorMore(
                fileName = "MainActivity.kt",
                isDirty = true,
                onRunClick = {},
            )
            CodeSample(gutter = "1", code = "package demo")
            CodeSample(gutter = "2", code = "")
            CodeSample(gutter = "3", code = "fun main() {")
            CodeSample(gutter = "4", code = "    println(\"Hello ASL\")", stringHighlight = true)
            CodeSample(gutter = "5", code = "}")
            Spacer(modifier = Modifier.weight(1f))
        }
        Toast(
            message = "File saved",
            variant = ToastVariant.Success,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
        )
    }
}

@Composable
private fun LoadingScreen() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopBarBackTitle(title = "Build")
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator(label = "Preparing workspace…")
        }
        Button(
            label = "Cancel",
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            variant = ButtonVariant.Secondary,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}
