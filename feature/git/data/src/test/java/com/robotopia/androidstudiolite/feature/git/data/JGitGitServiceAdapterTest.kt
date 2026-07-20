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

        val afterCheckout = git.listBranches(root)
        assertTrue(afterCheckout.local.any { it.name == "feature/poc" })
        assertTrue(afterCheckout.local.any { it.name == "master" || it.name == "main" })
        assertEquals("feature/poc", afterCheckout.recent.first().name)
        assertTrue(afterCheckout.recent.first().isCurrent)
        assertTrue(afterCheckout.recent.size <= 3)

        git.renameBranch(root, "feature/poc", "feature/renamed")
        assertEquals("feature/renamed", git.currentBranch(root))

        git.checkout(root, "master")
        git.merge(root, "feature/renamed")
        assertTrue(File(root, "note.txt").isFile)

        val recent = git.listBranches(root).recent
        assertEquals("master", recent.first().name)
        assertTrue(recent.any { it.name == "feature/renamed" } || recent.size == 1)

        git.createBranch(root, "to-delete")
        assertTrue(git.listBranches(root).local.any { it.name == "to-delete" })
        git.deleteBranch(root, "to-delete")
        assertFalse(git.listBranches(root).local.any { it.name == "to-delete" })
    }
}
