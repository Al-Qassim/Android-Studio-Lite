package com.robotopia.androidstudiolite.integration.navigation

import com.robotopia.androidstudiolite.feature.editor.model.DocumentId
import com.robotopia.androidstudiolite.feature.projects.model.ProjectId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IdeRouteSaverTest {

    @Test
    fun roundTripsSimpleRoutes() {
        assertRoundTrip(IdeRoute.Onboarding)
        assertRoundTrip(IdeRoute.Projects)
        assertRoundTrip(IdeRoute.Settings)
    }

    @Test
    fun roundTripsDeepLinks() {
        val projectId = ProjectId("p1")
        assertRoundTrip(IdeRoute.Files(projectId))
        assertRoundTrip(
            IdeRoute.Editor(DocumentId(projectId, "app/src/MainActivity.kt")),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = projectId,
                returnTo = IdeRoute.Files(projectId),
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = projectId,
                returnTo = IdeRoute.Editor(DocumentId(projectId, "Foo.kt")),
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = projectId,
                returnTo = IdeRoute.Projects,
            ),
        )
    }

    @Test
    fun rejectsUnknownTokens() {
        assertNull(listOf("nope").toIdeRoute())
        assertNull(emptyList<String>().toIdeRoute())
    }

    private fun assertRoundTrip(route: IdeRoute) {
        assertEquals(route, route.toSaveList().toIdeRoute())
    }
}
