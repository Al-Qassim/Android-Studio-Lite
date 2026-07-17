package com.robotopia.androidstudiolite.designsystem.preview.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.component.Button
import com.robotopia.androidstudiolite.designsystem.component.ButtonVariant
import com.robotopia.androidstudiolite.designsystem.component.InfoCard
import com.robotopia.androidstudiolite.designsystem.component.IslandScaffold
import com.robotopia.androidstudiolite.designsystem.component.LoadingIndicator
import com.robotopia.androidstudiolite.designsystem.component.TopBarBackTitle
import com.robotopia.androidstudiolite.designsystem.component.UserCodeRow
import com.robotopia.androidstudiolite.designsystem.icon.IconSuccess
import com.robotopia.androidstudiolite.designsystem.preview.ExamplePreviewBackground
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class AuthExampleCase {
    Preparing,
    ShowCode,
    Connected,
    Failed,
}

private class AuthExampleCaseProvider : PreviewParameterProvider<AuthExampleCase> {
    override val values = AuthExampleCase.entries.asSequence()
    override fun getDisplayName(index: Int): String = values.elementAt(index).name
}

@Preview(
    name = "Auth",
    showBackground = true,
    backgroundColor = ExamplePreviewBackground,
    widthDp = 360,
    heightDp = 640,
)
@Composable
fun AuthExamplePreview(
    @PreviewParameter(AuthExampleCaseProvider::class) case: AuthExampleCase,
) {
    when (case) {
        AuthExampleCase.Preparing -> AuthPreparingScreen()
        AuthExampleCase.ShowCode -> AuthShowCodeScreen()
        AuthExampleCase.Connected -> AuthConnectedScreen()
        AuthExampleCase.Failed -> AuthFailedScreen()
    }
}

@Composable
private fun AuthPreparingScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Connect GitHub") },
    ) {
        Box(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            LoadingIndicator(label = "Preparing…")
        }
    }
}

@Composable
private fun AuthShowCodeScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Connect GitHub") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Open GitHub",
                    onClick = {},
                    variant = ButtonVariant.Primary,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(
                text = "Copy the code below",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            UserCodeRow(code = "ABCD-1234")
            BasicText(
                text = "Paste it at github.com/login/device",
                style = Typography.Body.copy(color = Colors.Muted),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AuthConnectedScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Connect GitHub") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.End,
            ) {
                Button(
                    label = "Continue",
                    onClick = {},
                    variant = ButtonVariant.Primary,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconSuccess(tint = Colors.Primary, size = 48.dp)
            BasicText(
                text = "Connected",
                style = Typography.Headline.copy(color = Colors.Text),
            )
            InfoCard(
                label = "GitHub",
                title = "@alex-dev",
            )
        }
    }
}

@Composable
private fun AuthFailedScreen() {
    IslandScaffold(
        topBar = { TopBarBackTitle(title = "Connect GitHub") },
        footer = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                Button(
                    label = "Cancel",
                    onClick = {},
                    variant = ButtonVariant.Secondary,
                )
                Button(
                    label = "Try again",
                    onClick = {},
                    variant = ButtonVariant.Primary,
                )
            }
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            BasicText(
                text = "Couldn't connect",
                style = Typography.Headline.copy(color = Colors.Danger),
            )
            BasicText(
                text = "The code expired or was declined. Start again to get a new code.",
                style = Typography.Body.copy(color = Colors.Muted),
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}
