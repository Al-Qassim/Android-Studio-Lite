package com.robotopia.androidstudiolite.feature.git.data

import java.io.File
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * JVM proof that local SCM ops work via JGit (not ART — see androidTest).
 */
class JGitGitServiceAdapterTest {
    @get:Rule
    val temp = TemporaryFolder()

    private lateinit var git: JGitGitServiceAdapter
    private lateinit var root: File

    @Before
    fun setUp() {
        git = JGitGitServiceAdapter()
        root = temp.newFolder("repo")
    }

    @Test
    fun init_status_commit_and_branch() = runBlocking {
        git.init(root)
        assertEquals("master", git.currentBranch(root))

        File(root, "README.md").writeText("hello\n")
        val dirty = git.status(root)
        assertFalse(dirty.isClean)
        assertTrue(dirty.untracked.contains("README.md"))

        git.stageAll(root)
        val sha = git.commit(
            projectRoot = root,
            message = "Initial commit",
            authorName = "ASL POC",
            authorEmail = "poc@androidstudiolite.local",
        )
        assertTrue(sha.isNotBlank())
        assertTrue(git.status(root).isClean)

        git.createBranch(root, "feature/poc")
        git.checkout(root, "feature/poc")
        assertEquals("feature/poc", git.currentBranch(root))

        File(root, "note.txt").writeText("on branch\n")
        git.stageAll(root)
        git.commit(
            projectRoot = root,
            message = "Branch work",
            authorName = "ASL POC",
            authorEmail = "poc@androidstudiolite.local",
        )

        val branches = git.listBranches(root)
        assertTrue(branches.any { it.contains("feature/poc") })
        assertTrue(branches.any { it == "master" || it == "main" })
    }
}
