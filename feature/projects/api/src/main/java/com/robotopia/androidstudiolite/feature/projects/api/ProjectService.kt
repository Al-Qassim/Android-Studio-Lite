package com.robotopia.androidstudiolite.feature.projects.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectFieldErrors
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectExportResult
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.flow.Flow

interface ProjectService {
    fun observeProjects(): Flow<List<Project>>
    suspend fun getProject(id: ProjectId): Project?
    suspend fun createProject(request: CreateProjectRequest): Project
    suspend fun deleteProject(id: ProjectId)
    suspend fun markOpened(id: ProjectId)

    /**
     * Zips the project (skipping build/IDE junk), saves a copy under Downloads/AndroidStudioLite,
     * and returns a cache-path zip for sharing via FileProvider.
     */
    suspend fun exportProject(id: ProjectId): ProjectExportResult

    /**
     * Imports a project zip from a content or file [zipUri].
     * Requires `settings.gradle` / `settings.gradle.kts`. Name collisions get a numeric suffix.
     */
    suspend fun importProject(zipUri: String): Project

    /**
     * Validates create-project form fields. Implementation lives in `:data`.
     * [minSdk] is null when the field is empty or not a number.
     */
    fun validateCreateProject(
        name: String,
        packageName: String,
        minSdk: Int?,
    ): CreateProjectFieldErrors
}

/**
 * Feature UI surface. Integration calls [NavHost] only; list ↔ create is owned here.
 * Individual screens stay available for previews / tests.
 */
interface ProjectsScreens {
    @Composable
    fun NavHost(
        onOpenProject: (Project) -> Unit,
        onRunProject: (Project) -> Unit,
        onOpenSettings: () -> Unit,
    )

    @Composable
    fun ProjectsList(
        onOpenProject: (Project) -> Unit,
        onRunProject: (Project) -> Unit,
        onCreateProject: () -> Unit,
        onOpenSettings: () -> Unit,
    )

    @Composable
    fun CreateProject(
        onCreated: (projectId: ProjectId) -> Unit,
        onCancel: () -> Unit,
    )
}
