package com.robotopia.androidstudiolite.feature.buildapk.data

import android.content.Context
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubReleaseRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubWorkflowRun
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

/**
 * Cloud APK builds on the user’s GitHub account via Actions + ephemeral releases.
 */
class GitHubActionsBuildService(
    context: Context,
    private val authSession: AuthSession,
    private val gitHubClient: GitHubClient,
) : BuildService {

    private val appContext = context.applicationContext
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val jobs = ConcurrentHashMap<String, BuildJob>()

    override fun observeBuild(jobId: String): Flow<BuildProgress> {
        val job = jobs[jobId]
            ?: return MutableStateFlow(
                BuildProgress(
                    jobId = jobId,
                    phase = BuildPhase.Failed,
                    error = "Build not found",
                ),
            ).asStateFlow()
        return job.progress.asStateFlow()
    }

    override suspend fun startBuild(request: BuildRequest): String {
        val token = authSession.accessToken()
            ?: throw AppException("Connect your build account before starting a build.")
        val jobId = UUID.randomUUID().toString()
        val progress = MutableStateFlow(
            BuildProgress(
                jobId = jobId,
                phase = BuildPhase.Preparing,
                message = "Preparing build sandbox…",
                providerName = authSession.providerDisplayName,
            ),
        )
        val buildJob = BuildJob(progress = progress)
        jobs[jobId] = buildJob
        buildJob.runner = scope.launch {
            runCloudBuild(
                jobId = jobId,
                request = request,
                token = token,
                progress = progress,
            )
        }
        return jobId
    }

    override suspend fun cancelBuild(jobId: String) {
        val job = jobs[jobId]
            ?: throw AppException("Build not found")
        job.runner.cancel()
        val token = authSession.accessToken()
        val repo = job.repo
        val runId = job.runId
        if (token != null && repo != null && runId != null) {
            runCatching { gitHubClient.cancelWorkflowRun(token, repo, runId) }
        }
        if (token != null && repo != null && job.release != null) {
            val release = job.release!!
            runCatching {
                gitHubClient.deleteRelease(token, repo, release.id, release.tag)
            }
        }
        job.progress.update {
            it.copy(
                phase = BuildPhase.Cancelled,
                message = "No APK was produced. You can start a new build when you're ready.",
                error = null,
            )
        }
    }

    private suspend fun runCloudBuild(
        jobId: String,
        request: BuildRequest,
        token: String,
        progress: MutableStateFlow<BuildProgress>,
    ) {
        try {
            val repo = prepareSandbox(jobId = jobId, token = token, progress = progress)
            val releaseTag = uploadProject(
                jobId = jobId,
                request = request,
                token = token,
                repo = repo,
                progress = progress,
            )
            awaitRemoteBuild(
                jobId = jobId,
                token = token,
                repo = repo,
                releaseTag = releaseTag,
                progress = progress,
            )
            if (!coroutineContext.isActive) return
            downloadReadyApk(
                jobId = jobId,
                projectName = request.projectName,
                token = token,
                repo = repo,
                releaseTag = releaseTag,
                progress = progress,
            )
        } catch (e: CancellationException) {
            // cancelBuild owns Cancelled progress; do not flash Failed.
            throw e
        } catch (e: AppException) {
            if (progress.value.phase != BuildPhase.Cancelled) {
                progress.update {
                    it.copy(
                        phase = BuildPhase.Failed,
                        message = null,
                        error = e.uiMessage,
                    )
                }
            }
        } catch (_: Exception) {
            if (progress.value.phase != BuildPhase.Cancelled) {
                progress.update {
                    it.copy(
                        phase = BuildPhase.Failed,
                        message = null,
                        error = "Build failed. Open the build log.",
                    )
                }
            }
        } finally {
            val job = jobs[jobId]
            val repo = job?.repo
            val release = job?.release
            if (repo != null && release != null) {
                runCatching { gitHubClient.deleteRelease(token, repo, release.id, release.tag) }
            }
        }
    }

    private suspend fun prepareSandbox(
        jobId: String,
        token: String,
        progress: MutableStateFlow<BuildProgress>,
    ): GitHubRepoRef {
        progress.update {
            it.copy(
                phase = BuildPhase.Preparing,
                message = "Ensuring build sandbox…",
                providerName = authSession.providerDisplayName,
                error = null,
            )
        }
        val repo = gitHubClient.ensureSandboxRepo(token)
        jobs[jobId]?.repo = repo
        gitHubClient.ensureWorkflowFile(token, repo)
        delay(WORKFLOW_REGISTER_DELAY_MS)
        return repo
    }

    private suspend fun uploadProject(
        jobId: String,
        request: BuildRequest,
        token: String,
        repo: GitHubRepoRef,
        progress: MutableStateFlow<BuildProgress>,
    ): String {
        progress.update {
            it.copy(
                phase = BuildPhase.Uploading,
                message = "Zipping project sources…",
                providerName = authSession.providerDisplayName,
                error = null,
            )
        }
        val zipFile = File(appContext.cacheDir, "buildapk/project-$jobId.zip")
        ProjectZipper.zipProject(File(request.projectRoot.absolutePath), zipFile)

        val tag = "asl-build-$jobId"
        progress.update {
            it.copy(
                phase = BuildPhase.Uploading,
                message = "Uploading project sources…",
                providerName = authSession.providerDisplayName,
                error = null,
            )
        }
        val release = gitHubClient.createRelease(token, repo, tag)
        gitHubClient.uploadReleaseAsset(token, release, zipFile, "project.zip")
        jobs[jobId]?.release = release
        zipFile.delete()
        return tag
    }

    private suspend fun awaitRemoteBuild(
        jobId: String,
        token: String,
        repo: GitHubRepoRef,
        releaseTag: String,
        progress: MutableStateFlow<BuildProgress>,
    ) {
        progress.update {
            it.copy(
                phase = BuildPhase.Queued,
                message = "Starting remote build…",
                providerName = authSession.providerDisplayName,
                error = null,
            )
        }
        val dispatchAt = dispatchWorkflowWithRetry(token = token, repo = repo, releaseTag = releaseTag)
        val run = findWorkflowRun(
            token = token,
            repo = repo,
            notBeforeEpochMs = dispatchAt,
        )
        jobs[jobId]?.runId = run.id
        pollUntilBuildSucceeds(
            token = token,
            repo = repo,
            runId = run.id,
            initialLogUrl = run.htmlUrl,
            progress = progress,
        )
    }

    private suspend fun dispatchWorkflowWithRetry(
        token: String,
        repo: GitHubRepoRef,
        releaseTag: String,
    ): Long {
        val dispatchAt = System.currentTimeMillis()
        var lastError: Exception? = null
        for (attempt in 0 until DISPATCH_ATTEMPTS) {
            try {
                gitHubClient.dispatchWorkflow(token, repo, releaseTag)
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
        repo: GitHubRepoRef,
        notBeforeEpochMs: Long,
    ): GitHubWorkflowRun {
        var run = gitHubClient.findLatestWorkflowRun(token, repo, notBeforeEpochMs)
        for (attempt in 0 until FIND_RUN_ATTEMPTS) {
            if (!coroutineContext.isActive) {
                throw CancellationException()
            }
            if (run != null) return run
            delay(FIND_RUN_POLL_MS)
            run = gitHubClient.findLatestWorkflowRun(token, repo, notBeforeEpochMs)
        }
        throw AppException("Couldn't find the build run after starting it. Try again.")
    }

    /**
     * Stay on Queued until GitHub reports in_progress; never regress to Queued after Building.
     */
    private suspend fun pollUntilBuildSucceeds(
        token: String,
        repo: GitHubRepoRef,
        runId: Long,
        initialLogUrl: String?,
        progress: MutableStateFlow<BuildProgress>,
    ) {
        var logUrl = initialLogUrl
        while (coroutineContext.isActive) {
            val status = gitHubClient.getWorkflowRun(token, repo, runId)
            logUrl = status.htmlUrl ?: logUrl
            when (status.status) {
                "completed" -> {
                    if (status.conclusion != "success") {
                        throw AppException("Build failed. Open the build log.")
                    }
                    return
                }
                "queued", "waiting", "pending", "requested" -> {
                    if (progress.value.phase.ordinal < BuildPhase.Building.ordinal) {
                        progress.update {
                            it.copy(
                                phase = BuildPhase.Queued,
                                message = "Waiting in queue…",
                                providerName = authSession.providerDisplayName,
                                logUrl = logUrl,
                                error = null,
                            )
                        }
                    }
                    delay(RUN_POLL_MS)
                }
                else -> {
                    progress.update {
                        it.copy(
                            phase = BuildPhase.Building,
                            message = "Building APK remotely…",
                            providerName = authSession.providerDisplayName,
                            logUrl = logUrl,
                            error = null,
                        )
                    }
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
        repo: GitHubRepoRef,
        releaseTag: String,
        progress: MutableStateFlow<BuildProgress>,
    ) {
        val logUrl = progress.value.logUrl
        progress.update {
            it.copy(
                phase = BuildPhase.Downloading,
                message = "Downloading APK…",
                providerName = authSession.providerDisplayName,
                logUrl = logUrl,
                error = null,
            )
        }
        val tempApk = File(appContext.cacheDir, "buildapk/asl-$jobId.apk")
        val assetUrl = gitHubClient.findReleaseApkAssetUrl(token, repo, releaseTag)
        gitHubClient.downloadAssetToFile(token, assetUrl, tempApk)
        val downloadsUri = ApkDownloads.publish(
            context = appContext,
            source = tempApk,
            displayName = projectName,
        )
        tempApk.delete()
        progress.update {
            it.copy(
                phase = BuildPhase.ReadyToInstall,
                message = "APK saved to Downloads",
                apkLocalPath = downloadsUri,
                logUrl = logUrl,
                error = null,
            )
        }
    }

    private class BuildJob(
        val progress: MutableStateFlow<BuildProgress>,
    ) {
        lateinit var runner: Job
        var repo: GitHubRepoRef? = null
        var runId: Long? = null
        var release: GitHubReleaseRef? = null
    }

    private companion object {
        const val WORKFLOW_REGISTER_DELAY_MS = 2_000L
        const val DISPATCH_ATTEMPTS = 8
        const val DISPATCH_RETRY_BASE_MS = 3_000L
        const val FIND_RUN_ATTEMPTS = 30
        const val FIND_RUN_POLL_MS = 2_000L
        const val RUN_POLL_MS = 5_000L
    }
}
