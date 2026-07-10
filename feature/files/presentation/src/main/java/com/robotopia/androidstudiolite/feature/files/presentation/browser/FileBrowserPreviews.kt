package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

internal val fileBrowserPreviewEntries = listOf(
    FsNode.Folder(name = "app", relativePath = "app"),
    FsNode.Folder(name = "gradle", relativePath = "gradle"),
    FsNode.File(name = "build.gradle.kts", relativePath = "build.gradle.kts"),
    FsNode.File(name = "settings.gradle.kts", relativePath = "settings.gradle.kts"),
)

@Composable
internal fun FileBrowserPreviewHost(state: FileBrowserUiState) {
    val scope = rememberCoroutineScope()
    val context = remember(scope) {
        FileBrowserScreenContext(
            updateState = {},
            fileExplorerService = PreviewFileExplorerService,
            onOpenFile = {},
            onNavigateBack = {},
            scope = scope,
        )
    }
    context.FileBrowserScreen(state)
}

internal fun fileBrowserEmptyPreviewState() =
    FileBrowserUiState(projectName = "MyApp")

internal fun fileBrowserFilledPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
    )

internal fun fileBrowserNestedPathPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        currentRelativePath = "app/src/main",
        entries = listOf(
            FsNode.Folder(name = "java", relativePath = "app/src/main/java"),
            FsNode.Folder(name = "res", relativePath = "app/src/main/res"),
        ),
    )

internal fun fileBrowserCreateFilePreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        dialog = FileBrowserDialog.CreateFile(name = "MainActivity.kt"),
    )

internal fun fileBrowserCreateFileFieldErrorPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        dialog = FileBrowserDialog.CreateFile(
            name = "bad/name",
            nameError = "Name contains invalid characters",
        ),
    )

internal fun fileBrowserRenameFieldErrorPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        dialog = FileBrowserDialog.Rename(
            item = fileBrowserPreviewEntries.last(),
            name = "",
            nameError = "Name is required",
        ),
    )

internal fun fileBrowserMenuPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        menuItem = fileBrowserPreviewEntries.first(),
    )

internal fun fileBrowserDeletePreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        dialog = FileBrowserDialog.DeleteConfirm(fileBrowserPreviewEntries.last()),
    )

internal fun fileBrowserMoveBarPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        clipboard = ClipboardState(
            mode = ClipboardMode.Cut,
            relativePath = "build.gradle.kts",
            node = fileBrowserPreviewEntries[2],
        ),
    )

internal fun fileBrowserCopyBarPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        clipboard = ClipboardState(
            mode = ClipboardMode.Copy,
            relativePath = "settings.gradle.kts",
            node = fileBrowserPreviewEntries[3],
        ),
    )

internal fun fileBrowserActionErrorPreviewState() =
    FileBrowserUiState(
        projectName = "MyApp",
        entries = fileBrowserPreviewEntries,
        actionError = "A file or folder with that name already exists",
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
