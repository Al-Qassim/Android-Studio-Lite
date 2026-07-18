package com.robotopia.androidstudiolite.feature.projects.api

/**
 * Runtime registry for project lifecycle listeners.
 *
 * Consumers inject this and call [addListener] when constructed.
 * Do **not** collect listeners via Koin `getAll()` into [ProjectService].
 */
interface ProjectEventHooks {
    fun addListener(listener: ProjectEventsListener)
    fun removeListener(listener: ProjectEventsListener)
}
