package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

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

    @Test
    fun rename_updatesFileName() = runBlocking {
        val root = setupProject()
        val file = service.createFile(root, "", "old.txt")
        service.writeText(root, file.relativePath, "body")

        val renamed = service.rename(root, file.relativePath, "new.txt")

        assertEquals("new.txt", renamed.name)
        assertEquals("new.txt", renamed.relativePath)
        assertEquals("body", service.readText(root, "new.txt"))
        val listing = service.observeListing(root, "").first()
        assertEquals(listOf("new.txt"), listing.entries.map { it.name })
    }

    @Test
    fun move_relocatesFileIntoFolder() = runBlocking {
        val root = setupProject()
        val folder = service.createFolder(root, "", "app")
        val file = service.createFile(root, "", "Main.kt")

        val moved = service.move(root, file.relativePath, folder.relativePath)

        assertEquals("app/Main.kt", moved.relativePath)
        val rootListing = service.observeListing(root, "").first()
        assertEquals(listOf("app"), rootListing.entries.map { it.name })
        val appListing = service.observeListing(root, "app").first()
        assertEquals(listOf("Main.kt"), appListing.entries.map { it.name })
    }

    @Test
    fun copy_duplicatesFileLeavingSource() = runBlocking {
        val root = setupProject()
        val folder = service.createFolder(root, "", "out")
        val file = service.createFile(root, "", "notes.txt")
        service.writeText(root, file.relativePath, "copied")

        val copied = service.copy(root, file.relativePath, folder.relativePath)

        assertEquals("out/notes.txt", copied.relativePath)
        assertEquals("copied", service.readText(root, "notes.txt"))
        assertEquals("copied", service.readText(root, "out/notes.txt"))
    }

    @Test
    fun move_rejectsFolderIntoItself() = runBlocking {
        val root = setupProject()
        service.createFolder(root, "", "app")
        service.createFolder(root, "app", "src")

        val error = assertThrows(AppException::class.java) {
            runBlocking {
                service.move(root, "app", "app/src")
            }
        }
        assertEquals("Cannot move a folder into itself", error.uiMessage)
    }

    @Test
    fun rename_rejectsNameConflict() = runBlocking {
        val root = setupProject()
        service.createFile(root, "", "a.txt")
        service.createFile(root, "", "b.txt")

        val error = assertThrows(AppException::class.java) {
            runBlocking {
                service.rename(root, "a.txt", "b.txt")
            }
        }
        assertEquals("A file or folder with that name already exists", error.uiMessage)
    }

    @Test
    fun observeListing_sortsFoldersBeforeFiles() = runBlocking {
        val root = setupProject()
        service.createFile(root, "", "z.txt")
        service.createFolder(root, "", "a")
        service.createFile(root, "", "b.txt")
        service.createFolder(root, "", "m")

        val listing = service.observeListing(root, "").first()
        assertTrue(listing.entries[0] is FsNode.Folder)
        assertTrue(listing.entries[1] is FsNode.Folder)
        assertEquals(listOf("a", "m", "b.txt", "z.txt"), listing.entries.map { it.name })
    }

    private fun setupProject(): ProjectRoot {
        val dir = tempDir.newFolder("project")
        return ProjectRoot(dir.absolutePath)
    }
}
