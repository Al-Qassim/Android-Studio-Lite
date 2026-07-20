package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestConnectPublishAccount
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestPublish
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setPublishPrivate
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.setPublishRepoName

/**
 * Create a GitHub repo for a local project with no remote, then push.
 * Preview-first shell — create-repo API + `remote add` / `push -u` come later.
 */
@Composable
internal fun ProjectGitScreenContext.ProjectGitPublishBody(state: ProjectGitUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (!state.publishAccountConnected) {
            PublishConnectAccount(state)
            return
        }

        BasicText(
            text = "Create a ${state.publishProviderName} repository and push “${state.currentBranch.ifBlank { "main" }}”.",
            style = Typography.Body.copy(color = Theme.colors.Muted),
        )
        TextField(
            value = state.publishRepoName,
            onValueChange = { if (!state.isBusy) setPublishRepoName(it) },
            placeholder = "my-app",
            variant = TextFieldVariant.Form,
            isError = state.publishNameError != null,
            errorMessage = state.publishNameError,
        )
        BasicText(
            text = "Visibility",
            style = Typography.Caption.copy(color = Theme.colors.Muted),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                label = "Private",
                onClick = { if (!state.isBusy) setPublishPrivate(true) },
                variant = if (state.publishPrivate) {
                    ButtonVariant.Primary
                } else {
                    ButtonVariant.Secondary
                },
                modifier = Modifier.weight(1f),
            )
            Button(
                label = "Public",
                onClick = { if (!state.isBusy) setPublishPrivate(false) },
                variant = if (!state.publishPrivate) {
                    ButtonVariant.Primary
                } else {
                    ButtonVariant.Secondary
                },
                modifier = Modifier.weight(1f),
            )
        }
        if (state.publishNeedsCommit) {
            BasicText(
                text = "Commit at least once on Changes before publishing.",
                style = Typography.Caption.copy(color = Theme.colors.Danger),
            )
        }
        if (state.publishError != null) {
            BasicText(
                text = state.publishError,
                style = Typography.Caption.copy(color = Theme.colors.Danger),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            label = when {
                state.isBusy -> "Publishing…"
                state.publishNeedsCommit -> "Commit first"
                else -> "Publish"
            },
            onClick = { requestPublish(state) },
            variant = when {
                state.isBusy -> ButtonVariant.Disabled
                state.publishNeedsCommit -> ButtonVariant.Secondary
                else -> ButtonVariant.Primary
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ProjectGitScreenContext.PublishConnectAccount(state: ProjectGitUiState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BasicText(
            text = "Connect your ${state.publishProviderName} build account to create a repository and push.",
            style = Typography.Body.copy(color = Theme.colors.Muted),
        )
        if (state.publishError != null) {
            BasicText(
                text = state.publishError,
                style = Typography.Caption.copy(color = Theme.colors.Danger),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            label = "Connect ${state.publishProviderName}",
            onClick = { requestConnectPublishAccount(state) },
            variant = ButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
