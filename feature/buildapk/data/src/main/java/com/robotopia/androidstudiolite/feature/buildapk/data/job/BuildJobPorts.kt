package com.robotopia.androidstudiolite.feature.buildapk.data.job

import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import kotlinx.coroutines.flow.Flow

internal fun BuildPhase.isTerminal(): Boolean = when (this) {
    BuildPhase.ReadyToInstall,
    BuildPhase.Failed,
    BuildPhase.Cancelled,
    -> true
    else -> false
}

/** Remote repo as the build-job logic sees it (provider-neutral). */
internal data class RemoteRepo(
    val owner: String,
    val name: String,
)

internal data class RemoteRelease(
    val id: Long,
    val tag: String,
    val uploadUrlTemplate: String,
)

internal data class RemoteRun(
    val id: Long,
    val status: String,
    val conclusion: String?,
    val htmlUrl: String?,
)

/** Opaque-enough resume cursor for re-attaching a non-terminal job. */
internal data class BuildResume(
    val repo: RemoteRepo,
    val runId: Long? = null,
    val release: RemoteRelease? = null,
)

/** Full job record for the build-job logic — no Room / GitHub types. */
internal data class BuildJobSnapshot(
    val jobId: String,
    val projectId: String,
    val projectName: String,
    val packageName: String,
    val projectRootPath: String,
    val phase: BuildPhase,
    val message: String? = null,
    val error: String? = null,
    val apkLocalPath: String? = null,
    val logUrl: String? = null,
    val providerName: String? = null,
    val resume: BuildResume? = null,
    val lastActivePhase: BuildPhase? = null,
    val startedAtEpochMs: Long,
    val finishedAtEpochMs: Long? = null,
)

/**
 * Persistence port owned by [BuildJobLogic].
 * Adapters map Room entities ↔ [BuildJobSnapshot].
 */
internal interface BuildJobRepository {
    fun observe(jobId: String): Flow<BuildJobSnapshot?>
    suspend fun get(jobId: String): BuildJobSnapshot?
    suspend fun save(job: BuildJobSnapshot)
    suspend fun nonTerminal(): List<BuildJobSnapshot>
    suspend fun nonTerminalForProject(projectId: String): List<BuildJobSnapshot>
}

/**
 * Cloud + local artifact port owned by [BuildJobLogic].
 * Adapters map GitHub / filesystem details.
 */
internal interface CloudBuildGateway {
    suspend fun prepareSandbox(token: String): RemoteRepo

    suspend fun uploadProjectZip(
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
        projectRootPath: String,
        jobId: String,
    ): RemoteRelease

    suspend fun dispatchBuild(token: String, repo: RemoteRepo, releaseTag: String)

    suspend fun findLatestRun(
        token: String,
        repo: RemoteRepo,
        notBeforeEpochMs: Long,
    ): RemoteRun?

    suspend fun getRun(token: String, repo: RemoteRepo, runId: Long): RemoteRun

    suspend fun cancelRun(token: String, repo: RemoteRepo, runId: Long)

    /** Downloads the APK and publishes it; returns the installable local path/URI. */
    suspend fun downloadAndPublishApk(
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
        projectName: String,
        jobId: String,
    ): String

    suspend fun deleteRelease(token: String, repo: RemoteRepo, release: RemoteRelease)
}
