package com.robotopia.androidstudiolite.feature.buildapk.data.job

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.coroutineContext

/**
 * Owns build-job lifecycle logic. Depends only on [BuildJobRepository] and
 * [CloudBuildGateway] — no Room / GitHub mapping.
 */
internal class BuildJobLogic(
    private val jobs: BuildJobRepository,
    private val cloud: CloudBuildGateway,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {
    private val runners = ConcurrentHashMap<String, RunnerState>()
    private val resumeMutex = Mutex()
    private var resumeStarted = false

    fun observeBuild(jobId: String): Flow<BuildProgress> =
        jobs.observe(jobId).map { snapshot ->
            snapshot?.toProgress()
                ?: BuildProgress(
                    jobId = jobId,
                    phase = BuildPhase.Failed,
                    error = "Build not found",
                )
        }

    suspend fun startBuild(
        request: BuildRequest,
        token: String,
        providerName: String,
    ): String {
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
                lastActivePhase = BuildPhase.Preparing,
                startedAtEpochMs = now,
            ),
        )
        val runner = RunnerState()
        runners[jobId] = runner
        runner.job = scope.launch {
            runCloudBuild(
                jobId = jobId,
                request = request,
                token = token,
                providerName = providerName,
                runner = runner,
            )
        }
        return jobId
    }

    suspend fun cancelBuild(jobId: String, token: String?) {
        val existing = jobs.get(jobId) ?: throw AppException("Build not found")
        if (existing.phase.isTerminal()) return
        runners[jobId]?.job?.cancel()
        val resume = existing.resume
        if (token != null && resume != null) {
            resume.runId?.let { runId ->
                runCatching { cloud.cancelRun(token, resume.repo, runId) }
            }
            resume.release?.let { release ->
                runCatching { cloud.deleteRelease(token, resume.repo, release) }
            }
        }
        persistProgress(
            jobId = jobId,
            progress = BuildProgress(
                jobId = jobId,
                phase = BuildPhase.Cancelled,
                message = "No APK was produced. You can start a new build when you're ready.",
                providerName = existing.providerName,
                logUrl = existing.logUrl,
            ),
            resume = existing.resume,
        )
        runners.remove(jobId)
    }

    suspend fun cancelBuildsForProject(projectId: String, token: String?) {
        jobs.nonTerminalForProject(projectId).forEach { job ->
            runCatching { cancelBuild(job.jobId, token) }
        }
    }

    fun onSignedIn(tokenProvider: suspend () -> String?, providerName: () -> String) {
        scope.launch {
            ensureResumeStarted(tokenProvider, providerName)
            resumeEligibleJobs(tokenProvider, providerName)
        }
    }

    fun startEagerResume(tokenProvider: suspend () -> String?, providerName: () -> String) {
        scope.launch { ensureResumeStarted(tokenProvider, providerName) }
    }

    private suspend fun ensureResumeStarted(
        tokenProvider: suspend () -> String?,
        providerName: () -> String,
    ) {
        resumeMutex.withLock {
            if (resumeStarted) return
            resumeStarted = true
        }
        resumeEligibleJobs(tokenProvider, providerName)
    }

    private suspend fun resumeEligibleJobs(
        tokenProvider: suspend () -> String?,
        providerName: () -> String,
    ) {
        val token = tokenProvider()
        for (job in jobs.nonTerminal()) {
            if (runners.containsKey(job.jobId)) continue
            val resume = job.resume
            val runId = resume?.runId
            if (resume == null || runId == null) {
                persistProgress(
                    jobId = job.jobId,
                    progress = BuildProgress(
                        jobId = job.jobId,
                        phase = BuildPhase.Failed,
                        error = "Build interrupted. Start a new build to try again.",
                        providerName = job.providerName,
                        logUrl = job.logUrl,
                    ),
                    resume = job.resume,
                    lastActivePhase = job.lastActivePhase ?: job.phase,
                )
                continue
            }
            if (token == null) continue
            val runner = RunnerState(
                resume = resume.copy(runId = runId),
            )
            runners[job.jobId] = runner
            runner.job = scope.launch {
                resumeCloudBuild(
                    job = job,
                    token = token,
                    providerName = job.providerName ?: providerName(),
                    runner = runner,
                )
            }
        }
    }

    private suspend fun resumeCloudBuild(
        job: BuildJobSnapshot,
        token: String,
        providerName: String,
        runner: RunnerState,
    ) {
        val jobId = job.jobId
        try {
            val resume = runner.resume
                ?: throw AppException("Build interrupted. Start a new build to try again.")
            val runId = resume.runId
                ?: throw AppException("Build interrupted. Start a new build to try again.")
            val releaseTag = resume.release?.tag
                ?: throw AppException("Build interrupted. Start a new build to try again.")
            val startingPhase = when (job.phase) {
                BuildPhase.Downloading -> BuildPhase.Downloading
                else -> BuildPhase.Building
            }
            persistProgress(
                jobId = jobId,
                progress = BuildProgress(
                    jobId = jobId,
                    phase = startingPhase,
                    message = "Resuming remote build…",
                    providerName = providerName,
                    logUrl = job.logUrl,
                ),
                resume = resume,
            )
            if (startingPhase != BuildPhase.Downloading) {
                pollUntilBuildSucceeds(
                    jobId = jobId,
                    token = token,
                    repo = resume.repo,
                    runId = runId,
                    initialLogUrl = job.logUrl,
                    providerName = providerName,
                    runner = runner,
                )
            }
            if (!coroutineContext.isActive) return
            downloadReadyApk(
                jobId = jobId,
                projectName = job.projectName,
                token = token,
                repo = resume.repo,
                releaseTag = releaseTag,
                providerName = providerName,
                runner = runner,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            failJob(jobId, e.uiMessage, runner.resume, providerName)
        } catch (_: Exception) {
            failJob(jobId, "Build failed. Open the build log.", runner.resume, providerName)
        } finally {
            cleanupRelease(token, runner)
            runners.remove(jobId)
        }
    }

    private suspend fun runCloudBuild(
        jobId: String,
        request: BuildRequest,
        token: String,
        providerName: String,
        runner: RunnerState,
    ) {
        try {
            val repo = prepareSandbox(jobId, token, providerName, runner)
            val releaseTag = uploadProject(jobId, request, token, repo, providerName, runner)
            awaitRemoteBuild(jobId, token, repo, releaseTag, providerName, runner)
            if (!coroutineContext.isActive) return
            downloadReadyApk(
                jobId = jobId,
                projectName = request.projectName,
                token = token,
                repo = repo,
                releaseTag = releaseTag,
                providerName = providerName,
                runner = runner,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: AppException) {
            val current = jobs.get(jobId)?.phase
            if (current != BuildPhase.Cancelled) {
                failJob(jobId, e.uiMessage, runner.resume, providerName)
            }
        } catch (_: Exception) {
            val current = jobs.get(jobId)?.phase
            if (current != BuildPhase.Cancelled) {
                failJob(jobId, "Build failed. Open the build log.", runner.resume, providerName)
            }
        } finally {
            cleanupRelease(token, runner)
            runners.remove(jobId)
        }
    }

    private suspend fun failJob(
        jobId: String,
        error: String,
        resume: BuildResume?,
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
            resume = resume ?: existing?.resume,
            lastActivePhase = existing?.lastActivePhase ?: existing?.phase,
        )
    }

    private suspend fun cleanupRelease(token: String, runner: RunnerState) {
        val resume = runner.resume ?: return
        val release = resume.release ?: return
        runCatching { cloud.deleteRelease(token, resume.repo, release) }
    }

    private suspend fun prepareSandbox(
        jobId: String,
        token: String,
        providerName: String,
        runner: RunnerState,
    ): RemoteRepo {
        emitProgress(jobId, BuildPhase.Preparing, "Ensuring build sandbox…", providerName, runner)
        val repo = cloud.prepareSandbox(token)
        runner.resume = BuildResume(repo = repo, release = runner.resume?.release, runId = null)
        persistResume(jobId, runner)
        delay(WORKFLOW_REGISTER_DELAY_MS)
        return repo
    }

    private suspend fun uploadProject(
        jobId: String,
        request: BuildRequest,
        token: String,
        repo: RemoteRepo,
        providerName: String,
        runner: RunnerState,
    ): String {
        emitProgress(jobId, BuildPhase.Uploading, "Zipping project sources…", providerName, runner)
        val tag = "asl-build-$jobId"
        emitProgress(jobId, BuildPhase.Uploading, "Uploading project sources…", providerName, runner)
        val release = cloud.uploadProjectZip(
            token = token,
            repo = repo,
            releaseTag = tag,
            projectRootPath = request.projectRoot.absolutePath,
            jobId = jobId,
        )
        runner.resume = BuildResume(repo = repo, release = release, runId = null)
        persistResume(jobId, runner)
        return tag
    }

    private suspend fun awaitRemoteBuild(
        jobId: String,
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
        providerName: String,
        runner: RunnerState,
    ) {
        emitProgress(jobId, BuildPhase.Queued, "Starting remote build…", providerName, runner)
        val dispatchAt = dispatchWorkflowWithRetry(token, repo, releaseTag)
        val run = findWorkflowRun(token, repo, dispatchAt)
        runner.resume = (runner.resume ?: BuildResume(repo = repo)).copy(runId = run.id)
        persistResume(jobId, runner)
        pollUntilBuildSucceeds(
            jobId = jobId,
            token = token,
            repo = repo,
            runId = run.id,
            initialLogUrl = run.htmlUrl,
            providerName = providerName,
            runner = runner,
        )
    }

    private suspend fun dispatchWorkflowWithRetry(
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
    ): Long {
        val dispatchAt = System.currentTimeMillis()
        var lastError: Exception? = null
        for (attempt in 0 until DISPATCH_ATTEMPTS) {
            try {
                cloud.dispatchBuild(token, repo, releaseTag)
                return dispatchAt
            } catch (e: Exception) {
                lastError = e
                delay(DISPATCH_RETRY_BASE_MS * (attempt + 1))
            }
        }
        throw lastError ?: AppException("Couldn't start the remote build. Try again.")
    }

    private suspend fun findWorkflowRun(
        token: String,
        repo: RemoteRepo,
        notBeforeEpochMs: Long,
    ): RemoteRun {
        var run = cloud.findLatestRun(token, repo, notBeforeEpochMs)
        for (attempt in 0 until FIND_RUN_ATTEMPTS) {
            if (!coroutineContext.isActive) throw CancellationException()
            if (run != null) return run
            delay(FIND_RUN_POLL_MS)
            run = cloud.findLatestRun(token, repo, notBeforeEpochMs)
        }
        throw AppException("Couldn't find the build run after starting it. Try again.")
    }

    private suspend fun pollUntilBuildSucceeds(
        jobId: String,
        token: String,
        repo: RemoteRepo,
        runId: Long,
        initialLogUrl: String?,
        providerName: String,
        runner: RunnerState,
    ) {
        var logUrl = initialLogUrl
        while (coroutineContext.isActive) {
            val status = cloud.getRun(token, repo, runId)
            logUrl = status.htmlUrl ?: logUrl
            when (status.status) {
                "completed" -> {
                    if (status.conclusion != "success") {
                        throw AppException("Build failed. Open the build log.")
                    }
                    return
                }
                "queued", "waiting", "pending", "requested" -> {
                    val current = jobs.get(jobId)?.phase
                    if (current != null && current.ordinal < BuildPhase.Building.ordinal) {
                        emitProgress(
                            jobId = jobId,
                            phase = BuildPhase.Queued,
                            message = "Waiting in queue…",
                            providerName = providerName,
                            runner = runner,
                            logUrl = logUrl,
                        )
                    }
                    delay(RUN_POLL_MS)
                }
                else -> {
                    emitProgress(
                        jobId = jobId,
                        phase = BuildPhase.Building,
                        message = "Building APK remotely…",
                        providerName = providerName,
                        runner = runner,
                        logUrl = logUrl,
                    )
                    delay(RUN_POLL_MS)
                }
            }
        }
        throw CancellationException()
    }

    private suspend fun downloadReadyApk(
        jobId: String,
        projectName: String,
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
        providerName: String,
        runner: RunnerState,
    ) {
        val logUrl = jobs.get(jobId)?.logUrl
        emitProgress(
            jobId = jobId,
            phase = BuildPhase.Downloading,
            message = "Downloading APK…",
            providerName = providerName,
            runner = runner,
            logUrl = logUrl,
        )
        val apkPath = cloud.downloadAndPublishApk(
            token = token,
            repo = repo,
            releaseTag = releaseTag,
            projectName = projectName,
            jobId = jobId,
        )
        emitProgress(
            jobId = jobId,
            phase = BuildPhase.ReadyToInstall,
            message = "APK saved to Downloads",
            providerName = providerName,
            runner = runner,
            logUrl = logUrl,
            apkLocalPath = apkPath,
        )
    }

    private suspend fun emitProgress(
        jobId: String,
        phase: BuildPhase,
        message: String?,
        providerName: String,
        runner: RunnerState,
        logUrl: String? = null,
        apkLocalPath: String? = null,
    ) {
        persistProgress(
            jobId = jobId,
            progress = BuildProgress(
                jobId = jobId,
                phase = phase,
                message = message,
                apkLocalPath = apkLocalPath,
                providerName = providerName,
                logUrl = logUrl,
            ),
            resume = runner.resume,
        )
    }

    private suspend fun persistResume(jobId: String, runner: RunnerState) {
        val existing = jobs.get(jobId) ?: return
        jobs.save(existing.copy(resume = runner.resume))
    }

    private suspend fun persistProgress(
        jobId: String,
        progress: BuildProgress,
        resume: BuildResume?,
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
                resume = resume ?: existing.resume,
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

    private class RunnerState(
        var job: Job? = null,
        var resume: BuildResume? = null,
    )

    private companion object {
        const val WORKFLOW_REGISTER_DELAY_MS = 2_000L
        const val DISPATCH_ATTEMPTS = 8
        const val DISPATCH_RETRY_BASE_MS = 3_000L
        const val FIND_RUN_ATTEMPTS = 30
        const val FIND_RUN_POLL_MS = 2_000L
        const val RUN_POLL_MS = 5_000L
    }
}
