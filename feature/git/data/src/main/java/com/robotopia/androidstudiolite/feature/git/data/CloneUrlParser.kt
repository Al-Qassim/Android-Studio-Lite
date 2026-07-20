package com.robotopia.androidstudiolite.feature.git.data

import com.robotopia.androidstudiolite.feature.git.api.CloneUrlValidation

internal object CloneUrlParser {
    private val ownerRepo = Regex("""^[A-Za-z0-9_.-]+/[A-Za-z0-9_.-]+$""")

    fun validate(input: String): CloneUrlValidation {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return CloneUrlValidation(errorMessage = "Enter a GitHub URL or owner/repo.")
        }

        val normalizedInput = ensureHttpsScheme(trimmed)
        val slug = when {
            normalizedInput.matches(ownerRepo) -> normalizedInput.removeSuffix(".git")
            else -> parseGithubHttps(normalizedInput)
        } ?: return CloneUrlValidation(
            errorMessage = "Use https://github.com/owner/repo or owner/repo.",
        )

        return CloneUrlValidation(normalizedHttpsUrl = "https://github.com/$slug.git")
    }

    /** If the user omitted only the scheme for a github.com URL, prepend https://. */
    private fun ensureHttpsScheme(raw: String): String {
        if (raw.contains("://")) return raw
        val lower = raw.lowercase()
        if (lower.startsWith("github.com/") || lower.startsWith("www.github.com/")) {
            return "https://$raw"
        }
        return raw
    }

    private fun parseGithubHttps(raw: String): String? {
        val withoutGitSuffix = raw.removeSuffix(".git")
        val uri = runCatching { java.net.URI(withoutGitSuffix) }.getOrNull() ?: return null
        if (uri.scheme != "https" && uri.scheme != "http") return null
        val host = uri.host?.lowercase() ?: return null
        if (host != "github.com" && host != "www.github.com") return null
        val parts = uri.path.trim('/').split('/').filter { it.isNotEmpty() }
        if (parts.size < 2) return null
        val owner = parts[0]
        val repo = parts[1].removeSuffix(".git")
        if (owner.isEmpty() || repo.isEmpty()) return null
        return "$owner/$repo"
    }
}
