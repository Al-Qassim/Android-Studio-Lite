package com.robotopia.androidstudiolite.feature.projects.presentation.create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class CreateProjectUiState(
    val name: String = "",
    val packageName: String = "com.example.",
    val minSdk: String = "26",
    val nameError: String? = null,
    val packageError: String? = null,
    val minSdkError: String? = null,
    val formError: String? = null,
    val isCreating: Boolean = false,
)

/** Holds create-form UI state across configuration changes. No business/UI logic. */
class CreateProjectViewModel : ViewModel() {
    val uiState = MutableStateFlow(CreateProjectUiState())
}
