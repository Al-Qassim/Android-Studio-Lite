package com.robotopia.androidstudiolite.feature.git.presentation.clone.logic

import android.content.Context
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectUiState
import com.robotopia.androidstudiolite.feature.git.presentation.logic.credentialsOrNull
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.Project
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

suspend fun cloneProject(
    context: Context,
    gitService: GitService,
    projectService: ProjectService,
    authSession: AuthSession,
    uiState: MutableStateFlow<CloneProjectUiState>,
    onCreated: (Project) -> Unit,
) {
    val state = uiState.value
    if (state.isCloning) return

    val validation = gitService.validateCloneUrl(state.url)
    if (!validation.isValid) {
        uiState.update {
            it.copy(urlError = validation.errorMessage, formError = null)
        }
        return
    }
    val url = validation.normalizedHttpsUrl!!

    uiState.update {
        it.copy(isCloning = true, urlError = null, formError = null)
    }

    val staging = File(context.cacheDir, "git-clone-${UUID.randomUUID()}")
    try {
        val credentials = credentialsOrNull(authSession)
        gitService.clone(httpsUrl = url, destDir = staging, credentials = credentials)
        val project = projectService.importFromDirectory(staging.absolutePath)
        onCreated(project)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        uiState.update {
            it.copy(
                isCloning = false,
                formError = e.userMessageOrNull(TAG) ?: GENERIC_ERROR,
            )
        }
    } finally {
        staging.deleteRecursively()
    }
}

private const val TAG = "CloneProject"
private const val GENERIC_ERROR = "Couldn't clone that repository."
