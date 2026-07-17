package com.robotopia.androidstudiolite.feature.onboarding.presentation.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.icon.IconApk
import com.robotopia.androidstudiolite.feature.onboarding.presentation.intro.OnboardingAppTitleBar
import com.robotopia.androidstudiolite.feature.onboarding.presentation.intro.OnboardingCenteredMessage
import com.robotopia.androidstudiolite.feature.onboarding.presentation.intro.OnboardingIntroContent

internal data class OnboardingIntroPreviewCase(
    private val label: String,
    val providerDisplayName: String,
) {
    override fun toString(): String = label
}

internal class OnboardingIntroPreviewProvider : PreviewParameterProvider<OnboardingIntroPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        OnboardingIntroPreviewCase("github", providerDisplayName = "GitHub"),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun OnboardingWelcomePreview() {
    OnboardingWelcomeContent(onContinueClick = {})
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun OnboardingIntroPreview(
    @PreviewParameter(OnboardingIntroPreviewProvider::class) preview: OnboardingIntroPreviewCase,
) {
    OnboardingIntroContent(
        providerDisplayName = preview.providerDisplayName,
        onConnectClick = {},
        onSkipClick = {},
    )
}

/**
 * Preview-only welcome screen. Not wired into onboarding navigation yet.
 */
@Composable
private fun OnboardingWelcomeContent(
    onContinueClick: () -> Unit,
) {
    IslandScaffold(
        topBar = { OnboardingAppTitleBar() },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Continue",
                    onClick = onContinueClick,
                    variant = ButtonVariant.Primary,
                )
            }
        },
    ) {
        OnboardingCenteredMessage(
            illustration = {
                IconApk(tint = Colors.Primary, size = 40.dp)
            },
            title = "Edit and build Android apps on your phone.",
            body = "Create a project, edit code, run a cloud build, and install the APK.",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}
