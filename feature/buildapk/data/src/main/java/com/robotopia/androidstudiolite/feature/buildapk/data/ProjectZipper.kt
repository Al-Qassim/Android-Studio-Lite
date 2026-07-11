package com.robotopia.androidstudiolite.feature.buildapk.data

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * Zips an ASL project tree, skipping common build/IDE junk and root `.gitignore` patterns
 * (simple prefix / basename matching — good enough for the empty-Compose template).
 */
internal object ProjectZipper {

    fun zipProject(projectRoot: File, destinationZip: File) {
        require(projectRoot.isDirectory) { "projectRoot must be a directory" }
        destinationZip.parentFile?.mkdirs()
        if (destinationZip.exists()) destinationZip.delete()

        val ignore = loadIgnoreRules(projectRoot)
        ZipOutputStream(BufferedOutputStream(FileOutputStream(destinationZip))).use { zos ->
            projectRoot.walkTopDown()
                .filter { it.isFile }
                .forEach { file ->
                    val relative = file.relativeTo(projectRoot).invariantSeparatorsPath
                    if (shouldSkip(relative, ignore)) return@forEach
                    zos.putNextEntry(ZipEntry(relative))
                    file.inputStream().use { it.copyTo(zos) }
                    zos.closeEntry()
                }
        }
    }

    private fun loadIgnoreRules(projectRoot: File): List<String> {
        val defaults = listOf(
            ".gradle/",
            "local.properties",
            ".idea/",
            ".DS_Store",
            "build/",
            "captures/",
            ".externalNativeBuild/",
            ".cxx/",
            "app/build/",
        )
        val fromFile = File(projectRoot, ".gitignore")
            .takeIf { it.isFile }
            ?.readLines()
            .orEmpty()
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .map { it.removePrefix("/") }
        return (defaults + fromFile).distinct()
    }

    private fun shouldSkip(relativePath: String, rules: List<String>): Boolean {
        val normalized = relativePath.replace('\\', '/')
        return rules.any { rule ->
            val pattern = rule.trim().removePrefix("/").replace('\\', '/')
            when {
                pattern.endsWith("/") -> {
                    val dir = pattern.dropLast(1)
                    normalized == dir ||
                        normalized.startsWith("$dir/") ||
                        normalized.contains("/$dir/")
                }
                pattern.contains("/") -> normalized == pattern || normalized.startsWith("$pattern/")
                else -> {
                    normalized == pattern ||
                        normalized.endsWith("/$pattern") ||
                        normalized.split('/').any { it == pattern }
                }
            }
        }
    }
}
