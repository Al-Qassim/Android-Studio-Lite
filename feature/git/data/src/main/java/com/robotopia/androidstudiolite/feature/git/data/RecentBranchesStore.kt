package com.robotopia.androidstudiolite.feature.git.data

import java.io.File

/**
 * MRU local branch names under `.git/asl-recent-branches` (one name per line, newest first).
 */
internal object RecentBranchesStore {
    private const val FILE_NAME = "asl-recent-branches"
    private const val MAX_STORED = 20

    fun record(gitDir: File, localBranch: String) {
        val name = localBranch.trim()
        if (name.isEmpty()) return
        val file = File(gitDir, FILE_NAME)
        val existing = read(gitDir).filter { it != name }
        val next = (listOf(name) + existing).take(MAX_STORED)
        file.writeText(next.joinToString("\n") + "\n")
    }

    fun read(gitDir: File): List<String> {
        val file = File(gitDir, FILE_NAME)
        if (!file.isFile) return emptyList()
        return file.readLines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }
}
