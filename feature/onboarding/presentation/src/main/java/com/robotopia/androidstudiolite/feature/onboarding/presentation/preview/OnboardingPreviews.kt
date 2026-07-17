package com.robotopia.androidstudiolite.feature.onboarding.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.onboarding.presentation.intro.OnboardingIntroContent
import com.robotopia.androidstudiolite.feature.onboarding.presentation.welcome.OnboardingWelcomeContent

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
