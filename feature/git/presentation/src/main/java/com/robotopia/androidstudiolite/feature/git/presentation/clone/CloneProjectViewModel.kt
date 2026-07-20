package com.robotopia.androidstudiolite.feature.git.presentation.clone

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class CloneProjectUiState(
    val url: String = "",
    val urlError: String? = null,
    val formError: String? = null,
    val isCloning: Boolean = false,
)

class CloneProjectViewModel : ViewModel() {
    val uiState = MutableStateFlow(CloneProjectUiState())
}
