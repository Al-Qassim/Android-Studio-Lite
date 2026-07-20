package com.robotopia.androidstudiolite.feature.git.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.git.api.CloneUrlValidation
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import com.robotopia.androidstudiolite.feature.git.api.GitBranchKind
import com.robotopia.androidstudiolite.feature.git.api.GitBranchesSnapshot
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials
import com.robotopia.androidstudiolite.feature.git.api.GitService
import com.robotopia.androidstudiolite.feature.git.api.GitStatusSnapshot
import com.robotopia.androidstudiolite.feature.git.presentation.clone.CloneProjectUiState
import com.robotopia.androidstudiolite.feature.git.presentation.clone.ui.CloneProjectBody
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreen
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitScreenContext
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitUiState
import java.io.File
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun CloneIdlePreview() {
    CloneProjectBody(
        state = CloneProjectUiState(url = "owner/repo"),
        onCancel = {},
        onUrlChange = {},
        onCloneClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun CloneErrorPreview() {
    CloneProjectBody(
        state = CloneProjectUiState(
            url = "bad",
            urlError = "Use https://github.com/owner/repo or owner/repo.",
        ),
        onCancel = {},
        onUrlChange = {},
        onCloneClick = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun ProjectGitBranchesPreview() {
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
    val main = GitBranch("main", GitBranchKind.Local, isCurrent = true)
    val feature = GitBranch("feature/login", GitBranchKind.Local)
    val fix = GitBranch("fix/crash", GitBranchKind.Local)
    context.ProjectGitScreen(
        state = ProjectGitUiState(
            isLoading = false,
            currentBranch = "main",
            recentBranches = listOf(main, feature),
            localBranches = listOf(main, feature, fix),
            remoteBranches = listOf(
                GitBranch("origin/main", GitBranchKind.Remote),
                GitBranch("origin/develop", GitBranchKind.Remote),
            ),
        ),
        projectName = "MyApp",
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun ProjectGitLoadErrorPreview() {
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
        state = ProjectGitUiState(
            isLoading = false,
            loadError = "Not a git repository.",
        ),
        projectName = "MyApp",
    )
}

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
    override suspend fun createBranch(projectRoot: File, name: String) = Unit
    override suspend fun deleteBranch(projectRoot: File, name: String) = Unit
    override suspend fun checkout(projectRoot: File, name: String) = Unit
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
    override suspend fun renameBranch(projectRoot: File, oldName: String, newName: String) = Unit
    override fun validateCloneUrl(input: String) = CloneUrlValidation(errorMessage = "preview")
}
