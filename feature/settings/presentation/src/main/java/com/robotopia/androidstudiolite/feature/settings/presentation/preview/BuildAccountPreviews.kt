package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount
import com.robotopia.androidstudiolite.feature.settings.presentation.account.BuildAccountContent

internal data class BuildAccountPreviewCase(
    private val label: String,
    val account: AuthAccount?,
    val providerDisplayName: String = "GitHub",
) {
    override fun toString(): String = label
}

internal class BuildAccountPreviewProvider : PreviewParameterProvider<BuildAccountPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        BuildAccountPreviewCase("logged out", account = null),
        BuildAccountPreviewCase(
            "connected",
            account = AuthAccount(providerName = "GitHub", identity = "@alex-dev"),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun BuildAccountPreview(
    @PreviewParameter(BuildAccountPreviewProvider::class) preview: BuildAccountPreviewCase,
) {
    BuildAccountContent(
        account = preview.account,
        providerDisplayName = preview.providerDisplayName,
        onBackClick = {},
        onConnectClick = {},
        onLogOutClick = {},
    )
}
