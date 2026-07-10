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
    var nameError by remember { mutableStateOf<String?>(null) }
    var packageError by remember { mutableStateOf<String?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }
    var isCreating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "Create project",
            onBackClick = onCancel,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            BasicText(
                text = "Empty Compose Activity",
                style = Typography.Body.copy(color = Colors.Muted),
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BasicText(
                    text = "Name",
                    style = Typography.Caption.copy(color = Colors.Muted),
                )
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = null
                        formError = null
                    },
                    placeholder = "MyApp",
                    variant = TextFieldVariant.Form,
                    isError = nameError != null,
                    errorMessage = nameError,
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                BasicText(
                    text = "Package name",
                    style = Typography.Caption.copy(color = Colors.Muted),
                )
                TextField(
                    value = packageName,
                    onValueChange = {
                        packageName = it
                        packageError = null
                        formError = null
                    },
                    placeholder = "com.example.myapp",
                    variant = TextFieldVariant.Form,
                    isError = packageError != null,
                    errorMessage = packageError,
                )
            }

            if (formError != null) {
                BasicText(
                    text = formError!!,
                    style = Typography.Caption.copy(color = Colors.Danger),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                label = if (isCreating) "Creating…" else "Create",
                onClick = {
                    val trimmedName = name.trim()
                    val trimmedPackage = packageName.trim()
                    nameError = when {
                        trimmedName.isEmpty() -> "Project name is required"
                        else -> null
                    }
                    packageError = when {
                        trimmedPackage.isEmpty() -> "Package name is required"
                        !PACKAGE_REGEX.matches(trimmedPackage) ->
                            "Use a valid Java package (e.g. com.example.app)"
                        else -> null
                    }
                    if (nameError != null || packageError != null || isCreating) return@Button

                    isCreating = true
                    formError = null
                    scope.launch {
                        runCatching {
                            projectService.createProject(
                                CreateProjectRequest(
                                    name = trimmedName,
                                    packageName = trimmedPackage,
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
        }
    }
}

private val PACKAGE_REGEX = Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")
