package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import com.robotopia.androidstudiolite.designsystem.component.ContextMenu
import com.robotopia.androidstudiolite.designsystem.component.CreateMenu
import com.robotopia.androidstudiolite.designsystem.component.DialogForm
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.FileRow
import com.robotopia.androidstudiolite.designsystem.component.FolderRow
import com.robotopia.androidstudiolite.designsystem.component.MoveBar
import com.robotopia.androidstudiolite.designsystem.component.PathBar
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitleAdd
import com.robotopia.androidstudiolite.designsystem.component.TransferBarMode
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.files.model.FsNode

@Composable
internal fun FileBrowserContent(
    state: FileBrowserUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onAddMenuDismiss: () -> Unit,
    onNewFileClick: () -> Unit,
    onNewFolderClick: () -> Unit,
    onPasteClick: () -> Unit,
    onClipboardCancel: () -> Unit,
    onFolderClick: (FsNode.Folder) -> Unit,
    onFileClick: (FsNode.File) -> Unit,
    onItemLongClick: (FsNode) -> Unit,
    onMenuDismiss: () -> Unit,
    onRenameMenuClick: (FsNode) -> Unit,
    onCopyMenuClick: (FsNode) -> Unit,
    onMoveMenuClick: (FsNode) -> Unit,
    onDeleteMenuClick: (FsNode) -> Unit,
    onDialogCancel: () -> Unit,
    onCreateFileNameChange: (String) -> Unit,
    onCreateFileConfirm: () -> Unit,
    onCreateFolderNameChange: (String) -> Unit,
    onCreateFolderConfirm: () -> Unit,
    onRenameNameChange: (String) -> Unit,
    onRenameConfirm: () -> Unit,
    onDeleteConfirm: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitleAdd(
            title = state.projectName,
            onBackClick = onBackClick,
            onAddClick = onAddClick,
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
            FileBrowserBody(
                entries = state.entries,
                menuItem = state.menuItem,
                onFolderClick = onFolderClick,
                onFileClick = onFileClick,
                onItemLongClick = onItemLongClick,
                onMenuDismiss = onMenuDismiss,
                onRenameMenuClick = onRenameMenuClick,
                onCopyMenuClick = onCopyMenuClick,
                onMoveMenuClick = onMoveMenuClick,
                onDeleteMenuClick = onDeleteMenuClick,
            )
        }
        state.clipboard?.let { clipboard ->
            MoveBar(
                name = clipboard.node.name,
                mode = when (clipboard.mode) {
                    ClipboardMode.Cut -> TransferBarMode.Move
                    ClipboardMode.Copy -> TransferBarMode.Copy
                },
                onCancel = onClipboardCancel,
                onMoveHere = onPasteClick,
            )
        }
    }

    if (state.addMenuOpen) {
        AddMenuPopup(
            onDismiss = onAddMenuDismiss,
            onNewFileClick = onNewFileClick,
            onNewFolderClick = onNewFolderClick,
        )
    }

    when (val dialog = state.dialog) {
        is FileBrowserDialog.CreateFile -> CreateFileDialog(
            locationLabel = locationLabel(state.currentRelativePath),
            name = dialog.name,
            nameError = dialog.nameError,
            onNameChange = onCreateFileNameChange,
            onCancel = onDialogCancel,
            onConfirm = onCreateFileConfirm,
        )

        is FileBrowserDialog.CreateFolder -> CreateFolderDialog(
            locationLabel = locationLabel(state.currentRelativePath),
            name = dialog.name,
            nameError = dialog.nameError,
            onNameChange = onCreateFolderNameChange,
            onCancel = onDialogCancel,
            onConfirm = onCreateFolderConfirm,
        )

        is FileBrowserDialog.Rename -> RenameDialog(
            item = dialog.item,
            name = dialog.name,
            nameError = dialog.nameError,
            onNameChange = onRenameNameChange,
            onCancel = onDialogCancel,
            onConfirm = onRenameConfirm,
        )

        is FileBrowserDialog.DeleteConfirm -> DeleteDialog(
            item = dialog.item,
            onCancel = onDialogCancel,
            onConfirm = onDeleteConfirm,
        )

        null -> Unit
    }

    state.actionError?.let { message ->
        ActionErrorDialog(
            message = message,
            onDismiss = onErrorDismiss,
        )
    }
}

@Composable
private fun FileBrowserBody(
    entries: List<FsNode>,
    menuItem: FsNode?,
    onFolderClick: (FsNode.Folder) -> Unit,
    onFileClick: (FsNode.File) -> Unit,
    onItemLongClick: (FsNode) -> Unit,
    onMenuDismiss: () -> Unit,
    onRenameMenuClick: (FsNode) -> Unit,
    onCopyMenuClick: (FsNode) -> Unit,
    onMoveMenuClick: (FsNode) -> Unit,
    onDeleteMenuClick: (FsNode) -> Unit,
) {
    if (entries.isEmpty()) {
        FileBrowserEmpty()
    } else {
        FileBrowserList(
            entries = entries,
            menuItem = menuItem,
            onFolderClick = onFolderClick,
            onFileClick = onFileClick,
            onItemLongClick = onItemLongClick,
            onMenuDismiss = onMenuDismiss,
            onRenameMenuClick = onRenameMenuClick,
            onCopyMenuClick = onCopyMenuClick,
            onMoveMenuClick = onMoveMenuClick,
            onDeleteMenuClick = onDeleteMenuClick,
        )
    }
}

@Composable
private fun FileBrowserEmpty() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        EmptyState(
            title = "Folder is empty",
            hint = "Tap + to create a file or folder.",
        )
    }
}

@Composable
private fun FileBrowserList(
    entries: List<FsNode>,
    menuItem: FsNode?,
    onFolderClick: (FsNode.Folder) -> Unit,
    onFileClick: (FsNode.File) -> Unit,
    onItemLongClick: (FsNode) -> Unit,
    onMenuDismiss: () -> Unit,
    onRenameMenuClick: (FsNode) -> Unit,
    onCopyMenuClick: (FsNode) -> Unit,
    onMoveMenuClick: (FsNode) -> Unit,
    onDeleteMenuClick: (FsNode) -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(entries, key = { it.relativePath }) { entry ->
            FileBrowserListItem(
                entry = entry,
                menuOpen = menuItem?.relativePath == entry.relativePath,
                onFolderClick = onFolderClick,
                onFileClick = onFileClick,
                onItemLongClick = onItemLongClick,
                onMenuDismiss = onMenuDismiss,
                onRenameMenuClick = onRenameMenuClick,
                onCopyMenuClick = onCopyMenuClick,
                onMoveMenuClick = onMoveMenuClick,
                onDeleteMenuClick = onDeleteMenuClick,
            )
        }
        item { FileBrowserFooterHint() }
    }
}

@Composable
private fun FileBrowserListItem(
    entry: FsNode,
    menuOpen: Boolean,
    onFolderClick: (FsNode.Folder) -> Unit,
    onFileClick: (FsNode.File) -> Unit,
    onItemLongClick: (FsNode) -> Unit,
    onMenuDismiss: () -> Unit,
    onRenameMenuClick: (FsNode) -> Unit,
    onCopyMenuClick: (FsNode) -> Unit,
    onMoveMenuClick: (FsNode) -> Unit,
    onDeleteMenuClick: (FsNode) -> Unit,
) {
    Box {
        when (entry) {
            is FsNode.Folder -> {
                FolderRow(
                    name = entry.name,
                    selected = menuOpen,
                    onClick = { onFolderClick(entry) },
                    onLongClick = { onItemLongClick(entry) },
                )
            }

            is FsNode.File -> {
                FileRow(
                    name = entry.name,
                    selected = menuOpen,
                    showChevron = false,
                    onClick = { onFileClick(entry) },
                    onLongClick = { onItemLongClick(entry) },
                )
            }
        }
        if (menuOpen) {
            ItemContextMenu(
                onRename = { onRenameMenuClick(entry) },
                onMove = { onMoveMenuClick(entry) },
                onCopy = { onCopyMenuClick(entry) },
                onDelete = { onDeleteMenuClick(entry) },
                onDismiss = onMenuDismiss,
            )
        }
    }
}

@Composable
private fun AddMenuPopup(
    onDismiss: () -> Unit,
    onNewFileClick: () -> Unit,
    onNewFolderClick: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        CreateMenu(
            onNewFile = onNewFileClick,
            onNewFolder = onNewFolderClick,
            modifier = Modifier.padding(top = 48.dp, end = 12.dp),
        )
    }
}

@Composable
private fun ItemContextMenu(
    onRename: () -> Unit,
    onMove: () -> Unit,
    onCopy: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        ContextMenu(
            onRename = onRename,
            onMove = onMove,
            onCopy = onCopy,
            onDelete = onDelete,
            modifier = Modifier.padding(top = 4.dp, end = 16.dp),
        )
    }
}

@Composable
private fun CreateFileDialog(
    locationLabel: String,
    name: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        DialogForm(
            title = "New file",
            locationLabel = locationLabel,
            fieldValue = name,
            onFieldChange = onNameChange,
            primaryActionLabel = "Create",
            fieldPlaceholder = "File name",
            errorMessage = nameError,
            onCancel = onCancel,
            onPrimaryAction = onConfirm,
        )
    }
}

@Composable
private fun CreateFolderDialog(
    locationLabel: String,
    name: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        DialogForm(
            title = "New folder",
            locationLabel = locationLabel,
            fieldValue = name,
            onFieldChange = onNameChange,
            primaryActionLabel = "Create",
            fieldPlaceholder = "Folder name",
            errorMessage = nameError,
            onCancel = onCancel,
            onPrimaryAction = onConfirm,
        )
    }
}

@Composable
private fun RenameDialog(
    item: FsNode,
    name: String,
    nameError: String?,
    onNameChange: (String) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        DialogForm(
            title = if (item is FsNode.Folder) "Rename folder" else "Rename file",
            fieldValue = name,
            onFieldChange = onNameChange,
            primaryActionLabel = "Rename",
            fieldPlaceholder = "New name",
            errorMessage = nameError,
            onCancel = onCancel,
            onPrimaryAction = onConfirm,
        )
    }
}

@Composable
private fun DeleteDialog(
    item: FsNode,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = onCancel) {
        DialogMessageAction(
            title = if (item is FsNode.Folder) "Delete folder?" else "Delete file?",
            message = "${item.name} will be permanently deleted. This cannot be undone.",
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

@Composable
private fun FileBrowserFooterHint() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = "Tap a folder to open, a file to edit",
            style = Typography.Caption.copy(
                color = Colors.Muted2,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = "Long-press for rename, move, copy, or delete",
            style = Typography.Caption.copy(
                color = Colors.Muted2,
                textAlign = TextAlign.Center,
            ),
        )
    }
}

private fun relativePathSegments(relativePath: String): List<String> =
    relativePath.split('/').filter { it.isNotEmpty() }

private fun locationLabel(relativePath: String): String {
    val display = if (relativePath.isEmpty()) "/" else relativePath
    return "Location: $display"
}

private val previewEntries = listOf(
    FsNode.Folder(name = "app", relativePath = "app"),
    FsNode.Folder(name = "gradle", relativePath = "gradle"),
    FsNode.File(name = "build.gradle.kts", relativePath = "build.gradle.kts"),
    FsNode.File(name = "settings.gradle.kts", relativePath = "settings.gradle.kts"),
)

@Composable
private fun FileBrowserPreviewHost(state: FileBrowserUiState) {
    FileBrowserContent(
        state = state,
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
        onClipboardCancel = {},
        onFolderClick = {},
        onFileClick = {},
        onItemLongClick = {},
        onMenuDismiss = {},
        onRenameMenuClick = {},
        onCopyMenuClick = {},
        onMoveMenuClick = {},
        onDeleteMenuClick = {},
        onDialogCancel = {},
        onCreateFileNameChange = {},
        onCreateFileConfirm = {},
        onCreateFolderNameChange = {},
        onCreateFolderConfirm = {},
        onRenameNameChange = {},
        onRenameConfirm = {},
        onDeleteConfirm = {},
        onErrorDismiss = {},
    )
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
            currentRelativePath = "",
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
