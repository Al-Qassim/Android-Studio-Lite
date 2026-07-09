package com.robotopia.androidstudiolite.feature.files.impl

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.core.model.ProjectRoot
import com.robotopia.androidstudiolite.feature.files.api.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.api.FsNode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.koin.dsl.module

private fun notImplemented(): Nothing = error("Not implemented — see #8")

internal class StubFileExplorerService : FileExplorerService {
    override fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing> =
        flowOf(DirectoryListing(relativePath, emptyList()))

    override suspend fun createFile(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): Result<FsNode.File> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun createFolder(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): Result<FsNode.Folder> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun rename(
        root: ProjectRoot,
        relativePath: String,
        newName: String,
    ): Result<FsNode> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun move(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): Result<FsNode> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun copy(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): Result<FsNode> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun delete(
        root: ProjectRoot,
        relativePath: String,
    ): Result<Unit> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun readText(
        root: ProjectRoot,
        relativePath: String,
    ): Result<String> = Result.failure(IllegalStateException("Not implemented — see #8"))

    override suspend fun writeText(
        root: ProjectRoot,
        relativePath: String,
        content: String,
    ): Result<Unit> = Result.failure(IllegalStateException("Not implemented — see #8"))
}

internal class StubFilesScreens : FilesScreens {
    @Composable
    override fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
    ) {
        Text("Files (stub)")
    }
}

val filesModule = module {
    single<FileExplorerService> { StubFileExplorerService() }
    single<FilesScreens> { StubFilesScreens() }
}
