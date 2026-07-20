package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.git.api.CloneUrlValidation
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.api.GitBranchesSnapshot
import com.robotopia.androidstudiolite.feature.git.api.GitCommitFileInfo
import com.robotopia.androidstudiolite.feature.git.api.GitCommitInfo
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials
import com.robotopia.androidstudiolite.feature.git.api.GitDiffLineInfo
import com.robotopia.androidstudiolite.feature.git.api.GitMergeResult
import com.robotopia.androidstudiolite.feature.git.api.GitRepositoryInfo
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreen
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceCode
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceTokenResult
import com.robotopia.androidstudiolite.feature.github.api.GitHubReleaseRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubUser
import com.robotopia.androidstudiolite.feature.github.api.GitHubWorkflowRun
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

internal const val GIT_PREVIEW_BG = 0xFF2B2D30

@Composable
internal fun PreviewProjectGit(state: ProjectGitUiState) {
    val scope = rememberCoroutineScope()
    val context = remember {
        ProjectGitScreenContext(
            updateState = {},
            gitService = PreviewGitService,
            gitHubClient = PreviewGitHubClient,
            authSession = PreviewAuthSession,
            projectRoot = File("/tmp/preview"),
            onBack = {},
            onConnectAccount = {},
            openUrl = {},
            scope = scope,
        )
    }
    context.ProjectGitScreen(
        state = state,
        projectName = "MyApp",
    )
}

internal fun sampleChangeFiles(): List<GitChangeFile> = listOf(
    GitChangeFile("app/src/main/java/MainActivity.kt", GitChangeKind.Modified),
    GitChangeFile("app/src/main/java/LoginScreen.kt", GitChangeKind.Added),
    GitChangeFile("README.md", GitChangeKind.Modified),
    GitChangeFile("scratch.tmp", GitChangeKind.Untracked, staged = false),
    GitChangeFile("old/LegacyScreen.kt", GitChangeKind.Deleted),
)

internal fun mergeConflictsState(): ProjectGitUiState = ProjectGitUiState(
    isLoading = false,
    tab = ProjectGitTab.Changes,
    currentBranch = "main",
    mergeSourceBranch = "feature/login",
    changeFiles = listOf(
        GitChangeFile("app/src/main/java/Nav.kt", GitChangeKind.Conflict, staged = false),
        GitChangeFile("app/src/main/java/Theme.kt", GitChangeKind.Conflict, staged = false),
        GitChangeFile("README.md", GitChangeKind.Modified, staged = true),
    ),
)

private object PreviewAuthSession : AuthSession {
    override fun observeAccount(): Flow<AuthAccount?> = flowOf(null)
    override suspend fun currentAccount(): AuthAccount? = null
    override suspend fun clearAccount() = Unit
    override val providerDisplayName: String = "GitHub"
    override suspend fun accessToken(): String? = null
}

private object PreviewGitHubClient : GitHubClient {
    override suspend fun requestDeviceCode(clientId: String) =
        GitHubDeviceCode("", "", "", 0, 0)
    override suspend fun pollDeviceToken(clientId: String, deviceCode: String) =
        GitHubDeviceTokenResult.Denied
    override suspend fun fetchAuthenticatedUser(accessToken: String) = GitHubUser("preview")
    override suspend fun ensureSandboxRepo(accessToken: String) =
        GitHubRepoRef("preview", "repo")
    override suspend fun createUserRepo(accessToken: String, name: String, private: Boolean) =
        GitHubRepoRef("preview", name)
    override suspend fun ensureWorkflowFile(accessToken: String, repo: GitHubRepoRef) = Unit
    override suspend fun createRelease(accessToken: String, repo: GitHubRepoRef, tag: String) =
        GitHubReleaseRef(0, tag, "")
    override suspend fun uploadReleaseAsset(
        accessToken: String,
        release: GitHubReleaseRef,
        file: File,
        assetName: String,
    ) = Unit
    override suspend fun dispatchWorkflow(accessToken: String, repo: GitHubRepoRef, releaseTag: String) = Unit
    override suspend fun findLatestWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        notBeforeEpochMs: Long,
    ): GitHubWorkflowRun? = null
    override suspend fun getWorkflowRun(accessToken: String, repo: GitHubRepoRef, runId: Long) =
        GitHubWorkflowRun(0, "completed", "success", null)
    override suspend fun cancelWorkflowRun(accessToken: String, repo: GitHubRepoRef, runId: Long) = Unit
    override suspend fun findReleaseApkAssetUrl(accessToken: String, repo: GitHubRepoRef, tag: String) = ""
    override suspend fun downloadAssetToFile(accessToken: String, assetApiUrl: String, destination: File) = Unit
    override suspend fun deleteRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseId: Long,
        tag: String,
    ) = Unit
}

private object PreviewGitService : GitService {
    override suspend fun isRepository(projectRoot: File) = true
    override suspend fun init(projectRoot: File) = Unit
    override suspend fun status(projectRoot: File) = GitStatusSnapshot(
        branch = "main",
        isClean = true,
        added = emptySet(),
        changed = emptySet(),
        removed = emptySet(),
        modified = emptySet(),
        untracked = emptySet(),
        conflicting = emptySet(),
        missing = emptySet(),
    )
    override suspend fun repositoryInfo(projectRoot: File) = GitRepositoryInfo(
        hasRemote = false,
        remoteHtmlUrl = null,
        aheadCount = 0,
        behindCount = 0,
        isMerging = false,
    )
    override suspend fun stageAll(projectRoot: File) = Unit
    override suspend fun stagePaths(projectRoot: File, paths: List<String>) = Unit
    override suspend fun unstagePaths(projectRoot: File, paths: List<String>) = Unit
    override suspend fun discardPaths(projectRoot: File, paths: List<String>) = Unit
    override suspend fun discardAll(projectRoot: File) = Unit
    override suspend fun undoLastCommit(projectRoot: File) = Unit
    override suspend fun commit(
        projectRoot: File,
        message: String,
        authorName: String,
        authorEmail: String,
    ) = "abc"
    override suspend fun currentBranch(projectRoot: File) = "main"
    override suspend fun createBranch(projectRoot: File, name: String, startPoint: String?) = Unit
    override suspend fun deleteBranch(projectRoot: File, name: String) = Unit
    override suspend fun checkout(projectRoot: File, name: String, force: Boolean) = Unit
    override suspend fun listBranches(projectRoot: File) = GitBranchesSnapshot(
        currentBranch = "main",
        recent = listOf(GitBranch("main", GitBranchKind.Local, isCurrent = true)),
        local = listOf(GitBranch("main", GitBranchKind.Local, isCurrent = true)),
        remote = emptyList(),
    )
    override suspend fun clone(httpsUrl: String, destDir: File, credentials: GitCredentials?) = Unit
    override suspend fun fetch(projectRoot: File, credentials: GitCredentials?) = Unit
    override suspend fun pull(projectRoot: File, credentials: GitCredentials?) = Unit
    override suspend fun push(projectRoot: File, credentials: GitCredentials?) = Unit
    override suspend fun pushSetUpstream(
        projectRoot: File,
        remote: String,
        branch: String,
        credentials: GitCredentials?,
    ) = Unit
    override suspend fun addRemote(projectRoot: File, name: String, httpsUrl: String) = Unit
    override suspend fun merge(projectRoot: File, branchName: String) = GitMergeResult(false)
    override suspend fun abortMerge(projectRoot: File) = Unit
    override suspend fun renameBranch(
        projectRoot: File,
        oldName: String,
        newName: String,
        credentials: GitCredentials?,
    ) = Unit
    override suspend fun readWorkingFile(projectRoot: File, relativePath: String) = ""
    override suspend fun writeWorkingFile(projectRoot: File, relativePath: String, content: String) = Unit
    override suspend fun appendGitignore(projectRoot: File, pattern: String) = Unit
    override suspend fun diffWorkingTree(projectRoot: File, relativePath: String): List<GitDiffLineInfo> =
        emptyList()
    override suspend fun diffCommit(
        projectRoot: File,
        commitId: String,
        relativePath: String,
    ): List<GitDiffLineInfo> = emptyList()
    override suspend fun log(projectRoot: File, maxCount: Int): List<GitCommitInfo> = emptyList()
    override suspend fun commitFiles(projectRoot: File, commitId: String): List<GitCommitFileInfo> =
        emptyList()
    override fun validateCloneUrl(input: String) = CloneUrlValidation(errorMessage = "preview")
}
