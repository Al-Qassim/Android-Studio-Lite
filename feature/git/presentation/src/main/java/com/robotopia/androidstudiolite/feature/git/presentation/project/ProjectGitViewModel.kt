package com.robotopia.androidstudiolite.feature.git.presentation.project

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import kotlinx.coroutines.flow.MutableStateFlow

enum class ProjectGitTab {
    Changes,
    History,
    Branches,
}

/** One row in the current-branch commit history list. */
data class GitCommitSummary(
    val id: String,
    val shortId: String,
    val subject: String,
    val authorName: String,
    /** Short relative time for the list (UI shell). */
    val authoredRelative: String,
)

/** A file touched by a selected commit. */
data class GitCommitFileChange(
    val path: String,
    val kind: GitChangeKind,
)

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

/** Visual paint for a conflict-buffer line (cleared after the user edits that line). */
enum class ConflictLinePaint {
    None,
    Marker,
    Ours,
    Theirs,
}

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
    /** Commits on [currentBranch] only (no full DAG). Newest first. */
    val historyCommits: List<GitCommitSummary> = emptyList(),
    val selectedCommit: GitCommitSummary? = null,
    val selectedCommitFiles: List<GitCommitFileChange> = emptyList(),
    val commitMessage: String = "",
    val commitError: String? = null,
    val selectedDiffPath: String? = null,
    val diffTitle: String = "",
    val diffLines: List<GitDiffLine> = emptyList(),
    val isDiffLoading: Boolean = false,
    /** True when the open file editor is a conflict resolve view. */
    val isConflictEditor: Boolean = false,
    /** Editable conflict buffer (markers included) while [isConflictEditor] is true. */
    val conflictText: String = "",
    /**
     * Per-line conflict paint for [conflictText]. Cleared for a line when the user edits it.
     * Same size as `conflictText` line count.
     */
    val conflictLinePaint: List<ConflictLinePaint> = emptyList(),
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
    /** True when `origin` (or any remote) is configured. */
    val hasRemote: Boolean = false,
    /** Full-screen Publish to GitHub form (local repo with no remote). */
    val showPublish: Boolean = false,
    /** Preview/runtime flag: build account connected for GitHub HTTPS. */
    val publishAccountConnected: Boolean = false,
    val publishProviderName: String = "GitHub",
    val publishRepoName: String = "",
    val publishPrivate: Boolean = true,
    val publishNameError: String? = null,
    val publishError: String? = null,
    /** True when publish should be blocked until the user commits at least once. */
    val publishNeedsCommit: Boolean = false,
    /** Commits ahead of upstream (0 when unknown / no tracking). */
    val aheadCount: Int = 0,
    /** Commits behind upstream. */
    val behindCount: Int = 0,
    /** File-row overflow menu on Changes (`path`). */
    val changeFileMenuPath: String? = null,
    /**
     * Discard confirm: `null` path = discard all local changes;
     * non-null = discard that file only.
     */
    val discardConfirmPath: String? = null,
    val showDiscardAllConfirm: Boolean = false,
    val showUndoCommitConfirm: Boolean = false,
    /**
     * HTTPS GitHub repo page when published (`https://github.com/owner/repo`).
     * Used for Open on GitHub actions.
     */
    val remoteHtmlUrl: String? = null,
) {
    val unresolvedConflictCount: Int
        get() = changeFiles.count { it.kind == GitChangeKind.Conflict && !it.staged }

    /** True while the conflict buffer still contains Git conflict markers. */
    val hasOpenConflictHunks: Boolean
        get() = isConflictEditor && conflictTextHasMarkers(conflictText)
}

/** Standard Git conflict marker lines (`<<<<<<<`, `=======`, `>>>>>>>`). */
fun conflictTextHasMarkers(text: String): Boolean =
    text.lineSequence().any { line ->
        line.startsWith("<<<<<<<") ||
            line.startsWith("=======") ||
            line.startsWith(">>>>>>>")
    }

class ProjectGitViewModel : ViewModel() {
    val uiState = MutableStateFlow(ProjectGitUiState())
}
