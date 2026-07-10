package com.robotopia.androidstudiolite.feature.editor.data

import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot

class FileExplorerDocumentStore(
    private val fileExplorerService: FileExplorerService,
) : DocumentStore {
    override suspend fun load(root: ProjectRoot, relativePath: String): String =
        fileExplorerService.readText(root, relativePath)

    override suspend fun save(root: ProjectRoot, relativePath: String, content: String) {
        fileExplorerService.writeText(root, relativePath, content)
    }
}
