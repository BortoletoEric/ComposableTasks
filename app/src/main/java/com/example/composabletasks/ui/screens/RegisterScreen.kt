package com.example.composabletasks.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composabletasks.R
import com.example.composabletasks.ui.components.CustomTextField
import com.example.composabletasks.ui.components.PrimaryButton
import com.example.composabletasks.ui.themes.PersonalPurple
import com.example.composabletasks.viewmodel.RegisterViewModel

@Composable
fun RegisterContent(
    nameValue: String,
    onNameChange: (String) -> Unit,
    emailValue: String,
    onEmailChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
) {
    val gradientBackground = Brush.linearGradient(
        colors = listOf(PersonalPurple, Color.Black),
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset.Infinite,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        CustomTextField(
            value = nameValue,
            onValueChange = onNameChange,
            label = "Nome",
            iconResId = R.drawable.ic_name,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = emailValue,
            onValueChange = onEmailChange,
            label = "E-mail",
            iconResId = R.drawable.ic_email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = passwordValue,
            onValueChange = onPasswordChange,
            label = "Senha",
            isPassword = true,
            iconResId = R.drawable.ic_password,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "CADASTRAR",
            onClick = { /* Lógica de cadastro */ },
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit
) {
    val context = LocalContext.current
    val registerResult by viewModel.createUser.observeAsState() // Observa a criação[cite: 6]

    LaunchedEffect(registerResult) {
        registerResult?.let {
            if (it.status()) {
                onRegisterSuccess()
            } else {
                Toast.makeText(context, it.message(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    val gradientBackground = Brush.linearGradient(
        colors = listOf(PersonalPurple, Color.Black),
        start = androidx.compose.ui.geometry.Offset.Zero,
        end = androidx.compose.ui.geometry.Offset.Infinite,
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBackground) // Mesma cor do login
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        CustomTextField(
            value = viewModel.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = "Nome",
            iconResId = R.drawable.ic_name,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = viewModel.email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = "E-mail",
            iconResId = R.drawable.ic_email,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = viewModel.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = "Senha",
            isPassword = true,
            iconResId = R.drawable.ic_password,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para baixo

        PrimaryButton(
            text = "CADASTRAR", //
            onClick = {
                viewModel.create(viewModel.name, viewModel.email, viewModel.password)
            },
            modifier = Modifier.padding(bottom = 32.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun RegisterScreenPreview() {
    RegisterContent(
        nameValue = "Paulo",
        onNameChange = {},
        emailValue = "paulopostal@gmail.com",
        onEmailChange = {},
        passwordValue = "",
        onPasswordChange = {}
    )
}