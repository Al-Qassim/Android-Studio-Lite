package com.robotopia.androidstudiolite.feature.files.data

import com.robotopia.androidstudiolite.core.error.AppException
import java.io.File

/**
 * Resolves paths under a project root and rejects sandbox escape attempts.
 */
internal object SandboxPaths {
    fun normalizeRelative(relativePath: String): String {
        val trimmed = relativePath.trim().replace('\\', '/')
        if (trimmed.isEmpty() || trimmed == ".") return ""
        if (File(trimmed).isAbsolute) {
            throw AppException("Path is outside the project")
        }

        val segments = trimmed.split('/').filter { it.isNotEmpty() && it != "." }
        val stack = ArrayDeque<String>()
        for (segment in segments) {
            when (segment) {
                ".." -> {
                    if (stack.isEmpty()) {
                        throw AppException("Path is outside the project")
                    }
                    stack.removeLast()
                }
                else -> stack.addLast(segment)
            }
        }
        return stack.joinToString("/")
    }

    fun resolve(root: File, relativePath: String): File {
        val rootCanonical = root.canonicalFile
        val normalized = normalizeRelative(relativePath)
        val target = if (normalized.isEmpty()) {
            rootCanonical
        } else {
            File(rootCanonical, normalized).canonicalFile
        }
        ensureInsideRoot(rootCanonical, target)
        return target
    }

    fun toRelativePath(root: File, file: File): String {
        val rootCanonical = root.canonicalFile
        val fileCanonical = file.canonicalFile
        ensureInsideRoot(rootCanonical, fileCanonical)
        if (fileCanonical == rootCanonical) return ""
        val prefix = rootCanonical.path + File.separator
        return fileCanonical.path
            .removePrefix(prefix)
            .replace(File.separatorChar, '/')
    }

    fun join(parentRelative: String, name: String): String {
        val parent = normalizeRelative(parentRelative)
        return if (parent.isEmpty()) name else "$parent/$name"
    }

    fun parentRelative(relativePath: String): String {
        val normalized = normalizeRelative(relativePath)
        if (normalized.isEmpty()) return ""
        val lastSlash = normalized.lastIndexOf('/')
        return if (lastSlash < 0) "" else normalized.substring(0, lastSlash)
    }

    fun isDescendantOrSelf(ancestorRelative: String, candidateRelative: String): Boolean {
        val ancestor = normalizeRelative(ancestorRelative)
        val candidate = normalizeRelative(candidateRelative)
        if (ancestor.isEmpty()) return true
        return candidate == ancestor || candidate.startsWith("$ancestor/")
    }

    private fun ensureInsideRoot(rootCanonical: File, targetCanonical: File) {
        if (targetCanonical == rootCanonical) return
        val prefix = rootCanonical.path + File.separator
        if (!targetCanonical.path.startsWith(prefix)) {
            throw AppException("Path is outside the project")
        }
    }
}
