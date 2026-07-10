package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class DefaultFileExplorerServiceTest {
    @get:Rule
    val tempDir = TemporaryFolder()

    private val service = DefaultFileExplorerService()

    @Test
    fun readWriteText_roundTripsContent() = runBlocking {
        val root = setupProject()
        val file = service.createFile(root, "", "notes.txt")
        service.writeText(root, file.relativePath, "hello")

        assertEquals("hello", service.readText(root, file.relativePath))
    }

    @Test
    fun delete_removesFile() = runBlocking {
        val root = setupProject()
        val file = service.createFile(root, "", "temp.txt")
        service.delete(root, file.relativePath)

        val listing = service.observeListing(root, "").first()
        assertEquals(0, listing.entries.size)
    }

    @Test
    fun createFile_rejectsEscapePath() = runBlocking {
        val root = setupProject()

        val error = assertThrows(AppException::class.java) {
            runBlocking {
                service.createFile(root, "../outside", "hack.txt")
            }
        }
        assertEquals("Path is outside the project", error.uiMessage)
    }

    private fun setupProject(): ProjectRoot {
        val dir = tempDir.newFolder("project")
        return ProjectRoot(dir.absolutePath)
    }
}
