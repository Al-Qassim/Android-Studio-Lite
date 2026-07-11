package com.robotopia.androidstudiolite.feature.buildapk.data

import android.content.Context
import com.robotopia.androidstudiolite.feature.buildapk.api.GitHubTokenAuth

/** POC token store — plain SharedPreferences is fine for a draft demo. */
class GitHubTokenStore(
    context: Context,
) : GitHubTokenAuth {
    private val prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    override fun hasToken(): Boolean = !getToken().isNullOrBlank()

    override fun getToken(): String? = prefs.getString(KEY_TOKEN, null)?.takeIf { it.isNotBlank() }

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token.trim()).apply()
    }

    override fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    private companion object {
        const val PREFS = "asl_github_poc"
        const val KEY_TOKEN = "access_token"
    }
}
