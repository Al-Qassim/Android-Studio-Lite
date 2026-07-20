package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.presentation.logic.credentialsOrNull
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

fun ProjectGitScreenContext.requestPull() {
    scope.launch { pull() }
}

fun ProjectGitScreenContext.requestPush() {
    scope.launch { push() }
}

fun ProjectGitScreenContext.requestFetch() {
    scope.launch { fetch() }
}

fun ProjectGitScreenContext.requestCheckout(branch: GitBranch) {
    scope.launch { checkout(branch.name) }
}

fun ProjectGitScreenContext.requestMerge(branchName: String) {
    scope.launch { merge(branchName) }
}

fun ProjectGitScreenContext.requestRename(oldName: String, newName: String) {
    scope.launch { renameBranch(oldName, newName) }
}

fun ProjectGitScreenContext.requestCreateBranch(name: String) {
    scope.launch { createBranch(name) }
}

fun ProjectGitScreenContext.requestDeleteBranch(branch: String) {
    scope.launch { deleteBranch(branch) }
}

fun ProjectGitScreenContext.openBranchMenu(branch: GitBranch) {
    updateState { copy(menuBranch = branch, actionError = null) }
}

fun ProjectGitScreenContext.dismissBranchMenu() {
    updateState { copy(menuBranch = null) }
}

fun ProjectGitScreenContext.openRename(branch: GitBranch) {
    updateState {
        copy(
            menuBranch = null,
            renameBranch = branch.name,
            renameValue = branch.name,
            renameError = null,
        )
    }
}

fun ProjectGitScreenContext.dismissRename() {
    updateState {
        copy(renameBranch = null, renameValue = "", renameError = null)
    }
}

fun ProjectGitScreenContext.setRenameValue(value: String) {
    updateState { copy(renameValue = value, renameError = null) }
}

fun ProjectGitScreenContext.openMergeConfirm(branch: GitBranch) {
    updateState {
        copy(menuBranch = null, mergeConfirmBranch = branch.name, actionError = null)
    }
}

fun ProjectGitScreenContext.dismissMergeConfirm() {
    updateState { copy(mergeConfirmBranch = null) }
}

fun ProjectGitScreenContext.openCreateBranch() {
    updateState {
        copy(
            showCreateBranch = true,
            createBranchValue = "",
            createBranchError = null,
            actionError = null,
        )
    }
}

fun ProjectGitScreenContext.dismissCreateBranch() {
    updateState {
        copy(showCreateBranch = false, createBranchValue = "", createBranchError = null)
    }
}

fun ProjectGitScreenContext.setCreateBranchValue(value: String) {
    updateState { copy(createBranchValue = value, createBranchError = null) }
}

fun ProjectGitScreenContext.openDeleteConfirm(branch: GitBranch) {
    updateState {
        copy(menuBranch = null, deleteConfirmBranch = branch.name, actionError = null)
    }
}

fun ProjectGitScreenContext.dismissDeleteConfirm() {
    updateState { copy(deleteConfirmBranch = null) }
}

fun ProjectGitScreenContext.clearToast() {
    updateState { copy(toastMessage = null) }
}

suspend fun ProjectGitScreenContext.pull() {
    runBusy("Pulled.") {
        gitService.pull(projectRoot, credentialsOrNull(authSession))
    }
}

suspend fun ProjectGitScreenContext.push() {
    runBusy("Pushed.") {
        gitService.push(projectRoot, credentialsOrNull(authSession))
    }
}

suspend fun ProjectGitScreenContext.fetch() {
    runBusy("Fetched remote branches.") {
        gitService.fetch(projectRoot, credentialsOrNull(authSession))
    }
}

suspend fun ProjectGitScreenContext.checkout(branchName: String) {
    updateState { copy(menuBranch = null) }
    runBusy("Checked out $branchName.") {
        gitService.checkout(projectRoot, branchName)
    }
}

suspend fun ProjectGitScreenContext.merge(branchName: String) {
    updateState { copy(mergeConfirmBranch = null) }
    runBusy("Merged $branchName.") {
        gitService.merge(projectRoot, branchName)
    }
}

suspend fun ProjectGitScreenContext.renameBranch(oldName: String, newName: String) {
    val trimmed = newName.trim()
    if (trimmed.isEmpty()) {
        updateState { copy(renameError = "Enter a branch name.") }
        return
    }
    runBusy("Renamed branch.") {
        gitService.renameBranch(projectRoot, oldName, trimmed)
        updateState { copy(renameBranch = null, renameValue = "", renameError = null) }
    }
}

suspend fun ProjectGitScreenContext.createBranch(name: String) {
    val trimmed = name.trim()
    if (trimmed.isEmpty()) {
        updateState { copy(createBranchError = "Enter a branch name.") }
        return
    }
    runBusy("Created $trimmed.") {
        gitService.createBranch(projectRoot, trimmed)
        updateState {
            copy(showCreateBranch = false, createBranchValue = "", createBranchError = null)
        }
    }
}

suspend fun ProjectGitScreenContext.deleteBranch(branch: String) {
    updateState { copy(deleteConfirmBranch = null) }
    runBusy("Deleted $branch.") {
        gitService.deleteBranch(projectRoot, branch)
    }
}

private suspend fun ProjectGitScreenContext.runBusy(
    successToast: String,
    block: suspend () -> Unit,
) {
    updateState { copy(isBusy = true, actionError = null) }
    try {
        block()
        refreshBranches(showLoading = false)
        updateState { copy(isBusy = false, toastMessage = successToast) }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        updateState {
            copy(
                isBusy = false,
                actionError = e.userMessageOrNull(TAG) ?: GENERIC_ERROR,
            )
        }
    }
}

private const val TAG = "ProjectGit"
private const val GENERIC_ERROR = "Something went wrong."
