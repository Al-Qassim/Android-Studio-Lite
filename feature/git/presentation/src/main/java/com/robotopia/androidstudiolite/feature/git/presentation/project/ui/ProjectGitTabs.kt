package com.robotopia.androidstudiolite.feature.git.presentation.project.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.typography.Typography
import com.robotopia.androidstudiolite.feature.git.presentation.project.ProjectGitTab

@Composable
internal fun ProjectGitTabBar(
    selected: ProjectGitTab,
    changesCount: Int,
    onSelect: (ProjectGitTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.colors.Surface),
    ) {
        ProjectGitTabItem(
            label = if (changesCount > 0) "Changes ($changesCount)" else "Changes",
            selected = selected == ProjectGitTab.Changes,
            onClick = { onSelect(ProjectGitTab.Changes) },
            modifier = Modifier.weight(1f),
        )
        ProjectGitTabItem(
            label = "History",
            selected = selected == ProjectGitTab.History,
            onClick = { onSelect(ProjectGitTab.History) },
            modifier = Modifier.weight(1f),
        )
        ProjectGitTabItem(
            label = "Branches",
            selected = selected == ProjectGitTab.Branches,
            onClick = { onSelect(ProjectGitTab.Branches) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ProjectGitTabItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = label,
            style = Typography.BodyStrong.copy(
                color = if (selected) Theme.colors.Text else Theme.colors.Muted,
            ),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(if (selected) Theme.colors.Primary else Theme.colors.Border),
        )
    }
}
