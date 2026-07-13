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
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
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
        val runner = scope.launch {
            runCloudBuild(
                jobId = jobId,
                request = request,
                token = token,
                progress = progress,
            )
        }
        jobs[jobId] = BuildJob(progress = progress, runner = runner)
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
        var release: GitHubReleaseRef? = null
        var repo: GitHubRepoRef? = null
        var logUrl: String? = null
        try {
            progress.update {
                it.copy(
                    phase = BuildPhase.Preparing,
                    message = "Ensuring build sandbox…",
                    providerName = authSession.providerDisplayName,
                )
            }
            val buildRepo = gitHubClient.ensureSandboxRepo(token)
            repo = buildRepo
            jobs[jobId]?.repo = buildRepo
            gitHubClient.ensureWorkflowFile(token, buildRepo)
            delay(2_000)

            progress.update {
                it.copy(phase = BuildPhase.Uploading, message = "Zipping project sources…")
            }
            val zipFile = File(appContext.cacheDir, "buildapk/project-$jobId.zip")
            ProjectZipper.zipProject(File(request.projectRoot.absolutePath), zipFile)

            val tag = "asl-build-$jobId"
            progress.update {
                it.copy(phase = BuildPhase.Uploading, message = "Uploading project sources…")
            }
            release = gitHubClient.createRelease(token, buildRepo, tag).also {
                gitHubClient.uploadReleaseAsset(token, it, zipFile, "project.zip")
                jobs[jobId]?.release = it
            }
            zipFile.delete()

            progress.update {
                it.copy(phase = BuildPhase.Queued, message = "Starting remote build…")
            }
            val dispatchAt = System.currentTimeMillis()
            var lastError: Exception? = null
            var dispatched = false
            for (attempt in 0 until 8) {
                try {
                    gitHubClient.dispatchWorkflow(token, buildRepo, tag)
                    dispatched = true
                    break
                } catch (e: Exception) {
                    lastError = e
                    delay(3_000L * (attempt + 1))
                }
            }
            if (!dispatched) {
                throw lastError ?: AppException("Couldn't start the remote build. Try again.")
            }

            var run = gitHubClient.findLatestWorkflowRun(token, buildRepo, dispatchAt)
            for (attempt in 0 until 30) {
                if (!coroutineContext.isActive) return
                if (run != null) break
                delay(2_000)
                run = gitHubClient.findLatestWorkflowRun(token, buildRepo, dispatchAt)
            }
            val resolved = run
                ?: throw AppException("Couldn't find the build run after starting it. Try again.")
            jobs[jobId]?.runId = resolved.id
            logUrl = resolved.htmlUrl

            progress.update {
                it.copy(
                    phase = BuildPhase.Building,
                    message = "Building APK remotely…",
                    logUrl = logUrl,
                )
            }
            while (coroutineContext.isActive) {
                val status = gitHubClient.getWorkflowRun(token, buildRepo, resolved.id)
                logUrl = status.htmlUrl ?: logUrl
                when (status.status) {
                    "completed" -> {
                        if (status.conclusion != "success") {
                            throw AppException("Build failed. Open the build log.")
                        }
                        break
                    }
                    "queued", "waiting", "pending" -> {
                        progress.update {
                            it.copy(
                                phase = BuildPhase.Queued,
                                message = "Waiting in queue…",
                                logUrl = logUrl,
                            )
                        }
                        delay(5_000)
                    }
                    else -> {
                        progress.update {
                            it.copy(
                                phase = BuildPhase.Building,
                                message = "Building APK remotely…",
                                logUrl = logUrl,
                            )
                        }
                        delay(5_000)
                    }
                }
            }
            if (!coroutineContext.isActive) return

            progress.update {
                it.copy(phase = BuildPhase.Downloading, message = "Downloading APK…")
            }
            val apkFile = File(appContext.cacheDir, "buildapk/asl-$jobId.apk")
            val assetUrl = gitHubClient.findReleaseApkAssetUrl(token, buildRepo, tag)
            gitHubClient.downloadAssetToFile(token, assetUrl, apkFile)

            progress.update {
                it.copy(
                    phase = BuildPhase.ReadyToInstall,
                    message = "APK ready to install",
                    apkLocalPath = apkFile.absolutePath,
                    logUrl = logUrl,
                )
            }
        } catch (e: AppException) {
            progress.update {
                it.copy(
                    phase = BuildPhase.Failed,
                    message = null,
                    error = e.uiMessage,
                    logUrl = logUrl,
                )
            }
        } catch (_: Exception) {
            progress.update {
                it.copy(
                    phase = BuildPhase.Failed,
                    message = null,
                    error = "Build failed. Open the build log.",
                    logUrl = logUrl,
                )
            }
        } finally {
            val rel = release
            val r = repo
            if (rel != null && r != null) {
                runCatching { gitHubClient.deleteRelease(token, r, rel.id, rel.tag) }
            }
        }
    }

    private data class BuildJob(
        val progress: MutableStateFlow<BuildProgress>,
        val runner: Job,
        var repo: GitHubRepoRef? = null,
        var runId: Long? = null,
        var release: GitHubReleaseRef? = null,
    )
}
