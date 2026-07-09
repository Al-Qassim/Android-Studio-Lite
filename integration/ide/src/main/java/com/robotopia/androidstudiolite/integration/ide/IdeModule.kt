package com.robotopia.androidstudiolite.integration.ide

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.core.database.databaseModule
import com.robotopia.androidstudiolite.feature.build.impl.buildModule
import com.robotopia.androidstudiolite.feature.editor.impl.editorModule
import com.robotopia.androidstudiolite.feature.files.impl.filesModule
import com.robotopia.androidstudiolite.feature.projects.impl.projectsModule
import org.koin.dsl.module

/**
 * Wires feature Koin modules. NavHost graph lands in #11.
 */
val ideModule = module {
    includes(databaseModule, projectsModule, filesModule, editorModule, buildModule)
}

@Composable
fun IdeRootPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("IDE scaffold ready")
    }
}
