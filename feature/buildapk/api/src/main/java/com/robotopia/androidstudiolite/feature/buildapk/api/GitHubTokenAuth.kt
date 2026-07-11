package com.robotopia.androidstudiolite.feature.buildapk.api

/** POC: user pastes a GitHub PAT; stored in app prefs for API calls. */
interface GitHubTokenAuth {
    fun hasToken(): Boolean
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}
