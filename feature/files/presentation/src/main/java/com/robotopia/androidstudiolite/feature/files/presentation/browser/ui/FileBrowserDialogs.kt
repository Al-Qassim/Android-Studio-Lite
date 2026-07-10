package com.robotopia.androidstudiolite.feature.files.presentation.browser.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.robotopia.androidstudiolite.designsystem.component.DialogForm
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserScreenContext
import com.robotopia.androidstudiolite.feature.files.presentation.browser.FileBrowserUiState
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.confirmCreateFile
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.confirmCreateFolder
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.confirmDelete
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.confirmRename
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.dismissActionError
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.dismissDialog
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.onCreateFileNameChange
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.onCreateFolderNameChange
import com.robotopia.androidstudiolite.feature.files.presentation.browser.logic.onRenameNameChange

@Composable
internal fun FileBrowserScreenContext.FileBrowserDialogs(state: FileBrowserUiState) {
    when (val dialog = state.dialog) {
        is FileBrowserDialog.CreateFile -> {
            Dialog(onDismissRequest = { dismissDialog() }) {
                DialogForm(
                    title = "New file",
                    locationLabel = locationLabel(state.currentRelativePath),
                    fieldValue = dialog.name,
                    onFieldChange = { onCreateFileNameChange(state, it) },
                    primaryActionLabel = "Create",
                    fieldPlaceholder = "File name",
                    errorMessage = dialog.nameError,
                    onCancel = { dismissDialog() },
                    onPrimaryAction = { confirmCreateFile(state) },
                )
            }
        }

        is FileBrowserDialog.CreateFolder -> {
            Dialog(onDismissRequest = { dismissDialog() }) {
                DialogForm(
                    title = "New folder",
                    locationLabel = locationLabel(state.currentRelativePath),
                    fieldValue = dialog.name,
                    onFieldChange = { onCreateFolderNameChange(state, it) },
                    primaryActionLabel = "Create",
                    fieldPlaceholder = "Folder name",
                    errorMessage = dialog.nameError,
                    onCancel = { dismissDialog() },
                    onPrimaryAction = { confirmCreateFolder(state) },
                )
            }
        }

        is FileBrowserDialog.Rename -> {
            Dialog(onDismissRequest = { dismissDialog() }) {
                DialogForm(
                    title = if (dialog.item is FsNode.Folder) "Rename folder" else "Rename file",
                    fieldValue = dialog.name,
                    onFieldChange = { onRenameNameChange(state, it) },
                    primaryActionLabel = "Rename",
                    fieldPlaceholder = "New name",
                    errorMessage = dialog.nameError,
                    onCancel = { dismissDialog() },
                    onPrimaryAction = { confirmRename(state) },
                )
            }
        }

        is FileBrowserDialog.DeleteConfirm -> {
            Dialog(onDismissRequest = { dismissDialog() }) {
                DialogMessageAction(
                    title = if (dialog.item is FsNode.Folder) "Delete folder?" else "Delete file?",
                    message = "${dialog.item.name} will be permanently deleted. This cannot be undone.",
                    actionLabel = "Delete",
                    dangerAction = true,
                    onCancel = { dismissDialog() },
                    onAction = { confirmDelete(state) },
                )
            }
        }

        null -> Unit
    }

    state.actionError?.let { message ->
        Dialog(onDismissRequest = { dismissActionError() }) {
            DialogMessageAction(
                title = "Something went wrong",
                message = message,
                actionLabel = "OK",
                onCancel = { dismissActionError() },
                onAction = { dismissActionError() },
            )
        }
    }
}

private fun locationLabel(relativePath: String): String {
    val display = if (relativePath.isEmpty()) "/" else relativePath
    return "Location: $display"
}
