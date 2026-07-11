package com.robotopia.androidstudiolite.feature.buildapk.presentation.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    starting: Boolean,
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
            Spacer(modifier = Modifier.weight(1f))
            Button(
                label = if (starting) "Starting…" else "Start build",
                onClick = onStartBuildClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
                enabled = !starting,
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
        starting = false,
        onBackClick = {},
        onStartBuildClick = {},
    )
}
