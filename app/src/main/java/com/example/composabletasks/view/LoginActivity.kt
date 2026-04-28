package com.example.composabletasks.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.composabletasks.MainActivity
import com.example.composabletasks.R
import com.example.composabletasks.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.verifyAuthentication()

        setContent {
            MaterialTheme {
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToMain = {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    },
                    onNavigateToRegister = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                    },
                    onShowBiometrics = { showAuthentication() }
                )
            }
        }
    }

    private fun showAuthentication() {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Autenticação")
            .setNegativeButtonText("Cancelar")
            .build()
        biometricPrompt.authenticate(info)
    }
}

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToMain: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onShowBiometrics: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    val loginResult by viewModel.login.observeAsState()
    val loggedUser by viewModel.loggedUser.observeAsState(false)

    // Side Effects
    LaunchedEffect(loginResult) {
        loginResult?.let {
            if (it.status()) onNavigateToMain()
            else Toast.makeText(context, it.message(), Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(loggedUser) {
        if (loggedUser) onShowBiometrics()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2C2C2C)) // Substitua pela sua cor de fundo
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Substitui o ImageView
        Image(
            painter = painterResource(id = R.drawable.ic_logotipo),
            contentDescription = "Logo",
            modifier = Modifier.width(200.dp).padding(bottom = 64.dp)
        )

        // Substitui o EditEmail
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White, focusedTextColor = Color.White
            )
        )

        // Substitui o EditPassword
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(bottom = 64.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = Color.White, focusedTextColor = Color.White
            )
        )

        // Substitui o Button
        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Text(stringResource(R.string.button_login))
        }

        // Substitui a base do text_new_account e text_register
        Row {
            Text("Não tem conta? ", color = Color.White)
            Text(
                text = "Cadastre-se",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }
    }
}