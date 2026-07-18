package com.robotopia.androidstudiolite.feature.projects.api

import com.robotopia.androidstudiolite.feature.projects.model.ProjectId

/**
 * Project lifecycle hook. Register via [ProjectEventHooks.addListener]
 * (e.g. from `BuildService` construction), not through Koin `getAll()` multi-bind.
 *
 * [onProjectDeleted] runs after a successful delete; the dispatcher isolates
 * exceptions per listener so one failure cannot undo delete.
 */
fun interface ProjectEventsListener {
    suspend fun onProjectDeleted(id: ProjectId)
}
