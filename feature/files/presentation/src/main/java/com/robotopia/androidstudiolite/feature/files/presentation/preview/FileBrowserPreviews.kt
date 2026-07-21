package com.robotopia.androidstudiolite.feature.files.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ClipboardMode
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ClipboardState
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreen
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal data class FileBrowserPreviewCase(
    private val label: String,
    val state: FileBrowserUiState,
) {
    override fun toString(): String = label
}

internal class FileBrowserPreviewProvider : PreviewParameterProvider<FileBrowserPreviewCase> {
    override fun getDisplayName(index: Int): String {
        return values.toList()[index].toString()
    }
    override val values = sequenceOf(
        FileBrowserPreviewCase(
            "loading",
            FileBrowserUiState(projectName = "MyApp", isLoading = true),
        ),
        FileBrowserPreviewCase(
            "empty",
            FileBrowserUiState(projectName = "MyApp", isLoading = false),
        ),
        FileBrowserPreviewCase(
            "add menu",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                addMenuOpen = true,
            ),
        ),
        FileBrowserPreviewCase(
            "filled",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
            ),
        ),
        FileBrowserPreviewCase(
            "nested path",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                currentRelativePath = "app/src/main",
                entries = listOf(
                    FsNode.Folder(name = "java", relativePath = "app/src/main/java"),
                    FsNode.Folder(name = "res", relativePath = "app/src/main/res"),
                ),
            ),
        ),
        FileBrowserPreviewCase(
            "create file",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                dialog = FileBrowserDialog.CreateFile(name = "MainActivity.kt"),
            ),
        ),
        FileBrowserPreviewCase(
            "create file field error",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                dialog = FileBrowserDialog.CreateFile(
                    name = "bad/name",
                    nameError = "Name contains invalid characters",
                ),
            ),
        ),
        FileBrowserPreviewCase(
            "rename field error",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                dialog = FileBrowserDialog.Rename(
                    item = previewEntries.last(),
                    name = "",
                    nameError = "Name is required",
                ),
            ),
        ),
        FileBrowserPreviewCase(
            "menu open",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                menuItem = previewEntries.first(),
            ),
        ),
        FileBrowserPreviewCase(
            "delete confirm",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                dialog = FileBrowserDialog.DeleteConfirm(previewEntries.last()),
            ),
        ),
        FileBrowserPreviewCase(
            "move bar",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                clipboard = ClipboardState(
                    mode = ClipboardMode.Cut,
                    relativePath = "build.gradle.kts",
                    node = previewEntries[2],
                ),
            ),
        ),
        FileBrowserPreviewCase(
            "copy bar",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                clipboard = ClipboardState(
                    mode = ClipboardMode.Copy,
                    relativePath = "settings.gradle.kts",
                    node = previewEntries[3],
                ),
            ),
        ),
        FileBrowserPreviewCase(
            "action error",
            FileBrowserUiState(
                projectName = "MyApp",
                isLoading = false,
                entries = previewEntries,
                actionError = "A file or folder with that name already exists",
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun FileBrowserPreview(
    @PreviewParameter(FileBrowserPreviewProvider::class) case: FileBrowserPreviewCase,
) {
    FileBrowserPreviewHost(case.state)
}

@Composable
private fun FileBrowserPreviewHost(state: FileBrowserUiState) {
    val scope = rememberCoroutineScope()
    val context = remember(scope) {
        FileBrowserScreenContext(
            updateState = {},
            fileExplorerService = PreviewFileExplorerService,
            onOpenFile = {},
            onNavigateBack = {},
            onRun = {},
            onOpenGit = {},
            scope = scope,
        )
    }
    context.FileBrowserScreen(state)
}

private val previewEntries = listOf(
    FsNode.Folder(name = "app", relativePath = "app"),
    FsNode.Folder(name = "gradle", relativePath = "gradle"),
    FsNode.File(name = "build.gradle.kts", relativePath = "build.gradle.kts"),
    FsNode.File(name = "settings.gradle.kts", relativePath = "settings.gradle.kts"),
)

private object PreviewFileExplorerService : FileExplorerService {
    override fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing> =
        emptyFlow()

    override suspend fun createFile(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): FsNode.File = error("Preview")

    override suspend fun createFolder(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): FsNode.Folder = error("Preview")

    override suspend fun rename(
        root: ProjectRoot,
        relativePath: String,
        newName: String,
    ): FsNode = error("Preview")

    override suspend fun move(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): FsNode = error("Preview")

    override suspend fun copy(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): FsNode = error("Preview")

    override suspend fun delete(root: ProjectRoot, relativePath: String) = error("Preview")

    override suspend fun readText(root: ProjectRoot, relativePath: String): String = error("Preview")

    override suspend fun writeText(
        root: ProjectRoot,
        relativePath: String,
        content: String,
    ) = error("Preview")

    override fun validateFileName(name: String): FileNameFieldErrors = FileNameFieldErrors()
}
