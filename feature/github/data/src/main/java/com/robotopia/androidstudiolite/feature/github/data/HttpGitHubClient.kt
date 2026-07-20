package com.robotopia.androidstudiolite.feature.github.data

import android.util.Base64
import com.robotopia.androidstudiolite.core.error.AppException
import com.robotopia.androidstudiolite.feature.github.api.GitHubClient
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceCode
import com.robotopia.androidstudiolite.feature.github.api.GitHubDeviceTokenResult
import com.robotopia.androidstudiolite.feature.github.api.GitHubReleaseRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubRepoRef
import com.robotopia.androidstudiolite.feature.github.api.GitHubUser
import com.robotopia.androidstudiolite.feature.github.api.GitHubWorkflowRun
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class HttpGitHubClient(
    private val client: OkHttpClient = defaultClient(),
) : GitHubClient {

    override suspend fun requestDeviceCode(clientId: String): GitHubDeviceCode =
        withContext(Dispatchers.IO) {
            val body = FormBody.Builder()
                .add("client_id", clientId)
                .add("scope", DEVICE_SCOPE)
                .build()
            val json = formPost("$LOGIN_ROOT/device/code", body)
            GitHubDeviceCode(
                deviceCode = json.getString("device_code"),
                userCode = json.getString("user_code"),
                verificationUri = json.optString("verification_uri")
                    .ifBlank { "https://github.com/login/device" },
                expiresInSeconds = json.optInt("expires_in", 900),
                intervalSeconds = json.optInt("interval", 5),
            )
        }

    override suspend fun pollDeviceToken(
        clientId: String,
        deviceCode: String,
    ): GitHubDeviceTokenResult = withContext(Dispatchers.IO) {
        val body = FormBody.Builder()
            .add("client_id", clientId)
            .add("device_code", deviceCode)
            .add("grant_type", "urn:ietf:params:oauth:grant-type:device_code")
            .build()
        val json = formPost("$LOGIN_ROOT/oauth/access_token", body)
        val error = json.optString("error")
        when {
            error == "authorization_pending" || error == "slow_down" ->
                GitHubDeviceTokenResult.Pending
            error == "access_denied" -> GitHubDeviceTokenResult.Denied
            error == "expired_token" -> GitHubDeviceTokenResult.Expired
            json.has("access_token") ->
                GitHubDeviceTokenResult.Success(accessToken = json.getString("access_token"))
            else -> GitHubDeviceTokenResult.Expired
        }
    }

    override suspend fun fetchAuthenticatedUser(accessToken: String): GitHubUser =
        withContext(Dispatchers.IO) {
            val json = apiGet(accessToken, "/user")
            GitHubUser(login = json.getString("login"))
        }

    override suspend fun ensureSandboxRepo(accessToken: String): GitHubRepoRef =
        withContext(Dispatchers.IO) {
            val login = apiGet(accessToken, "/user").getString("login")
            val existing = apiGetOrNull(accessToken, "/repos/$login/$REPO_NAME")
            if (existing != null) {
                val marker = apiGetOrNull(
                    accessToken,
                    "/repos/$login/$REPO_NAME/contents/$SANDBOX_MARKER",
                )
                if (marker == null) {
                    throw AppException(
                        "A GitHub repo named \"$REPO_NAME\" already exists but isn’t an " +
                            "Android Studio Lite build sandbox. Rename or delete it, then try again.",
                    )
                }
                return@withContext GitHubRepoRef(owner = login, name = REPO_NAME)
            }
            val createBody = JSONObject()
                .put("name", REPO_NAME)
                .put("private", false)
                .put(
                    "description",
                    "Android Studio Lite cloud builds (auto-created public sandbox)",
                )
                .put("auto_init", true)
                .toString()
                .toRequestBody(JSON)
            apiPost(accessToken, "/user/repos", createBody)
            val repo = GitHubRepoRef(owner = login, name = REPO_NAME)
            putTextFile(
                accessToken = accessToken,
                repo = repo,
                path = SANDBOX_MARKER,
                content = SANDBOX_MARKER_JSON,
                message = "Mark ASL build sandbox",
            )
            putTextFile(
                accessToken = accessToken,
                repo = repo,
                path = "README.md",
                content = README_MD,
                message = "Add ASL sandbox README",
            )
            repo
        }

    override suspend fun ensureWorkflowFile(accessToken: String, repo: GitHubRepoRef) =
        withContext(Dispatchers.IO) {
            val path = ".github/workflows/$WORKFLOW_FILE"
            val existing = apiGetOrNull(accessToken, "/repos/${repo.fullName}/contents/$path")
            val encoded = Base64.encodeToString(
                WORKFLOW_YAML.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP,
            )
            val body = JSONObject()
                .put("message", if (existing == null) "Add ASL cloud build workflow" else "Update ASL cloud build workflow")
                .put("content", encoded)
            if (existing != null) {
                body.put("sha", existing.getString("sha"))
            }
            apiPut(
                accessToken,
                "/repos/${repo.fullName}/contents/$path",
                body.toString().toRequestBody(JSON),
            )
            Unit
        }

    override suspend fun createRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        tag: String,
    ): GitHubReleaseRef = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("tag_name", tag)
            .put("name", tag)
            .put("body", "Ephemeral ASL cloud build")
            .put("draft", false)
            .put("prerelease", true)
            .toString()
            .toRequestBody(JSON)
        val json = apiPost(accessToken, "/repos/${repo.fullName}/releases", body)
        GitHubReleaseRef(
            id = json.getLong("id"),
            tag = tag,
            uploadUrlTemplate = json.getString("upload_url"),
        )
    }

    override suspend fun uploadReleaseAsset(
        accessToken: String,
        release: GitHubReleaseRef,
        file: File,
        assetName: String,
    ) = withContext(Dispatchers.IO) {
        val uploadUrl = release.uploadUrlTemplate
            .substringBefore("{")
            .trimEnd('?', '&') + "?name=" + assetName
        val request = Request.Builder()
            .url(uploadUrl)
            .header("Authorization", "Bearer $accessToken")
            .header("Accept", "application/vnd.github+json")
            .header("X-GitHub-Api-Version", API_VERSION)
            .post(file.asRequestBody("application/zip".toMediaType()))
            .build()
        execute(request)
        Unit
    }

    override suspend fun dispatchWorkflow(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseTag: String,
    ) = withContext(Dispatchers.IO) {
        val body = JSONObject()
            .put("ref", "main")
            .put("inputs", JSONObject().put("release_tag", releaseTag))
            .toString()
            .toRequestBody(JSON)
        apiPostRaw(
            accessToken,
            "/repos/${repo.fullName}/actions/workflows/$WORKFLOW_FILE/dispatches",
            body,
        )
    }

    override suspend fun findLatestWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        notBeforeEpochMs: Long,
    ): GitHubWorkflowRun? = withContext(Dispatchers.IO) {
        val json = apiGet(
            accessToken,
            "/repos/${repo.fullName}/actions/workflows/$WORKFLOW_FILE/runs?per_page=5",
        )
        val runs = json.optJSONArray("workflow_runs") ?: return@withContext null
        for (i in 0 until runs.length()) {
            val run = runs.getJSONObject(i)
            val created = parseGithubTime(run.optString("created_at"))
            if (created != null && created >= notBeforeEpochMs - 5_000L) {
                return@withContext GitHubWorkflowRun(
                    id = run.getLong("id"),
                    status = run.optString("status"),
                    conclusion = run.optString("conclusion")
                        .takeIf { it.isNotBlank() && it != "null" },
                    htmlUrl = run.optString("html_url").takeIf { it.isNotBlank() },
                )
            }
        }
        null
    }

    override suspend fun getWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        runId: Long,
    ): GitHubWorkflowRun = withContext(Dispatchers.IO) {
        val json = apiGet(accessToken, "/repos/${repo.fullName}/actions/runs/$runId")
        GitHubWorkflowRun(
            id = runId,
            status = json.optString("status"),
            conclusion = json.optString("conclusion")
                .takeIf { it.isNotBlank() && it != "null" },
            htmlUrl = json.optString("html_url").takeIf { it.isNotBlank() },
        )
    }

    override suspend fun cancelWorkflowRun(
        accessToken: String,
        repo: GitHubRepoRef,
        runId: Long,
    ) = withContext(Dispatchers.IO) {
        runCatching {
            apiPostRaw(
                accessToken,
                "/repos/${repo.fullName}/actions/runs/$runId/cancel",
                ByteArray(0).toRequestBody(null),
            )
        }
        Unit
    }

    override suspend fun findReleaseApkAssetUrl(
        accessToken: String,
        repo: GitHubRepoRef,
        tag: String,
    ): String = withContext(Dispatchers.IO) {
        val release = apiGet(accessToken, "/repos/${repo.fullName}/releases/tags/$tag")
        val assets = release.optJSONArray("assets") ?: JSONArray()
        for (i in 0 until assets.length()) {
            val asset = assets.getJSONObject(i)
            val name = asset.optString("name")
            if (name.endsWith(".apk", ignoreCase = true)) {
                return@withContext asset.getString("url")
            }
        }
        throw AppException("Build finished but no APK asset was found on the release.")
    }

    override suspend fun downloadAssetToFile(
        accessToken: String,
        assetApiUrl: String,
        destination: File,
    ) = withContext(Dispatchers.IO) {
        destination.parentFile?.mkdirs()
        val request = Request.Builder()
            .url(assetApiUrl)
            .header("Authorization", "Bearer $accessToken")
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
        Unit
    }

    override suspend fun deleteRelease(
        accessToken: String,
        repo: GitHubRepoRef,
        releaseId: Long,
        tag: String,
    ) = withContext(Dispatchers.IO) {
        runCatching { apiDelete(accessToken, "/repos/${repo.fullName}/releases/$releaseId") }
        runCatching { apiDelete(accessToken, "/repos/${repo.fullName}/git/refs/tags/$tag") }
        Unit
    }

    private fun putTextFile(
        accessToken: String,
        repo: GitHubRepoRef,
        path: String,
        content: String,
        message: String,
    ) {
        val existing = apiGetOrNull(accessToken, "/repos/${repo.fullName}/contents/$path")
        val encoded = Base64.encodeToString(content.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
        val body = JSONObject()
            .put("message", message)
            .put("content", encoded)
        if (existing != null) {
            body.put("sha", existing.getString("sha"))
        }
        apiPut(
            accessToken,
            "/repos/${repo.fullName}/contents/$path",
            body.toString().toRequestBody(JSON),
        )
    }

    private fun formPost(url: String, body: FormBody): JSONObject {
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/json")
            .post(body)
            .build()
        return jsonObject(execute(request))
    }

    private fun apiGet(accessToken: String, path: String): JSONObject =
        jsonObject(execute(authed(accessToken, path).get().build()))

    private fun apiGetOrNull(accessToken: String, path: String): JSONObject? {
        val request = authed(accessToken, path).get().build()
        client.newCall(request).execute().use { response ->
            if (response.code == 404) return null
            val text = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw AppException(githubErrorMessage(response.code, text))
            }
            return JSONObject(text.ifBlank { "{}" })
        }
    }

    private fun apiPost(accessToken: String, path: String, body: okhttp3.RequestBody): JSONObject =
        jsonObject(execute(authed(accessToken, path).post(body).build()))

    private fun apiPostRaw(accessToken: String, path: String, body: okhttp3.RequestBody) {
        execute(authed(accessToken, path).post(body).build())
    }

    private fun apiPut(accessToken: String, path: String, body: okhttp3.RequestBody): JSONObject =
        jsonObject(execute(authed(accessToken, path).put(body).build()))

    private fun apiDelete(accessToken: String, path: String) {
        execute(authed(accessToken, path).delete().build())
    }

    private fun authed(accessToken: String, path: String): Request.Builder =
        Request.Builder()
            .url("$API_ROOT$path")
            .header("Authorization", "Bearer $accessToken")
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
            throw AppException("Couldn't reach GitHub. Check your connection and try again.", e)
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

    companion object {
        const val REPO_NAME = "asl-builds-android-studio-lite"
        const val WORKFLOW_FILE = "asl-build.yml"
        private const val SANDBOX_MARKER = ".asl-sandbox.json"
        private const val SANDBOX_MARKER_JSON =
            """{"product":"android-studio-lite","role":"build-sandbox","version":1}""" + "\n"
        private const val README_MD =
            "# Android Studio Lite builds\n\n" +
                "Auto-created public sandbox for cloud APK builds. Do not store personal projects here.\n"
        private const val DEVICE_SCOPE = "repo workflow"
        private const val API_ROOT = "https://api.github.com"
        private const val LOGIN_ROOT = "https://github.com/login"
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
                      # Must match AGP in ASL / modern templates (AGP 9.x needs Gradle 9.4.1+).
                      gradle-version: "9.4.1"
                  - name: Set up Android SDK
                    uses: android-actions/setup-android@v3
                  - name: Assemble debug APK
                    working-directory: project
                    run: gradle assembleDebug --no-daemon
                  - name: Upload APK to release
                    env:
                      GH_TOKEN: ${'$'}{{ secrets.GITHUB_TOKEN }}
                    run: |
                      APK=${'$'}(find project -path "*/build/outputs/apk/debug/*.apk" | head -n 1)
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
