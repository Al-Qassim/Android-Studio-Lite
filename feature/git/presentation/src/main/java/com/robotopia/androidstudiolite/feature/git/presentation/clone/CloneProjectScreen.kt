package com.robotopia.androidstudiolite.feature.git.presentation.clone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.presentation.clone.logic.cloneProject
import com.robotopia.androidstudiolite.feature.git.presentation.clone.ui.CloneProjectBody
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.Project
import java.util.UUID
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun CloneProjectScreen(
    gitService: GitService,
    projectService: ProjectService,
    authSession: AuthSession,
    onCreated: (Project) -> Unit,
    onCancel: () -> Unit,
    viewModel: CloneProjectViewModel = koinViewModel(
        key = rememberSaveable { UUID.randomUUID().toString() },
    ),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    CloneProjectBody(
        state = state,
        onCancel = onCancel,
        onUrlChange = { value ->
            viewModel.uiState.update {
                it.copy(url = value, urlError = null, formError = null)
            }
        },
        onCloneClick = {
            scope.launch {
                cloneProject(
                    context = context,
                    gitService = gitService,
                    projectService = projectService,
                    authSession = authSession,
                    uiState = viewModel.uiState,
                    onCreated = onCreated,
                )
            }
        },
    )
}
