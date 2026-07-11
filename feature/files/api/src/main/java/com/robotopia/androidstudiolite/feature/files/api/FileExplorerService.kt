package com.robotopia.androidstudiolite.feature.files.api

import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.flow.Flow

interface FileExplorerService {
    fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing>
    suspend fun createFile(root: ProjectRoot, parentRelative: String, name: String): FsNode.File
    suspend fun createFolder(root: ProjectRoot, parentRelative: String, name: String): FsNode.Folder
    suspend fun rename(root: ProjectRoot, relativePath: String, newName: String): FsNode
    suspend fun move(root: ProjectRoot, fromRelative: String, toParentRelative: String): FsNode
    suspend fun copy(root: ProjectRoot, fromRelative: String, toParentRelative: String): FsNode
    suspend fun delete(root: ProjectRoot, relativePath: String)
    suspend fun readText(root: ProjectRoot, relativePath: String): String
    suspend fun writeText(root: ProjectRoot, relativePath: String, content: String)

    /** Validates a file or folder name. Implementation lives in `:data`. */
    fun validateFileName(name: String): FileNameFieldErrors
}

interface FilesScreens {
    /** Feature-owned entry; integration calls this rather than individual screens. */
    @Composable
    fun NavHost(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    )

    @Composable
    fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
        onRun: (() -> Unit)?,
    )
}
