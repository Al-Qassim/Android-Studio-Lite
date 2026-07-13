package com.robotopia.androidstudiolite.feature.buildapk.data

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.robotopia.androidstudiolite.core.error.AppException
import java.io.File

/**
 * Publishes a built APK into the device's public Downloads directory via MediaStore.
 * @return content URI string suitable for [DefaultApkInstaller]
 */
internal object ApkDownloads {

    fun publish(
        context: Context,
        source: File,
        displayName: String,
    ): String {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, sanitizeFileName(displayName))
            put(MediaStore.MediaColumns.MIME_TYPE, APK_MIME_TYPE)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/$ASL_DOWNLOADS_SUBDIR",
            )
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values)
            ?: throw AppException("Couldn't save the APK to Downloads.")
        try {
            resolver.openOutputStream(uri)?.use { out ->
                source.inputStream().use { input -> input.copyTo(out) }
            } ?: throw AppException("Couldn't save the APK to Downloads.")
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } catch (error: Exception) {
            resolver.delete(uri, null, null)
            if (error is AppException) throw error
            throw AppException("Couldn't save the APK to Downloads.", error)
        }
        return uri.toString()
    }

    private fun sanitizeFileName(name: String): String {
        val trimmed = name.trim().ifEmpty { "app" }
        val safe = trimmed.replace(INVALID_FILE_CHARS, "_")
        return if (safe.endsWith(".apk", ignoreCase = true)) safe else "$safe.apk"
    }

    private const val APK_MIME_TYPE = "application/vnd.android.package-archive"
    private const val ASL_DOWNLOADS_SUBDIR = "AndroidStudioLite"
    private val INVALID_FILE_CHARS = Regex("""[\\/:*?"<>|\u0000-\u001F]""")
}
