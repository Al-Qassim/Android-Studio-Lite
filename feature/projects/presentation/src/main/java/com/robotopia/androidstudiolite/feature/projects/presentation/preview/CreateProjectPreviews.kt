package com.robotopia.androidstudiolite.feature.projects.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.robotopia.androidstudiolite.feature.projects.presentation.create.CreateProjectContent
import com.robotopia.androidstudiolite.feature.projects.presentation.create.CreateProjectUiState

internal data class CreateProjectPreviewCase(
    private val label: String,
    val state: CreateProjectUiState,
) {
    override fun toString(): String = label
}

internal class CreateProjectPreviewProvider : PreviewParameterProvider<CreateProjectPreviewCase> {
    override fun getDisplayName(index: Int): String = values.toList()[index].toString()

    override val values = sequenceOf(
        CreateProjectPreviewCase("empty", CreateProjectUiState()),
        CreateProjectPreviewCase(
            "filled",
            CreateProjectUiState(
                name = "MyApp",
                packageName = "com.example.myapp",
                minSdk = "26",
            ),
        ),
        CreateProjectPreviewCase(
            "field errors",
            CreateProjectUiState(
                name = "",
                packageName = "BadPackage",
                minSdk = "10",
                nameError = "App name is required",
                packageError = "Use a valid Java package (e.g. com.example.app)",
                minSdkError = "Min SDK must be between 21 and 35",
            ),
        ),
        CreateProjectPreviewCase(
            "form error",
            CreateProjectUiState(
                name = "MyApp",
                packageName = "com.example.myapp",
                minSdk = "26",
                formError = "Something went wrong",
            ),
        ),
        CreateProjectPreviewCase(
            "creating",
            CreateProjectUiState(
                name = "MyApp",
                packageName = "com.example.myapp",
                minSdk = "26",
                isCreating = true,
            ),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF2B2D30, widthDp = 360, heightDp = 640)
@Composable
private fun CreateProjectPreview(
    @PreviewParameter(CreateProjectPreviewProvider::class) preview: CreateProjectPreviewCase,
) {
    CreateProjectContent(
        state = preview.state,
        onCancel = {},
        onNameChange = {},
        onPackageNameChange = {},
        onMinSdkChange = {},
        onCreateClick = {},
    )
}
