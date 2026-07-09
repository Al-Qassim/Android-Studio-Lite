package com.robotopia.androidstudiolite.designsystem.icon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.R
import com.robotopia.androidstudiolite.designsystem.color.Colors

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
    tint: Color = Colors.Text,
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
