package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.robotopia.androidstudiolite.designsystem.component.DialogForm
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageStackedActions
import com.robotopia.androidstudiolite.feature.git.presentation.project.CheckoutOverwritePrompt
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.commitBeforeCheckout
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissAbortMergeConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissCheckoutOverwrite
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissCreateBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissDeleteConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissDiscardConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissMergeConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissRename
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissUndoCommitConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestAbortMerge
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestCreateBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestDeleteBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestDiscardAll
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestDiscardAndCheckout
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestDiscardFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestMerge
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestRename
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestUndoCommit
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setCreateBranchValue
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setRenameValue

@Composable
internal fun ProjectGitScreenContext.ProjectGitDialogs(state: ProjectGitUiState) {
    if (state.showCreateBranch) {
        val from = state.createBranchFrom
        Dialog(onDismissRequest = { dismissCreateBranch() }) {
            DialogForm(
                title = if (from != null) "New branch from $from" else "New branch",
                fieldValue = state.createBranchValue,
                onFieldChange = { setCreateBranchValue(it) },
                primaryActionLabel = "Create",
                onCancel = { dismissCreateBranch() },
                onPrimaryAction = {
                    requestCreateBranch(state.createBranchValue, state.createBranchFrom)
                },
                fieldPlaceholder = "feature/my-branch",
                errorMessage = state.createBranchError,
            )
        }
    }

    val renameBranch = state.renameBranch
    if (renameBranch != null) {
        val isRemote = renameBranch.contains('/')
        Dialog(onDismissRequest = { dismissRename() }) {
            DialogForm(
                title = if (isRemote) "Rename remote branch" else "Rename branch",
                fieldValue = state.renameValue,
                onFieldChange = { setRenameValue(it) },
                primaryActionLabel = "Rename",
                onCancel = { dismissRename() },
                onPrimaryAction = { requestRename(renameBranch, state.renameValue) },
                fieldPlaceholder = "branch-name",
                errorMessage = state.renameError,
            )
        }
    }

    val mergeBranch = state.mergeConfirmBranch
    if (mergeBranch != null) {
        Dialog(onDismissRequest = { dismissMergeConfirm() }) {
            DialogMessageAction(
                title = "Merge branch?",
                message = "Merge “$mergeBranch” into “${state.currentBranch}”.",
                actionLabel = "Merge",
                onCancel = { dismissMergeConfirm() },
                onAction = { requestMerge(mergeBranch) },
            )
        }
    }

    val deleteBranch = state.deleteConfirmBranch
    if (deleteBranch != null) {
        Dialog(onDismissRequest = { dismissDeleteConfirm() }) {
            DialogMessageAction(
                title = "Delete branch?",
                message = "Delete local branch “$deleteBranch”. This can’t be undone.",
                actionLabel = "Delete",
                onCancel = { dismissDeleteConfirm() },
                onAction = { requestDeleteBranch(deleteBranch) },
                dangerAction = true,
            )
        }
    }

    if (state.showAbortMergeConfirm) {
        Dialog(onDismissRequest = { dismissAbortMergeConfirm() }) {
            DialogMessageAction(
                title = "Abort merge?",
                message = "Discard the in-progress merge of “${state.mergeSourceBranch}” into “${state.currentBranch}” and return to a clean state.",
                actionLabel = "Abort merge",
                onCancel = { dismissAbortMergeConfirm() },
                onAction = { requestAbortMerge() },
                dangerAction = true,
            )
        }
    }

    val checkoutOverwrite = state.checkoutOverwrite
    if (checkoutOverwrite != null) {
        Dialog(onDismissRequest = { dismissCheckoutOverwrite() }) {
            DialogMessageStackedActions(
                title = "Local changes would be overwritten",
                message = checkoutOverwriteMessage(checkoutOverwrite),
                primaryLabel = "Commit first",
                onPrimary = { commitBeforeCheckout() },
                dangerLabel = "Discard & switch",
                onDanger = { requestDiscardAndCheckout(checkoutOverwrite.targetBranch) },
                onCancel = { dismissCheckoutOverwrite() },
            )
        }
    }

    val discardPath = state.discardConfirmPath
    if (discardPath != null) {
        Dialog(onDismissRequest = { dismissDiscardConfirm() }) {
            DialogMessageAction(
                title = "Discard changes?",
                message = "Discard local changes in “$discardPath”. This can’t be undone.",
                actionLabel = "Discard",
                onCancel = { dismissDiscardConfirm() },
                onAction = { requestDiscardFile(discardPath) },
                dangerAction = true,
            )
        }
    }

    if (state.showDiscardAllConfirm) {
        Dialog(onDismissRequest = { dismissDiscardConfirm() }) {
            DialogMessageAction(
                title = "Discard all changes?",
                message = "Discard all local changes in this project. This can’t be undone.",
                actionLabel = "Discard all",
                onCancel = { dismissDiscardConfirm() },
                onAction = { requestDiscardAll() },
                dangerAction = true,
            )
        }
    }

    if (state.showUndoCommitConfirm) {
        val tip = state.historyCommits.firstOrNull()
        val base = if (tip != null) {
            "Remove “${tip.subject}” (${tip.shortId}) from the branch. File contents stay as local changes."
        } else {
            "Remove the latest commit from the branch. File contents stay as local changes."
        }
        val message = if (state.hasRemote && state.aheadCount == 0) {
            "$base\n\nThis commit may already be on the remote. Undoing it only changes your local branch."
        } else {
            base
        }
        Dialog(onDismissRequest = { dismissUndoCommitConfirm() }) {
            DialogMessageAction(
                title = "Undo last commit?",
                message = message,
                actionLabel = "Undo commit",
                onCancel = { dismissUndoCommitConfirm() },
                onAction = { requestUndoCommit(state) },
                dangerAction = true,
            )
        }
    }
}

private fun checkoutOverwriteMessage(prompt: CheckoutOverwritePrompt): String {
    val paths = prompt.conflictingPaths
    val listed = when {
        paths.isEmpty() ->
            "Checking out “${prompt.targetBranch}” would overwrite local changes."
        paths.size <= 4 -> {
            val bullets = paths.joinToString("\n") { "• $it" }
            "Checking out “${prompt.targetBranch}” would overwrite:\n$bullets"
        }
        else -> {
            val shown = paths.take(3).joinToString("\n") { "• $it" }
            "Checking out “${prompt.targetBranch}” would overwrite:\n$shown\n• and ${paths.size - 3} more"
        }
    }
    return "$listed\n\nCommit your changes first, or discard them to switch."
}
