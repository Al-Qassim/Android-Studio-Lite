package com.robotopia.androidstudiolite.feature.buildapk.data.github

import com.robotopia.androidstudiolite.feature.github.api.GitHubReleaseRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
internal data class GitHubResumePayload(
    val owner: String,
    val repo: String,
    val runId: Long? = null,
    val releaseId: Long? = null,
    val releaseTag: String? = null,
    val uploadUrlTemplate: String? = null,
) {
    fun toRepoRef(): GitHubRepoRef = GitHubRepoRef(owner = owner, name = repo)

    fun toReleaseRefOrNull(): GitHubReleaseRef? {
        val id = releaseId ?: return null
        val tag = releaseTag ?: return null
        return GitHubReleaseRef(
            id = id,
            tag = tag,
            uploadUrlTemplate = uploadUrlTemplate.orEmpty(),
        )
    }

    fun encode(): String = ResumeJson.encodeToString(serializer(), this)

    companion object {
        private val ResumeJson = Json {
            ignoreUnknownKeys = true
            encodeDefaults = false
        }

        fun decode(json: String?): GitHubResumePayload? {
            if (json.isNullOrBlank()) return null
            return runCatching {
                ResumeJson.decodeFromString(serializer(), json)
            }.getOrNull()
        }
    }
}
