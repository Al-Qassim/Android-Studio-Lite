package com.robotopia.androidstudiolite.feature.files.presentation.browser.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ClipboardMode
import com.robotopia.androidstudiolite.feature.files.presentation.browser.ClipboardState
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState
import kotlinx.coroutines.launch

private const val TAG = "FileBrowser"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"

internal fun FileBrowserScreenContext.openAddMenu() {
    updateState { copy(addMenuOpen = true, menuItem = null) }
}

internal fun FileBrowserScreenContext.dismissAddMenu() {
    updateState { copy(addMenuOpen = false) }
}

internal fun FileBrowserScreenContext.openCreateFileDialog() {
    updateState {
        copy(
            addMenuOpen = false,
            dialog = FileBrowserDialog.CreateFile(),
        )
    }
}

internal fun FileBrowserScreenContext.openCreateFolderDialog() {
    updateState {
        copy(
            addMenuOpen = false,
            dialog = FileBrowserDialog.CreateFolder(),
        )
    }
}

internal fun FileBrowserScreenContext.openFolder(folder: FsNode.Folder) {
    updateState {
        copy(
            currentRelativePath = folder.relativePath,
            menuItem = null,
            addMenuOpen = false,
        )
    }
}

internal fun FileBrowserScreenContext.openFile(file: FsNode.File) {
    updateState { copy(menuItem = null) }
    onOpenFile(file.relativePath)
}

internal fun FileBrowserScreenContext.openItemMenu(item: FsNode) {
    updateState { copy(menuItem = item, addMenuOpen = false) }
}

internal fun FileBrowserScreenContext.dismissItemMenu() {
    updateState { copy(menuItem = null) }
}

internal fun FileBrowserScreenContext.openRenameDialog(item: FsNode) {
    updateState {
        copy(
            menuItem = null,
            dialog = FileBrowserDialog.Rename(item = item, name = item.name),
        )
    }
}

internal fun FileBrowserScreenContext.copyItem(item: FsNode) {
    updateState {
        copy(
            menuItem = null,
            clipboard = ClipboardState(
                mode = ClipboardMode.Copy,
                relativePath = item.relativePath,
                node = item,
            ),
        )
    }
}

internal fun FileBrowserScreenContext.moveItem(item: FsNode) {
    updateState {
        copy(
            menuItem = null,
            clipboard = ClipboardState(
                mode = ClipboardMode.Cut,
                relativePath = item.relativePath,
                node = item,
            ),
        )
    }
}

internal fun FileBrowserScreenContext.openDeleteDialog(item: FsNode) {
    updateState {
        copy(
            menuItem = null,
            dialog = FileBrowserDialog.DeleteConfirm(item),
        )
    }
}

internal fun FileBrowserScreenContext.dismissDialog() {
    updateState { copy(dialog = null) }
}

internal fun FileBrowserScreenContext.clearClipboard() {
    updateState { copy(clipboard = null) }
}

internal fun FileBrowserScreenContext.dismissActionError() {
    updateState { copy(actionError = null) }
}

internal fun FileBrowserScreenContext.onCreateFileNameChange(
    state: FileBrowserUiState,
    name: String,
) {
    val nameError = fileExplorerService.validateFileName(name).name
    val dialog = state.dialog as? FileBrowserDialog.CreateFile ?: return
    updateState { copy(dialog = dialog.copy(name = name, nameError = nameError)) }
}

internal fun FileBrowserScreenContext.onCreateFolderNameChange(
    state: FileBrowserUiState,
    name: String,
) {
    val nameError = fileExplorerService.validateFileName(name).name
    val dialog = state.dialog as? FileBrowserDialog.CreateFolder ?: return
    updateState { copy(dialog = dialog.copy(name = name, nameError = nameError)) }
}

internal fun FileBrowserScreenContext.onRenameNameChange(
    state: FileBrowserUiState,
    name: String,
) {
    val dialog = state.dialog as? FileBrowserDialog.Rename ?: return
    val nameError = fileExplorerService.validateFileName(name).name
    updateState { copy(dialog = dialog.copy(name = name, nameError = nameError)) }
}

internal fun FileBrowserScreenContext.confirmCreateFile(state: FileBrowserUiState) {
    val dialog = state.dialog as? FileBrowserDialog.CreateFile ?: return
    val parentPath = state.currentRelativePath
    confirmNamedMutation(
        name = dialog.name,
        onInvalid = { nameError ->
            updateState { copy(dialog = dialog.copy(nameError = nameError)) }
        },
    ) {
        fileExplorerService.createFile(state.root, parentPath, dialog.name)
    }
}

internal fun FileBrowserScreenContext.confirmCreateFolder(state: FileBrowserUiState) {
    val dialog = state.dialog as? FileBrowserDialog.CreateFolder ?: return
    val parentPath = state.currentRelativePath
    confirmNamedMutation(
        name = dialog.name,
        onInvalid = { nameError ->
            updateState { copy(dialog = dialog.copy(nameError = nameError)) }
        },
    ) {
        fileExplorerService.createFolder(state.root, parentPath, dialog.name)
    }
}

internal fun FileBrowserScreenContext.confirmRename(state: FileBrowserUiState) {
    val dialog = state.dialog as? FileBrowserDialog.Rename ?: return
    confirmNamedMutation(
        name = dialog.name,
        onInvalid = { nameError ->
            updateState { copy(dialog = dialog.copy(nameError = nameError)) }
        },
    ) {
        fileExplorerService.rename(state.root, dialog.item.relativePath, dialog.name)
    }
}

internal fun FileBrowserScreenContext.confirmDelete(state: FileBrowserUiState) {
    val dialog = state.dialog as? FileBrowserDialog.DeleteConfirm ?: return
    updateState { copy(dialog = null) }
    scope.launch {
        runMutation {
            fileExplorerService.delete(state.root, dialog.item.relativePath)
        }
    }
}

internal fun FileBrowserScreenContext.pasteClipboard(state: FileBrowserUiState) {
    val clipboard = state.clipboard ?: return
    updateState { copy(addMenuOpen = false) }
    scope.launch {
        val destination = state.currentRelativePath
        val succeeded = runMutation {
            when (clipboard.mode) {
                ClipboardMode.Copy ->
                    fileExplorerService.copy(state.root, clipboard.relativePath, destination)
                ClipboardMode.Cut ->
                    fileExplorerService.move(state.root, clipboard.relativePath, destination)
            }
        }
        if (succeeded) {
            updateState { copy(clipboard = null) }
        }
    }
}

private fun FileBrowserScreenContext.confirmNamedMutation(
    name: String,
    onInvalid: (nameError: String) -> Unit,
    block: suspend () -> Unit,
) {
    val nameError = fileExplorerService.validateFileName(name).name
    if (nameError != null) {
        onInvalid(nameError)
        return
    }
    updateState { copy(dialog = null) }
    scope.launch {
        runMutation(block = block)
    }
}

private suspend fun FileBrowserScreenContext.runMutation(
    block: suspend () -> Unit,
): Boolean =
    runCatching { block() }
        .onFailure { error ->
            updateState {
                copy(actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE)
            }
        }
        .isSuccess
