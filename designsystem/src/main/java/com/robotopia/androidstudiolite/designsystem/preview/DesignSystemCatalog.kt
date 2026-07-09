package com.robotopia.androidstudiolite.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.component.AslButton
import com.robotopia.androidstudiolite.designsystem.component.AslButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.AslCodeSample
import com.robotopia.androidstudiolite.designsystem.component.AslContextMenu
import com.robotopia.androidstudiolite.designsystem.component.AslCreateMenu
import com.robotopia.androidstudiolite.designsystem.component.AslDialogForm
import com.robotopia.androidstudiolite.designsystem.component.AslDialogMessageAction
import com.robotopia.androidstudiolite.designsystem.component.AslEditorMenu
import com.robotopia.androidstudiolite.designsystem.component.AslEmptyState
import com.robotopia.androidstudiolite.designsystem.component.AslFileRow
import com.robotopia.androidstudiolite.designsystem.component.AslFolderRow
import com.robotopia.androidstudiolite.designsystem.component.AslMoveBar
import com.robotopia.androidstudiolite.designsystem.component.AslPathBar
import com.robotopia.androidstudiolite.designsystem.component.AslProjectCard
import com.robotopia.androidstudiolite.designsystem.component.AslStatusBar
import com.robotopia.androidstudiolite.designsystem.component.AslTextField
import com.robotopia.androidstudiolite.designsystem.component.AslTextFieldVariant
import com.robotopia.androidstudiolite.designsystem.component.AslToast
import com.robotopia.androidstudiolite.designsystem.component.AslTopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.component.AslTopBarEditorMore
import com.robotopia.androidstudiolite.designsystem.component.AslTopBarPathAdd
import com.robotopia.androidstudiolite.designsystem.component.AslTopBarTitleAction
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

/**
 * Optional scrollable catalog of ASL design system components for previews.
 */
@Composable
fun DesignSystemCatalog(modifier: Modifier = Modifier) {
    var field by remember { mutableStateOf("") }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AslColors.Bg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Section("Status / Top bars")
        AslStatusBar()
        AslTopBarTitleAction(title = "Projects", actionLabel = "+ New")
        AslTopBarBackTitle(title = "MyApp")
        AslTopBarPathAdd(pathSegments = listOf("app", "src", "main"))
        AslTopBarEditorMore(fileName = "MainActivity.kt", isDirty = true)

        Section("Buttons")
        AslButton(label = "Create", onClick = {}, variant = AslButtonVariant.Primary)
        AslButton(label = "Cancel", onClick = {}, variant = AslButtonVariant.Secondary)
        AslButton(label = "Run", onClick = {}, variant = AslButtonVariant.Run)

        Section("Text field")
        AslTextField(
            value = field,
            onValueChange = { field = it },
            placeholder = "Name",
            variant = AslTextFieldVariant.Form,
        )

        Section("Rows / Card")
        AslFolderRow(name = "src")
        AslFileRow(name = "MainActivity.kt", selected = true)
        AslProjectCard(
            name = "Android Studio Lite",
            packageName = "com.robotopia.androidstudiolite",
            meta = "Last opened · Today",
        )

        Section("Menus")
        AslContextMenu()
        AslCreateMenu()
        AslEditorMenu()

        Section("Dialogs")
        AslDialogForm(
            title = "New file",
            locationLabel = "Location: app/src",
            fieldValue = field,
            onFieldChange = { field = it },
            primaryActionLabel = "Create",
        )
        AslDialogMessageAction(
            title = "Delete?",
            message = "This cannot be undone.",
            actionLabel = "Delete",
            dangerAction = true,
        )

        Section("Misc")
        AslMoveBar(name = "Theme.kt")
        AslEmptyState(title = "Empty folder", hint = "Create a file to get started.")
        AslToast(message = "Copied")
        AslPathBar(segments = listOf("app", "src", "main"))
        AslCodeSample(gutter = "1", code = "@Composable", stringHighlight = false)

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun Section(title: String) {
    BasicText(
        text = title,
        style = AslTypography.Label.copy(color = AslColors.Muted),
        modifier = Modifier.padding(top = 8.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, heightDp = 1200)
@Composable
private fun DesignSystemCatalogPreview() {
    DesignSystemCatalog()
}
