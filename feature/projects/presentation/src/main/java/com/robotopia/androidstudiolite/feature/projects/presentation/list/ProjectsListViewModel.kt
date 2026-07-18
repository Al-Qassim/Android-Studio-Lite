package com.robotopia.androidstudiolite.feature.projects.presentation.list

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.projects.model.Project
import com.robotopia.androidstudiolite.feature.projects.model.ProjectExportResult
import kotlinx.coroutines.flow.MutableStateFlow

data class ProjectsListUiState(
    val isLoading: Boolean = true,
    val projects: List<Project> = emptyList(),
    val hubMenuOpen: Boolean = false,
    val menuProject: Project? = null,
    val pendingDelete: Project? = null,
    val isBusy: Boolean = false,
    val actionError: String? = null,
    val toastMessage: String? = null,
    /** Set by export logic; Screen consumes once to launch the sharesheet. */
    val pendingShare: ProjectExportResult? = null,
)

/** Holds list UI state across configuration changes. No business/UI logic. */
class ProjectsListViewModel : ViewModel() {
    val uiState = MutableStateFlow(ProjectsListUiState())
}
