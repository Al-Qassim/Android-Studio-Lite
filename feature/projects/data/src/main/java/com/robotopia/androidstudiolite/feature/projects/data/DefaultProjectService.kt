package com.robotopia.androidstudiolite.feature.projects.data

import android.content.Context
import android.net.Uri
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.projects.api.ProjectService
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectFieldErrors
import com.robotopia.androidstudiolite.feature.projects.model.CreateProjectRequest
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectExportResult
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
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
                throw AppException("Project directory already exists")
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
            val entity = projectDao.getById(id.value)
                ?: throw AppException("Project not found")

            projectDao.deleteById(id.value)
            try {
                val root = File(entity.rootPath)
                if (root.exists() && !root.deleteRecursively()) {
                    throw AppException("Could not delete project files")
                }
            } catch (t: Throwable) {
                projectDao.upsert(entity)
                throw t
            }
        }
    }

    override suspend fun markOpened(id: ProjectId) {
        withContext(Dispatchers.IO) {
            val entity = projectDao.getById(id.value)
                ?: throw AppException("Project not found")
            projectDao.upsert(entity.copy(lastOpenedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun exportProject(id: ProjectId): ProjectExportResult =
        withContext(Dispatchers.IO) {
            val entity = projectDao.getById(id.value)
                ?: throw AppException("Project not found")
            val root = File(entity.rootPath)
            if (!root.isDirectory) {
                throw AppException("Couldn't find the project files to export.")
            }

            val displayName = sanitizeZipDisplayName(entity.name)
            val exportDir = File(context.cacheDir, EXPORT_CACHE_DIR)
            exportDir.mkdirs()
            val zipFile = File(exportDir, displayName)
            ProjectArchive.zipProject(root, zipFile)

            val downloadsUri = runCatching {
                ProjectZipDownloads.publish(context, zipFile, displayName)
            }.getOrNull()

            ProjectExportResult(
                localZipPath = zipFile.absolutePath,
                displayName = displayName,
                downloadsUri = downloadsUri,
            )
        }

    override suspend fun importProject(zipUri: String): Project =
        withContext(Dispatchers.IO) {
            val uri = Uri.parse(zipUri)
            val id = UUID.randomUUID().toString()
            val stagingZip = File(context.cacheDir, "import-$id.zip")
            val extractDir = File(context.cacheDir, "import-extract-$id")
            val rootDir = projectDir(id)

            try {
                copyUriToFile(uri, stagingZip)
                if (extractDir.exists()) extractDir.deleteRecursively()
                val unpackedRoot = ProjectArchive.unzipProject(stagingZip, extractDir)

                val fallbackName = uri.lastPathSegment
                    ?.substringAfterLast('/')
                    ?.removeSuffix(".zip")
                    ?.removeSuffix(".ZIP")
                    ?: "ImportedProject"
                val meta = ProjectImportMetadata.read(unpackedRoot, fallbackName = fallbackName)
                val existingNames = projectDao.getAllNames().toSet()
                val name = ProjectImportMetadata.allocateUniqueName(meta.name, existingNames)

                if (rootDir.exists()) {
                    throw AppException("Project directory already exists")
                }
                if (!unpackedRoot.copyRecursively(rootDir, overwrite = false)) {
                    throw AppException("Couldn't copy the imported project files.")
                }

                val now = System.currentTimeMillis()
                val entity = ProjectEntity(
                    id = id,
                    name = name,
                    packageName = meta.packageName,
                    rootPath = rootDir.absolutePath,
                    lastOpenedAt = now,
                )
                projectDao.upsert(entity)
                entity.toDomain()
            } catch (t: Throwable) {
                rootDir.deleteRecursively()
                throw t
            } finally {
                stagingZip.delete()
                extractDir.deleteRecursively()
            }
        }

    override fun validateCreateProject(
        name: String,
        packageName: String,
        minSdk: Int?,
    ): CreateProjectFieldErrors =
        ProjectValidation.fieldErrors(name = name, packageName = packageName, minSdk = minSdk)

    private fun copyUriToFile(uri: Uri, destination: File) {
        destination.parentFile?.mkdirs()
        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: throw AppException("Couldn't open the selected zip file.")
            input.use { stream ->
                destination.outputStream().use { out -> stream.copyTo(out) }
            }
        } catch (e: AppException) {
            throw e
        } catch (e: IOException) {
            throw AppException("Couldn't read the selected zip file.", e)
        }
        if (!destination.isFile || destination.length() == 0L) {
            throw AppException("The selected file is empty or unreadable.")
        }
    }

    private fun projectDir(id: String): File =
        File(File(context.filesDir, PROJECTS_DIR), id)

    private companion object {
        const val PROJECTS_DIR = "projects"
        const val EXPORT_CACHE_DIR = "project-export"

        fun sanitizeZipDisplayName(projectName: String): String {
            val safe = projectName.trim()
                .replace(Regex("""[\\/:*?"<>|\u0000-\u001F]"""), "_")
                .ifEmpty { "project" }
            return if (safe.endsWith(".zip", ignoreCase = true)) safe else "$safe.zip"
        }
    }
}
