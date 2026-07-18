package com.robotopia.androidstudiolite.feature.buildapk.api

/**
 * Runtime registry for build-history lifecycle listeners.
 * [BuildService] registers cancel-on-delete here; do not wire via Koin multi-bind.
 */
interface BuildHistoryEventHooks {
    fun addListener(listener: BuildHistoryEventsListener)
    fun removeListener(listener: BuildHistoryEventsListener)
}

fun interface BuildHistoryEventsListener {
    /** Invoked before a history row is removed. Dispatcher isolates exceptions. */
    suspend fun onHistoryJobDeleting(jobId: String)
}
