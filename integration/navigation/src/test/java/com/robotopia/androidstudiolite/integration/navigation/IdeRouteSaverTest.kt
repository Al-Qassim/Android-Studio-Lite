package com.robotopia.androidstudiolite.integration.navigation

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
        val files = IdeRoute.Files(
            projectId = projectId,
            projectName = "Hello",
            rootPath = "/projects/p1",
            packageName = "com.example.hello",
        )
        val editor = IdeRoute.Editor(
            projectId = projectId,
            relativePath = "app/src/MainActivity.kt",
            projectName = "Hello",
            rootPath = "/projects/p1",
            packageName = "com.example.hello",
        )
        assertRoundTrip(files)
        assertRoundTrip(editor)
        assertRoundTrip(
            IdeRoute.Build(
                projectId = projectId,
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
                returnTo = files,
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = projectId,
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
                returnTo = editor,
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = projectId,
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
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
