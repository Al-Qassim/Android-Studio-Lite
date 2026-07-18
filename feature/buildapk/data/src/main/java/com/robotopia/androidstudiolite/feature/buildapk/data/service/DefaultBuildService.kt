package com.robotopia.androidstudiolite.feature.buildapk.data.service

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventHooks
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventsListener
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildEngine
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildEngineSession
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildEngineUpdate
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildJobRepository
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildJobSnapshot
import com.robotopia.androidstudiolite.feature.buildapk.data.job.isTerminal
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventHooks
import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventsListener
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * [BuildService] implementation: job lifecycle (persist, runners, cancel, resume) plus
 * history/project-delete hooks. Depends on injected [BuildJobRepository] + [BuildEngine].
 * No auth/account — engines own credentials and optional resume hints.
 */
class DefaultBuildService(
    private val jobs: BuildJobRepository,
    private val engine: BuildEngine,
    historyEventHooks: BuildHistoryEventHooks,
    projectEventHooks: ProjectEventHooks,
) : BuildService {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val runners = ConcurrentHashMap<String, Job>()
    private val resumeMutex = Mutex()
    private var resumeStarted = false

    init {
        historyEventHooks.addListener(
            BuildHistoryEventsListener { jobId ->
                cancelBuild(jobId)
            },
        )
        projectEventHooks.addListener(
            ProjectEventsListener { projectId ->
                cancelBuildsForProject(projectId.value)
            },
        )
        // Always attempt resume on process start (works for local / no-account engines).
        startEagerResume()
        // Engine-specific hints (e.g. cloud sign-in) may trigger another attach pass.
        scope.launch {
            engine.observeResumeHints().collect {
                resumeEligibleJobs()
            }
        }
    }

    override fun observeBuild(jobId: String): Flow<BuildProgress> =
        jobs.observe(jobId).map { snapshot ->
            snapshot?.toProgress()
                ?: BuildProgress(
                    jobId = jobId,
                    phase = BuildPhase.Failed,
                    error = "Build not found",
                )
        }

    override suspend fun startBuild(request: BuildRequest): String {
        val providerName = engine.providerDisplayName
        val jobId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        jobs.save(
            BuildJobSnapshot(
                jobId = jobId,
                projectId = request.projectId.value,
                projectName = request.projectName,
                packageName = request.packageName,
                projectRootPath = request.projectRoot.absolutePath,
                phase = BuildPhase.Preparing,
                message = "Preparing build sandbox…",
                providerName = providerName,
                providerId = engine.providerId,
                lastActivePhase = BuildPhase.Preparing,
                startedAtEpochMs = now,
            ),
        )
        runners[jobId] = scope.launch {
            runEngine(
                jobId = jobId,
                session = BuildEngineSession(
                    jobId = jobId,
                    projectName = request.projectName,
                    packageName = request.packageName,
                    projectRootPath = request.projectRoot.absolutePath,
                ),
                providerName = providerName,
                block = { session ->
                    engine.execute(session) { update ->
                        persistEngineUpdate(jobId, update)
                    }
                },
            )
        }
        return jobId
    }

    override suspend fun cancelBuild(jobId: String) {
        val existing = jobs.get(jobId) ?: throw AppException("Build not found")
        if (existing.phase.isTerminal()) return
        runners[jobId]?.cancel()
        runCatching { engine.cancel(existing.resumeCursor) }
        persistProgress(
            jobId = jobId,
            progress = BuildProgress(
                jobId = jobId,
                phase = BuildPhase.Cancelled,
                message = "No APK was produced. You can start a new build when you're ready.",
                providerName = existing.providerName,
                logUrl = existing.logUrl,
            ),
            resumeCursor = existing.resumeCursor,
        )
        runners.remove(jobId)
    }

    override suspend fun cancelBuildsForProject(projectId: String) {
        jobs.nonTerminalForProject(projectId).forEach { job ->
            runCatching { cancelBuild(job.jobId) }
        }
    }

    private fun startEagerResume() {
        scope.launch { ensureResumeStarted() }
    }

    private fun resumeEligibleJobs() {
        scope.launch { resumeEligibleJobsInternal() }
    }

    private suspend fun ensureResumeStarted() {
        resumeMutex.withLock {
            if (resumeStarted) return
            resumeStarted = true
        }
        resumeEligibleJobsInternal()
    }

    private suspend fun resumeEligibleJobsInternal() {
        for (job in jobs.nonTerminal()) {
            if (runners.containsKey(job.jobId)) continue
            val cursor = job.resumeCursor
            if (!engine.canResume(cursor)) {
                persistProgress(
                    jobId = job.jobId,
                    progress = BuildProgress(
                        jobId = job.jobId,
                        phase = BuildPhase.Failed,
                        error = "Build interrupted. Start a new build to try again.",
                        providerName = job.providerName,
                        logUrl = job.logUrl,
                    ),
                    resumeCursor = cursor,
                    lastActivePhase = job.lastActivePhase ?: job.phase,
                )
                continue
            }
            val resumeCursor = cursor ?: continue
            runners[job.jobId] = scope.launch {
                runEngine(
                    jobId = job.jobId,
                    session = BuildEngineSession(
                        jobId = job.jobId,
                        projectName = job.projectName,
                        packageName = job.packageName,
                        projectRootPath = job.projectRootPath,
                    ),
                    providerName = job.providerName,
                    block = { session ->
                        engine.resume(
                            session = session,
                            resumeCursor = resumeCursor,
                            onUpdate = { update -> persistEngineUpdate(job.jobId, update) },
                        )
                    },
                )
            }
        }
    }

    private suspend fun runEngine(
        jobId: String,
        session: BuildEngineSession,
        providerName: String?,
        block: suspend (BuildEngineSession) -> Unit,
    ) {
        try {
            block(session)
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            val current = jobs.get(jobId)?.phase
            if (current != BuildPhase.Cancelled) {
                failJob(jobId, e.uiMessage, providerName)
            }
        } catch (_: Exception) {
            val current = jobs.get(jobId)?.phase
            if (current != BuildPhase.Cancelled) {
                failJob(jobId, "Build failed. Open the build log.", providerName)
            }
        } finally {
            runners.remove(jobId)
        }
    }

    private suspend fun persistEngineUpdate(jobId: String, update: BuildEngineUpdate) {
        val existing = jobs.get(jobId) ?: return
        persistProgress(
            jobId = jobId,
            progress = BuildProgress(
                jobId = jobId,
                phase = update.phase,
                message = update.message,
                apkLocalPath = update.apkLocalPath,
                providerName = existing.providerName,
                logUrl = update.logUrl,
            ),
            resumeCursor = update.resumeCursor ?: existing.resumeCursor,
        )
    }

    private suspend fun failJob(
        jobId: String,
        error: String,
        providerName: String?,
    ) {
        val existing = jobs.get(jobId)
        persistProgress(
            jobId = jobId,
            progress = BuildProgress(
                jobId = jobId,
                phase = BuildPhase.Failed,
                error = error,
                providerName = existing?.providerName ?: providerName,
                logUrl = existing?.logUrl,
            ),
            resumeCursor = existing?.resumeCursor,
            lastActivePhase = existing?.lastActivePhase ?: existing?.phase,
        )
    }

    private suspend fun persistProgress(
        jobId: String,
        progress: BuildProgress,
        resumeCursor: String?,
        lastActivePhase: BuildPhase? = null,
    ) {
        val existing = jobs.get(jobId) ?: return
        val resolvedLastActive = when {
            lastActivePhase != null -> lastActivePhase
            !progress.phase.isTerminal() -> progress.phase
            else -> existing.lastActivePhase
        }
        val finishedAt = if (progress.phase.isTerminal()) {
            existing.finishedAtEpochMs ?: System.currentTimeMillis()
        } else {
            null
        }
        jobs.save(
            existing.copy(
                phase = progress.phase,
                message = progress.message,
                error = progress.error,
                apkLocalPath = progress.apkLocalPath ?: existing.apkLocalPath,
                logUrl = progress.logUrl ?: existing.logUrl,
                providerName = progress.providerName ?: existing.providerName,
                resumeCursor = resumeCursor ?: existing.resumeCursor,
                lastActivePhase = resolvedLastActive,
                finishedAtEpochMs = finishedAt,
            ),
        )
    }

    private fun BuildJobSnapshot.toProgress(): BuildProgress =
        BuildProgress(
            jobId = jobId,
            phase = phase,
            message = message,
            apkLocalPath = apkLocalPath,
            error = error,
            providerName = providerName,
            logUrl = logUrl,
        )
}
