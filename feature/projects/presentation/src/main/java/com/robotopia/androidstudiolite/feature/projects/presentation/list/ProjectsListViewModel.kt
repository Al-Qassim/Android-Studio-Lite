package com.robotopia.androidstudiolite.feature.projects.presentation.list

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.projects.model.Project
import kotlinx.coroutines.flow.MutableStateFlow

data class ProjectsListUiState(
    val projects: List<Project> = emptyList(),
    val menuProject: Project? = null,
    val pendingDelete: Project? = null,
    val actionError: String? = null,
)

/** Holds list UI state across configuration changes. No business/UI logic. */
class ProjectsListViewModel : ViewModel() {
    val uiState = MutableStateFlow(ProjectsListUiState())
}
