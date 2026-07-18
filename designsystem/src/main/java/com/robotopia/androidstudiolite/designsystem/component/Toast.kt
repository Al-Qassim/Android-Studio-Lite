package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.icon.IconWarning
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class ToastVariant {
    Success,
    Error,
}

private val ToastBottomGap = 24.dp

/**
 * Pill toast — surface fill, no border. Success uses check; error uses warning.
 *
 * For screen overlays, prefer [ToastBottom] so the pill clears the system nav bar.
 */
@Composable
fun Toast(
    message: String,
    modifier: Modifier = Modifier,
    variant: ToastVariant = ToastVariant.Success,
) {
    val shape = RoundedCornerShape(999.dp)
    Row(
        modifier = modifier
            .shadow(6.dp, shape)
            .clip(shape)
            .background(Theme.colors.Surface)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (variant) {
            ToastVariant.Success -> IconSuccess(tint = Theme.colors.Primary, size = 18.dp)
            ToastVariant.Error -> IconWarning(tint = Theme.colors.Danger, size = 18.dp)
        }
        BasicText(
            text = message,
            style = Typography.BodyMedium.copy(color = Theme.colors.Text),
        )
    }
}

/**
 * Bottom-centered toast overlay. Clears the system navigation bar, then adds
 * [ToastBottomGap] above that inset.
 */
@Composable
fun BoxScope.ToastBottom(
    message: String,
    variant: ToastVariant = ToastVariant.Success,
) {
    Toast(
        message = message,
        variant = variant,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .navigationBarsPadding()
            .padding(bottom = ToastBottomGap),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun ToastSuccessPreview() {
    Toast(
        message = "File saved",
        modifier = Modifier.padding(16.dp),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF1E1F22)
@Composable
private fun ToastErrorPreview() {
    Toast(
        message = "Couldn't save file",
        variant = ToastVariant.Error,
        modifier = Modifier.padding(16.dp),
    )
}
