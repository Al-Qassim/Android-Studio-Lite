package com.robotopia.androidstudiolite.feature.projects.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
internal fun CreateProjectContent(
    state: CreateProjectUiState,
    onCancel: () -> Unit,
    onNameChange: (String) -> Unit,
    onPackageNameChange: (String) -> Unit,
    onMinSdkChange: (String) -> Unit,
    onCreateClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "New project",
            onBackClick = onCancel,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BasicText(
                text = "Single Activity + Compose",
                style = Typography.Body.copy(color = Colors.Primary),
            )

            FormField(
                label = "App name",
                value = state.name,
                onValueChange = onNameChange,
                placeholder = "MyApp",
                error = state.nameError,
            )

            FormField(
                label = "Package name",
                value = state.packageName,
                onValueChange = onPackageNameChange,
                placeholder = "com.example.myapp",
                error = state.packageError,
            )

            FormField(
                label = "Min SDK",
                value = state.minSdk,
                onValueChange = onMinSdkChange,
                placeholder = "26",
                error = state.minSdkError,
            )

            if (state.formError != null) {
                BasicText(
                    text = state.formError,
                    style = Typography.Caption.copy(color = Colors.Danger),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        BasicText(
            text = label,
            style = Typography.Caption.copy(color = Colors.Muted),
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            variant = TextFieldVariant.Form,
            isError = error != null,
            errorMessage = error,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun CreateProjectScreenPreview() {
    CreateProjectContent(
        state = CreateProjectUiState(
            name = "MyApp",
            packageName = "com.example.myapp",
            minSdk = "26",
        ),
        onCancel = {},
        onNameChange = {},
        onPackageNameChange = {},
        onMinSdkChange = {},
        onCreateClick = {},
    )
}
