package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import com.robotopia.androidstudiolite.designsystem.component.DialogForm
import com.robotopia.androidstudiolite.designsystem.component.DialogMessageAction
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissCreateBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissDeleteConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissMergeConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissRename
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestCreateBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestDeleteBranch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestMerge
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestRename
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setCreateBranchValue
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setRenameValue

@Composable
internal fun ProjectGitScreenContext.ProjectGitDialogs(state: ProjectGitUiState) {
    if (state.showCreateBranch) {
        Dialog(onDismissRequest = { dismissCreateBranch() }) {
            DialogForm(
                title = "New branch",
                fieldValue = state.createBranchValue,
                onFieldChange = { setCreateBranchValue(it) },
                primaryActionLabel = "Create",
                onCancel = { dismissCreateBranch() },
                onPrimaryAction = { requestCreateBranch(state.createBranchValue) },
                fieldPlaceholder = "feature/my-branch",
                errorMessage = state.createBranchError,
            )
        }
    }

    val renameBranch = state.renameBranch
    if (renameBranch != null) {
        Dialog(onDismissRequest = { dismissRename() }) {
            DialogForm(
                title = "Rename branch",
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
}
