package com.robotopia.androidstudiolite.feature.projects.presentation

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TextField
import com.robotopia.androidstudiolite.designsystem.component.TextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.launch

@Composable
internal fun CreateProjectScreen(
    projectService: ProjectService,
    onCreated: (projectId: ProjectId) -> Unit,
    onCancel: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.example.") }
    var minSdk by remember { mutableStateOf("26") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var packageError by remember { mutableStateOf<String?>(null) }
    var minSdkError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
                style = Typography.Body.copy(color = Colors.Muted),
            )

            FormField(
                label = "App name",
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                    formError = null
                },
                placeholder = "MyApp",
                error = nameError,
            )

            FormField(
                label = "Package name",
                value = packageName,
                onValueChange = {
                    packageName = it
                    packageError = null
                    formError = null
                },
                placeholder = "com.example.myapp",
                error = packageError,
            )

            FormField(
                label = "Min SDK",
                value = minSdk,
                onValueChange = {
                    minSdk = it.filter { ch -> ch.isDigit() }.take(2)
                    minSdkError = null
                    formError = null
                },
                placeholder = "26",
                error = minSdkError,
            )

            if (formError != null) {
                BasicText(
                    text = formError!!,
                    style = Typography.Caption.copy(color = Colors.Danger),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                label = if (isCreating) "Creating…" else "Create project",
                onClick = {
                    val trimmedName = name.trim()
                    val trimmedPackage = packageName.trim()
                    val parsedMinSdk = minSdk.toIntOrNull()
                    nameError = when {
                        trimmedName.isEmpty() -> "App name is required"
                        else -> null
                    }
                    packageError = when {
                        trimmedPackage.isEmpty() -> "Package name is required"
                        !PACKAGE_REGEX.matches(trimmedPackage) ->
                            "Use a valid Java package (e.g. com.example.app)"
                        else -> null
                    }
                    minSdkError = when {
                        parsedMinSdk == null -> "Min SDK is required"
                        parsedMinSdk !in 21..35 -> "Min SDK must be between 21 and 35"
                        else -> null
                    }
                    if (nameError != null || packageError != null || minSdkError != null || isCreating) {
                        return@Button
                    }

                    isCreating = true
                    formError = null
                    scope.launch {
                        runCatching {
                            projectService.createProject(
                                CreateProjectRequest(
                                    name = trimmedName,
                                    packageName = trimmedPackage,
                                    minSdk = parsedMinSdk!!,
                                ),
                            )
                        }.onSuccess { project ->
                            onCreated(project.id)
                        }.onFailure { error ->
                            formError = error.message ?: "Could not create project"
                            isCreating = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                variant = if (isCreating) ButtonVariant.Disabled else ButtonVariant.Primary,
                enabled = !isCreating,
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

private val PACKAGE_REGEX = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")
