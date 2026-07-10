package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import com.robotopia.androidstudiolite.designsystem.component.TopBarPathAdd
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
        TopBarPathAdd(
            pathSegments = pathSegments(state.projectName, state.currentRelativePath),
            onBackClick = onBackClick,
            onAddClick = onAddClick,
        )
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

    if (state.addMenuOpen) {
        AddMenuPopup(
            hasClipboard = state.clipboard != null,
            onDismiss = onAddMenuDismiss,
            onNewFileClick = onNewFileClick,
            onNewFolderClick = onNewFolderClick,
            onPasteClick = onPasteClick,
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

@OptIn(ExperimentalFoundationApi::class)
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
                    modifier = Modifier.combinedClickable(
                        onClick = { onFolderClick(entry) },
                        onLongClick = { onItemLongClick(entry) },
                    ),
                )
            }

            is FsNode.File -> {
                FileRow(
                    name = entry.name,
                    selected = menuOpen,
                    showChevron = false,
                    modifier = Modifier.combinedClickable(
                        onClick = { onFileClick(entry) },
                        onLongClick = { onItemLongClick(entry) },
                    ),
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
    hasClipboard: Boolean,
    onDismiss: () -> Unit,
    onNewFileClick: () -> Unit,
    onNewFolderClick: () -> Unit,
    onPasteClick: () -> Unit,
) {
    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true),
    ) {
        Column(
            modifier = Modifier.padding(top = 48.dp, end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            CreateMenu(
                onNewFile = onNewFileClick,
                onNewFolder = onNewFolderClick,
            )
            if (hasClipboard) {
                PasteMenu(onPaste = onPasteClick)
            }
        }
    }
}

@Composable
private fun PasteMenu(
    onPaste: () -> Unit,
) {
    val shape = RoundedCornerShape(10.dp)
    BasicText(
        text = "Paste",
        style = Typography.Menu.copy(color = Colors.Text),
        modifier = Modifier
            .width(180.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(Colors.Menu)
            .clickable(onClick = onPaste)
            .padding(horizontal = 14.dp, vertical = 12.dp),
    )
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

private fun pathSegments(projectName: String, relativePath: String): List<String> {
    val tail = relativePath.split('/').filter { it.isNotEmpty() }
    return listOf(projectName) + tail
}

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

@Preview(
    showBackground = true,
    backgroundColor = 0xFF12171C,
    widthDp = 360,
    heightDp = 640,
    name = "Browser · empty",
)
@Composable
private fun FileBrowserEmptyPreview() {
    FileBrowserContent(
        state = FileBrowserUiState(projectName = "MyApp"),
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
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
    name = "Browser · filled",
)
@Composable
private fun FileBrowserFilledPreview() {
    FileBrowserContent(
        state = FileBrowserUiState(
            projectName = "MyApp",
            currentRelativePath = "",
            entries = previewEntries,
        ),
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
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
    name = "Browser · create file",
)
@Composable
private fun FileBrowserCreateFilePreview() {
    FileBrowserContent(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            dialog = FileBrowserDialog.CreateFile(name = "MainActivity.kt"),
        ),
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
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
    name = "Browser · menu open",
)
@Composable
private fun FileBrowserMenuPreview() {
    FileBrowserContent(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            menuItem = previewEntries.first(),
        ),
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
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
    name = "Browser · delete confirm",
)
@Composable
private fun FileBrowserDeletePreview() {
    FileBrowserContent(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            dialog = FileBrowserDialog.DeleteConfirm(previewEntries.last()),
        ),
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
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
    name = "Browser · action error",
)
@Composable
private fun FileBrowserActionErrorPreview() {
    FileBrowserContent(
        state = FileBrowserUiState(
            projectName = "MyApp",
            entries = previewEntries,
            actionError = "A file or folder with that name already exists",
        ),
        onBackClick = {},
        onAddClick = {},
        onAddMenuDismiss = {},
        onNewFileClick = {},
        onNewFolderClick = {},
        onPasteClick = {},
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
