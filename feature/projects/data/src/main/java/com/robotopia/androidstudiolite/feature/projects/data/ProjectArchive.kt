package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.core.error.AppException
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

/**
 * Zips / unzips ASL project trees.
 * Skips common build/IDE junk and root `.gitignore` patterns
 * (simple prefix / basename matching — same rules as cloud-build packaging).
 */
internal object ProjectArchive {

    fun zipProject(projectRoot: File, destinationZip: File) {
        if (!projectRoot.isDirectory) {
            throw AppException("Couldn't find the project files to export.")
        }
        try {
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
        } catch (e: AppException) {
            throw e
        } catch (e: IOException) {
            throw AppException("Couldn't package the project. Try again.", e)
        }
    }

    /**
     * Extracts [zipFile] into [destinationDir] (must be empty or newly created).
     * Returns the Gradle project root (handles a single top-level folder wrapper).
     */
    fun unzipProject(zipFile: File, destinationDir: File): File {
        if (!zipFile.isFile) {
            throw AppException("Couldn't open the project zip.")
        }
        destinationDir.mkdirs()
        try {
            ZipFile(zipFile).use { zip ->
                val entries = zip.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val target = resolveZipEntry(destinationDir, entry.name)
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            target.outputStream().use { output -> input.copyTo(output) }
                        }
                    }
                }
            }
        } catch (e: AppException) {
            throw e
        } catch (e: IOException) {
            throw AppException("Couldn't unpack the project zip. Try again.", e)
        }

        return findGradleProjectRoot(destinationDir)
            ?: throw AppException(
                "This zip isn't a recognized Android project. " +
                    "It needs a settings.gradle or settings.gradle.kts file.",
            )
    }

    fun findGradleProjectRoot(dir: File): File? {
        if (hasSettingsGradle(dir)) return dir
        val children = dir.listFiles()?.filter { it.isDirectory }.orEmpty()
        if (children.size == 1 && hasSettingsGradle(children.single())) {
            return children.single()
        }
        // Nested: search one level for a folder that looks like a project root
        children.firstOrNull { hasSettingsGradle(it) }?.let { return it }
        return null
    }

    internal fun shouldSkip(relativePath: String, rules: List<String>): Boolean {
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

    internal fun loadIgnoreRules(projectRoot: File): List<String> {
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

    private fun hasSettingsGradle(dir: File): Boolean =
        File(dir, "settings.gradle.kts").isFile || File(dir, "settings.gradle").isFile

    private fun resolveZipEntry(destinationDir: File, entryName: String): File {
        val normalized = entryName.replace('\\', '/').trimStart('/')
        if (normalized.isEmpty() || normalized.contains("..")) {
            throw AppException("The zip contains an unsafe path and can't be imported.")
        }
        val target = File(destinationDir, normalized)
        val destCanonical = destinationDir.canonicalFile
        val targetCanonical = target.canonicalFile
        if (!targetCanonical.path.startsWith(destCanonical.path + File.separator) &&
            targetCanonical != destCanonical
        ) {
            throw AppException("The zip contains an unsafe path and can't be imported.")
        }
        return target
    }
}
