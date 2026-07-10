package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class StubFileExplorerService : FileExplorerService {
    override fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing> =
        flowOf(DirectoryListing(relativePath, emptyList()))

    override suspend fun createFile(root: ProjectRoot, parentRelative: String, name: String): FsNode.File =
        error("Not implemented — see #8")

    override suspend fun createFolder(root: ProjectRoot, parentRelative: String, name: String): FsNode.Folder =
        error("Not implemented — see #8")

    override suspend fun rename(root: ProjectRoot, relativePath: String, newName: String): FsNode =
        error("Not implemented — see #8")

    override suspend fun move(root: ProjectRoot, fromRelative: String, toParentRelative: String): FsNode =
        error("Not implemented — see #8")

    override suspend fun copy(root: ProjectRoot, fromRelative: String, toParentRelative: String): FsNode =
        error("Not implemented — see #8")

    override suspend fun delete(root: ProjectRoot, relativePath: String) =
        error("Not implemented — see #8")

    override suspend fun readText(root: ProjectRoot, relativePath: String): String =
        error("Not implemented — see #8")

    override suspend fun writeText(root: ProjectRoot, relativePath: String, content: String) =
        error("Not implemented — see #8")
}
