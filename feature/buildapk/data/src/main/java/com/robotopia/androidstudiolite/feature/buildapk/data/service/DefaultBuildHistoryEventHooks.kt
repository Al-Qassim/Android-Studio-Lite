package com.robotopia.androidstudiolite.feature.buildapk.data.service

import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventHooks
import com.robotopia.androidstudiolite.feature.buildapk.api.BuildHistoryEventsListener
import java.util.concurrent.CopyOnWriteArrayList

class DefaultBuildHistoryEventHooks : BuildHistoryEventHooks {
    private val listeners = CopyOnWriteArrayList<BuildHistoryEventsListener>()

    override fun addListener(listener: BuildHistoryEventsListener) {
        listeners.addIfAbsent(listener)
    }

    override fun removeListener(listener: BuildHistoryEventsListener) {
        listeners.remove(listener)
    }

    suspend fun notifyHistoryJobDeleting(jobId: String) {
        listeners.forEach { listener ->
            runCatching { listener.onHistoryJobDeleting(jobId) }
        }
    }
}
