package com.robotopia.androidstudiolite.feature.buildapk.presentation.history

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class BuildHistoryDetailViewModel : ViewModel() {
    val uiState = MutableStateFlow(BuildHistoryDetailUiState())
}
