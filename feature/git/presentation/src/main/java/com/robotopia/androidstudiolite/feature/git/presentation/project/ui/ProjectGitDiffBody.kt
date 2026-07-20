package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.LocalColorScheme
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.CodeEditorField
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLine
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitDiffLineKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.acceptConflictOurs
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.acceptConflictTheirs
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.conflictHighlightTransformation
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.markConflictResolvedManually
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestOpenWorkingFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setConflictText

/**
 * Working-tree / commit diffs stay read-only. Conflict resolve uses an editable
 * [CodeEditorField] so the user can manually finish the merge buffer.
 */
@Composable
internal fun ProjectGitScreenContext.ProjectGitDiffBody(state: ProjectGitUiState) {
    when {
        state.isDiffLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator(label = "Opening changes…")
            }
        }

        else -> {
            Column(modifier = Modifier.fillMaxSize()) {
                if (state.isConflictEditor) {
                    ConflictResolveHeader(state)
                    val colors = LocalColorScheme.current
                    val conflictHighlight = remember(state.conflictLinePaint, colors) {
                        conflictHighlightTransformation(state.conflictLinePaint, colors)
                    }
                    CodeEditorField(
                        value = state.conflictText,
                        onValueChange = { setConflictText(it) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        wrapText = true,
                        visualTransformation = conflictHighlight,
                    )
                    ConflictResolveActions(
                        acceptEnabled = !state.isBusy && state.hasOpenConflictHunks,
                        markResolvedEnabled = !state.isBusy && !state.hasOpenConflictHunks,
                        onAcceptOurs = { acceptConflictOurs(state) },
                        onAcceptTheirs = { acceptConflictTheirs(state) },
                        onMarkResolved = { markConflictResolvedManually(state) },
                    )
                } else {
                    val verticalScroll = rememberScrollState()
                    val horizontalScroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(verticalScroll)
                            .horizontalScroll(horizontalScroll)
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                    ) {
                        state.diffLines.forEach { line ->
                            DiffEditorLine(line = line)
                        }
                    }
                    val openPath = state.selectedDiffPath
                    if (openPath != null) {
                        Button(
                            label = "Open file",
                            onClick = { requestOpenWorkingFile(openPath) },
                            variant = if (state.isBusy) {
                                ButtonVariant.Disabled
                            } else {
                                ButtonVariant.Secondary
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConflictResolveHeader(state: ProjectGitUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.Surface2)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BasicText(
            text = "Merging ${state.mergeSourceBranch ?: "incoming"} → ${state.currentBranch.ifBlank { "HEAD" }}",
            style = Typography.Caption.copy(color = Theme.colors.Muted),
        )
    }
}

@Composable
private fun ConflictResolveActions(
    acceptEnabled: Boolean,
    markResolvedEnabled: Boolean,
    onAcceptOurs: () -> Unit,
    onAcceptTheirs: () -> Unit,
    onMarkResolved: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.colors.Surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                label = "Accept current",
                onClick = onAcceptOurs,
                variant = if (acceptEnabled) ButtonVariant.Secondary else ButtonVariant.Disabled,
                modifier = Modifier.weight(1f),
            )
            Button(
                label = "Accept incoming",
                onClick = onAcceptTheirs,
                variant = if (acceptEnabled) ButtonVariant.Secondary else ButtonVariant.Disabled,
                modifier = Modifier.weight(1f),
            )
        }
        Button(
            label = "Mark as resolved",
            onClick = onMarkResolved,
            variant = if (markResolvedEnabled) {
                ButtonVariant.Primary
            } else {
                ButtonVariant.Disabled
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DiffEditorLine(line: GitDiffLine) {
    val background = when (line.kind) {
        GitDiffLineKind.Add -> Theme.colors.Run.copy(alpha = 0.14f)
        GitDiffLineKind.Remove -> Theme.colors.Danger.copy(alpha = 0.14f)
        GitDiffLineKind.ConflictOurs -> Theme.colors.Primary.copy(alpha = 0.16f)
        GitDiffLineKind.ConflictTheirs -> Theme.colors.Run.copy(alpha = 0.16f)
        GitDiffLineKind.ConflictMarker -> Theme.colors.Danger.copy(alpha = 0.22f)
        GitDiffLineKind.Context -> Color.Transparent
    }
    val prefix = when (line.kind) {
        GitDiffLineKind.Add -> "+"
        GitDiffLineKind.Remove -> "-"
        GitDiffLineKind.ConflictOurs -> "|"
        GitDiffLineKind.ConflictTheirs -> "|"
        GitDiffLineKind.ConflictMarker -> "!"
        GitDiffLineKind.Context -> " "
    }
    val color = when (line.kind) {
        GitDiffLineKind.Add -> Theme.colors.Run
        GitDiffLineKind.Remove -> Theme.colors.Danger
        GitDiffLineKind.ConflictOurs -> Theme.colors.Primary
        GitDiffLineKind.ConflictTheirs -> Theme.colors.Run
        GitDiffLineKind.ConflictMarker -> Theme.colors.Danger
        GitDiffLineKind.Context -> Theme.colors.Text
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .padding(vertical = 1.dp),
    ) {
        DiffGutterNumber(line.oldLine)
        DiffGutterNumber(line.newLine)
        BasicText(
            text = "$prefix${line.text}",
            style = Typography.Code.copy(color = color),
        )
    }
}

@Composable
private fun DiffGutterNumber(number: Int?) {
    BasicText(
        text = number?.toString().orEmpty(),
        style = Typography.CodeGutter.copy(
            color = Theme.colors.Gutter,
            textAlign = TextAlign.End,
        ),
        modifier = Modifier
            .widthIn(min = 28.dp)
            .padding(end = 8.dp),
    )
}
