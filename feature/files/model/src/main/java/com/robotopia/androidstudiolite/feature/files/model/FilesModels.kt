package com.robotopia.androidstudiolite.feature.files.model

data class ProjectRoot(val absolutePath: String)

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
