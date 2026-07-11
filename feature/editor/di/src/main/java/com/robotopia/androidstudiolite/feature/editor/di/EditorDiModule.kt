package com.robotopia.androidstudiolite.feature.editor.di

import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorPreferences
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.data.DefaultEditorSession
import com.robotopia.androidstudiolite.feature.editor.data.FileExplorerDocumentStore
import com.robotopia.androidstudiolite.feature.editor.data.PrefsEditorPreferences
import com.robotopia.androidstudiolite.feature.editor.presentation.DefaultEditorScreens
import com.robotopia.androidstudiolite.feature.editor.presentation.editor.EditorViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val editorDiModule = module {
    single<EditorSession> { DefaultEditorSession() }
    single<EditorPreferences> { PrefsEditorPreferences(context = androidContext()) }
    single<DocumentStore> { FileExplorerDocumentStore(fileExplorerService = get()) }
    single<EditorScreens> {
        DefaultEditorScreens(
            editorSession = get(),
            documentStore = get(),
            editorPreferences = get(),
        )
    }
    viewModelOf(::EditorViewModel)
}
