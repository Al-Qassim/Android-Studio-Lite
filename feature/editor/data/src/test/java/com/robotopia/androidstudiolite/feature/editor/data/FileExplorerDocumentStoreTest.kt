package com.robotopia.androidstudiolite.feature.editor.data

import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FileExplorerDocumentStoreTest {
    private val fileExplorer = FakeFileExplorerService()
    private val store = FileExplorerDocumentStore(fileExplorer)

    @Test
    fun loadAndSave_delegateToFileExplorer() = runBlocking {
        val root = ProjectRoot("/project")
        fileExplorer.contents["notes.txt"] = "initial"

        assertEquals("initial", store.load(root, "notes.txt"))

        store.save(root, "notes.txt", "updated")
        assertEquals("updated", fileExplorer.contents["notes.txt"])
        assertEquals("updated", store.load(root, "notes.txt"))
    }
}

private class FakeFileExplorerService : FileExplorerService {
    val contents = mutableMapOf<String, String>()

    override fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing> =
        emptyFlow()

    override suspend fun createFile(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): FsNode.File = error("unused")

    override suspend fun createFolder(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): FsNode.Folder = error("unused")

    override suspend fun rename(
        root: ProjectRoot,
        relativePath: String,
        newName: String,
    ): FsNode = error("unused")

    override suspend fun move(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): FsNode = error("unused")

    override suspend fun copy(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): FsNode = error("unused")

    override suspend fun delete(root: ProjectRoot, relativePath: String) = error("unused")

    override suspend fun readText(root: ProjectRoot, relativePath: String): String =
        contents.getValue(relativePath)

    override suspend fun writeText(root: ProjectRoot, relativePath: String, content: String) {
        contents[relativePath] = content
    }

    override fun validateFileName(name: String): FileNameFieldErrors = FileNameFieldErrors()
}
