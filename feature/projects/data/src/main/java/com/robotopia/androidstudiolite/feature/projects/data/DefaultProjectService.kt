package com.robotopia.androidstudiolite.feature.projects.data

import android.content.Context
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

class DefaultProjectService(
    private val context: Context,
    private val projectDao: ProjectDao,
) : ProjectService {

    override fun observeProjects(): Flow<List<Project>> =
        projectDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override suspend fun getProject(id: ProjectId): Project? =
        projectDao.getById(id.value)?.toDomain()

    override suspend fun createProject(request: CreateProjectRequest): Project =
        withContext(Dispatchers.IO) {
            ProjectValidation.validate(request)
            val name = request.name.trim()
            val packageName = request.packageName.trim()
            val id = UUID.randomUUID().toString()
            val rootDir = projectDir(id)
            if (rootDir.exists()) {
                error("Project directory already exists")
            }

            try {
                EmptyComposeProjectTemplate.write(
                    projectRoot = rootDir,
                    projectName = name,
                    packageName = packageName,
                    minSdk = request.minSdk,
                )
                val now = System.currentTimeMillis()
                val entity = ProjectEntity(
                    id = id,
                    name = name,
                    packageName = packageName,
                    rootPath = rootDir.absolutePath,
                    lastOpenedAt = now,
                )
                projectDao.upsert(entity)
                entity.toDomain()
            } catch (t: Throwable) {
                rootDir.deleteRecursively()
                throw t
            }
        }

    override suspend fun deleteProject(id: ProjectId) {
        withContext(Dispatchers.IO) {
            val entity = projectDao.getById(id.value) ?: return@withContext
            projectDao.deleteById(id.value)
            File(entity.rootPath).deleteRecursively()
        }
    }

    override suspend fun markOpened(id: ProjectId) {
        withContext(Dispatchers.IO) {
            val entity = projectDao.getById(id.value) ?: return@withContext
            projectDao.upsert(entity.copy(lastOpenedAt = System.currentTimeMillis()))
        }
    }

    private fun projectDir(id: String): File =
        File(File(context.filesDir, PROJECTS_DIR), id)

    private companion object {
        const val PROJECTS_DIR = "projects"
    }
}
