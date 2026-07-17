package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class TransferBarMode {
    Move,
    Copy,
}

@Composable
fun MoveBar(
    name: String,
    onCancel: () -> Unit = {},
    onMoveHere: () -> Unit = {},
    modifier: Modifier = Modifier,
    mode: TransferBarMode = TransferBarMode.Move,
) {
    val prefix = when (mode) {
        TransferBarMode.Move -> "Moving: "
        TransferBarMode.Copy -> "Copying: "
    }
    val confirmLabel = when (mode) {
        TransferBarMode.Move -> "Move here"
        TransferBarMode.Copy -> "Paste here"
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.Transparent)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(
            text = prefix,
            style = Typography.BodyMedium.copy(color = Colors.Muted),
        )
        BasicText(
            text = name,
            style = Typography.BodyStrong.copy(color = Colors.Text),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            label = "Cancel",
            onClick = onCancel,
            variant = ButtonVariant.TextAction,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Button(
            label = confirmLabel,
            onClick = onMoveHere,
            variant = ButtonVariant.Primary,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "MoveBar · move")
@Composable
private fun MoveBarPreview() {
    MoveBar(name = "MainActivity.kt")
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22, name = "MoveBar · copy")
@Composable
private fun MoveBarCopyPreview() {
    MoveBar(name = "Theme.kt", mode = TransferBarMode.Copy)
}
