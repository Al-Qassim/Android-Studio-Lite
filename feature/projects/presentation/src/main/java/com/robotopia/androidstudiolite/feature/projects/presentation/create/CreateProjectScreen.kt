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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
internal fun CreateProjectScreen(
    projectService: ProjectService,
    onCreated: (projectId: ProjectId) -> Unit,
    onCancel: () -> Unit,
    viewModel: CreateProjectViewModel = koinViewModel(
        key = rememberSaveable { UUID.randomUUID().toString() },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    CreateProjectContent(
        state = state,
        onCancel = onCancel,
        onNameChange = { value ->
            viewModel.uiState.update {
                it.copy(name = value, nameError = null, formError = null)
            }
        },
        onPackageNameChange = { value ->
            viewModel.uiState.update {
                it.copy(packageName = value, packageError = null, formError = null)
            }
        },
        onMinSdkChange = { value ->
            viewModel.uiState.update {
                it.copy(
                    minSdk = value.filter { ch -> ch.isDigit() }.take(2),
                    minSdkError = null,
                    formError = null,
                )
            }
        },
        onCreateClick = {
            scope.launch {
                createProject(
                    projectService = projectService,
                    uiState = viewModel.uiState,
                    onCreated = onCreated,
                )
            }
        },
    )
}

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

private fun validateCreateForm(
    projectService: ProjectService,
    uiState: MutableStateFlow<CreateProjectUiState>,
): CreateProjectRequest? {
    val state = uiState.value
    if (state.isCreating) return null

    val parsedMinSdk = state.minSdk.toIntOrNull()
    val errors = projectService.validateCreateProject(
        name = state.name,
        packageName = state.packageName,
        minSdk = parsedMinSdk,
    )
    uiState.update {
        it.copy(
            nameError = errors.name,
            packageError = errors.packageName,
            minSdkError = errors.minSdk,
        )
    }
    if (errors.hasErrors) return null

    return CreateProjectRequest(
        name = state.name.trim(),
        packageName = state.packageName.trim(),
        minSdk = parsedMinSdk!!,
    )
}

private suspend fun createProject(
    projectService: ProjectService,
    uiState: MutableStateFlow<CreateProjectUiState>,
    onCreated: (ProjectId) -> Unit,
) {
    val request = validateCreateForm(projectService, uiState) ?: return
    uiState.update { it.copy(isCreating = true, formError = null) }
    runCatching { projectService.createProject(request) }
        .onSuccess { project -> onCreated(project.id) }
        .onFailure { error ->
            uiState.update {
                it.copy(
                    isCreating = false,
                    formError = error.userMessageOrNull(TAG) ?: GENERIC_ERROR_MESSAGE,
                )
            }
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

private const val TAG = "CreateProject"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"
