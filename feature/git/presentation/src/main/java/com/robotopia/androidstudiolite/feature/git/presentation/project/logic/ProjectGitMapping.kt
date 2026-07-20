package com.robotopia.androidstudiolite.feature.git.presentation.project.logic

import com.robotopia.androidstudiolite.feature.git.api.GitCommitFileInfo
import com.robotopia.androidstudiolite.feature.git.api.GitCommitInfo
import com.robotopia.androidstudiolite.feature.git.api.GitDiffLineInfo
import com.robotopia.androidstudiolite.feature.git.api.GitDiffLineType
import com.robotopia.androidstudiolite.feature.git.api.GitFileChangeKind
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitCommitFileChange
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitCommitSummary
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import java.util.concurrent.TimeUnit

fun GitStatusSnapshot.toChangeFiles(): List<GitChangeFile> {
    val byPath = linkedMapOf<String, GitChangeFile>()
    for (path in conflicting.sorted()) {
        byPath[path] = GitChangeFile(path, GitChangeKind.Conflict, staged = false)
    }
    for (path in added.sorted()) {
        byPath.putIfAbsent(path, GitChangeFile(path, GitChangeKind.Added, staged = true))
    }
    for (path in changed.sorted()) {
        byPath.putIfAbsent(path, GitChangeFile(path, GitChangeKind.Modified, staged = true))
    }
    for (path in removed.sorted()) {
        byPath.putIfAbsent(path, GitChangeFile(path, GitChangeKind.Deleted, staged = true))
    }
    for (path in modified.sorted()) {
        byPath.putIfAbsent(path, GitChangeFile(path, GitChangeKind.Modified, staged = false))
    }
    for (path in missing.sorted()) {
        byPath.putIfAbsent(path, GitChangeFile(path, GitChangeKind.Deleted, staged = false))
    }
    for (path in untracked.sorted()) {
        byPath.putIfAbsent(path, GitChangeFile(path, GitChangeKind.Untracked, staged = false))
    }
    return byPath.values.toList()
}

fun GitDiffLineInfo.toUiLine(): GitDiffLine = GitDiffLine(
    kind = when (type) {
        GitDiffLineType.Context -> GitDiffLineKind.Context
        GitDiffLineType.Add -> GitDiffLineKind.Add
        GitDiffLineType.Remove -> GitDiffLineKind.Remove
    },
    text = text,
    oldLine = oldLine,
    newLine = newLine,
)

fun GitCommitInfo.toSummary(nowEpochMs: Long = System.currentTimeMillis()): GitCommitSummary =
    GitCommitSummary(
        id = id,
        shortId = shortId,
        subject = subject,
        authorName = authorName,
        authoredRelative = relativeTimeLabel(authoredAtEpochMs, nowEpochMs),
    )

fun GitCommitFileInfo.toUiFile(): GitCommitFileChange = GitCommitFileChange(
    path = path,
    kind = when (kind) {
        GitFileChangeKind.Added -> GitChangeKind.Added
        GitFileChangeKind.Deleted -> GitChangeKind.Deleted
        GitFileChangeKind.Modified -> GitChangeKind.Modified
    },
)

fun relativeTimeLabel(epochMs: Long, nowEpochMs: Long = System.currentTimeMillis()): String {
    val delta = (nowEpochMs - epochMs).coerceAtLeast(0L)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(delta)
    val hours = TimeUnit.MILLISECONDS.toHours(delta)
    val days = TimeUnit.MILLISECONDS.toDays(delta)
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        days < 14 -> "${days}d ago"
        days < 60 -> "${days / 7}w ago"
        else -> "${days / 30}mo ago"
    }
}
