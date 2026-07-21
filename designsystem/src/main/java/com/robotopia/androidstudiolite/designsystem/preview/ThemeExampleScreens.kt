package com.robotopia.androidstudiolite.designsystem.preview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.robotopia.androidstudiolite.designsystem.color.DarkColorScheme
import com.robotopia.androidstudiolite.designsystem.color.DraculaColorScheme
import com.robotopia.androidstudiolite.designsystem.color.LightColorScheme
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.CodeEditorField
import com.robotopia.androidstudiolite.designsystem.component.EmptyState
import com.robotopia.androidstudiolite.designsystem.component.FileRow
import com.robotopia.androidstudiolite.designsystem.component.FolderRow
import com.robotopia.androidstudiolite.designsystem.component.InsetDivider
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.Menu
import com.robotopia.androidstudiolite.designsystem.component.MenuItem
import com.robotopia.androidstudiolite.designsystem.component.PathBar
import com.robotopia.androidstudiolite.designsystem.component.PhaseItem
import com.robotopia.androidstudiolite.designsystem.component.PhaseList
import com.robotopia.androidstudiolite.designsystem.component.PhaseStatus
import com.robotopia.androidstudiolite.designsystem.component.ProjectRow
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitleAdd
import com.robotopia.androidstudiolite.designsystem.component.TopBarEditorMore
import com.robotopia.androidstudiolite.designsystem.component.TopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.icon.IconCloud
import com.robotopia.androidstudiolite.designsystem.icon.IconFolder
import com.robotopia.androidstudiolite.designsystem.icon.IconRun
import com.robotopia.androidstudiolite.designsystem.icon.IconSave
import com.robotopia.androidstudiolite.designsystem.popup.topEndPopupOffset

/** Example product-shaped screens under alternate color schemes (preview-only). */
@Composable
private fun ExampleProjectsScreen() {
    IslandScaffold(
        topBar = {
            TopBarTitleAction(
                title = "Projects",
                onActionClick = {},
                onSettingsClick = {},
            )
        },
    ) {
        ProjectRow(
            name = "HelloCompose",
            packageName = "com.example.hellocompose",
            meta = "Opened 2m ago",
            onClick = {},
            onMenuClick = {},
        )
        ProjectRow(
            name = "TodoApp",
            packageName = "com.example.todo",
            meta = "Opened yesterday",
            selected = true,
            onClick = {},
            onMenuClick = {},
        )
        ProjectRow(
            name = "Notes",
            packageName = "com.example.notes",
            meta = "Opened 3d ago",
            onClick = {},
            onMenuClick = {},
        )
    }
}

@Composable
private fun ExampleFilesScreen() {
    IslandScaffold(
        topBar = {
            TopBarBackTitleAdd(
                title = "HelloCompose",
                onBackClick = {},
                onRunClick = {},
                onAddClick = {},
            )
        },
    ) {
        PathBar(segments = listOf("/", "app", "src", "main"))
        InsetDivider()
        FolderRow(name = "java", onClick = {}, onMenuClick = {})
        FolderRow(name = "res", onClick = {}, onMenuClick = {})
        FileRow(
            name = "AndroidManifest.xml",
            showChevron = false,
            onClick = {},
            onMenuClick = {},
        )
        FileRow(
            name = "MainActivity.kt",
            selected = true,
            showChevron = false,
            onClick = {},
            onMenuClick = {},
        )
    }
}

@Composable
private fun ExampleEditorScreen() {
    IslandScaffold(
        topBar = {
            TopBarEditorMore(
                fileName = "MainActivity.kt",
                onBackClick = {},
                onRunClick = {},
                onMoreClick = {},
            )
        },
    ) {
        CodeEditorField(
            value = """
                package com.example

                import android.os.Bundle
                import androidx.activity.ComponentActivity

                class MainActivity : ComponentActivity() {
                  override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    println("Hello")
                  }
                }
            """.trimIndent(),
            onValueChange = {},
            wrapText = true,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF2B2D30,
    widthDp = 360,
    heightDp = 640,
    name = "Dark · Projects",
)
@Composable
private fun DarkProjectsExamplePreview() {
    Theme(colors = DarkColorScheme) { ExampleProjectsScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEBECF0,
    widthDp = 360,
    heightDp = 640,
    name = "Light · Projects",
)
@Composable
private fun LightProjectsExamplePreview() {
    Theme(colors = LightColorScheme) { ExampleProjectsScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF21222C,
    widthDp = 360,
    heightDp = 640,
    name = "Dracula · Projects",
)
@Composable
private fun DraculaProjectsExamplePreview() {
    Theme(colors = DraculaColorScheme) { ExampleProjectsScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEBECF0,
    widthDp = 360,
    heightDp = 640,
    name = "Light · Projects + menu",
)
@Composable
private fun LightProjectsMenuExamplePreview() {
    Theme(colors = LightColorScheme) {
        Box(modifier = Modifier.fillMaxSize()) {
            IslandScaffold(
                topBar = {
                    TopBarTitleAction(
                        title = "Projects",
                        onActionClick = {},
                        onSettingsClick = {},
                    )
                },
            ) {
                ProjectRow(
                    name = "HelloCompose",
                    packageName = "com.example.hellocompose",
                    meta = "Opened 2m ago",
                    selected = true,
                    onClick = {},
                    onMenuClick = {},
                )
                ProjectRow(
                    name = "TodoApp",
                    packageName = "com.example.todo",
                    meta = "Opened yesterday",
                    onClick = {},
                    onMenuClick = {},
                )
            }
            Popup(
                alignment = Alignment.TopEnd,
                offset = topEndPopupOffset(top = 110.dp, end = 8.dp),
                properties = PopupProperties(focusable = false),
            ) {
                Menu(
                    items = listOf(
                        MenuItem.Button(
                            label = "Open",
                            onClick = {},
                            icon = { tint, size -> IconFolder(tint = tint, size = size) },
                        ),
                        MenuItem.Divider,
                        MenuItem.Button(
                            label = "Run",
                            onClick = {},
                            icon = { tint, size -> IconRun(tint = tint, size = size) },
                        ),
                        MenuItem.Button(
                            label = "Build history",
                            onClick = {},
                            icon = { tint, size -> IconCloud(tint = tint, size = size) },
                        ),
                        MenuItem.Divider,
                        MenuItem.Button(
                            label = "Export…",
                            onClick = {},
                            icon = { tint, size -> IconSave(tint = tint, size = size) },
                        ),
                        MenuItem.Button(label = "Delete", onClick = {}, danger = true),
                    ),
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEBECF0,
    widthDp = 360,
    heightDp = 640,
    name = "Light · Files",
)
@Composable
private fun LightFilesExamplePreview() {
    Theme(colors = LightColorScheme) { ExampleFilesScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF21222C,
    widthDp = 360,
    heightDp = 640,
    name = "Dracula · Files",
)
@Composable
private fun DraculaFilesExamplePreview() {
    Theme(colors = DraculaColorScheme) { ExampleFilesScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEBECF0,
    widthDp = 360,
    heightDp = 640,
    name = "Light · Editor",
)
@Composable
private fun LightEditorExamplePreview() {
    Theme(colors = LightColorScheme) { ExampleEditorScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF21222C,
    widthDp = 360,
    heightDp = 640,
    name = "Dracula · Editor",
)
@Composable
private fun DraculaEditorExamplePreview() {
    Theme(colors = DraculaColorScheme) { ExampleEditorScreen() }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEBECF0,
    widthDp = 360,
    heightDp = 640,
    name = "Light · Build progress",
)
@Composable
private fun LightBuildProgressExamplePreview() {
    Theme(colors = LightColorScheme) {
        IslandScaffold(
            topBar = {
                TopBarBackTitleAdd(
                    title = "Build",
                    onBackClick = {},
                    onRunClick = {},
                )
            },
        ) {
            PhaseList(
                phases = listOf(
                    PhaseItem("Preparing", PhaseStatus.Complete),
                    PhaseItem("Uploading", PhaseStatus.Complete),
                    PhaseItem("Building", PhaseStatus.Current),
                    PhaseItem("Downloading", PhaseStatus.Upcoming),
                    PhaseItem("Ready to install", PhaseStatus.Upcoming),
                ),
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFEBECF0,
    widthDp = 360,
    heightDp = 640,
    name = "Light · Empty projects",
)
@Composable
private fun LightEmptyProjectsExamplePreview() {
    Theme(colors = LightColorScheme) {
        IslandScaffold(
            topBar = {
                TopBarTitleAction(
                    title = "Projects",
                    onActionClick = {},
                    onSettingsClick = {},
                )
            },
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                EmptyState(
                    title = "No projects yet",
                    hint = "Tap + to create or import a project.",
                )
            }
        }
    }
}
