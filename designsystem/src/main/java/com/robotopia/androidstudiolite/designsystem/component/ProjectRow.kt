package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.modifier.insetClickable
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/**
 * Flat project list row — large name, package subtitle, optional meta.
 * Optional [leading] status/icon chrome; sits flush on an island (no card border).
 */
@Composable
fun ProjectRow(
    name: String,
    packageName: String,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    meta: String? = null,
    selected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .insetClickable(
                onClick = onClick,
                selected = selected,
                onLongClick = onLongClick,
            )
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = if (leading != null) Alignment.Top else Alignment.CenterVertically,
    ) {
        if (leading != null) {
            leading()
            Spacer(modifier = Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = name,
                style = Typography.Headline.copy(color = Colors.Text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(4.dp))
            BasicText(
                text = packageName,
                style = Typography.Code.copy(color = Colors.Muted),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (!meta.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                BasicText(
                    text = meta,
                    style = Typography.Caption.copy(color = Colors.Muted2),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (onMenuClick != null) {
            IconButton(
                onClick = onMenuClick,
                variant = IconButtonVariant.Ghost,
                size = 32.dp,
                iconSize = 18.dp,
                icon = { tint, size -> IconMore(tint = tint, size = size) },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun ProjectRowPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        ProjectRow(
            name = "HelloCompose",
            packageName = "com.example.hellocompose",
            meta = "Opened just now",
            onMenuClick = {},
        )
        ProjectRow(
            name = "TodoApp",
            packageName = "com.example.todo",
            meta = "Opened yesterday",
            selected = true,
            onMenuClick = {},
            leading = { IconSuccess(tint = Colors.Run, size = 20.dp) },
        )
        ProjectRow(
            name = "WeatherDemo",
            packageName = "com.example.weather",
            meta = "Opened 3 days ago",
            onMenuClick = {},
        )
    }
}
