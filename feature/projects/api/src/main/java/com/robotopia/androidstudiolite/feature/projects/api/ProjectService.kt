package com.robotopia.androidstudiolite.feature.projects.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.CreateProjectRequest
import com.robotopia.androidstudiolite.core.model.Project
import com.robotopia.androidstudiolite.core.model.ProjectId
import kotlinx.coroutines.flow.Flow

interface ProjectService {
    fun observeProjects(): Flow<List<Project>>
    suspend fun getProject(id: ProjectId): Project?
    suspend fun createProject(request: CreateProjectRequest): Project
    suspend fun deleteProject(id: ProjectId)
    suspend fun markOpened(id: ProjectId)
}

interface ProjectsScreens {
    @Composable
    fun ProjectsList(
        onOpenProject: (projectId: String) -> Unit,
        onCreateProject: () -> Unit = {},
    )

    @Composable
    fun CreateProject(
        onCreated: (projectId: String) -> Unit,
        onCancel: () -> Unit,
    )
}
