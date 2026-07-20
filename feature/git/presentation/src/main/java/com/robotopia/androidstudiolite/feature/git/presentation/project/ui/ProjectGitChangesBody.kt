package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.IconButton
import com.robotopia.androidstudiolite.designsystem.component.IconButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.popup.rememberEndAlignedMenuPopupPositionProvider
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.dismissChangeFileMenu
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openAbortMergeConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openChangeDiff
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openChangeFileMenu
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openDiscardAllConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.openDiscardFileConfirm
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestCommit
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestIgnorePath
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setCommitMessage
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.toggleChangeStaged

@Composable
internal fun ProjectGitScreenContext.ProjectGitChangesBody(state: ProjectGitUiState) {
    val conflicts = state.changeFiles.filter { it.kind == GitChangeKind.Conflict }
    val otherChanges = state.changeFiles.filterNot { it.kind == GitChangeKind.Conflict }
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = "On branch ${state.currentBranch.ifBlank { "—" }}",
                    style = Typography.Caption.copy(color = Theme.colors.Muted),
                )
                if (state.changeFiles.isNotEmpty() && state.mergeSourceBranch == null) {
                    Button(
                        label = "Discard all",
                        onClick = { openDiscardAllConfirm() },
                        variant = if (state.isBusy) {
                            ButtonVariant.Disabled
                        } else {
                            ButtonVariant.DangerText
                        },
                    )
                }
            }
            if (state.mergeSourceBranch != null) {
                MergeInProgressBanner(
                    currentBranch = state.currentBranch,
                    sourceBranch = state.mergeSourceBranch,
                    unresolvedCount = state.unresolvedConflictCount,
                )
            }
            if (state.changeFiles.isEmpty()) {
                EmptyState(
                    title = if (state.mergeSourceBranch != null) {
                        "Merge ready to commit"
                    } else {
                        "No local changes"
                    },
                    hint = if (state.mergeSourceBranch != null) {
                        "Commit to finish the merge."
                    } else {
                        "Edit files, then come back to commit."
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (conflicts.isNotEmpty()) {
                        item(key = "conflicts-header") {
                            BasicText(
                                text = "Conflicts (${state.unresolvedConflictCount} left)",
                                style = Typography.Caption.copy(color = Theme.colors.Danger),
                            )
                        }
                        items(conflicts, key = { "conflict-${it.path}" }) { file ->
                            ChangeFileRow(
                                file = file,
                                menuOpen = state.changeFileMenuPath == file.path,
                                enabled = !state.isBusy,
                                onToggleStaged = { toggleChangeStaged(file.path) },
                                onOpen = { openChangeDiff(file, state) },
                                onMore = { openChangeFileMenu(file.path) },
                            )
                        }
                    }
                    if (otherChanges.isNotEmpty()) {
                        item(key = "changes-header") {
                            BasicText(
                                text = if (conflicts.isNotEmpty()) "Other changes" else "Changes",
                                style = Typography.Caption.copy(color = Theme.colors.Muted),
                                modifier = Modifier.padding(
                                    top = if (conflicts.isNotEmpty()) 8.dp else 0.dp,
                                ),
                            )
                        }
                        items(otherChanges, key = { it.path }) { file ->
                            ChangeFileRow(
                                file = file,
                                menuOpen = state.changeFileMenuPath == file.path,
                                enabled = !state.isBusy,
                                onToggleStaged = { toggleChangeStaged(file.path) },
                                onOpen = { openChangeDiff(file, state) },
                                onMore = { openChangeFileMenu(file.path) },
                            )
                        }
                    }
                }
            }
        }
        CommitComposer(
            state = state,
            onMessageChange = { setCommitMessage(it) },
            onCommit = { requestCommit(state) },
            onAbortMerge = { openAbortMergeConfirm() },
            mergeBlocked = state.unresolvedConflictCount > 0,
        )
    }
}

@Composable
private fun MergeInProgressBanner(
    currentBranch: String,
    sourceBranch: String,
    unresolvedCount: Int,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.Surface2, shape)
            .border(1.dp, Theme.colors.Danger.copy(alpha = 0.5f), shape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "Merging $sourceBranch into $currentBranch",
            style = Typography.BodyStrong.copy(color = Theme.colors.Text),
        )
        BasicText(
            text = if (unresolvedCount > 0) {
                "$unresolvedCount conflict(s) left"
            } else {
                "Conflicts resolved — commit to finish."
            },
            style = Typography.Caption.copy(color = Theme.colors.Muted),
        )
    }
}

@Composable
private fun ProjectGitScreenContext.ChangeFileRow(
    file: GitChangeFile,
    menuOpen: Boolean,
    enabled: Boolean,
    onToggleStaged: () -> Unit,
    onOpen: () -> Unit,
    onMore: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    Box(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.colors.Surface, shape)
                .border(1.dp, Theme.colors.Border, shape)
                .padding(start = 12.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            StageToggle(
                staged = file.staged,
                enabled = enabled && file.kind != GitChangeKind.Conflict,
                onClick = onToggleStaged,
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable(enabled = enabled, onClick = onOpen),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                BasicText(
                    text = file.path,
                    style = Typography.BodyStrong.copy(color = Theme.colors.Text),
                )
                BasicText(
                    text = when {
                        file.kind == GitChangeKind.Conflict && file.staged -> "Resolved"
                        else -> kindLabel(file.kind)
                    },
                    style = Typography.Caption.copy(color = Theme.colors.Muted2),
                )
            }
            BasicText(
                text = kindBadge(file.kind),
                style = Typography.Caption.copy(color = kindColor(file.kind)),
                modifier = Modifier.clickable(enabled = enabled, onClick = onOpen),
            )
            if (file.kind != GitChangeKind.Conflict) {
                IconButton(
                    onClick = { if (enabled) onMore() },
                    variant = IconButtonVariant.Ghost,
                    size = 32.dp,
                    iconSize = 18.dp,
                    icon = { tint, size -> IconMore(tint = tint, size = size) },
                )
            }
        }
        if (menuOpen) {
            ChangeFileOverflowMenu(file)
        }
    }
}

@Composable
private fun ProjectGitScreenContext.ChangeFileOverflowMenu(file: GitChangeFile) {
    val positionProvider = rememberEndAlignedMenuPopupPositionProvider()
    Popup(
        popupPositionProvider = positionProvider,
        onDismissRequest = { dismissChangeFileMenu() },
        properties = PopupProperties(focusable = true),
    ) {
        val canIgnore = file.kind == GitChangeKind.Untracked ||
            file.kind == GitChangeKind.Modified
        Menu(
            items = buildList {
                add(
                    MenuItem.Button(
                        label = "Discard",
                        onClick = { openDiscardFileConfirm(file.path) },
                        danger = true,
                    ),
                )
                if (canIgnore) {
                    add(
                        MenuItem.Button(
                            label = "Ignore",
                            onClick = { requestIgnorePath(file.path) },
                        ),
                    )
                }
            },
        )
    }
}

@Composable
private fun StageToggle(
    staged: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .size(22.dp)
            .background(
                if (staged) Theme.colors.Primary else Theme.colors.Input,
                shape,
            )
            .border(1.dp, if (staged) Theme.colors.Primary else Theme.colors.Border, shape)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (staged) {
            BasicText(
                text = "✓",
                style = Typography.Caption.copy(color = Theme.colors.Text),
            )
        }
    }
}

@Composable
private fun CommitComposer(
    state: ProjectGitUiState,
    onMessageChange: (String) -> Unit,
    onCommit: () -> Unit,
    onAbortMerge: () -> Unit,
    mergeBlocked: Boolean = false,
) {
    val shape = RoundedCornerShape(8.dp)
    val stagedCount = state.changeFiles.count { it.staged }
    val merging = state.mergeSourceBranch != null
    val mergeReady = merging && !mergeBlocked
    val topBorderColor = Theme.colors.Border
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = 1.dp.toPx()
                drawLine(
                    color = topBorderColor,
                    start = Offset(0f, stroke / 2f),
                    end = Offset(size.width, stroke / 2f),
                    strokeWidth = stroke,
                )
            }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = if (mergeReady) "Merge commit message" else "Commit message",
            style = Typography.Caption.copy(color = Theme.colors.Muted),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 72.dp)
                .background(Theme.colors.Input, shape)
                .border(
                    1.dp,
                    if (state.commitError != null) Theme.colors.Danger else Theme.colors.Border,
                    shape,
                )
                .padding(12.dp),
        ) {
            if (state.commitMessage.isEmpty()) {
                BasicText(
                    text = if (mergeBlocked) {
                        "Resolve conflicts before committing…"
                    } else {
                        "Describe your changes…"
                    },
                    style = Typography.Body.copy(color = Theme.colors.Muted),
                )
            }
            BasicTextField(
                value = state.commitMessage,
                onValueChange = onMessageChange,
                textStyle = Typography.Body.copy(color = Theme.colors.Text),
                cursorBrush = SolidColor(Theme.colors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (state.commitError != null) {
            BasicText(
                text = state.commitError,
                style = Typography.Caption.copy(color = Theme.colors.Danger),
            )
        }
        Button(
            label = when {
                mergeBlocked -> "Resolve conflicts to commit"
                mergeReady -> "Commit merge"
                stagedCount > 0 -> "Commit $stagedCount file(s)"
                else -> "Commit"
            },
            onClick = onCommit,
            variant = when {
                state.isBusy || mergeBlocked -> ButtonVariant.Disabled
                mergeReady -> ButtonVariant.Primary
                state.changeFiles.isEmpty() && !merging -> ButtonVariant.Disabled
                else -> ButtonVariant.Primary
            },
            modifier = Modifier.fillMaxWidth(),
        )
        if (merging) {
            Button(
                label = "Abort merge",
                onClick = onAbortMerge,
                variant = if (state.isBusy) ButtonVariant.Disabled else ButtonVariant.Secondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private fun kindLabel(kind: GitChangeKind): String = when (kind) {
    GitChangeKind.Modified -> "Modified"
    GitChangeKind.Added -> "Added"
    GitChangeKind.Deleted -> "Deleted"
    GitChangeKind.Untracked -> "Untracked"
    GitChangeKind.Conflict -> "Conflict"
}

private fun kindBadge(kind: GitChangeKind): String = when (kind) {
    GitChangeKind.Modified -> "M"
    GitChangeKind.Added -> "A"
    GitChangeKind.Deleted -> "D"
    GitChangeKind.Untracked -> "U"
    GitChangeKind.Conflict -> "C"
}

@Composable
private fun kindColor(kind: GitChangeKind) = when (kind) {
    GitChangeKind.Modified -> Theme.colors.Primary
    GitChangeKind.Added -> Theme.colors.Run
    GitChangeKind.Deleted -> Theme.colors.Danger
    GitChangeKind.Untracked -> Theme.colors.Muted
    GitChangeKind.Conflict -> Theme.colors.Danger
}
