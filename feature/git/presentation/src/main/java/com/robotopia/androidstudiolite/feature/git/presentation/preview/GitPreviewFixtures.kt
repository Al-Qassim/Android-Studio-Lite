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
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeFile
import com.robotopia.androidstudiolite.feature.git.presentation.project.GitChangeKind
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreen
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
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
            authSession = PreviewAuthSession,
            projectRoot = File("/tmp/preview"),
            onBack = {},
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

private object PreviewGitService : GitService {
    override suspend fun init(projectRoot: File) = Unit
    override suspend fun status(projectRoot: File) = GitStatusSnapshot(
        branch = "main",
        isClean = true,
        added = emptySet(),
        changed = emptySet(),
        modified = emptySet(),
        untracked = emptySet(),
        conflicting = emptySet(),
        missing = emptySet(),
    )
    override suspend fun stageAll(projectRoot: File) = Unit
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
    override suspend fun merge(projectRoot: File, branchName: String) = Unit
    override suspend fun renameBranch(
        projectRoot: File,
        oldName: String,
        newName: String,
        credentials: GitCredentials?,
    ) = Unit
    override fun validateCloneUrl(input: String) = CloneUrlValidation(errorMessage = "preview")
}
