package com.robotopia.androidstudiolite

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.buildapk.api.ApkInstaller
import java.io.File

class DefaultApkInstaller(
    private val context: Context,
) : ApkInstaller {

    override fun requestInstall(apkLocalPath: String) {
        val apkFile = File(apkLocalPath)
        if (!apkFile.exists()) {
            throw AppException("APK file not found")
        }

        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile,
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, APK_MIME_TYPE)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    private companion object {
        const val APK_MIME_TYPE = "application/vnd.android.package-archive"
    }
}
