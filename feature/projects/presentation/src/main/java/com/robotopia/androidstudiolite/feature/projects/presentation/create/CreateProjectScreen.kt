package com.robotopia.androidstudiolite.feature.projects.presentation.create

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
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

private const val TAG = "CreateProject"
private const val GENERIC_ERROR_MESSAGE = "Something went wrong"
