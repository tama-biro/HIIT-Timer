package com.example.hiittimer.landingpage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Generic number picker composable with increment and decrement buttons.
 */
@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    min: Int = 0,
    max: Int = 59
) {
    var textValue by remember { mutableStateOf(value.toString()) }

    LaunchedEffect(value) {
        if (value.toString() != textValue) {
            textValue = value.toString()
        }
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = {
                if (value > min) onValueChange(value - 1)
            },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Remove, contentDescription = "Decrement")
        }

        val textBoxColor = if (isSystemInDarkTheme()) Color(0xFF000000) else Color(0xFFe8e8e9)

        BasicTextField(
            value = textValue,
            onValueChange = { newText ->
                textValue = newText
                val newValue = newText.toIntOrNull()
                if (newValue != null) {
                    val clampedValue = newValue.coerceIn(min, max)
                    onValueChange(clampedValue)
                }
            },
            modifier = Modifier
                .width(48.dp)
                .background(color = textBoxColor)
                .padding(horizontal = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        )
        Text(text = label, fontSize = 16.sp, modifier = Modifier.padding(start = 1.dp))
        IconButton(
            onClick = {
                if (value < max) onValueChange(value + 1)
            },
            modifier = Modifier.size(36.dp),
            colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Increment")
        }
    }
}
