package com.robotopia.androidstudiolite.feature.projects.data

import java.io.File

internal data class ImportedProjectMetadata(
    val name: String,
    val packageName: String,
)

/**
 * Best-effort metadata from an on-disk Gradle Android project tree.
 */
internal object ProjectImportMetadata {

    private val rootProjectNameRegex =
        Regex("""rootProject\.name\s*=\s*["']([^"']+)["']""")
    private val applicationIdRegex =
        Regex("""applicationId\s*=\s*["']([^"']+)["']""")
    private val namespaceRegex =
        Regex("""namespace\s*=\s*["']([^"']+)["']""")
    private val packageNameRegex =
        Regex("^[a-z][a-z0-9_]*(\\.[a-z][a-z0-9_]*)+$")

    fun read(projectRoot: File, fallbackName: String = "ImportedProject"): ImportedProjectMetadata {
        val name = readProjectName(projectRoot) ?: sanitizeName(fallbackName)
        val packageName = readPackageName(projectRoot) ?: FALLBACK_PACKAGE
        return ImportedProjectMetadata(name = name, packageName = packageName)
    }

    fun allocateUniqueName(desired: String, existingNames: Set<String>): String {
        val base = sanitizeName(desired)
        if (base !in existingNames) return base
        var n = 2
        while ("$base ($n)" in existingNames) {
            n++
        }
        return "$base ($n)"
    }

    private fun readProjectName(projectRoot: File): String? {
        val settings = listOf("settings.gradle.kts", "settings.gradle")
            .map { File(projectRoot, it) }
            .firstOrNull { it.isFile }
            ?: return null
        val match = rootProjectNameRegex.find(settings.readText()) ?: return null
        return sanitizeName(match.groupValues[1])
    }

    private fun readPackageName(projectRoot: File): String? {
        val candidates = listOf(
            File(projectRoot, "app/build.gradle.kts"),
            File(projectRoot, "app/build.gradle"),
            File(projectRoot, "build.gradle.kts"),
            File(projectRoot, "build.gradle"),
        )
        for (file in candidates) {
            if (!file.isFile) continue
            val text = file.readText()
            applicationIdRegex.find(text)?.groupValues?.get(1)?.let { id ->
                if (packageNameRegex.matches(id.trim())) return id.trim()
            }
            namespaceRegex.find(text)?.groupValues?.get(1)?.let { ns ->
                if (packageNameRegex.matches(ns.trim())) return ns.trim()
            }
        }
        return null
    }

    private fun sanitizeName(raw: String): String {
        val trimmed = raw.trim()
            .replace('/', '-')
            .replace('\\', '-')
            .take(64)
        return trimmed.ifEmpty { "ImportedProject" }
    }

    private const val FALLBACK_PACKAGE = "com.imported.app"
}
