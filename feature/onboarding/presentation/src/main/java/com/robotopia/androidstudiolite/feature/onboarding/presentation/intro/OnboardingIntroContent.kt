package com.robotopia.androidstudiolite.feature.onboarding.presentation.intro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Theme
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.icon.IconCloud
import com.robotopia.androidstudiolite.feature.onboarding.presentation.OnboardingAppTitleBar
import com.robotopia.androidstudiolite.feature.onboarding.presentation.OnboardingCenteredMessage

@Composable
internal fun OnboardingIntroContent(
    providerDisplayName: String,
    onConnectClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    IslandScaffold(
        topBar = { OnboardingAppTitleBar() },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    label = "Skip for now",
                    onClick = onSkipClick,
                    variant = ButtonVariant.Secondary,
                )
                Button(
                    label = "Connect $providerDisplayName",
                    onClick = onConnectClick,
                    variant = ButtonVariant.Primary,
                )
            }
        },
    ) {
        OnboardingCenteredMessage(
            illustration = {
                IconCloud(tint = Theme.colors.Primary, size = 40.dp)
            },
            title = "Connect $providerDisplayName for cloud builds.",
            body = "Optional — you can skip and connect later in Settings.",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}
