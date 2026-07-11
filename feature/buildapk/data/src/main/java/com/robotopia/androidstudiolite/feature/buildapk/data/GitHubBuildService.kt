package com.robotopia.androidstudiolite.feature.buildapk.data

import android.content.Context
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildService
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildProgress
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildRequest
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
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

/**
 * POC BuildService: zip project → GitHub release → Actions workflow_dispatch → download APK.
 */
class GitHubBuildService(
    context: Context,
    private val tokenStore: GitHubTokenStore,
) : BuildService {

    private val appContext = context.applicationContext
    private val api = GitHubApi(tokenStore)
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
        if (!tokenStore.hasToken()) {
            throw AppException("Paste a GitHub Personal Access Token before starting a build.")
        }
        val jobId = UUID.randomUUID().toString()
        val progress = MutableStateFlow(
            BuildProgress(
                jobId = jobId,
                phase = BuildPhase.Queued,
                message = "Preparing GitHub build…",
            ),
        )
        val runner = scope.launch {
            runCloudBuild(jobId, request, progress)
        }
        jobs[jobId] = BuildJob(progress = progress, runner = runner)
        return jobId
    }

    override suspend fun cancelBuild(jobId: String) {
        val job = jobs.remove(jobId)
            ?: throw AppException("Build not found")
        job.runner.cancel()
        job.repo?.let { repo ->
            job.runId?.let { runId -> api.cancelWorkflowRun(repo, runId) }
        }
        job.progress.update {
            it.copy(
                phase = BuildPhase.Cancelled,
                message = "No APK was produced. You can start a new build when you're ready.",
            )
        }
    }

    private suspend fun runCloudBuild(
        jobId: String,
        request: BuildRequest,
        progress: MutableStateFlow<BuildProgress>,
    ) {
        var release: GitHubApi.ReleaseRef? = null
        var repo: GitHubApi.RepoRef? = null
        try {
            progress.update {
                it.copy(phase = BuildPhase.Queued, message = "Ensuring build repo…")
            }
            val login = withContext(Dispatchers.IO) { api.getAuthenticatedLogin() }
            val buildRepo = withContext(Dispatchers.IO) { api.ensureBuildRepo(login) }
            repo = buildRepo
            jobs[jobId]?.repo = buildRepo

            withContext(Dispatchers.IO) { api.ensureWorkflowFile(buildRepo) }
            // Give GitHub a moment if the workflow was just created.
            delay(2_000)

            progress.update {
                it.copy(phase = BuildPhase.Uploading, message = "Zipping project sources…")
            }
            val zipFile = File(appContext.cacheDir, "buildapk/project-$jobId.zip")
            withContext(Dispatchers.IO) {
                ProjectZipper.zipProject(File(request.projectRoot.absolutePath), zipFile)
            }

            val tag = "asl-build-$jobId"
            progress.update {
                it.copy(phase = BuildPhase.Uploading, message = "Uploading project.zip to GitHub…")
            }
            release = withContext(Dispatchers.IO) {
                val rel = api.createRelease(buildRepo, tag)
                api.uploadReleaseAsset(rel, zipFile, "project.zip")
                rel
            }
            zipFile.delete()

            progress.update {
                it.copy(phase = BuildPhase.Building, message = "Starting GitHub Actions build…")
            }
            val dispatchAt = System.currentTimeMillis()
            withContext(Dispatchers.IO) {
                var lastError: Exception? = null
                repeat(8) { attempt ->
                    try {
                        api.dispatchWorkflow(buildRepo, tag)
                        return@withContext
                    } catch (e: Exception) {
                        lastError = e
                        // Workflow file may not be registered yet right after create.
                        delay(3_000L * (attempt + 1))
                    }
                }
                throw lastError ?: AppException("Failed to start workflow_dispatch")
            }

            var runId: Long? = null
            repeat(30) {
                if (!coroutineContext.isActive) return
                runId = withContext(Dispatchers.IO) {
                    api.findLatestWorkflowRunId(buildRepo, dispatchAt)
                }
                if (runId != null) return@repeat
                delay(2_000)
            }
            val resolvedRunId = runId
                ?: throw AppException("Could not find the Actions run after dispatch.")
            jobs[jobId]?.runId = resolvedRunId

            progress.update {
                it.copy(phase = BuildPhase.Building, message = "Building APK on GitHub Actions…")
            }
            while (coroutineContext.isActive) {
                val status = withContext(Dispatchers.IO) {
                    api.getWorkflowRun(buildRepo, resolvedRunId)
                }
                when (status.status) {
                    "completed" -> {
                        if (status.conclusion != "success") {
                            val link = status.htmlUrl?.let { " See $it" }.orEmpty()
                            throw AppException("GitHub Actions build failed (${status.conclusion}).$link")
                        }
                        break
                    }
                    else -> delay(5_000)
                }
            }
            if (!coroutineContext.isActive) return

            progress.update {
                it.copy(phase = BuildPhase.Downloading, message = "Downloading APK…")
            }
            val apkFile = File(appContext.cacheDir, "buildapk/asl-$jobId.apk")
            withContext(Dispatchers.IO) {
                val assetUrl = api.findReleaseApkAssetUrl(buildRepo, tag)
                api.downloadAssetToFile(assetUrl, apkFile)
            }

            progress.update {
                it.copy(
                    phase = BuildPhase.ReadyToInstall,
                    message = "APK ready to install",
                    apkLocalPath = apkFile.absolutePath,
                )
            }
        } catch (e: AppException) {
            progress.update {
                it.copy(phase = BuildPhase.Failed, message = null, error = e.uiMessage)
            }
        } catch (e: Exception) {
            progress.update {
                it.copy(
                    phase = BuildPhase.Failed,
                    message = null,
                    error = e.message?.takeIf { it.isNotBlank() } ?: "Cloud build failed",
                )
            }
        } finally {
            val rel = release
            val r = repo
            if (rel != null && r != null) {
                withContext(Dispatchers.IO) {
                    api.deleteRelease(r, rel.id, rel.tag)
                }
            }
        }
    }

    private data class BuildJob(
        val progress: MutableStateFlow<BuildProgress>,
        val runner: Job,
        var repo: GitHubApi.RepoRef? = null,
        var runId: Long? = null,
    )
}
