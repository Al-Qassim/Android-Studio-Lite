package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.icon.IconCopy
import com.robotopia.androidstudiolite.designsystem.typography.Typography

/** Device-login code with copy action (auth connect flow). */
@Composable
fun UserCodeRow(
    code: String,
    onCopy: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Colors.Surface2)
            .padding(horizontal = 10.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        BasicText(
            text = code,
            style = Typography.Headline.copy(
                color = Colors.Text,
                fontSize = 24.sp,
                lineHeight = 30.sp,
                letterSpacing = 1.5.sp,
            ),
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onCopy)
                .padding(horizontal = 4.dp, vertical = 6.dp),
        )
        IconButton(
            onClick = onCopy,
            modifier = Modifier.semantics { contentDescription = "Copy code" },
            variant = IconButtonVariant.Ghost,
            icon = { _, size -> IconCopy(tint = Colors.Primary, size = size) },
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
