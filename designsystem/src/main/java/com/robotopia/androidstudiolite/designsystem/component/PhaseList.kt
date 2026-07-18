package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class PhaseStatus {
    Complete,
    Current,
    Upcoming,
    Failed,
}

data class PhaseItem(
    val label: String,
    val status: PhaseStatus,
)

/** Stacked build/work phases with status icons. */
@Composable
fun PhaseList(
    phases: List<PhaseItem>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.colors.Surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        phases.forEach { phase ->
            PhaseRow(label = phase.label, status = phase.status)
        }
    }
}

@Composable
private fun PhaseRow(
    label: String,
    status: PhaseStatus,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PhaseStatusIcon(status = status)
        BasicText(
            text = label,
            style = when (status) {
                PhaseStatus.Current -> Typography.BodyStrong.copy(color = Theme.colors.Text)
                PhaseStatus.Complete -> Typography.Body.copy(color = Theme.colors.Muted)
                PhaseStatus.Upcoming -> Typography.Body.copy(color = Theme.colors.Muted2)
                PhaseStatus.Failed -> Typography.BodyStrong.copy(color = Theme.colors.Danger)
            },
        )
    }
}

@Composable
private fun PhaseStatusIcon(status: PhaseStatus) {
    when (status) {
        PhaseStatus.Complete -> IconSuccess(tint = Theme.colors.Run, size = 16.dp)
        PhaseStatus.Current -> LoadingIndicator(size = 16.dp)
        PhaseStatus.Failed -> Box(
            modifier = Modifier.size(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = "✕",
                style = Typography.Caption.copy(
                    color = Theme.colors.Danger,
                    textAlign = TextAlign.Center,
                ),
            )
        }
        PhaseStatus.Upcoming -> Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(Theme.colors.Disabled),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun PhaseListPreview() {
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
