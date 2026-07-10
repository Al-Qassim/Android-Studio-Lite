package com.robotopia.androidstudiolite.core.error

import android.util.Log

/**
 * Returns [AppException.uiMessage] for planned failures.
 * Unexpected errors are logged and return `null` — never expose [Throwable.message].
 */
fun Throwable.userMessageOrNull(tag: String): String? =
    when (this) {
        is AppException -> uiMessage
        else -> {
            Log.e(tag, "Unexpected error", this)
            null
        }
    }
