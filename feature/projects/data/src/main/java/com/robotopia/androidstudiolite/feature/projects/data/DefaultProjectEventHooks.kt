package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventHooks
import com.robotopia.androidstudiolite.feature.projects.api.ProjectEventsListener
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import java.util.concurrent.CopyOnWriteArrayList

class DefaultProjectEventHooks : ProjectEventHooks {
    private val listeners = CopyOnWriteArrayList<ProjectEventsListener>()

    override fun addListener(listener: ProjectEventsListener) {
        listeners.addIfAbsent(listener)
    }

    override fun removeListener(listener: ProjectEventsListener) {
        listeners.remove(listener)
    }

    suspend fun notifyProjectDeleted(id: ProjectId) {
        listeners.forEach { listener ->
            runCatching { listener.onProjectDeleted(id) }
        }
    }
}
