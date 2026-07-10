package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.files.api.FileExplorerService
import com.robotopia.androidstudiolite.feature.files.model.DirectoryListing
import com.robotopia.androidstudiolite.feature.files.model.FileNameFieldErrors
import com.robotopia.androidstudiolite.feature.files.model.FsNode
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File

class DefaultFileExplorerService : FileExplorerService {
    private val refreshSignals =
        MutableSharedFlow<Unit>(replay = 1, extraBufferCapacity = 1).apply { tryEmit(Unit) }

    override fun observeListing(root: ProjectRoot, relativePath: String): Flow<DirectoryListing> =
        refreshSignals
            .onStart { emit(Unit) }
            .map { loadListing(root, relativePath) }
            .distinctUntilChanged()

    override suspend fun createFile(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): FsNode.File = withContext(Dispatchers.IO) {
        val created = createEntry(root, parentRelative, name, isDirectory = false)
        signalRefresh()
        created as FsNode.File
    }

    override suspend fun createFolder(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
    ): FsNode.Folder = withContext(Dispatchers.IO) {
        val created = createEntry(root, parentRelative, name, isDirectory = true)
        signalRefresh()
        created as FsNode.Folder
    }

    override suspend fun rename(
        root: ProjectRoot,
        relativePath: String,
        newName: String,
    ): FsNode = withContext(Dispatchers.IO) {
        FileValidation.validate(newName)
        val rootDir = rootDir(root)
        val source = SandboxPaths.resolve(rootDir, relativePath)
        if (!source.exists()) throw AppException("Item not found")

        val target = File(source.parentFile, newName.trim())
        ensureNoConflict(target)
        if (!source.renameTo(target)) throw AppException("Could not rename item")

        signalRefresh()
        target.toFsNode(rootDir)
    }

    override suspend fun move(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): FsNode = withContext(Dispatchers.IO) {
        transfer(root, fromRelative, toParentRelative, copy = false)
    }

    override suspend fun copy(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
    ): FsNode = withContext(Dispatchers.IO) {
        transfer(root, fromRelative, toParentRelative, copy = true)
    }

    override suspend fun delete(root: ProjectRoot, relativePath: String) {
        withContext(Dispatchers.IO) {
            val rootDir = rootDir(root)
            val target = SandboxPaths.resolve(rootDir, relativePath)
            if (!target.exists()) throw AppException("Item not found")
            if (!target.deleteRecursively()) throw AppException("Could not delete item")
            signalRefresh()
        }
    }

    override suspend fun readText(root: ProjectRoot, relativePath: String): String =
        withContext(Dispatchers.IO) {
            val rootDir = rootDir(root)
            val file = SandboxPaths.resolve(rootDir, relativePath)
            if (!file.isFile) throw AppException("File not found")
            file.readText()
        }

    override suspend fun writeText(
        root: ProjectRoot,
        relativePath: String,
        content: String,
    ) {
        withContext(Dispatchers.IO) {
            val rootDir = rootDir(root)
            val file = SandboxPaths.resolve(rootDir, relativePath)
            if (!file.isFile) throw AppException("File not found")
            file.writeText(content)
            signalRefresh()
        }
    }

    override fun validateFileName(name: String): FileNameFieldErrors =
        FileValidation.fieldErrors(name)

    private fun loadListing(root: ProjectRoot, relativePath: String): DirectoryListing {
        val rootDir = rootDir(root)
        val normalizedPath = SandboxPaths.normalizeRelative(relativePath)
        val directory = SandboxPaths.resolve(rootDir, normalizedPath)
        if (!directory.isDirectory) throw AppException("Folder not found")

        val entries = directory.listFiles()
            ?.mapNotNull { it.toFsNode(rootDir) }
            ?.sortedWith(compareBy<FsNode> { it !is FsNode.Folder }.thenBy { it.name.lowercase() })
            ?: emptyList()

        return DirectoryListing(
            currentRelativePath = normalizedPath,
            entries = entries,
        )
    }

    private fun createEntry(
        root: ProjectRoot,
        parentRelative: String,
        name: String,
        isDirectory: Boolean,
    ): FsNode {
        FileValidation.validate(name)
        val rootDir = rootDir(root)
        val parent = SandboxPaths.resolve(rootDir, parentRelative)
        if (!parent.isDirectory) throw AppException("Folder not found")

        val trimmedName = name.trim()
        val target = File(parent, trimmedName)
        ensureNoConflict(target)

        if (isDirectory) {
            if (!target.mkdir()) throw AppException("Could not create folder")
        } else {
            if (!target.createNewFile()) throw AppException("Could not create file")
        }
        return target.toFsNode(rootDir)
    }

    private fun transfer(
        root: ProjectRoot,
        fromRelative: String,
        toParentRelative: String,
        copy: Boolean,
    ): FsNode {
        val rootDir = rootDir(root)
        val source = SandboxPaths.resolve(rootDir, fromRelative)
        if (!source.exists()) throw AppException("Item not found")

        val destinationParent = SandboxPaths.resolve(rootDir, toParentRelative)
        if (!destinationParent.isDirectory) throw AppException("Destination folder not found")

        val normalizedFrom = SandboxPaths.normalizeRelative(fromRelative)
        val normalizedDestination = SandboxPaths.normalizeRelative(toParentRelative)
        if (!copy && source.isDirectory &&
            SandboxPaths.isDescendantOrSelf(normalizedFrom, normalizedDestination)
        ) {
            throw AppException("Cannot move a folder into itself")
        }

        val target = File(destinationParent, source.name)
        if (target.canonicalFile == source.canonicalFile) {
            return source.toFsNode(rootDir)
        }
        ensureNoConflict(target)

        val success = if (copy) {
            if (source.isDirectory) {
                copyDirectory(source, target)
            } else {
                source.copyTo(target)
                true
            }
        } else {
            source.renameTo(target)
        }
        if (!success) {
            throw AppException(if (copy) "Could not copy item" else "Could not move item")
        }

        signalRefresh()
        return target.toFsNode(rootDir)
    }

    private fun copyDirectory(source: File, target: File): Boolean {
        if (!target.mkdirs()) return false
        source.listFiles()?.forEach { child ->
            val childTarget = File(target, child.name)
            val ok = if (child.isDirectory) {
                copyDirectory(child, childTarget)
            } else {
                child.copyTo(childTarget)
                true
            }
            if (!ok) return false
        }
        return true
    }

    private fun ensureNoConflict(target: File) {
        if (target.exists()) throw AppException("A file or folder with that name already exists")
    }

    private fun rootDir(root: ProjectRoot): File {
        val dir = File(root.absolutePath)
        if (!dir.isDirectory) throw AppException("Project folder not found")
        return dir
    }

    private fun File.toFsNode(rootDir: File): FsNode {
        val relativePath = SandboxPaths.toRelativePath(rootDir, this)
        return if (isDirectory) {
            FsNode.Folder(name = name, relativePath = relativePath)
        } else {
            FsNode.File(name = name, relativePath = relativePath)
        }
    }

    private fun signalRefresh() {
        refreshSignals.tryEmit(Unit)
    }
}
