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
import com.robotopia.androidstudiolite.designsystem.color.Colors
import com.robotopia.androidstudiolite.designsystem.typography.Typography

enum class TextFieldVariant {
    Form,
    Dialog,
}

@Composable
fun TextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    variant: TextFieldVariant = TextFieldVariant.Form,
    isError: Boolean = false,
    singleLine: Boolean = true,
) {
    val height = if (variant == TextFieldVariant.Form) 40.dp else 36.dp
    val shape = RoundedCornerShape(8.dp)
    val borderColor = when {
        isError -> Colors.Danger
        variant == TextFieldVariant.Form -> Colors.Border
        else -> null
    }
    val background = when (variant) {
        TextFieldVariant.Form -> Colors.Input
        TextFieldVariant.Dialog -> Colors.Surface2
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
                    style = Typography.Body.copy(color = Colors.Muted),
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = Typography.Body.copy(color = Colors.Text),
                cursorBrush = SolidColor(Colors.Primary),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (isError) {
            BasicText(
                text = "Invalid value",
                style = Typography.Caption.copy(color = Colors.Danger),
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12171C)
@Composable
private fun TextFieldPreview() {
    var form by remember { mutableStateOf("") }
    var dialog by remember { mutableStateOf("MainActivity.kt") }
    var error by remember { mutableStateOf("bad name") }
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TextField(
            value = form,
            onValueChange = { form = it },
            placeholder = "Project name",
            variant = TextFieldVariant.Form,
        )
        TextField(
            value = dialog,
            onValueChange = { dialog = it },
            variant = TextFieldVariant.Dialog,
        )
        TextField(
            value = error,
            onValueChange = { error = it },
            variant = TextFieldVariant.Form,
            isError = true,
        )
    }
}
