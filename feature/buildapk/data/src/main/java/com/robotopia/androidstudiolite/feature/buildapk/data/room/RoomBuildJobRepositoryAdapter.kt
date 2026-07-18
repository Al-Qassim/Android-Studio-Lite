package com.robotopia.androidstudiolite.feature.buildapk.data.room

import com.robotopia.androidstudiolite.feature.buildapk.data.github.GitHubResumePayload
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildJobRepository
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildJobSnapshot
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildResume
import com.robotopia.androidstudiolite.feature.buildapk.data.job.RemoteRelease
import com.robotopia.androidstudiolite.feature.buildapk.data.job.RemoteRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Adapts [BuildJobDao] / Room entities to [BuildJobRepository]. */
internal class RoomBuildJobRepositoryAdapter(
    private val buildJobDao: BuildJobDao,
) : BuildJobRepository {
    override fun observe(jobId: String): Flow<BuildJobSnapshot?> =
        buildJobDao.observeById(jobId).map { it?.toSnapshot() }

    override suspend fun get(jobId: String): BuildJobSnapshot? =
        buildJobDao.getById(jobId)?.toSnapshot()

    override suspend fun save(job: BuildJobSnapshot) {
        buildJobDao.upsert(job.toEntity())
    }

    override suspend fun nonTerminal(): List<BuildJobSnapshot> =
        buildJobDao.getNonTerminal().map { it.toSnapshot() }

    override suspend fun nonTerminalForProject(projectId: String): List<BuildJobSnapshot> =
        buildJobDao.getNonTerminalForProject(projectId).map { it.toSnapshot() }
}

private fun BuildJobEntity.toSnapshot(): BuildJobSnapshot =
    BuildJobSnapshot(
        jobId = jobId,
        projectId = projectId,
        projectName = projectName,
        packageName = packageName,
        projectRootPath = projectRootPath,
        phase = phase.toBuildPhase(),
        message = message,
        error = error,
        apkLocalPath = apkLocalPath,
        logUrl = logUrl,
        providerName = providerName,
        resume = GitHubResumePayload.decode(resumeJson)?.toBuildResume(),
        lastActivePhase = lastActivePhase?.toBuildPhase(),
        startedAtEpochMs = startedAtEpochMs,
        finishedAtEpochMs = finishedAtEpochMs,
    )

private fun BuildJobSnapshot.toEntity(): BuildJobEntity =
    BuildJobEntity(
        jobId = jobId,
        projectId = projectId,
        projectName = projectName,
        packageName = packageName,
        projectRootPath = projectRootPath,
        phase = phase.toStorageName(),
        message = message,
        error = error,
        apkLocalPath = apkLocalPath,
        logUrl = logUrl,
        providerName = providerName,
        providerId = "github",
        resumeJson = resume?.toGitHubPayload()?.encode(),
        lastActivePhase = lastActivePhase?.toStorageName(),
        startedAtEpochMs = startedAtEpochMs,
        finishedAtEpochMs = finishedAtEpochMs,
    )

private fun GitHubResumePayload.toBuildResume(): BuildResume =
    BuildResume(
        repo = RemoteRepo(owner = owner, name = repo),
        runId = runId,
        release = toReleaseRefOrNull()?.let {
            RemoteRelease(
                id = it.id,
                tag = it.tag,
                uploadUrlTemplate = it.uploadUrlTemplate,
            )
        },
    )

private fun BuildResume.toGitHubPayload(): GitHubResumePayload =
    GitHubResumePayload(
        owner = repo.owner,
        repo = repo.name,
        runId = runId,
        releaseId = release?.id,
        releaseTag = release?.tag,
        uploadUrlTemplate = release?.uploadUrlTemplate,
    )
