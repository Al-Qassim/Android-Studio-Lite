package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.git.presentation.project.logic.requestInitRepository

@Composable
internal fun ProjectGitScreenContext.ProjectGitInitBody(state: ProjectGitUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = "Create a Git repository",
            style = Typography.TitleNav.copy(
                color = Theme.colors.Text,
                textAlign = TextAlign.Center,
            ),
        )
        BasicText(
            text = "This project isn’t a Git repository yet.",
            style = Typography.Body.copy(
                color = Theme.colors.Muted,
                textAlign = TextAlign.Center,
            ),
        )
        if (state.actionError != null) {
            BasicText(
                text = state.actionError,
                style = Typography.Caption.copy(
                    color = Theme.colors.Danger,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        Button(
            label = if (state.isBusy) "Initializing…" else "Initialize repository",
            onClick = { requestInitRepository() },
            variant = if (state.isBusy) ButtonVariant.Disabled else ButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
