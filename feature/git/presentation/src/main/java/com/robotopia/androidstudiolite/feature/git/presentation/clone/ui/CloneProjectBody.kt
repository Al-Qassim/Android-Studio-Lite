package com.robotopia.androidstudiolite.feature.git.presentation.clone.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectUiState

@Composable
internal fun CloneProjectBody(
    state: CloneProjectUiState,
    onCancel: () -> Unit,
    onUrlChange: (String) -> Unit,
    onCloneClick: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "Clone from GitHub",
                onBackClick = onCancel,
            )
        },
        footer = {
            Button(
                label = if (state.isCloning) "Cloning…" else "Clone",
                onClick = onCloneClick,
                variant = if (state.isCloning) ButtonVariant.Disabled else ButtonVariant.Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(
                text = "Paste a GitHub URL or owner/repo. Private repos need a connected build account.",
                style = Typography.Body.copy(color = Theme.colors.Muted),
            )
            TextField(
                value = state.url,
                onValueChange = onUrlChange,
                placeholder = "https://github.com/owner/repo",
                variant = TextFieldVariant.Form,
                isError = state.urlError != null,
                errorMessage = state.urlError,
            )
            if (state.formError != null) {
                BasicText(
                    text = state.formError,
                    style = Typography.Caption.copy(color = Theme.colors.Danger),
                )
            }
            if (state.isCloning) {
                Spacer(modifier = Modifier.height(8.dp))
                LoadingIndicator()
            }
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
