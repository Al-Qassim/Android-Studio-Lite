package com.robotopia.androidstudiolite.feature.auth.presentation.connect

import androidx.lifecycle.ViewModel
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface ConnectUiState {
    data class ShowCode(
        val userCode: String,
        val verificationUri: String,
        val providerName: String,
    ) : ConnectUiState

    data class Connected(
        val account: AuthAccount,
    ) : ConnectUiState

    data class Failed(
        val message: String,
    ) : ConnectUiState

    data object Loading : ConnectUiState
}

/** Holds Connect UI state across configuration changes. No service or navigation logic. */
class ConnectAccountViewModel : ViewModel() {
    val uiState = MutableStateFlow<ConnectUiState>(ConnectUiState.Loading)
    val connectAttempt = MutableStateFlow(0)

    fun retryConnect() {
        connectAttempt.value = connectAttempt.value + 1
    }
}
