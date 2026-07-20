package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitCommitFileChange
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitCommitSummary
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openCommit
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openCommitFileDiff

@Composable
internal fun ProjectGitScreenContext.ProjectGitHistoryBody(state: ProjectGitUiState) {
    if (state.historyCommits.isEmpty()) {
        EmptyState(
            title = "No commits yet",
            hint = "Commits on ${state.currentBranch.ifBlank { "this branch" }} will show here.",
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "history-branch") {
            BasicText(
                text = "On ${state.currentBranch.ifBlank { "—" }}",
                style = Typography.Caption.copy(color = Theme.colors.Muted),
            )
        }
        items(state.historyCommits, key = { it.id }) { commit ->
            HistoryCommitRow(
                commit = commit,
                enabled = !state.isBusy,
                onOpen = { openCommit(commit) },
            )
        }
    }
}

@Composable
internal fun ProjectGitScreenContext.ProjectGitCommitDetailBody(state: ProjectGitUiState) {
    val commit = state.selectedCommit ?: return
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 12.dp,
            bottom = 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "commit-meta") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BasicText(
                    text = commit.subject,
                    style = Typography.BodyStrong.copy(color = Theme.colors.Text),
                )
                BasicText(
                    text = "${commit.authorName} · ${commit.authoredRelative}",
                    style = Typography.Caption.copy(color = Theme.colors.Muted),
                )
                BasicText(
                    text = commit.shortId,
                    style = Typography.Code.copy(color = Theme.colors.Muted2),
                )
            }
        }
        item(key = "files-header") {
            BasicText(
                text = if (state.selectedCommitFiles.isEmpty()) {
                    "No files"
                } else {
                    "Changed files (${state.selectedCommitFiles.size})"
                },
                style = Typography.Caption.copy(color = Theme.colors.Muted),
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        items(state.selectedCommitFiles, key = { it.path }) { file ->
            CommitFileRow(
                file = file,
                enabled = !state.isBusy,
                onOpen = { openCommitFileDiff(file) },
            )
        }
    }
}

@Composable
private fun HistoryCommitRow(
    commit: GitCommitSummary,
    enabled: Boolean,
    onOpen: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.Surface, shape)
            .border(1.dp, Theme.colors.Border, shape)
            .clickable(enabled = enabled, onClick = onOpen)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BasicText(
            text = commit.subject,
            style = Typography.BodyStrong.copy(color = Theme.colors.Text),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = "${commit.authorName} · ${commit.authoredRelative}",
                style = Typography.Caption.copy(color = Theme.colors.Muted),
            )
            BasicText(
                text = commit.shortId,
                style = Typography.Caption.copy(color = Theme.colors.Muted2),
            )
        }
    }
}

@Composable
private fun CommitFileRow(
    file: GitCommitFileChange,
    enabled: Boolean,
    onOpen: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.Surface, shape)
            .border(1.dp, Theme.colors.Border, shape)
            .clickable(enabled = enabled, onClick = onOpen)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        BasicText(
            text = file.path,
            style = Typography.BodyStrong.copy(color = Theme.colors.Text),
            modifier = Modifier.weight(1f),
        )
        BasicText(
            text = commitFileBadge(file.kind),
            style = Typography.Caption.copy(color = commitFileColor(file.kind)),
        )
    }
}

private fun commitFileBadge(kind: GitChangeKind): String = when (kind) {
    GitChangeKind.Modified -> "M"
    GitChangeKind.Added -> "A"
    GitChangeKind.Deleted -> "D"
    GitChangeKind.Untracked -> "U"
    GitChangeKind.Conflict -> "C"
}

@Composable
private fun commitFileColor(kind: GitChangeKind) = when (kind) {
    GitChangeKind.Modified -> Theme.colors.Primary
    GitChangeKind.Added -> Theme.colors.Run
    GitChangeKind.Deleted -> Theme.colors.Danger
    GitChangeKind.Untracked -> Theme.colors.Muted
    GitChangeKind.Conflict -> Theme.colors.Danger
}
