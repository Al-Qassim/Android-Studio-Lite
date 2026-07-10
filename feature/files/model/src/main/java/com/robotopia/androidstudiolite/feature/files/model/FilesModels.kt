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

/** Per-field errors for file/folder name forms. Null means the field is valid. */
data class FileNameFieldErrors(
    val name: String? = null,
) {
    val hasErrors: Boolean
        get() = name != null
}

/**
 * Parent of [relativePath] for in-project navigation.
 * Returns `null` at project root (caller should leave the feature).
 * Returns `""` when stepping up from a top-level entry.
 */
fun parentRelativePathOrNull(relativePath: String): String? {
    if (relativePath.isEmpty()) return null
    val lastSlash = relativePath.lastIndexOf('/')
    return if (lastSlash < 0) "" else relativePath.substring(0, lastSlash)
}
