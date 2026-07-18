package com.robotopia.androidstudiolite.feature.buildapk.data.github

import android.content.Context
import com.robotopia.androidstudiolite.feature.buildapk.data.job.CloudBuildGateway
import com.robotopia.androidstudiolite.feature.buildapk.data.job.RemoteRelease
import com.robotopia.androidstudiolite.feature.buildapk.data.job.RemoteRepo
import com.robotopia.androidstudiolite.feature.buildapk.data.job.RemoteRun
import com.robotopia.androidstudiolite.feature.buildapk.data.local.ApkDownloads
import com.robotopia.androidstudiolite.feature.buildapk.data.local.ProjectZipper
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubReleaseRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubWorkflowRun
import java.io.File

/** Adapts [GitHubClient] (+ local zip/Downloads) to [CloudBuildGateway]. */
internal class GitHubCloudBuildGatewayAdapter(
    context: Context,
    private val gitHubClient: GitHubClient,
) : CloudBuildGateway {
    private val appContext = context.applicationContext

    override suspend fun prepareSandbox(token: String): RemoteRepo {
        val repo = gitHubClient.ensureSandboxRepo(token)
        gitHubClient.ensureWorkflowFile(token, repo)
        return repo.toRemote()
    }

    override suspend fun uploadProjectZip(
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
        projectRootPath: String,
        jobId: String,
    ): RemoteRelease {
        val zipFile = File(appContext.cacheDir, "buildapk/project-$jobId.zip")
        ProjectZipper.zipProject(File(projectRootPath), zipFile)
        val githubRepo = repo.toGitHub()
        val release = gitHubClient.createRelease(token, githubRepo, releaseTag)
        gitHubClient.uploadReleaseAsset(token, release, zipFile, "project.zip")
        zipFile.delete()
        return release.toRemote()
    }

    override suspend fun dispatchBuild(token: String, repo: RemoteRepo, releaseTag: String) {
        gitHubClient.dispatchWorkflow(token, repo.toGitHub(), releaseTag)
    }

    override suspend fun findLatestRun(
        token: String,
        repo: RemoteRepo,
        notBeforeEpochMs: Long,
    ): RemoteRun? =
        gitHubClient.findLatestWorkflowRun(token, repo.toGitHub(), notBeforeEpochMs)?.toRemote()

    override suspend fun getRun(token: String, repo: RemoteRepo, runId: Long): RemoteRun =
        gitHubClient.getWorkflowRun(token, repo.toGitHub(), runId).toRemote()

    override suspend fun cancelRun(token: String, repo: RemoteRepo, runId: Long) {
        gitHubClient.cancelWorkflowRun(token, repo.toGitHub(), runId)
    }

    override suspend fun downloadAndPublishApk(
        token: String,
        repo: RemoteRepo,
        releaseTag: String,
        projectName: String,
        jobId: String,
    ): String {
        val tempApk = File(appContext.cacheDir, "buildapk/asl-$jobId.apk")
        val assetUrl = gitHubClient.findReleaseApkAssetUrl(token, repo.toGitHub(), releaseTag)
        gitHubClient.downloadAssetToFile(token, assetUrl, tempApk)
        val downloadsUri = ApkDownloads.publish(
            context = appContext,
            source = tempApk,
            displayName = projectName,
        )
        tempApk.delete()
        return downloadsUri
    }

    override suspend fun deleteRelease(token: String, repo: RemoteRepo, release: RemoteRelease) {
        gitHubClient.deleteRelease(token, repo.toGitHub(), release.id, release.tag)
    }
}

private fun GitHubRepoRef.toRemote(): RemoteRepo =
    RemoteRepo(owner = owner, name = name)

private fun RemoteRepo.toGitHub(): GitHubRepoRef =
    GitHubRepoRef(owner = owner, name = name)

private fun GitHubReleaseRef.toRemote(): RemoteRelease =
    RemoteRelease(id = id, tag = tag, uploadUrlTemplate = uploadUrlTemplate)

private fun GitHubWorkflowRun.toRemote(): RemoteRun =
    RemoteRun(
        id = id,
        status = status,
        conclusion = conclusion,
        htmlUrl = htmlUrl,
    )
