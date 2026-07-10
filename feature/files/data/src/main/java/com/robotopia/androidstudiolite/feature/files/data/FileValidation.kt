package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors

internal object FileValidation {
    private val invalidChars = charArrayOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')

    fun fieldErrors(name: String): FileNameFieldErrors {
        val trimmed = name.trim()
        val nameError = when {
            trimmed.isEmpty() -> "Name is required"
            trimmed == "." || trimmed == ".." -> "Name cannot be . or .."
            trimmed.any { it in invalidChars } -> "Name contains invalid characters"
            trimmed.length > 255 -> "Name is too long"
            else -> null
        }
        return FileNameFieldErrors(name = nameError)
    }

    fun validate(name: String) {
        fieldErrors(name).name?.let { throw AppException(it) }
    }
}
