package com.robotopia.androidstudiolite.feature.onboarding.presentation.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
internal fun OnboardingIntroContent(
    providerDisplayName: String,
    onConnectClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Colors.Bg)
            .systemBarsPadding()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(
                text = "Connect $providerDisplayName to build",
                style = Typography.Headline.copy(color = Colors.Text),
            )
            BasicText(
                text = "Cloud builds use your $providerDisplayName account. You can skip and connect later in Settings.",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                label = "Connect $providerDisplayName",
                onClick = onConnectClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Primary,
            )
            Button(
                label = "Skip for now",
                onClick = onSkipClick,
                modifier = Modifier.fillMaxWidth(),
                variant = ButtonVariant.Secondary,
            )
        }
    }
}
