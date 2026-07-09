package com.robotopia.androidstudiolite.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.robotopia.androidstudiolite.designsystem.color.AslColors
import com.robotopia.androidstudiolite.designsystem.typography.AslTypography

enum class AslTextFieldVariant {
    Form,
    Dialog,
}

@Composable
fun AslTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    variant: AslTextFieldVariant = AslTextFieldVariant.Form,
    isError: Boolean = false,
    singleLine: Boolean = true,
) {
    val height = if (variant == AslTextFieldVariant.Form) 40.dp else 36.dp
    val shape = RoundedCornerShape(8.dp)
    val borderColor = when {
        isError -> AslColors.Danger
        variant == AslTextFieldVariant.Form -> AslColors.Border
        else -> null
    }
    val background = when (variant) {
        AslTextFieldVariant.Form -> AslColors.Input
        AslTextFieldVariant.Dialog -> AslColors.Surface2
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(background, shape)
                .then(
                    if (borderColor != null) {
                        Modifier.border(1.dp, borderColor, shape)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (value.isEmpty() && placeholder.isNotEmpty()) {
                BasicText(
                    text = placeholder,
                    style = AslTypography.Body.copy(color = AslColors.Muted),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = AslTypography.Body.copy(color = AslColors.Text),
                cursorBrush = SolidColor(AslColors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (isError) {
            BasicText(
                text = "Invalid value",
                style = AslTypography.Caption.copy(color = AslColors.Danger),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun AslTextFieldPreview() {
    var form by remember { mutableStateOf("") }
    var dialog by remember { mutableStateOf("MainActivity.kt") }
    var error by remember { mutableStateOf("bad name") }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AslTextField(
            value = form,
            onValueChange = { form = it },
            placeholder = "Project name",
            variant = AslTextFieldVariant.Form,
        )
        AslTextField(
            value = dialog,
            onValueChange = { dialog = it },
            variant = AslTextFieldVariant.Dialog,
        )
        AslTextField(
            value = error,
            onValueChange = { error = it },
            variant = AslTextFieldVariant.Form,
            isError = true,
        )
    }
}
