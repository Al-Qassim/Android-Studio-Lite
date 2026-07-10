package com.robotopia.androidstudiolite.feature.files.presentation.browser

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.model.parentRelativePathOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun FileBrowserScreen(
    root: ProjectRoot,
    projectName: String,
    initialRelativePath: String,
    fileExplorerService: FileExplorerService,
    onOpenFile: (relativePath: String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: FileBrowserViewModel = koinViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    LaunchedEffect(root, projectName, initialRelativePath) {
        viewModel.uiState.update {
            it.copy(
                projectName = projectName,
                currentRelativePath = initialRelativePath,
            )
        }
    }

    LaunchedEffect(root, state.currentRelativePath) {
        collectListing(
            fileExplorerService = fileExplorerService,
            root = root,
            relativePath = state.currentRelativePath,
            uiState = viewModel.uiState,
        )
    }

    FileBrowserContent(
        state = state,
        onBackClick = {
            val parent = parentRelativePathOrNull(state.currentRelativePath)
            if (parent == null) {
                onNavigateBack()
            } else {
                viewModel.uiState.update {
                    it.copy(
                        currentRelativePath = parent,
                        menuItem = null,
                        addMenuOpen = false,
                    )
                }
            }
        },
        onAddClick = {
            viewModel.uiState.update { it.copy(addMenuOpen = true, menuItem = null) }
        },
        onAddMenuDismiss = {
            viewModel.uiState.update { it.copy(addMenuOpen = false) }
        },
        onNewFileClick = {
            viewModel.uiState.update {
                it.copy(
                    addMenuOpen = false,
                    dialog = FileBrowserDialog.CreateFile(),
                )
            }
        },
        onNewFolderClick = {
            viewModel.uiState.update {
                it.copy(
                    addMenuOpen = false,
                    dialog = FileBrowserDialog.CreateFolder(),
                )
            }
        },
        onPasteClick = {
            val clipboard = viewModel.uiState.value.clipboard ?: return@FileBrowserContent
            viewModel.uiState.update { it.copy(addMenuOpen = false) }
            scope.launch {
                pasteClipboard(
                    fileExplorerService = fileExplorerService,
                    root = root,
                    uiState = viewModel.uiState,
                    clipboard = clipboard,
                )
            }
        },
        onClipboardCancel = {
            viewModel.uiState.update { it.copy(clipboard = null) }
        },
        onFolderClick = { folder ->
            viewModel.uiState.update {
                it.copy(
                    currentRelativePath = folder.relativePath,
                    menuItem = null,
                    addMenuOpen = false,
                )
            }
        },
        onFileClick = { file ->
            viewModel.uiState.update { it.copy(menuItem = null) }
            onOpenFile(file.relativePath)
        },
        onItemLongClick = { item ->
            viewModel.uiState.update { it.copy(menuItem = item, addMenuOpen = false) }
        },
        onMenuDismiss = {
            viewModel.uiState.update { it.copy(menuItem = null) }
        },
        onRenameMenuClick = { item ->
            viewModel.uiState.update {
                it.copy(
                    menuItem = null,
                    dialog = FileBrowserDialog.Rename(item = item, name = item.name),
                )
            }
        },
        onCopyMenuClick = { item ->
            viewModel.uiState.update {
                it.copy(
                    menuItem = null,
                    clipboard = ClipboardState(
                        mode = ClipboardMode.Copy,
                        relativePath = item.relativePath,
                        node = item,
                    ),
                )
            }
        },
        onMoveMenuClick = { item ->
            viewModel.uiState.update {
                it.copy(
                    menuItem = null,
                    clipboard = ClipboardState(
                        mode = ClipboardMode.Cut,
                        relativePath = item.relativePath,
                        node = item,
                    ),
                )
            }
        },
        onDeleteMenuClick = { item ->
            viewModel.uiState.update {
                it.copy(
                    menuItem = null,
                    dialog = FileBrowserDialog.DeleteConfirm(item),
                )
            }
        },
        onDialogCancel = {
            viewModel.uiState.update { it.copy(dialog = null) }
        },
        onCreateFileNameChange = { name ->
            val nameError = fileExplorerService.validateFileName(name).name
            updateCreateFileDialog(viewModel.uiState, name, nameError)
        },
        onCreateFileConfirm = {
            val dialog = viewModel.uiState.value.dialog as? FileBrowserDialog.CreateFile
                ?: return@FileBrowserContent
            val parentPath = viewModel.uiState.value.currentRelativePath
            confirmNamedMutation(
                name = dialog.name,
                fileExplorerService = fileExplorerService,
                uiState = viewModel.uiState,
                scope = scope,
                onInvalid = { nameError ->
                    updateCreateFileDialog(viewModel.uiState, dialog.name, nameError)
                },
            ) {
                fileExplorerService.createFile(root, parentPath, dialog.name)
            }
        },
        onCreateFolderNameChange = { name ->
            val nameError = fileExplorerService.validateFileName(name).name
            updateCreateFolderDialog(viewModel.uiState, name, nameError)
        },
        onCreateFolderConfirm = {
            val dialog = viewModel.uiState.value.dialog as? FileBrowserDialog.CreateFolder
                ?: return@FileBrowserContent
            val parentPath = viewModel.uiState.value.currentRelativePath
            confirmNamedMutation(
                name = dialog.name,
                fileExplorerService = fileExplorerService,
                uiState = viewModel.uiState,
                scope = scope,
                onInvalid = { nameError ->
                    updateCreateFolderDialog(viewModel.uiState, dialog.name, nameError)
                },
            ) {
                fileExplorerService.createFolder(root, parentPath, dialog.name)
            }
        },
        onRenameNameChange = { name ->
            val dialog = viewModel.uiState.value.dialog as? FileBrowserDialog.Rename
                ?: return@FileBrowserContent
            val nameError = fileExplorerService.validateFileName(name).name
            viewModel.uiState.update {
                it.copy(dialog = dialog.copy(name = name, nameError = nameError))
            }
        },
        onRenameConfirm = {
            val dialog = viewModel.uiState.value.dialog as? FileBrowserDialog.Rename
                ?: return@FileBrowserContent
            confirmNamedMutation(
                name = dialog.name,
                fileExplorerService = fileExplorerService,
                uiState = viewModel.uiState,
                scope = scope,
                onInvalid = { nameError ->
                    viewModel.uiState.update {
                        it.copy(dialog = dialog.copy(nameError = nameError))
                    }
                },
            ) {
                fileExplorerService.rename(root, dialog.item.relativePath, dialog.name)
            }
        },
        onDeleteConfirm = {
            val dialog = viewModel.uiState.value.dialog as? FileBrowserDialog.DeleteConfirm
                ?: return@FileBrowserContent
            viewModel.uiState.update { it.copy(dialog = null) }
            scope.launch {
                runMutation(uiState = viewModel.uiState) {
                    fileExplorerService.delete(root, dialog.item.relativePath)
                }
            }
        },
        onErrorDismiss = {
            viewModel.uiState.update { it.copy(actionError = null) }
        },
    )
}

private suspend fun collectListing(
    fileExplorerService: FileExplorerService,
    root: ProjectRoot,
    relativePath: String,
    uiState: MutableStateFlow<FileBrowserUiState>,
) {
    fileExplorerService.observeListing(root, relativePath)
        .catch { error ->
            uiState.update {
                it.copy(
                    entries = emptyList(),
                    menuItem = null,
                    actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
                )
            }
        }
        .collect { listing ->
            uiState.update { state ->
                state.copy(
                    currentRelativePath = listing.currentRelativePath,
                    entries = listing.entries,
                    menuItem = state.menuItem?.takeIf { menu ->
                        listing.entries.any { it.relativePath == menu.relativePath }
                    },
                )
            }
        }
}

private suspend fun pasteClipboard(
    fileExplorerService: FileExplorerService,
    root: ProjectRoot,
    uiState: MutableStateFlow<FileBrowserUiState>,
    clipboard: ClipboardState,
) {
    val destination = uiState.value.currentRelativePath
    val succeeded = runMutation(uiState = uiState) {
        when (clipboard.mode) {
            ClipboardMode.Copy ->
                fileExplorerService.copy(root, clipboard.relativePath, destination)
            ClipboardMode.Cut ->
                fileExplorerService.move(root, clipboard.relativePath, destination)
        }
    }
    if (succeeded) {
        uiState.update { it.copy(clipboard = null) }
    }
}

private fun confirmNamedMutation(
    name: String,
    fileExplorerService: FileExplorerService,
    uiState: MutableStateFlow<FileBrowserUiState>,
    scope: CoroutineScope,
    onInvalid: (nameError: String) -> Unit,
    block: suspend () -> Unit,
) {
    val nameError = fileExplorerService.validateFileName(name).name
    if (nameError != null) {
        onInvalid(nameError)
        return
    }
    uiState.update { it.copy(dialog = null) }
    scope.launch {
        runMutation(uiState = uiState, block = block)
    }
}

private suspend fun runMutation(
    uiState: MutableStateFlow<FileBrowserUiState>,
    block: suspend () -> Unit,
): Boolean =
    runCatching { block() }
        .onFailure { error ->
            uiState.update {
                it.copy(actionError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE)
            }
        }
        .isSuccess

private fun updateCreateFileDialog(
    uiState: MutableStateFlow<FileBrowserUiState>,
    name: String,
    nameError: String?,
) {
    val dialog = uiState.value.dialog as? FileBrowserDialog.CreateFile ?: return
    uiState.update { it.copy(dialog = dialog.copy(name = name, nameError = nameError)) }
}

private fun updateCreateFolderDialog(
    uiState: MutableStateFlow<FileBrowserUiState>,
    name: String,
    nameError: String?,
) {
    val dialog = uiState.value.dialog as? FileBrowserDialog.CreateFolder ?: return
    uiState.update { it.copy(dialog = dialog.copy(name = name, nameError = nameError)) }
}

private const val TAG = "FileBrowser"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"
