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

    private val _wrapText = MutableStateFlow(prefs.getBoolean(KEY_WRAP_TEXT, DEFAULT_WRAP_TEXT))
    override val wrapText: StateFlow<Boolean> = _wrapText.asStateFlow()

    override fun setAutoSave(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SAVE, enabled).apply()
        _autoSave.value = enabled
    }

    override fun setWrapText(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_WRAP_TEXT, enabled).apply()
        _wrapText.value = enabled
    }

    private companion object {
        const val PREFS_NAME = "editor_preferences"
        const val KEY_AUTO_SAVE = "auto_save"
        const val KEY_WRAP_TEXT = "wrap_text"
        const val DEFAULT_AUTO_SAVE = true
        const val DEFAULT_WRAP_TEXT = false
    }
}
