package com.robotopia.androidstudiolite.feature.git.presentation.project

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import kotlinx.coroutines.flow.MutableStateFlow

enum class ProjectGitTab {
    Changes,
    Branches,
}

enum class GitChangeKind {
    Modified,
    Added,
    Deleted,
    Untracked,
    Conflict,
}

data class GitChangeFile(
    val path: String,
    val kind: GitChangeKind,
    val staged: Boolean = true,
)

enum class GitDiffLineKind {
    Context,
    Add,
    Remove,
    /** `<<<<<<<`, `=======`, `>>>>>>>` markers. */
    ConflictMarker,
    ConflictOurs,
    ConflictTheirs,
}

data class GitDiffLine(
    val kind: GitDiffLineKind,
    val text: String,
    /** Line number in the old/base side; null for pure additions and markers. */
    val oldLine: Int? = null,
    /** Line number in the new/working-tree side; null for pure deletions and markers. */
    val newLine: Int? = null,
)

/** Checkout blocked because local changes would be overwritten on [targetBranch]. */
data class CheckoutOverwritePrompt(
    val targetBranch: String,
    val conflictingPaths: List<String>,
)

data class ProjectGitUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    /** Project has no `.git` yet — show init CTA instead of Changes/Branches. */
    val needsInit: Boolean = false,
    val tab: ProjectGitTab = ProjectGitTab.Changes,
    val currentBranch: String = "",
    val recentBranches: List<GitBranch> = emptyList(),
    val localBranches: List<GitBranch> = emptyList(),
    val remoteBranches: List<GitBranch> = emptyList(),
    val changeFiles: List<GitChangeFile> = emptyList(),
    val commitMessage: String = "",
    val commitError: String? = null,
    val selectedDiffPath: String? = null,
    val diffTitle: String = "",
    val diffLines: List<GitDiffLine> = emptyList(),
    val isDiffLoading: Boolean = false,
    /** True when the open file editor is a conflict resolve view. */
    val isConflictEditor: Boolean = false,
    /**
     * Non-null while a merge is in progress (conflicts or merge commit pending).
     * Holds the branch being merged into [currentBranch].
     */
    val mergeSourceBranch: String? = null,
    val showAbortMergeConfirm: Boolean = false,
    /**
     * Shown when checkout would overwrite local changes.
     * Choices: cancel, commit first (Changes tab), or discard & switch.
     */
    val checkoutOverwrite: CheckoutOverwritePrompt? = null,
    val isBusy: Boolean = false,
    val actionError: String? = null,
    val toastMessage: String? = null,
    val menuBranch: GitBranch? = null,
    /** Distinguishes the same branch name in Recent vs Local vs Remote rows. */
    val menuBranchKey: String? = null,
    val renameBranch: String? = null,
    val renameValue: String = "",
    val renameError: String? = null,
    val mergeConfirmBranch: String? = null,
    val showCreateBranch: Boolean = false,
    /** Branch the new branch should start from (local or `origin/…`). */
    val createBranchFrom: String? = null,
    val createBranchValue: String = "",
    val createBranchError: String? = null,
    val deleteConfirmBranch: String? = null,
) {
    val unresolvedConflictCount: Int
        get() = changeFiles.count { it.kind == GitChangeKind.Conflict && !it.staged }

    /** True while the open conflict buffer still has markers or side hunks. */
    val hasOpenConflictHunks: Boolean
        get() = diffLines.any {
            it.kind == GitDiffLineKind.ConflictMarker ||
                it.kind == GitDiffLineKind.ConflictOurs ||
                it.kind == GitDiffLineKind.ConflictTheirs
        }
}

class ProjectGitViewModel : ViewModel() {
    val uiState = MutableStateFlow(ProjectGitUiState())
}
