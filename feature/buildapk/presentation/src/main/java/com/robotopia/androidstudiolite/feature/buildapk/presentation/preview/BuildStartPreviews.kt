package com.robotopia.androidstudiolite.feature.buildapk.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.buildapk.presentation.start.BuildStartContent

internal data class BuildStartPreviewCase(
    private val label: String,
    val projectName: String = "HelloCompose",
    val packageName: String = "com.example.hellocompose",
    val starting: Boolean = false,
    val signedIn: Boolean,
    val providerDisplayName: String = "GitHub",
) {
    override fun toString(): String = label
}

internal class BuildStartPreviewProvider : PreviewParameterProvider<BuildStartPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        BuildStartPreviewCase("signed in", signedIn = true),
        BuildStartPreviewCase("needs auth", signedIn = false),
        BuildStartPreviewCase("starting", signedIn = true, starting = true),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun BuildStartPreview(
    @PreviewParameter(BuildStartPreviewProvider::class) preview: BuildStartPreviewCase,
) {
    BuildStartContent(
        projectName = preview.projectName,
        packageName = preview.packageName,
        starting = preview.starting,
        signedIn = preview.signedIn,
        providerDisplayName = preview.providerDisplayName,
        onBackClick = {},
        onStartBuildClick = {},
        onConnectAccountClick = {},
        onHistoryClick = {},
    )
}
