package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconMore
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
fun ProjectCard(
    name: String,
    packageName: String,
    meta: String,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Colors.Surface)
            .border(1.dp, Colors.Border, shape)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = name,
                style = Typography.Subtitle.copy(color = Colors.Text),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
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
        Spacer(modifier = Modifier.height(4.dp))
        BasicText(
            text = packageName,
            style = Typography.Body.copy(color = Colors.Muted),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(8.dp))
        BasicText(
            text = meta,
            style = Typography.Caption.copy(color = Colors.Muted2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun ProjectCardPreview() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProjectCard(
            name = "Android Studio Lite",
            packageName = "com.robotopia.androidstudiolite",
            meta = "Opened just now",
            onMenuClick = {},
        )
    }
}
