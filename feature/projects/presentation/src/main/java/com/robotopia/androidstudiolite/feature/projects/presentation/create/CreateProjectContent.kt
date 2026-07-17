package com.robotopia.androidstudiolite.feature.projects.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.projects.presentation.preview.CreateProjectPreviewCase
import com.robotopia.androidstudiolite.feature.projects.presentation.preview.CreateProjectPreviewProvider

@Composable
internal fun CreateProjectContent(
    state: CreateProjectUiState,
    onCancel: () -> Unit,
    onNameChange: (String) -> Unit,
    onPackageNameChange: (String) -> Unit,
    onMinSdkChange: (String) -> Unit,
    onCreateClick: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            TopBarBackTitle(
                title = "New project",
                onBackClick = onCancel,
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
                text = "Single Activity + Compose",
                style = Typography.Caption.copy(color = Colors.Muted),
            )

            TextField(
                value = state.name,
                onValueChange = onNameChange,
                placeholder = "App name",
                variant = TextFieldVariant.Form,
                isError = state.nameError != null,
                errorMessage = state.nameError,
            )

            TextField(
                value = state.packageName,
                onValueChange = onPackageNameChange,
                placeholder = "Package name",
                variant = TextFieldVariant.Form,
                isError = state.packageError != null,
                errorMessage = state.packageError,
            )

            TextField(
                value = state.minSdk,
                onValueChange = onMinSdkChange,
                placeholder = "Min SDK",
                variant = TextFieldVariant.Form,
                isError = state.minSdkError != null,
                errorMessage = state.minSdkError,
            )

            if (state.formError != null) {
                BasicText(
                    text = state.formError,
                    style = Typography.Caption.copy(color = Colors.Danger),
                )
            }

            Button(
                label = if (state.isCreating) "Creating…" else "Create project",
                onClick = onCreateClick,
                modifier = Modifier.fillMaxWidth(),
                variant = if (state.isCreating) ButtonVariant.Disabled else ButtonVariant.Primary,
                enabled = !state.isCreating,
            )

            BasicText(
                text = "Copies template into app storage",
                style = Typography.Caption.copy(color = Colors.Muted2),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun CreateProjectContentPreview(
    @PreviewParameter(CreateProjectPreviewProvider::class) preview: CreateProjectPreviewCase,
) {
    CreateProjectContent(
        state = preview.state,
        onCancel = {},
        onNameChange = {},
        onPackageNameChange = {},
        onMinSdkChange = {},
        onCreateClick = {},
    )
}
