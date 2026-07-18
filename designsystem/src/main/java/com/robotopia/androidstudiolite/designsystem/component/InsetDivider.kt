package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme

private val DefaultInset = 12.dp

/** 1dp hairline with horizontal inset — not edge-to-edge. */
@Composable
fun InsetDivider(
    modifier: Modifier = Modifier,
    inset: Dp = DefaultInset,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = inset)
            .height(1.dp)
            .background(Theme.colors.Border),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun InsetDividerPreview() {
    InsetDivider(modifier = Modifier.padding(vertical = 16.dp))
}
