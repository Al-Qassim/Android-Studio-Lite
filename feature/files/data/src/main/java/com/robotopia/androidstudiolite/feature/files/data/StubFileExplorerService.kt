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

    override suspend fun createFile(root: ProjectRoot, parentRelative: String, name: String): Result<FsNode.File> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun createFolder(root: ProjectRoot, parentRelative: String, name: String): Result<FsNode.Folder> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun rename(root: ProjectRoot, relativePath: String, newName: String): Result<FsNode> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun move(root: ProjectRoot, fromRelative: String, toParentRelative: String): Result<FsNode> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun copy(root: ProjectRoot, fromRelative: String, toParentRelative: String): Result<FsNode> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun delete(root: ProjectRoot, relativePath: String): Result<Unit> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun readText(root: ProjectRoot, relativePath: String): Result<String> =
        Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun writeText(root: ProjectRoot, relativePath: String, content: String): Result<Unit> =
        Result.failure(IllegalStateException("Not implemented — see #8"))
}
