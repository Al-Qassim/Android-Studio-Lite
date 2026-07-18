package com.robotopia.androidstudiolite.feature.buildapk.presentation.history.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstallOutcome
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.presentation.history.BuildHistoryDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

private const val INSTALL_TAG = "BuildHistoryInstall"
private const val GENERIC_INSTALL_ERROR = "Couldn't start the install. Try again."
private const val MISSING_APK_ERROR = "APK is no longer available on this device."
private const val UNKNOWN_SOURCES_HINT =
    "Allow installs from this app, then tap Install again."

internal fun requestHistoryDetailInstall(
    apkInstaller: ApkInstaller,
    uiState: MutableStateFlow<BuildHistoryDetailUiState>,
) {
    val state = uiState.value
    if (state.isLoading || state.loadError != null || state.isInstalling) return

    val apkLocalPath = state.apkLocalPath
    if (apkLocalPath.isNullOrBlank()) {
        uiState.update {
            it.copy(isInstalling = false, installError = MISSING_APK_ERROR)
        }
        return
    }

    uiState.update { it.copy(isInstalling = true, installError = null) }
    try {
        when (apkInstaller.requestInstall(apkLocalPath)) {
            ApkInstallOutcome.InstallerOpened ->
                uiState.update { it.copy(isInstalling = false) }
            ApkInstallOutcome.UnknownSourcesSettingsOpened ->
                uiState.update {
                    it.copy(isInstalling = false, installError = UNKNOWN_SOURCES_HINT)
                }
        }
    } catch (error: CancellationException) {
        uiState.update { it.copy(isInstalling = false) }
        throw error
    } catch (error: Exception) {
        uiState.update {
            it.copy(
                isInstalling = false,
                installError = error.userMessageOrNull(INSTALL_TAG) ?: GENERIC_INSTALL_ERROR,
            )
        }
    }
}

internal fun openHistoryLogUrl(context: Context, logUrl: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(logUrl)))
}
