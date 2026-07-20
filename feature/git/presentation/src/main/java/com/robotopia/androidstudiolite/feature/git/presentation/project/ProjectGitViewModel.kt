package com.robotopia.androidstudiolite.feature.git.presentation.project

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.git.api.GitBranch
import kotlinx.coroutines.flow.MutableStateFlow

data class ProjectGitUiState(
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val currentBranch: String = "",
    val recentBranches: List<GitBranch> = emptyList(),
    val localBranches: List<GitBranch> = emptyList(),
    val remoteBranches: List<GitBranch> = emptyList(),
    val isBusy: Boolean = false,
    val actionError: String? = null,
    val toastMessage: String? = null,
    val menuBranch: GitBranch? = null,
    val renameBranch: String? = null,
    val renameValue: String = "",
    val renameError: String? = null,
    val mergeConfirmBranch: String? = null,
    val showCreateBranch: Boolean = false,
    val createBranchValue: String = "",
    val createBranchError: String? = null,
    val deleteConfirmBranch: String? = null,
)

class ProjectGitViewModel : ViewModel() {
    val uiState = MutableStateFlow(ProjectGitUiState())
}
