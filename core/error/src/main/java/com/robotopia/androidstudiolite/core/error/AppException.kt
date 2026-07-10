package com.robotopia.androidstudiolite.core.error

/**
 * Planned, user-facing failure.
 *
 * [uiMessage] is safe to show in the UI. Unexpected failures must not use this type —
 * log them and show a fixed generic message instead of [Throwable.message].
 */
class AppException(
    val uiMessage: String,
    cause: Throwable? = null,
) : Exception(uiMessage, cause)
