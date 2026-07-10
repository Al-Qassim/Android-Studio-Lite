package com.robotopia.androidstudiolite.integration.navigation

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.projects.api.ProjectsScreens
import org.koin.compose.koinInject

/**
 * IDE root: switches between feature sub-navigations.
 * Feature-internal routes (e.g. projects list ↔ create) stay inside each feature’s [NavHost].
 * Full cross-feature graph (files / editor / build) lands in #11.
 */
@Composable
fun IdeNavHost() {
    val projectsScreens: ProjectsScreens = koinInject()
    projectsScreens.NavHost(
        onOpenProject = {
            // Hand off to files feature when #8 / #11 wire that route.
        },
    )
}
