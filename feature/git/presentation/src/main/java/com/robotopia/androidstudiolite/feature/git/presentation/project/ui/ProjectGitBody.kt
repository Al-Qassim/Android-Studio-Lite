package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.IconButton
import com.robotopia.androidstudiolite.designsystem.component.IconButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.icon.IconWarning
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openBranchMenu
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openPublish
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestFetch
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestPull
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestPush
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestRetryLoad
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.selectTab

@Composable
internal fun ProjectGitScreenContext.ProjectGitBody(state: ProjectGitUiState) {
    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }
        }

        state.needsInit -> {
            ProjectGitInitBody(state)
        }

        state.selectedDiffPath != null -> {
            ProjectGitDiffBody(state)
        }

        state.selectedCommit != null -> {
            ProjectGitCommitDetailBody(state)
        }

        state.showPublish -> {
            ProjectGitPublishBody(state)
        }

        state.loadError != null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    IconWarning(tint = Theme.colors.Danger, size = 32.dp)
                    EmptyState(
                        title = "Couldn't open Git",
                        hint = state.loadError,
                    )
                    Button(
                        label = "Retry",
                        onClick = { requestRetryLoad() },
                        variant = ButtonVariant.Primary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                ProjectGitTabBar(
                    selected = state.tab,
                    changesCount = state.changeFiles.size,
                    onSelect = { selectTab(it) },
                )
                when (state.tab) {
                    ProjectGitTab.Changes -> ProjectGitChangesBody(state)
                    ProjectGitTab.History -> ProjectGitHistoryBody(state)
                    ProjectGitTab.Branches -> ProjectGitBranchesBody(state)
                }
            }
        }
    }
}

@Composable
internal fun ProjectGitScreenContext.ProjectGitBranchesBody(state: ProjectGitUiState) {
    // One LazyColumn so bottom contentPadding clears the island corner (nested
    // Column+weight lists still clipped the last row against the rounded clip).
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(key = "branches-chrome") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BasicText(
                    text = "Current branch: ${state.currentBranch}",
                    style = Typography.BodyStrong.copy(color = Theme.colors.Text),
                )
                if (state.hasRemote && (state.aheadCount > 0 || state.behindCount > 0)) {
                    BasicText(
                        text = syncStatusLabel(state.aheadCount, state.behindCount),
                        style = Typography.Caption.copy(color = Theme.colors.Muted),
                    )
                }
                if (state.hasRemote) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Button(
                            label = "Fetch",
                            onClick = { requestFetch() },
                            variant = if (state.isBusy) {
                                ButtonVariant.Disabled
                            } else {
                                ButtonVariant.Secondary
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Button(
                            label = "Pull",
                            onClick = { requestPull() },
                            variant = if (state.isBusy) {
                                ButtonVariant.Disabled
                            } else {
                                ButtonVariant.Secondary
                            },
                            modifier = Modifier.weight(1f),
                        )
                        Button(
                            label = "Push",
                            onClick = { requestPush() },
                            variant = if (state.isBusy) {
                                ButtonVariant.Disabled
                            } else {
                                ButtonVariant.Secondary
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Button(
                        label = "Publish on GitHub",
                        onClick = { openPublish() },
                        variant = if (state.isBusy) {
                            ButtonVariant.Disabled
                        } else {
                            ButtonVariant.Secondary
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                if (state.actionError != null) {
                    BasicText(
                        text = state.actionError,
                        style = Typography.Caption.copy(color = Theme.colors.Danger),
                    )
                }
            }
        }
        if (state.recentBranches.isNotEmpty()) {
            item(key = "header-recent") {
                SectionHeader("Recent")
            }
            items(state.recentBranches, key = { "recent-${it.name}" }) { branch ->
                val rowKey = "recent-${branch.name}"
                BranchRow(
                    branch = branch,
                    rowKey = rowKey,
                    menuOpen = state.menuBranchKey == rowKey,
                    enabled = !state.isBusy,
                )
            }
        }
        item(key = "header-local") {
            SectionHeader("Local")
        }
        if (state.localBranches.isEmpty()) {
            item(key = "empty-local") {
                EmptyHint("No local branches")
            }
        } else {
            items(state.localBranches, key = { "local-${it.name}" }) { branch ->
                val rowKey = "local-${branch.name}"
                BranchRow(
                    branch = branch,
                    rowKey = rowKey,
                    menuOpen = state.menuBranchKey == rowKey,
                    enabled = !state.isBusy,
                )
            }
        }
        item(key = "header-remote") {
            SectionHeader("Remote")
        }
        if (state.remoteBranches.isEmpty()) {
            item(key = "empty-remote") {
                EmptyHint("Fetch to load remote branches")
            }
        } else {
            items(state.remoteBranches, key = { "remote-${it.name}" }) { branch ->
                val rowKey = "remote-${branch.name}"
                BranchRow(
                    branch = branch,
                    rowKey = rowKey,
                    menuOpen = state.menuBranchKey == rowKey,
                    enabled = !state.isBusy,
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    BasicText(
        text = title,
        style = Typography.Caption.copy(color = Theme.colors.Muted),
        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp),
    )
}

@Composable
private fun EmptyHint(text: String) {
    BasicText(
        text = text,
        style = Typography.Caption.copy(color = Theme.colors.Muted2),
        modifier = Modifier.padding(vertical = 4.dp),
    )
}

private fun syncStatusLabel(ahead: Int, behind: Int): String = when {
    ahead > 0 && behind > 0 -> "$ahead ahead · $behind behind"
    ahead > 0 -> "$ahead ahead"
    behind > 0 -> "$behind behind"
    else -> "Up to date with origin"
}

@Composable
private fun ProjectGitScreenContext.BranchRow(
    branch: GitBranch,
    rowKey: String,
    menuOpen: Boolean,
    enabled: Boolean,
) {
    val subtitle = when {
        branch.isCurrent -> "Current"
        branch.kind == GitBranchKind.Remote -> "Remote"
        else -> null
    }
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Theme.colors.Surface2)
                .clickable(enabled = enabled) {
                    openBranchMenu(branch, rowKey)
                }
                .padding(start = 14.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                BasicText(
                    text = branch.name,
                    style = Typography.Subtitle.copy(color = Theme.colors.Text),
                )
                if (subtitle != null) {
                    BasicText(
                        text = subtitle,
                        style = Typography.Body.copy(color = Theme.colors.Muted),
                    )
                }
            }
            IconButton(
                onClick = { if (enabled) openBranchMenu(branch, rowKey) },
                variant = IconButtonVariant.Ghost,
                size = 32.dp,
                iconSize = 18.dp,
                icon = { tint, size -> IconMore(tint = tint, size = size) },
            )
        }
        if (menuOpen) {
            BranchOverflowMenu(branch)
        }
    }
}
