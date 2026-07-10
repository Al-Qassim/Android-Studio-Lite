package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.core.error.AppException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class SandboxPathsTest {
    @get:Rule
    val tempDir = TemporaryFolder()

    @Test
    fun normalizeRelative_collapsesDotSegments() {
        assertEquals("foo/bar", SandboxPaths.normalizeRelative("./foo/./bar"))
    }

    @Test
    fun normalizeRelative_rejectsEscapeAttempts() {
        assertEquals("foo", SandboxPaths.normalizeRelative("foo/../foo"))
        assertThrows(AppException::class.java) {
            SandboxPaths.normalizeRelative("..")
        }
        assertThrows(AppException::class.java) {
            SandboxPaths.normalizeRelative("foo/../../..")
        }
        assertThrows(AppException::class.java) {
            SandboxPaths.normalizeRelative("../outside")
        }
    }

    @Test
    fun resolve_staysInsideRoot() {
        val root = tempDir.newFolder("project")
        val nested = File(root, "app/src").apply { mkdirs() }

        val resolved = SandboxPaths.resolve(root, "app/src")
        assertEquals(nested.canonicalFile, resolved)
    }

    @Test
    fun resolve_blocksEscapeViaParentSegments() {
        val root = tempDir.newFolder("project")
        val outside = tempDir.newFolder("outside")

        val error = assertThrows(AppException::class.java) {
            SandboxPaths.resolve(root, "../${outside.name}")
        }
        assertEquals("Path is outside the project", error.uiMessage)
    }

    @Test
    fun resolve_blocksAbsolutePathsOutsideRoot() {
        val root = tempDir.newFolder("project")

        val error = assertThrows(AppException::class.java) {
            SandboxPaths.resolve(root, tempDir.root.absolutePath)
        }
        assertEquals("Path is outside the project", error.uiMessage)
    }

    @Test
    fun toRelativePath_roundTrips() {
        val root = tempDir.newFolder("project")
        val file = File(root, "app/Main.kt").apply {
            parentFile!!.mkdirs()
            createNewFile()
        }

        assertEquals("app/Main.kt", SandboxPaths.toRelativePath(root, file))
    }

    @Test
    fun isDescendantOrSelf_detectsNestedFolders() {
        assertEquals(true, SandboxPaths.isDescendantOrSelf("app", "app/src"))
        assertEquals(false, SandboxPaths.isDescendantOrSelf("app/src", "app"))
    }
}
