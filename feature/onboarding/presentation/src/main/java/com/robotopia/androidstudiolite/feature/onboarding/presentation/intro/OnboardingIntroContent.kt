package com.robotopia.androidstudiolite.feature.onboarding.presentation.intro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.typography.Typography

@Composable
internal fun OnboardingIntroContent(
    providerDisplayName: String,
    onConnectClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    IslandScaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.Transparent)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BasicText(
                    text = "Android Studio Lite",
                    style = Typography.TitleNav.copy(color = Colors.Text),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp),
                )
            }
        },
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            BasicText(
                text = "Connect $providerDisplayName for cloud builds.",
                style = Typography.Headline.copy(color = Colors.Text),
            )
            BasicText(
                text = "Optional — you can skip and connect later in Settings.",
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
