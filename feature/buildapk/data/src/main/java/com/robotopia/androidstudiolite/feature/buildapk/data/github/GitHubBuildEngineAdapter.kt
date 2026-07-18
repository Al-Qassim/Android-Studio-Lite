package com.robotopia.androidstudiolite.feature.buildapk.data.github

import android.content.Context
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildEngine
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildEngineSession
import com.robotopia.androidstudiolite.feature.buildapk.data.job.BuildEngineUpdate
import com.robotopia.androidstudiolite.feature.buildapk.data.local.ApkDownloads
import com.robotopia.androidstudiolite.feature.buildapk.data.local.ProjectZipper
import com.robotopia.androidstudiolite.feature.buildapk.model.BuildPhase
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubWorkflowRun
import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * GitHub Actions build backend. Reads credentials from [AuthSession] — tokens never
 * cross the [BuildEngine] port.
 */
class GitHubBuildEngineAdapter(
    context: Context,
    private val gitHubClient: GitHubClient,
    private val authSession: AuthSession,
) : BuildEngine {
    private val appContext = context.applicationContext

    override val providerId: String = "github"

    override val providerDisplayName: String
        get() = authSession.providerDisplayName

    override fun observeResumeHints(): Flow<Unit> =
        authSession.observeAccount()
            .map { it != null }
            .distinctUntilChanged()
            .filter { signedIn -> signedIn }
            .map { }

    override fun canResume(resumeCursor: String?): Boolean {
        val payload = GitHubResumePayload.decode(resumeCursor) ?: return false
        return payload.runId != null && payload.releaseTag != null
    }

    override suspend fun execute(
        session: BuildEngineSession,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
    ) {
        val token = requireToken()
        var cursor: GitHubResumePayload? = null
        try {
            val repo = prepareSandbox(token, onUpdate) { cursor = it }
            val releaseTag = uploadProject(session, token, repo, onUpdate) { cursor = it }
            awaitRemoteBuild(token, repo, releaseTag, cursor, onUpdate) { cursor = it }
            if (!coroutineContext.isActive) return
            downloadReadyApk(session, token, repo, releaseTag, onUpdate)
        } finally {
            cleanupRelease(token, cursor)
        }
    }

    override suspend fun resume(
        session: BuildEngineSession,
        resumeCursor: String,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
    ) {
        val token = requireToken()
        var cursor = GitHubResumePayload.decode(resumeCursor)
            ?: throw AppException("Build interrupted. Start a new build to try again.")
        val runId = cursor.runId
            ?: throw AppException("Build interrupted. Start a new build to try again.")
        val releaseTag = cursor.releaseTag
            ?: throw AppException("Build interrupted. Start a new build to try again.")
        val repo = cursor.toRepoRef()
        try {
            onUpdate(
                BuildEngineUpdate(
                    phase = BuildPhase.Building,
                    message = "Resuming remote build…",
                    resumeCursor = cursor.encode(),
                ),
            )
            pollUntilBuildSucceeds(
                token = token,
                repo = repo,
                runId = runId,
                initialLogUrl = null,
                resumeCursor = { cursor.encode() },
                onUpdate = onUpdate,
            )
            if (!coroutineContext.isActive) return
            downloadReadyApk(session, token, repo, releaseTag, onUpdate)
        } finally {
            cleanupRelease(token, cursor)
        }
    }

    override suspend fun cancel(resumeCursor: String?) {
        val token = authSession.accessToken() ?: return
        val payload = GitHubResumePayload.decode(resumeCursor) ?: return
        val repo = payload.toRepoRef()
        payload.runId?.let { runId ->
            runCatching { gitHubClient.cancelWorkflowRun(token, repo, runId) }
        }
        payload.toReleaseRefOrNull()?.let { release ->
            runCatching {
                gitHubClient.deleteRelease(token, repo, release.id, release.tag)
            }
        }
    }

    private suspend fun requireToken(): String =
        authSession.accessToken()
            ?: throw AppException("Connect your build account before starting a build.")

    private suspend fun prepareSandbox(
        token: String,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
        updateCursor: (GitHubResumePayload) -> Unit,
    ): GitHubRepoRef {
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Preparing,
                message = "Ensuring build sandbox…",
            ),
        )
        val repo = gitHubClient.ensureSandboxRepo(token)
        gitHubClient.ensureWorkflowFile(token, repo)
        val cursor = GitHubResumePayload(owner = repo.owner, repo = repo.name)
        updateCursor(cursor)
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Preparing,
                message = "Ensuring build sandbox…",
                resumeCursor = cursor.encode(),
            ),
        )
        delay(WORKFLOW_REGISTER_DELAY_MS)
        return repo
    }

    private suspend fun uploadProject(
        session: BuildEngineSession,
        token: String,
        repo: GitHubRepoRef,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
        updateCursor: (GitHubResumePayload) -> Unit,
    ): String {
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Uploading,
                message = "Zipping project sources…",
            ),
        )
        val tag = "asl-build-${session.jobId}"
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Uploading,
                message = "Uploading project sources…",
            ),
        )
        val zipFile = File(appContext.cacheDir, "buildapk/project-${session.jobId}.zip")
        ProjectZipper.zipProject(File(session.projectRootPath), zipFile)
        val release = gitHubClient.createRelease(token, repo, tag)
        gitHubClient.uploadReleaseAsset(token, release, zipFile, "project.zip")
        zipFile.delete()
        val cursor = GitHubResumePayload(
            owner = repo.owner,
            repo = repo.name,
            releaseId = release.id,
            releaseTag = release.tag,
            uploadUrlTemplate = release.uploadUrlTemplate,
        )
        updateCursor(cursor)
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Uploading,
                message = "Uploading project sources…",
                resumeCursor = cursor.encode(),
            ),
        )
        return tag
    }

    private suspend fun awaitRemoteBuild(
        token: String,
        repo: GitHubRepoRef,
        releaseTag: String,
        previous: GitHubResumePayload?,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
        updateCursor: (GitHubResumePayload) -> Unit,
    ) {
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Queued,
                message = "Starting remote build…",
            ),
        )
        val dispatchAt = dispatchWorkflowWithRetry(token, repo, releaseTag)
        val run = findWorkflowRun(token, repo, dispatchAt)
        val cursor = GitHubResumePayload(
            owner = repo.owner,
            repo = repo.name,
            runId = run.id,
            releaseId = previous?.releaseId,
            releaseTag = previous?.releaseTag ?: releaseTag,
            uploadUrlTemplate = previous?.uploadUrlTemplate,
        )
        updateCursor(cursor)
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Queued,
                message = "Starting remote build…",
                logUrl = run.htmlUrl,
                resumeCursor = cursor.encode(),
            ),
        )
        pollUntilBuildSucceeds(
            token = token,
            repo = repo,
            runId = run.id,
            initialLogUrl = run.htmlUrl,
            resumeCursor = { cursor.encode() },
            onUpdate = onUpdate,
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
            if (!coroutineContext.isActive) throw CancellationException()
            if (run != null) return run
            delay(FIND_RUN_POLL_MS)
            run = gitHubClient.findLatestWorkflowRun(token, repo, notBeforeEpochMs)
        }
        throw AppException("Couldn't find the build run after starting it. Try again.")
    }

    private suspend fun pollUntilBuildSucceeds(
        token: String,
        repo: GitHubRepoRef,
        runId: Long,
        initialLogUrl: String?,
        resumeCursor: () -> String,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
    ) {
        var logUrl = initialLogUrl
        while (coroutineContext.isActive) {
            val status = gitHubClient.getWorkflowRun(token, repo, runId)
            logUrl = status.htmlUrl ?: logUrl
            val encoded = resumeCursor()
            when (status.status) {
                "completed" -> {
                    if (status.conclusion != "success") {
                        throw AppException("Build failed. Open the build log.")
                    }
                    return
                }
                "queued", "waiting", "pending", "requested" -> {
                    onUpdate(
                        BuildEngineUpdate(
                            phase = BuildPhase.Queued,
                            message = "Waiting in queue…",
                            logUrl = logUrl,
                            resumeCursor = encoded,
                        ),
                    )
                    delay(RUN_POLL_MS)
                }
                else -> {
                    onUpdate(
                        BuildEngineUpdate(
                            phase = BuildPhase.Building,
                            message = "Building APK remotely…",
                            logUrl = logUrl,
                            resumeCursor = encoded,
                        ),
                    )
                    delay(RUN_POLL_MS)
                }
            }
        }
        throw CancellationException()
    }

    private suspend fun downloadReadyApk(
        session: BuildEngineSession,
        token: String,
        repo: GitHubRepoRef,
        releaseTag: String,
        onUpdate: suspend (BuildEngineUpdate) -> Unit,
    ) {
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.Downloading,
                message = "Downloading APK…",
            ),
        )
        val tempApk = File(appContext.cacheDir, "buildapk/asl-${session.jobId}.apk")
        val assetUrl = gitHubClient.findReleaseApkAssetUrl(token, repo, releaseTag)
        gitHubClient.downloadAssetToFile(token, assetUrl, tempApk)
        val downloadsUri = ApkDownloads.publish(
            context = appContext,
            source = tempApk,
            displayName = session.projectName,
        )
        tempApk.delete()
        onUpdate(
            BuildEngineUpdate(
                phase = BuildPhase.ReadyToInstall,
                message = "APK saved to Downloads",
                apkLocalPath = downloadsUri,
            ),
        )
    }

    private suspend fun cleanupRelease(token: String, cursor: GitHubResumePayload?) {
        val payload = cursor ?: return
        val release = payload.toReleaseRefOrNull() ?: return
        runCatching {
            gitHubClient.deleteRelease(token, payload.toRepoRef(), release.id, release.tag)
        }
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
