package com.robotopia.androidstudiolite.feature.onboarding.presentation.welcome

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
import com.robotopia.androidstudiolite.designsystem.icon.IconApk
import com.robotopia.androidstudiolite.feature.onboarding.presentation.OnboardingAppTitleBar
import com.robotopia.androidstudiolite.feature.onboarding.presentation.OnboardingCenteredMessage

@Composable
internal fun OnboardingWelcomeContent(
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
                IconApk(tint = Theme.colors.Primary, size = 40.dp)
            },
            title = "Edit and build Android apps on your phone.",
            body = "Create a project, edit code, run a cloud build, and install the APK.",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        )
    }
}
