package com.robotopia.androidstudiolite.integration.ide

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.robotopia.androidstudiolite.feature.buildapk.di.buildApkDiModule
import com.robotopia.androidstudiolite.feature.editor.di.editorDiModule
import com.robotopia.androidstudiolite.feature.files.di.filesDiModule
import com.robotopia.androidstudiolite.feature.projects.di.projectsDiModule
import com.robotopia.androidstudiolite.integration.database.databaseDiModule
import org.koin.dsl.module

/**
 * Product wiring: feature DI modules + database assembly.
 * NavHost graph lands in #11.
 */
val ideModule = module {
    includes(
        databaseDiModule,
        projectsDiModule,
        filesDiModule,
        editorDiModule,
        buildApkDiModule,
    )
}

@Composable
fun IdeRootPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("IDE scaffold ready")
    }
}
