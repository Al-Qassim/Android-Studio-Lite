package com.robotopia.androidstudiolite.feature.git.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ART spike: init / commit / branch under app filesDir on a real device or emulator.
 *
 * ./gradlew :feature:git:data:connectedDebugAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class JGitOnDevicePocTest {
    @Test
    fun init_commit_branch_under_filesDir() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val root = File(context.filesDir, "jgit-poc-${System.currentTimeMillis()}")
        root.mkdirs()
        try {
            val git = JGitGitServiceAdapter()
            git.init(root)

            File(root, "hello.txt").writeText("on-device\n")
            git.stageAll(root)
            val sha = git.commit(
                projectRoot = root,
                message = "On-device POC commit",
                authorName = "ASL POC",
                authorEmail = "poc@androidstudiolite.local",
            )
            assertTrue(sha.length >= 7)
            assertTrue(git.status(root).isClean)

            git.createBranch(root, "poc-branch")
            git.checkout(root, "poc-branch")
            assertEquals("poc-branch", git.currentBranch(root))
        } finally {
            root.deleteRecursively()
        }
    }
}
