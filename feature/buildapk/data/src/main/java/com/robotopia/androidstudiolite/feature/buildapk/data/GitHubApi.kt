package com.robotopia.androidstudiolite.feature.buildapk.data

import com.robotopia.androidstudiolite.core.error.AppException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

internal class GitHubApi(
    private val tokenStore: GitHubTokenStore,
    private val client: OkHttpClient = defaultClient(),
) {
    fun requireToken(): String =
        tokenStore.getToken()
            ?: throw AppException("Paste a GitHub Personal Access Token before starting a build.")

    fun getAuthenticatedLogin(): String {
        val json = apiGet("/user")
        return json.getString("login")
    }

    fun ensureBuildRepo(ownerLogin: String): RepoRef {
        val existing = apiGetOrNull("/repos/$ownerLogin/$REPO_NAME")
        if (existing != null) {
            return RepoRef(owner = ownerLogin, name = REPO_NAME)
        }
        val body = JSONObject()
            .put("name", REPO_NAME)
            .put("private", true)
            .put("description", "Android Studio Lite cloud builds (auto-created POC repo)")
            .put("auto_init", true)
            .toString()
            .toRequestBody(JSON)
        apiPost("/user/repos", body)
        return RepoRef(owner = ownerLogin, name = REPO_NAME)
    }

    fun ensureWorkflowFile(repo: RepoRef) {
        val path = ".github/workflows/$WORKFLOW_FILE"
        val existing = apiGetOrNull("/repos/${repo.fullName}/contents/$path")
        if (existing != null) return
        val encoded = android.util.Base64.encodeToString(
            WORKFLOW_YAML.toByteArray(Charsets.UTF_8),
            android.util.Base64.NO_WRAP,
        )
        val body = JSONObject()
            .put("message", "Add ASL cloud build workflow")
            .put("content", encoded)
            .toString()
            .toRequestBody(JSON)
        apiPut("/repos/${repo.fullName}/contents/$path", body)
    }

    fun createRelease(repo: RepoRef, tag: String): ReleaseRef {
        val body = JSONObject()
            .put("tag_name", tag)
            .put("name", tag)
            .put("body", "Ephemeral ASL cloud build")
            .put("draft", false)
            .put("prerelease", true)
            .toString()
            .toRequestBody(JSON)
        val json = apiPost("/repos/${repo.fullName}/releases", body)
        return ReleaseRef(
            id = json.getLong("id"),
            tag = tag,
            uploadUrlTemplate = json.getString("upload_url"),
        )
    }

    fun uploadReleaseAsset(release: ReleaseRef, file: File, assetName: String) {
        val uploadUrl = release.uploadUrlTemplate
            .substringBefore("{")
            .trimEnd('?', '&') + "?name=" + assetName
        val request = Request.Builder()
            .url(uploadUrl)
            .header("Authorization", "Bearer ${requireToken()}")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", API_VERSION)
            .post(file.asRequestBody("application/zip".toMediaType()))
            .build()
        execute(request)
    }

    fun dispatchWorkflow(repo: RepoRef, releaseTag: String) {
        val body = JSONObject()
            .put("ref", "main")
            .put("inputs", JSONObject().put("release_tag", releaseTag))
            .toString()
            .toRequestBody(JSON)
        apiPostRaw(
            "/repos/${repo.fullName}/actions/workflows/$WORKFLOW_FILE/dispatches",
            body,
        )
    }

    fun findLatestWorkflowRunId(repo: RepoRef, notBeforeEpochMs: Long): Long? {
        val json = apiGet(
            "/repos/${repo.fullName}/actions/workflows/$WORKFLOW_FILE/runs?per_page=5",
        )
        val runs = json.optJSONArray("workflow_runs") ?: return null
        for (i in 0 until runs.length()) {
            val run = runs.getJSONObject(i)
            val created = parseGithubTime(run.optString("created_at"))
            if (created != null && created >= notBeforeEpochMs - 5_000L) {
                return run.getLong("id")
            }
        }
        return null
    }

    fun getWorkflowRun(repo: RepoRef, runId: Long): WorkflowRunStatus {
        val json = apiGet("/repos/${repo.fullName}/actions/runs/$runId")
        return WorkflowRunStatus(
            status = json.optString("status"),
            conclusion = json.optString("conclusion").takeIf { it.isNotBlank() && it != "null" },
            htmlUrl = json.optString("html_url"),
        )
    }

    fun cancelWorkflowRun(repo: RepoRef, runId: Long) {
        runCatching {
            apiPostRaw(
                "/repos/${repo.fullName}/actions/runs/$runId/cancel",
                ByteArray(0).toRequestBody(null),
            )
        }
    }

    fun findReleaseApkAssetUrl(repo: RepoRef, tag: String): String {
        val release = apiGet("/repos/${repo.fullName}/releases/tags/$tag")
        val assets = release.optJSONArray("assets") ?: JSONArray()
        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            val name = asset.optString("name")
            if (name.endsWith(".apk", ignoreCase = true)) {
                return asset.getString("url")
            }
        }
        throw AppException("Build finished but no APK asset was found on the release.")
    }

    fun downloadAssetToFile(assetApiUrl: String, destination: File) {
        destination.parentFile?.mkdirs()
        val request = Request.Builder()
            .url(assetApiUrl)
            .header("Authorization", "Bearer ${requireToken()}")
            .header("Accept", "application/octet-stream")
            .header("X-GitHub-Api-Version", API_VERSION)
            .get()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw AppException("Failed to download APK (HTTP ${response.code})")
            }
            val body = response.body ?: throw AppException("Empty APK download")
            destination.outputStream().use { out -> body.byteStream().copyTo(out) }
        }
    }

    fun deleteRelease(repo: RepoRef, releaseId: Long, tag: String) {
        runCatching { apiDelete("/repos/${repo.fullName}/releases/$releaseId") }
        runCatching { apiDelete("/repos/${repo.fullName}/git/refs/tags/$tag") }
    }

    private fun apiGet(path: String): JSONObject = jsonObject(execute(authed(path).get().build()))

    private fun apiGetOrNull(path: String): JSONObject? {
        val request = authed(path).get().build()
        client.newCall(request).execute().use { response ->
            if (response.code == 404) return null
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw AppException(githubErrorMessage(response.code, text))
            }
            return JSONObject(text.ifBlank { "{}" })
        }
    }

    private fun apiPost(path: String, body: okhttp3.RequestBody): JSONObject =
        jsonObject(execute(authed(path).post(body).build()))

    private fun apiPostRaw(path: String, body: okhttp3.RequestBody) {
        execute(authed(path).post(body).build())
    }

    private fun apiPut(path: String, body: okhttp3.RequestBody): JSONObject =
        jsonObject(execute(authed(path).put(body).build()))

    private fun apiDelete(path: String) {
        execute(authed(path).delete().build())
    }

    private fun authed(path: String): Request.Builder =
        Request.Builder()
            .url("$API_ROOT$path")
            .header("Authorization", "Bearer ${requireToken()}")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", API_VERSION)

    private fun execute(request: Request): String {
        try {
            client.newCall(request).execute().use { response ->
                val text = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    throw AppException(githubErrorMessage(response.code, text))
                }
                return text
            }
        } catch (e: AppException) {
            throw e
        } catch (e: IOException) {
            throw AppException("Network error talking to GitHub", e)
        }
    }

    private fun jsonObject(text: String): JSONObject = JSONObject(text.ifBlank { "{}" })

    private fun githubErrorMessage(code: Int, body: String): String {
        val message = runCatching { JSONObject(body).optString("message") }.getOrNull()
        return when {
            !message.isNullOrBlank() -> "GitHub API error ($code): $message"
            else -> "GitHub API error (HTTP $code)"
        }
    }

    private fun parseGithubTime(iso: String?): Long? {
        if (iso.isNullOrBlank()) return null
        return runCatching { java.time.Instant.parse(iso).toEpochMilli() }.getOrNull()
    }

    data class RepoRef(val owner: String, val name: String) {
        val fullName: String get() = "$owner/$name"
    }

    data class ReleaseRef(
        val id: Long,
        val tag: String,
        val uploadUrlTemplate: String,
    )

    data class WorkflowRunStatus(
        val status: String,
        val conclusion: String?,
        val htmlUrl: String?,
    )

    companion object {
        const val REPO_NAME = "asl-builds-android-studio-lite"
        const val WORKFLOW_FILE = "asl-build.yml"
        private const val API_ROOT = "https://api.github.com"
        private const val API_VERSION = "2022-11-28"
        private val JSON = "application/json; charset=utf-8".toMediaType()

        val WORKFLOW_YAML: String = """
            name: ASL Build
            on:
              workflow_dispatch:
                inputs:
                  release_tag:
                    description: Ephemeral release tag containing project.zip
                    required: true
                    type: string
            permissions:
              contents: write
            jobs:
              build:
                runs-on: ubuntu-latest
                steps:
                  - name: Download project.zip from release
                    env:
                      GH_TOKEN: ${'$'}{{ secrets.GITHUB_TOKEN }}
                    run: |
                      gh release download "${'$'}{{ inputs.release_tag }}" -p project.zip -R "${'$'}{{ github.repository }}"
                      mkdir -p project
                      unzip -q project.zip -d project
                  - name: Set up JDK 17
                    uses: actions/setup-java@v4
                    with:
                      distribution: temurin
                      java-version: "17"
                  - name: Set up Gradle
                    uses: gradle/actions/setup-gradle@v4
                    with:
                      gradle-version: "8.11.1"
                  - name: Set up Android SDK
                    uses: android-actions/setup-android@v3
                  - name: Assemble debug APK
                    working-directory: project
                    run: gradle assembleDebug --no-daemon
                  - name: Upload APK to release
                    env:
                      GH_TOKEN: ${'$'}{{ secrets.GITHUB_TOKEN }}
                    run: |
                      APK=$(find project -path "*/build/outputs/apk/debug/*.apk" | head -n 1)
                      if [ -z "${'$'}APK" ]; then
                        echo "No debug APK found"
                        find project -name "*.apk" || true
                        exit 1
                      fi
                      cp "${'$'}APK" app-debug.apk
                      gh release upload "${'$'}{{ inputs.release_tag }}" app-debug.apk -R "${'$'}{{ github.repository }}" --clobber
        """.trimIndent() + "\n"

        fun defaultClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .build()
    }
}
