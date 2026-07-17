package com.robotopia.androidstudiolite.feature.buildapk.presentation.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

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
