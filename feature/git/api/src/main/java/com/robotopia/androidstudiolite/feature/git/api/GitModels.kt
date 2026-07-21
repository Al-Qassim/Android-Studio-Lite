package com.robotopia.androidstudiolite.feature.git.api

/** Remotes, tracking, and in-progress merge facts for a local repo. */
data class GitRepositoryInfo(
    val hasRemote: Boolean,
    /** HTTPS GitHub page for `origin` when the URL is a GitHub remote. */
    val remoteHtmlUrl: String?,
    val aheadCount: Int,
    val behindCount: Int,
    val isMerging: Boolean,
)

data class GitMergeResult(
    val conflicts: Boolean,
    val conflictingPaths: Set<String> = emptySet(),
)

data class GitCommitInfo(
    val id: String,
    val shortId: String,
    val subject: String,
    val authorName: String,
    val authoredAtEpochMs: Long,
)

enum class GitFileChangeKind {
    Modified,
    Added,
    Deleted,
}

data class GitCommitFileInfo(
    val path: String,
    val kind: GitFileChangeKind,
)

enum class GitDiffLineType {
    Context,
    Add,
    Remove,
}

data class GitDiffLineInfo(
    val type: GitDiffLineType,
    val text: String,
    val oldLine: Int? = null,
    val newLine: Int? = null,
)
