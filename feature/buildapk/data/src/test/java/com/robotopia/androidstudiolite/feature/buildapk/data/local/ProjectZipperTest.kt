package com.robotopia.androidstudiolite.feature.buildapk.data.local

import com.robotopia.androidstudiolite.core.error.AppException
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.zip.ZipFile

class ProjectZipperTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun zipProject_skipsBuildDirAndRespectsGitignore() {
        val root = tmp.newFolder("project")
        File(root, "app/src/main/AndroidManifest.xml").apply {
            parentFile.mkdirs()
            writeText("<manifest/>")
        }
        File(root, "app/build/outputs/apk/debug/app.apk").apply {
            parentFile.mkdirs()
            writeText("apk")
        }
        File(root, "secret.txt").writeText("nope")
        File(root, ".gitignore").writeText("secret.txt\n")

        val zip = tmp.newFile("out.zip")
        ProjectZipper.zipProject(root, zip)

        ZipFile(zip).use { zf ->
            val names = zf.entries().asSequence().map { it.name }.toSet()
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
            ProjectZipper.zipProject(missing, zip)
        }
    }
}
