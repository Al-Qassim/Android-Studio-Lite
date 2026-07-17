package com.robotopia.androidstudiolite.designsystem.preview.files

import androidx.compose.foundation.layout.Box
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
import com.robotopia.androidstudiolite.designsystem.component.ContextMenu
import com.robotopia.androidstudiolite.designsystem.component.CreateMenu
import com.robotopia.androidstudiolite.designsystem.component.DialogForm
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.FileRow
import com.robotopia.androidstudiolite.designsystem.component.FolderRow
import com.robotopia.androidstudiolite.designsystem.component.InsetDivider
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.MoveBar
import com.robotopia.androidstudiolite.designsystem.component.PathBar
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitleAdd
import com.robotopia.androidstudiolite.designsystem.component.TransferBarMode
import com.robotopia.androidstudiolite.designsystem.preview.ExamplePreviewBackground
import com.robotopia.androidstudiolite.designsystem.preview.PreviewScrim
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class FilesExampleCase {
    Browse,
    Empty,
    CreateMenu,
    NewFileDialog,
    MoveBar,
}

private class FilesExampleCaseProvider : PreviewParameterProvider<FilesExampleCase> {
    override val values = FilesExampleCase.entries.asSequence()
    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

@Preview(
    name = "Files",
    showBackground = true,
    backgroundColor = ExamplePreviewBackground,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun FilesExamplePreview(
    @PreviewParameter(FilesExampleCaseProvider::class) case: FilesExampleCase,
) {
    when (case) {
        FilesExampleCase.Browse -> FilesBrowseScreen()
        FilesExampleCase.Empty -> FilesEmptyScreen()
        FilesExampleCase.CreateMenu -> FilesCreateMenuScreen()
        FilesExampleCase.NewFileDialog -> FilesNewFileDialogScreen()
        FilesExampleCase.MoveBar -> FilesMoveBarScreen()
    }
}

@Composable
private fun FilesBrowseScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitleAdd(title = "MyApp", onRunClick = {}) },
    ) {
        PathBar(segments = listOf("/", "app", "src", "main"))
        InsetDivider()
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
    IslandScaffold(
        topBar = { TopBarBackTitleAdd(title = "MyApp", onRunClick = {}) },
    ) {
        PathBar(segments = listOf("/", "app", "empty"))
        InsetDivider()
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
        PreviewScrim {
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
        IslandScaffold(
            topBar = { TopBarBackTitleAdd(title = "MyApp", onRunClick = {}) },
            footer = {
                MoveBar(name = "build.gradle.kts", mode = TransferBarMode.Move)
            },
        ) {
            PathBar(segments = listOf("/"))
            InsetDivider()
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
    }
}
