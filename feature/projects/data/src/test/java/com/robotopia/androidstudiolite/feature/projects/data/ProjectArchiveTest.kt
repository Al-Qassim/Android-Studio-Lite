package com.robotopia.androidstudiolite.feature.projects.data

import com.robotopia.androidstudiolite.core.error.AppException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

class ProjectArchiveTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun zipProject_skipsBuildDirAndRespectsGitignore() {
        val root = tmp.newFolder("project")
        File(root, "settings.gradle.kts").writeText("rootProject.name = \"Demo\"\n")
        File(root, "app/src/main/AndroidManifest.xml").apply {
            parentFile!!.mkdirs()
            writeText("<manifest/>")
        }
        File(root, "app/build/outputs/apk/debug/app.apk").apply {
            parentFile!!.mkdirs()
            writeText("apk")
        }
        File(root, "secret.txt").writeText("nope")
        File(root, ".gitignore").writeText("secret.txt\n")

        val zip = tmp.newFile("out.zip")
        ProjectArchive.zipProject(root, zip)

        ZipFile(zip).use { zf ->
            val names = zf.entries().asSequence().map { it.name }.toSet()
            assertTrue(names.contains("settings.gradle.kts"))
            assertTrue(names.contains("app/src/main/AndroidManifest.xml"))
            assertFalse(names.any { it.startsWith("app/build/") })
            assertFalse(names.contains("secret.txt"))
        }
    }

    @Test
    fun zipProject_missingRoot_throwsAppException() {
        val missing = File(tmp.root, "does-not-exist")
        val zip = tmp.newFile("out.zip")
        assertThrows(AppException::class.java) {
            ProjectArchive.zipProject(missing, zip)
        }
    }

    @Test
    fun unzipProject_findsRootWhenZipHasWrapperFolder() {
        val zip = tmp.newFile("wrapped.zip")
        ZipOutputStream(zip.outputStream()).use { zos ->
            zos.putNextEntry(ZipEntry("MyApp/settings.gradle.kts"))
            zos.write("rootProject.name = \"MyApp\"\n".toByteArray())
            zos.closeEntry()
            zos.putNextEntry(ZipEntry("MyApp/app/build.gradle.kts"))
            zos.write("android { namespace = \"com.example.myapp\" }\n".toByteArray())
            zos.closeEntry()
        }

        val dest = tmp.newFolder("extracted")
        val root = ProjectArchive.unzipProject(zip, dest)
        assertTrue(File(root, "settings.gradle.kts").isFile)
        assertEquals("MyApp", root.name)
    }

    @Test
    fun unzipProject_rejectsZipSlip() {
        val zip = tmp.newFile("slip.zip")
        ZipOutputStream(zip.outputStream()).use { zos ->
            zos.putNextEntry(ZipEntry("../evil.txt"))
            zos.write("x".toByteArray())
            zos.closeEntry()
        }
        val dest = tmp.newFolder("safe")
        assertThrows(AppException::class.java) {
            ProjectArchive.unzipProject(zip, dest)
        }
    }

    @Test
    fun unzipProject_rejectsMissingSettingsGradle() {
        val zip = tmp.newFile("empty.zip")
        ZipOutputStream(zip.outputStream()).use { zos ->
            zos.putNextEntry(ZipEntry("readme.txt"))
            zos.write("hi".toByteArray())
            zos.closeEntry()
        }
        val dest = tmp.newFolder("extracted2")
        val error = assertThrows(AppException::class.java) {
            ProjectArchive.unzipProject(zip, dest)
        }
        assertTrue(error.uiMessage.contains("settings.gradle"))
    }
}
