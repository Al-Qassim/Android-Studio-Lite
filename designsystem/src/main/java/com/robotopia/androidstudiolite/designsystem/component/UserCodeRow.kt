package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.icon.IconCopy
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/** Device-login code with copy action (auth connect flow). */
@Composable
fun UserCodeRow(
    code: String,
    onCopy: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Theme.colors.Surface2)
            .padding(horizontal = 10.dp, vertical = 10.dp),
    ) {
        BasicText(
            text = code,
            style = Typography.Headline.copy(
                color = Theme.colors.Text,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .clickable(onClick = onCopy)
                .padding(horizontal = 40.dp, vertical = 6.dp),
        )
        IconButton(
            onClick = onCopy,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .semantics { contentDescription = "Copy code" },
            variant = IconButtonVariant.Ghost,
            icon = { _, size -> IconCopy(tint = Theme.colors.Primary, size = size) },
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, widthDp = 360)
@Composable
private fun UserCodeRowPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        UserCodeRow(code = "ABCD-1234")
    }
}
