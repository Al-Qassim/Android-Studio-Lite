package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.MoveBar
import com.robotopia.androidstudiolite.designsystem.component.PathBar
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitleAdd
import com.robotopia.androidstudiolite.designsystem.component.TransferBarMode
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.collectListing
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.clearClipboard
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.navigateUp
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.openAddMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.pasteClipboard
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ui.FileBrowserAddMenu
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ui.FileBrowserBody
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ui.FileBrowserDialogs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
internal fun FileBrowserScreenContext.FileBrowserScreen(state: FileBrowserUiState) {
    LaunchedEffect(state.root, state.currentRelativePath) {
        collectListing(
            root = state.root,
            relativePath = state.currentRelativePath,
        )
    }

    BackHandler { navigateUp(state) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitleAdd(
            title = state.projectName,
            onBackClick = { navigateUp(state) },
            onAddClick = { openAddMenu() },
        )
        val pathSegments = relativePathSegments(state.currentRelativePath)
        if (pathSegments.isNotEmpty()) {
            PathBar(segments = pathSegments)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            FileBrowserBody(state)
        }
        state.clipboard?.let { clipboard ->
            MoveBar(
                name = clipboard.node.name,
                mode = when (clipboard.mode) {
                    ClipboardMode.Cut -> TransferBarMode.Move
                    ClipboardMode.Copy -> TransferBarMode.Copy
                },
                onCancel = { clearClipboard() },
                onMoveHere = { pasteClipboard(state) },
            )
        }
    }

    FileBrowserAddMenu(state)
    FileBrowserDialogs(state)
}

private fun relativePathSegments(relativePath: String): List<String> =
    relativePath.split('/').filter { it.isNotEmpty() }

private val previewEntries = listOf(
    FsNode.Folder(name = "app", relativePath = "app"),
    FsNode.Folder(name = "gradle", relativePath = "gradle"),
    FsNode.File(name = "build.gradle.kts", relativePath = "build.gradle.kts"),
    FsNode.File(name = "settings.gradle.kts", relativePath = "settings.gradle.kts"),
)

@Composable
private fun FileBrowserPreviewHost(state: FileBrowserUiState) {
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

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · empty",
)
@Composable
private fun FileBrowserEmptyPreview() {
    FileBrowserPreviewHost(state = FileBrowserUiState(projectName = "MyApp"))
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · filled",
)
@Composable
private fun FileBrowserFilledPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · nested path",
)
@Composable
private fun FileBrowserNestedPathPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            currentRelativePath = "app/src/main",
            entries = listOf(
                FsNode.Folder(name = "java", relativePath = "app/src/main/java"),
                FsNode.Folder(name = "res", relativePath = "app/src/main/res"),
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · create file",
)
@Composable
private fun FileBrowserCreateFilePreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            dialog = FileBrowserDialog.CreateFile(name = "MainActivity.kt"),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · create file field error",
)
@Composable
private fun FileBrowserCreateFileFieldErrorPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            dialog = FileBrowserDialog.CreateFile(
                name = "bad/name",
                nameError = "Name contains invalid characters",
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · rename field error",
)
@Composable
private fun FileBrowserRenameFieldErrorPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            dialog = FileBrowserDialog.Rename(
                item = previewEntries.last(),
                name = "",
                nameError = "Name is required",
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · menu open",
)
@Composable
private fun FileBrowserMenuPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            menuItem = previewEntries.first(),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · delete confirm",
)
@Composable
private fun FileBrowserDeletePreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            dialog = FileBrowserDialog.DeleteConfirm(previewEntries.last()),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · move bar",
)
@Composable
private fun FileBrowserMoveBarPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            clipboard = ClipboardState(
                mode = ClipboardMode.Cut,
                relativePath = "build.gradle.kts",
                node = previewEntries[2],
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · copy bar",
)
@Composable
private fun FileBrowserCopyBarPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            clipboard = ClipboardState(
                mode = ClipboardMode.Copy,
                relativePath = "settings.gradle.kts",
                node = previewEntries[3],
            ),
        ),
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · action error",
)
@Composable
private fun FileBrowserActionErrorPreview() {
    FileBrowserPreviewHost(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            actionError = "A file or folder with that name already exists",
        ),
    )
}
