package com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.logic

import com.robotopia.androidstudiolite.core.error.userMessageOrNull
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstallOutcome
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import com.robotopia.androidstudiolite.feature.buildapk.presentation.progress.BuildProgressViewModel

private const val TAG = "BuildInstall"
private const val GENERIC_INSTALL_ERROR = "Couldn't start the install. Try again."
private const val MISSING_APK_ERROR = "APK isn't ready yet. Wait for the download to finish."
private const val UNKNOWN_SOURCES_HINT =
    "Allow installs from this app, then tap Install again."

internal fun requestApkInstall(
    apkLocalPath: String?,
    apkInstaller: ApkInstaller,
    viewModel: BuildProgressViewModel,
) {
    if (apkLocalPath.isNullOrBlank()) {
        viewModel.failInstall(MISSING_APK_ERROR)
        return
    }
    if (viewModel.uiState.value.isInstalling) return

    viewModel.beginInstall()
    try {
        when (apkInstaller.requestInstall(apkLocalPath)) {
            ApkInstallOutcome.InstallerOpened -> viewModel.finishInstall()
            ApkInstallOutcome.UnknownSourcesSettingsOpened ->
                viewModel.failInstall(UNKNOWN_SOURCES_HINT)
        }
    } catch (error: Exception) {
        viewModel.failInstall(error.userMessageOrNull(TAG) ?: GENERIC_INSTALL_ERROR)
    }
}
