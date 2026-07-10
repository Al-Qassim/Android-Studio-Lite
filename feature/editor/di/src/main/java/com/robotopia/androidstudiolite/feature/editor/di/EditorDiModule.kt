package com.robotopia.androidstudiolite.feature.editor.di

import com.robotopia.androidstudiolite.feature.editor.api.DocumentStore
import com.robotopia.androidstudiolite.feature.editor.api.EditorScreens
import com.robotopia.androidstudiolite.feature.editor.api.EditorSession
import com.robotopia.androidstudiolite.feature.editor.data.StubDocumentStore
import com.robotopia.androidstudiolite.feature.editor.data.StubEditorSession
import com.robotopia.androidstudiolite.feature.editor.presentation.StubEditorScreens
import org.koin.dsl.module

val editorDiModule = module {
    single<EditorSession> { StubEditorSession() }
    single<DocumentStore> { StubDocumentStore() }
    single<EditorScreens> { StubEditorScreens() }
}
