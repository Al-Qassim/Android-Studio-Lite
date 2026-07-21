package com.robotopia.androidstudiolite.integration.navigation

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
        val files = IdeRoute.Files(
            projectId = "p1",
            projectName = "Hello",
            rootPath = "/projects/p1",
            packageName = "com.example.hello",
        )
        val filesGit = files.copy(showGit = true)
        val editor = IdeRoute.Editor(
            projectId = "p1",
            relativePath = "app/src/MainActivity.kt",
            projectName = "Hello",
            rootPath = "/projects/p1",
            packageName = "com.example.hello",
        )
        val editorFromGit = editor.copy(returnToGit = true)
        assertRoundTrip(files)
        assertRoundTrip(filesGit)
        assertRoundTrip(editor)
        assertRoundTrip(editorFromGit)
        assertEquals(filesGit, filesGit.toEditor("app/src/MainActivity.kt").toFiles())
        assertEquals(files, files.toEditor("app/src/MainActivity.kt").toFiles())
        assertRoundTrip(
            IdeRoute.Build(
                projectId = "p1",
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
                returnTo = files,
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = "p1",
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
                returnTo = filesGit,
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = "p1",
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
                returnTo = editor,
            ),
        )
        assertRoundTrip(
            IdeRoute.Build(
                projectId = "p1",
                projectName = "Hello",
                rootPath = "/projects/p1",
                packageName = "com.example.hello",
                returnTo = IdeRoute.Projects,
            ),
        )
    }

    @Test
    fun rejectsCorruptPayloads() {
        assertNull("{}".decodeIdeRouteOrNull())
        assertNull("".decodeIdeRouteOrNull())
        assertNull("""{"type":"nope"}""".decodeIdeRouteOrNull())
    }

    private fun assertRoundTrip(route: IdeRoute) {
        assertEquals(route, route.encodeToString().decodeIdeRouteOrNull())
    }
}
