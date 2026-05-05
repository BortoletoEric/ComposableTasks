package com.example.composabletasks.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    iconResId: Int,
    isPassword: Boolean = false
) {
    TextField( // Utiliza TextField em vez de OutlinedTextField
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = { // Substitui o android:drawableStart do XML[cite: 16]
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = Color.White // Substitui o android:drawableTint[cite: 16]
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,

            // Torna o fundo completamente transparente
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,

            // Cores da linha inferior (Indicator)
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.LightGray,

            // Cores do texto de dica (Label)
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.LightGray,

            cursorColor = Color.White
        )
    )
}

@Composable
fun BasicTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = modifier
    )
}