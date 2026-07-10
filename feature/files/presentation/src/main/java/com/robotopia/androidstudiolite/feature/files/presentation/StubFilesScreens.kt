package com.robotopia.androidstudiolite.feature.files.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.robotopia.androidstudiolite.feature.files.api.FilesScreens
import com.robotopia.androidstudiolite.feature.files.model.ProjectRoot

class StubFilesScreens : FilesScreens {
    @Composable
    override fun FileBrowser(
        root: ProjectRoot,
        projectName: String,
        initialRelativePath: String,
        onOpenFile: (relativePath: String) -> Unit,
        onNavigateBack: () -> Unit,
    ) {
        Text("Files (stub)")
    }
}
