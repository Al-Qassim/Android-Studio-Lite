package com.robotopia.androidstudiolite.feature.projects.data

import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProjectImportMetadataTest {

    @get:Rule
    val tmp = TemporaryFolder()

    @Test
    fun read_parsesNameAndApplicationId() {
        val root = tmp.newFolder("proj")
        File(root, "settings.gradle.kts").writeText(
            """
            rootProject.name = "HelloCompose"
            include(":app")
            """.trimIndent(),
        )
        File(root, "app").mkdirs()
        File(root, "app/build.gradle.kts").writeText(
            """
            android {
                namespace = "com.example.hello"
                defaultConfig {
                    applicationId = "com.example.hello"
                }
            }
            """.trimIndent(),
        )

        val meta = ProjectImportMetadata.read(root)
        assertEquals("HelloCompose", meta.name)
        assertEquals("com.example.hello", meta.packageName)
    }

    @Test
    fun read_fallsBackWhenMetadataMissing() {
        val root = tmp.newFolder("bare")
        File(root, "settings.gradle.kts").writeText("include(\":app\")\n")

        val meta = ProjectImportMetadata.read(root, fallbackName = "FromZip")
        assertEquals("FromZip", meta.name)
        assertEquals("com.imported.app", meta.packageName)
    }

    @Test
    fun allocateUniqueName_appendsSuffixOnCollision() {
        val existing = setOf("Demo", "Demo (2)")
        assertEquals("Demo (3)", ProjectImportMetadata.allocateUniqueName("Demo", existing))
        assertEquals("Other", ProjectImportMetadata.allocateUniqueName("Other", existing))
    }

    @Test
    fun allocateUniqueName_stripsPathSeparators() {
        assertEquals(
            "bad-name",
            ProjectImportMetadata.allocateUniqueName("bad/name", emptySet()),
        )
    }
}
