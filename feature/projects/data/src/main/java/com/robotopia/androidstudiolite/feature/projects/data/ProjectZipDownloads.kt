package com.robotopia.androidstudiolite.feature.projects.data

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.robotopia.androidstudiolite.core.error.AppException
import java.io.File

/**
 * Publishes a project zip into the device's public Downloads directory via MediaStore.
 * Requires API 29+ ([MediaStore.Downloads]); older API levels throw [AppException].
 */
internal object ProjectZipDownloads {

    fun publish(
        context: Context,
        source: File,
        displayName: String,
    ): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            throw AppException("Couldn't save the zip to Downloads.")
        }
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, sanitizeFileName(displayName))
            put(MediaStore.MediaColumns.MIME_TYPE, ZIP_MIME_TYPE)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOWNLOADS}/$ASL_DOWNLOADS_SUBDIR",
            )
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, values)
            ?: throw AppException("Couldn't save the zip to Downloads.")
        try {
            resolver.openOutputStream(uri)?.use { out ->
                source.inputStream().use { input -> input.copyTo(out) }
            } ?: throw AppException("Couldn't save the zip to Downloads.")
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } catch (error: Exception) {
            resolver.delete(uri, null, null)
            if (error is AppException) throw error
            throw AppException("Couldn't save the zip to Downloads.", error)
        }
        return uri.toString()
    }

    private fun sanitizeFileName(name: String): String {
        val trimmed = name.trim().ifEmpty { "project" }
        val safe = trimmed.replace(INVALID_FILE_CHARS, "_")
        return if (safe.endsWith(".zip", ignoreCase = true)) safe else "$safe.zip"
    }

    private const val ZIP_MIME_TYPE = "application/zip"
    private const val ASL_DOWNLOADS_SUBDIR = "AndroidStudioLite"
    private val INVALID_FILE_CHARS = Regex("""[\\/:*?"<>|\u0000-\u001F]""")
}
