package com.example.composabletasks.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composabletasks.R
import com.example.composabletasks.ui.components.CustomTextField
import com.example.composabletasks.ui.components.PrimaryButton
import com.example.composabletasks.ui.themes.PersonalPurple
import com.example.composabletasks.viewmodel.LoginViewModel

// 1. Função Stateless (Totalmente visual, sem ViewModel. Recebe apenas dados e eventos)
@Composable
fun LoginContent(
    emailValue: String,
    onEmailChange: (String) -> Unit,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
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

        // Coloque seu Image Logo aqui

        Spacer(modifier = Modifier.height(64.dp))

        CustomTextField(
            value = emailValue,
            onValueChange = onEmailChange,
            label = "E-mail",
            iconResId = R.drawable.ic_email,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = passwordValue,
            onValueChange = onPasswordChange,
            label = "Senha",
            iconResId = R.drawable.ic_password,
            isPassword = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(64.dp))

        PrimaryButton(
            text = "LOGIN",
            onClick = onLoginClick
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Não tem uma conta? ", color = Color.White)
            Text(
                text = "Cadastre-se",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}

// 2. Função Stateful (Gere o ViewModel, os efeitos e chama o conteúdo visual)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRequireBiometrics: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current
    val loginResult by viewModel.login.observeAsState()
    val loggedUser by viewModel.loggedUser.observeAsState()

    LaunchedEffect(loginResult) {
        loginResult?.let {
            if (it.status()) onLoginSuccess()
            else Toast.makeText(context, it.message(), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loggedUser) {
        if (loggedUser == true) onRequireBiometrics()
    }

    // Chama a interface burra injetando os estados do ViewModel
    LoginContent(
        emailValue = viewModel.email,
        onEmailChange = { viewModel.onEmailChange(it) },
        passwordValue = viewModel.password,
        onPasswordChange = { viewModel.onPasswordChange(it) },
        onLoginClick = { viewModel.login(viewModel.email, viewModel.password) },
        onNavigateToRegister = onNavigateToRegister
    )
}

// showSystemUi = true desenha a barra de status e navegação para maior fidelidade
// device permite simular tamanhos de tela específicos
@Preview(showBackground = true, showSystemUi = true, device = "id:pixel_5")
@Composable
fun LoginScreenPreview() {
    // Aplique seu tema (Theme) aqui se tiver um configurado, ex: TasksTheme { ... }
    LoginContent(
        emailValue = "usuario@teste.com",
        onEmailChange = {},
        passwordValue = "123456",
        onPasswordChange = {},
        onLoginClick = {},
        onNavigateToRegister = {}
    )
}