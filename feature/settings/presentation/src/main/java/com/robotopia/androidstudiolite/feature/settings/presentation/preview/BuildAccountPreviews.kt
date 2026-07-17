package com.robotopia.androidstudiolite.feature.settings.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.auth.model.AuthAccount

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
