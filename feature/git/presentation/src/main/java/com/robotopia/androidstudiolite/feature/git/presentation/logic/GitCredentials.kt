package com.robotopia.androidstudiolite.feature.git.presentation.logic

import com.robotopia.androidstudiolite.feature.auth.api.AuthSession
import com.robotopia.androidstudiolite.feature.git.api.GitCredentials

suspend fun credentialsOrNull(authSession: AuthSession): GitCredentials? {
    val token = authSession.accessToken() ?: return null
    val username = authSession.currentAccount()?.identity?.ifBlank { null } ?: "git"
    return GitCredentials(username = username, passwordOrToken = token)
}
