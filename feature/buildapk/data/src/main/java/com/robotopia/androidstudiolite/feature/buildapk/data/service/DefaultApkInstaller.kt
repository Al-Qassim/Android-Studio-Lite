package com.robotopia.androidstudiolite.feature.buildapk.data.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstallOutcome
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import java.io.File

class DefaultApkInstaller(
    private val context: Context,
) : ApkInstaller {

    override fun requestInstall(apkLocalPath: String): ApkInstallOutcome {
        val uri = resolveApkUri(apkLocalPath)
            ?: throw AppException("Couldn't find the APK. Try building again.")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
            !context.packageManager.canRequestPackageInstalls()
        ) {
            context.startActivity(
                Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
            )
            return ApkInstallOutcome.UnknownSourcesSettingsOpened
        }

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, APK_MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (error: Exception) {
            throw AppException("Couldn't open the package installer.", error)
        }
        return ApkInstallOutcome.InstallerOpened
    }

    private fun resolveApkUri(apkLocalPath: String): Uri? {
        if (apkLocalPath.startsWith("content:", ignoreCase = true)) {
            val uri = Uri.parse(apkLocalPath)
            return runCatching {
                context.contentResolver.openInputStream(uri)?.use { uri }
            }.getOrNull()
        }
        val apkFile = File(apkLocalPath)
        if (!apkFile.exists()) return null
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile,
        )
    }

    private companion object {
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
    }
}
