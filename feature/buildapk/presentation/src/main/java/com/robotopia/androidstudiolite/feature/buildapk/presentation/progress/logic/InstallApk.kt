package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstallOutcome
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "BuildInstall"
private const val GENERIC_INSTALL_ERROR = "Couldn't start the install. Try again."
private const val MISSING_APK_ERROR = "APK isn't ready yet. Wait for the download to finish."
private const val UNKNOWN_SOURCES_HINT =
    "Allow installs from this app, then tap Install again."

internal fun requestApkInstall(
    apkLocalPath: String?,
    apkInstaller: ApkInstaller,
    uiState: MutableStateFlow<BuildProgressUiState>,
) {
    if (apkLocalPath.isNullOrBlank()) {
        uiState.update { it.copy(isInstalling = false, installError = MISSING_APK_ERROR) }
        return
    }
    if (uiState.value.isInstalling) return

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
                installError = error.userMessageOrNull(TAG) ?: GENERIC_INSTALL_ERROR,
            )
        }
    }
}
