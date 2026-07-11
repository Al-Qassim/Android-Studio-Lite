package com.robotopia.androidstudiolite.feature.buildapk.presentation.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
internal fun BuildStartContent(
    projectName: String,
    packageName: String,
    tokenDraft: String,
    hasSavedToken: Boolean,
    starting: Boolean,
    onTokenDraftChange: (String) -> Unit,
    onSaveToken: () -> Unit,
    onClearToken: () -> Unit,
    onBackClick: () -> Unit,
    onStartBuildClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg),
    ) {
        TopBarBackTitle(
            title = "Build",
            onBackClick = onBackClick,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            ProjectSummaryCard(
                projectName = projectName,
                packageName = packageName,
            )
            Spacer(modifier = Modifier.height(16.dp))
            PatCard(
                tokenDraft = tokenDraft,
                hasSavedToken = hasSavedToken,
                onTokenDraftChange = onTokenDraftChange,
                onSaveToken = onSaveToken,
                onClearToken = onClearToken,
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                label = if (starting) "Starting…" else "Start build",
                onClick = onStartBuildClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
                enabled = !starting && hasSavedToken,
            )
            if (!hasSavedToken) {
                Spacer(modifier = Modifier.height(8.dp))
                BasicText(
                    text = "Save a GitHub PAT (scopes: repo, workflow) to enable Start build.",
                    style = Typography.Body.copy(color = Colors.Muted),
                )
            }
        }
    }
}

@Composable
private fun PatCard(
    tokenDraft: String,
    hasSavedToken: Boolean,
    onTokenDraftChange: (String) -> Unit,
    onSaveToken: () -> Unit,
    onClearToken: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "GitHub Personal Access Token",
            style = Typography.Subtitle.copy(color = Colors.Text),
        )
        BasicText(
            text = if (hasSavedToken) "Token saved in app prefs." else "Paste a classic PAT with repo + workflow.",
            style = Typography.Body.copy(color = Colors.Muted),
        )
        BasicTextField(
            value = tokenDraft,
            onValueChange = onTokenDraftChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Colors.Bg)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            textStyle = Typography.Body.copy(color = Colors.Text),
            cursorBrush = SolidColor(Colors.Text),
            singleLine = true,
            decorationBox = { inner ->
                if (tokenDraft.isEmpty()) {
                    BasicText(
                        text = "ghp_…",
                        style = Typography.Body.copy(color = Colors.Muted),
                    )
                }
                inner()
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                label = "Save",
                onClick = onSaveToken,
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Primary,
                enabled = tokenDraft.isNotBlank(),
            )
            Button(
                label = "Clear",
                onClick = onClearToken,
                modifier = Modifier.weight(1f),
                variant = ButtonVariant.Secondary,
                enabled = hasSavedToken || tokenDraft.isNotBlank(),
            )
        }
    }
}

@Composable
private fun ProjectSummaryCard(
    projectName: String,
    packageName: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        BasicText(
            text = projectName,
            style = Typography.Subtitle.copy(color = Colors.Text),
        )
        BasicText(
            text = packageName,
            style = Typography.Body.copy(color = Colors.Muted),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C, widthDp = 360, heightDp = 640)
@Composable
private fun BuildStartContentPreview() {
    BuildStartContent(
        projectName = "HelloCompose",
        packageName = "com.example.hellocompose",
        tokenDraft = "",
        hasSavedToken = false,
        starting = false,
        onTokenDraftChange = {},
        onSaveToken = {},
        onClearToken = {},
        onBackClick = {},
        onStartBuildClick = {},
    )
}
