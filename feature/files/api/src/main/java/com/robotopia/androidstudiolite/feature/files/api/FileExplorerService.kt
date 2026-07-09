package com.robotopia.androidstudiolite.feature.files.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.ProjectRoot
import kotlinx.coroutines.flow.Flow

sealed class FsNode {
    abstract val name: String
    abstract val relativePath: String

    data class File(
        override val name: String,
        override val relativePath: String,
    ) : FsNode()

    data class Folder(
        override val name: String,
        override val relativePath: String,
    ) : FsNode()
}

data class DirectoryListing(
    val currentRelativePath: String,
    val entries: List<FsNode>,
)

sealed class FileOpError {
    data object OutsideSandbox : FileOpError()
    data object NameConflict : FileOpError()
    data object InvalidName : FileOpError()
    data object InvalidMove : FileOpError()
    data class Io(val message: String) : FileOpError()
}

interface FileExplorerService {
    fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing>
    suspend fun createFile(root: ProjectRoot, parentRelative: String, name: String): Result<FsNode.File>
    suspend fun createFolder(root: ProjectRoot, parentRelative: String, name: String): Result<FsNode.Folder>
    suspend fun rename(root: ProjectRoot, relativePath: String, newName: String): Result<FsNode>
    suspend fun move(root: ProjectRoot, fromRelative: String, toParentRelative: String): Result<FsNode>
    suspend fun copy(root: ProjectRoot, fromRelative: String, toParentRelative: String): Result<FsNode>
    suspend fun delete(root: ProjectRoot, relativePath: String): Result<Unit>
    suspend fun readText(root: ProjectRoot, relativePath: String): Result<String>
    suspend fun writeText(root: ProjectRoot, relativePath: String, content: String): Result<Unit>
}

interface FilesScreens {
    @Composable
    fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String = "",
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
    )
}
