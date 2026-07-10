package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Scaffold stub — real Room/FS impl lands in #7. */
class StubProjectService(
    private val projectDao: ProjectDao,
) : ProjectService {
    override fun observeProjects(): Flow<List<Project>> = flowOf(emptyList())
    override suspend fun getProject(id: ProjectId): Project? = projectDao.getById(id.value)?.toDomain()
    override suspend fun createProject(request: CreateProjectRequest): Project {
        error("Not implemented — see #7")
    }
    override suspend fun deleteProject(id: ProjectId) = Unit
    override suspend fun markOpened(id: ProjectId) = Unit
}
