package com.robotopia.androidstudiolite.feature.projects.impl

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.CreateProjectRequest
import com.robotopia.androidstudiolite.core.model.Project
import com.robotopia.androidstudiolite.core.model.ProjectId
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.dsl.module

internal class StubProjectService : ProjectService {
    override fun observeProjects(): Flow<List<Project>> = flowOf(emptyList())
    override suspend fun getProject(id: ProjectId): Project? = null
    override suspend fun createProject(request: CreateProjectRequest): Project {
        error("Not implemented — see #7")
    }
    override suspend fun deleteProject(id: ProjectId) = Unit
    override suspend fun markOpened(id: ProjectId) = Unit
}

internal class StubProjectsScreens : ProjectsScreens {
    @Composable
    override fun ProjectsList(
        onOpenProject: (projectId: String) -> Unit,
        onCreateProject: () -> Unit,
    ) {
        Text("Projects (stub)")
    }

    @Composable
    override fun CreateProject(
        onCreated: (projectId: String) -> Unit,
        onCancel: () -> Unit,
    ) {
        Text("Create project (stub)")
    }
}

val projectsModule = module {
    single<ProjectService> { StubProjectService() }
    single<ProjectsScreens> { StubProjectsScreens() }
}
