package com.robotopia.androidstudiolite.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.R
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
private fun VectorIcon(
    drawableResId: Int,
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) {
    Image(
        painter = painterResource(drawableResId),
        contentDescription = null,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(tint),
    )
}

@Composable
fun IconBack(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_back, modifier, tint, size)

@Composable
fun IconAdd(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_add, modifier, tint, size)

@Composable
fun IconMore(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_more, modifier, tint, size)

@Composable
fun IconChevron(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_chevron, modifier, tint, size)

@Composable
fun IconFolder(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_folder, modifier, tint, size)

@Composable
fun IconFile(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_file, modifier, tint, size)

@Composable
fun IconRun(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Run,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_run, modifier, tint, size)

@Composable
fun IconCloud(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_cloud, modifier, tint, size)

@Composable
fun IconSuccess(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_success, modifier, tint, size)

@Composable
fun IconCopy(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_copy, modifier, tint, size)

@Composable
fun IconSave(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_save, modifier, tint, size)

@Composable
fun IconWrapText(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_wrap_text, modifier, tint, size)

@Composable
fun IconSettings(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_settings, modifier, tint, size)

@Composable
fun IconApk(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_apk, modifier, tint, size)

@Composable
fun IconWarning(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_warning, modifier, tint, size)

@Composable
fun IconLocked(
    modifier: Modifier = Modifier,
    tint: Color = Colors.Text,
    size: Dp = 24.dp,
) = VectorIcon(R.drawable.ic_locked, modifier, tint, size)

private data class IconPreviewItem(
    val name: String,
    val usage: String,
    val content: @Composable () -> Unit,
)

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun IconsPreview() {
    val items = listOf(
        IconPreviewItem("back", "Navigate up / dismiss") { IconBack() },
        IconPreviewItem("add", "Create file or folder") { IconAdd() },
        IconPreviewItem("more", "Overflow / context menu") { IconMore() },
        IconPreviewItem("chevron", "Expand / navigate in") { IconChevron() },
        IconPreviewItem("folder", "Directory row") { IconFolder() },
        IconPreviewItem("file", "File row") { IconFile() },
        IconPreviewItem("run", "Build / run action") { IconRun() },
        IconPreviewItem("cloud", "Remote build status") { IconCloud() },
        IconPreviewItem("success", "Success / check") { IconSuccess() },
        IconPreviewItem("save", "Save document") { IconSave() },
        IconPreviewItem("wrap", "Wrap text") { IconWrapText() },
        IconPreviewItem("apk", "APK artifact") { IconApk() },
        IconPreviewItem("warning", "Warning / caution") { IconWarning() },
        IconPreviewItem("locked", "Read-only / locked") { IconLocked() },
    )

    Column(
        modifier = Modifier
            .background(Color(0xFF1E1F22))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = "Icons",
            style = Typography.Display.copy(color = Colors.Text),
        )
        BasicText(
            text = "Lucide SVG → Android vector drawables",
            style = Typography.Caption.copy(color = Colors.Muted2),
        )
        Spacer(modifier = Modifier.height(8.dp))
        items.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                row.forEach { item ->
                    IconPreviewCell(
                        item = item,
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun IconPreviewCell(
    item: IconPreviewItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Colors.Surface)
            .border(1.dp, Colors.Border, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center,
        ) {
            item.content()
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            BasicText(
                text = item.name,
                style = Typography.BodyMedium.copy(color = Colors.Text),
            )
            BasicText(
                text = item.usage,
                style = Typography.Caption.copy(color = Colors.Muted2),
            )
        }
    }
}
