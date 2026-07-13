package com.robotopia.androidstudiolite.feature.auth.data

import android.content.Context
import android.content.SharedPreferences
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefsAuthSessionStore(
    context: Context,
) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _account = MutableStateFlow(readAccount())
    val account: StateFlow<AuthAccount?> = _account.asStateFlow()

    fun save(account: AuthAccount, accessToken: String) {
        prefs.edit()
            .putString(KEY_PROVIDER, account.providerName)
            .putString(KEY_IDENTITY, account.identity)
            .putString(KEY_TOKEN, accessToken)
            .apply()
        _account.value = account
    }

    fun clear() {
        prefs.edit().clear().apply()
        _account.value = null
    }

    fun accessToken(): String? = prefs.getString(KEY_TOKEN, null)

    private fun readAccount(): AuthAccount? {
        val provider = prefs.getString(KEY_PROVIDER, null) ?: return null
        val identity = prefs.getString(KEY_IDENTITY, null) ?: return null
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        if (token.isBlank()) return null
        return AuthAccount(providerName = provider, identity = identity)
    }

    private companion object {
        const val PREFS_NAME = "auth_session"
        const val KEY_PROVIDER = "provider_name"
        const val KEY_IDENTITY = "identity"
        const val KEY_TOKEN = "access_token"
    }
}
