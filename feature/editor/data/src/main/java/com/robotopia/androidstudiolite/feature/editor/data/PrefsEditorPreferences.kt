package com.robotopia.androidstudiolite.feature.editor.data

import android.content.Context
import android.content.SharedPreferences
import com.robotopia.androidstudiolite.feature.editor.api.EditorPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefsEditorPreferences(
    context: Context,
) : EditorPreferences {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _autoSave = MutableStateFlow(prefs.getBoolean(KEY_AUTO_SAVE, DEFAULT_AUTO_SAVE))
    override val autoSave: StateFlow<Boolean> = _autoSave.asStateFlow()

    override fun setAutoSave(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SAVE, enabled).apply()
        _autoSave.value = enabled
    }

    private companion object {
        const val PREFS_NAME = "editor_preferences"
        const val KEY_AUTO_SAVE = "auto_save"
        const val DEFAULT_AUTO_SAVE = true
    }
}
