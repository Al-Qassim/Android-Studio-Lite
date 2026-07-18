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

/** Full job record for the build service — no Room / engine vendor types. */
data class BuildJobSnapshot(
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
    val providerId: String = "github",
    /** Opaque engine resume cursor (JSON string owned by the engine). */
    val resumeCursor: String? = null,
    val lastActivePhase: BuildPhase? = null,
    val startedAtEpochMs: Long,
    val finishedAtEpochMs: Long? = null,
)

/**
 * Persistence port for build jobs.
 * Adapters map Room entities ↔ [BuildJobSnapshot].
 */
interface BuildJobRepository {
    fun observe(jobId: String): Flow<BuildJobSnapshot?>
    suspend fun get(jobId: String): BuildJobSnapshot?
    suspend fun save(job: BuildJobSnapshot)
    suspend fun nonTerminal(): List<BuildJobSnapshot>
    suspend fun nonTerminalForProject(projectId: String): List<BuildJobSnapshot>
}

/** Project + job identity the engine needs to run or resume a build. */
data class BuildEngineSession(
    val jobId: String,
    val projectName: String,
    val packageName: String,
    val projectRootPath: String,
)

/** Engine → service progress tick. [resumeCursor] is opaque to the service/Room. */
data class BuildEngineUpdate(
    val phase: BuildPhase,
    val message: String? = null,
    val logUrl: String? = null,
    val apkLocalPath: String? = null,
    val resumeCursor: String? = null,
)

/**
 * Build backend port.
 * No credentials on this interface — engines that need auth pull it themselves.
 */
interface BuildEngine {
    val providerId: String
    val providerDisplayName: String

    fun canResume(resumeCursor: String?): Boolean

    /**
     * Emits when the service should retry attaching non-terminal jobs
     * (e.g. cloud engine after sign-in). Local engines may never emit.
     * Eager resume on process start is separate and always runs.
     */
    fun observeResumeHints(): Flow<Unit>

    suspend fun execute(
        session: BuildEngineSession,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
    )

    suspend fun resume(
        session: BuildEngineSession,
        resumeCursor: String,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
    )

    /** Best-effort remote cancel/cleanup for the opaque cursor. */
    suspend fun cancel(resumeCursor: String?)
}
