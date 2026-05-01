package com.example.composabletasks.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.composabletasks.view.components.CustomTextField
import com.example.composabletasks.view.components.PrimaryButton
import com.example.composabletasks.viewmodel.LoginViewModel
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.verifyAuthentication()

        setContent {
            // Aqui chamamos a estrutura da tela que criámos
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = { navigateToMain() },
                onRequireBiometrics = { showAuthentication() },
                onNavigateToRegister = {
                    startActivity(Intent(this, RegisterActivity::class.java))
                }
            )
        }
    }

    // LoginScreen.kt no pacote components (ou screens)
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

        // Observa o resultado do botão "LOGIN"
        LaunchedEffect(loginResult) {
            loginResult?.let {
                if (it.status()) onLoginSuccess()
                else Toast.makeText(context, it.message(), Toast.LENGTH_SHORT).show()
            }
        }

        // Observa a verificação automática de token (Auth Guard)
        LaunchedEffect(loggedUser) {
            if (loggedUser == true) {
                onRequireBiometrics()
            }
        }

        // Interface Visual (UI Completa)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E0B4B)) // Substitua pela cor exata do seu background
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Adicione o componente de imagem da sua Logo aqui

            Spacer(modifier = Modifier.height(64.dp))

            CustomTextField(
                value = viewModel.email,
                onValueChange = { viewModel.onEmailChange(it) },
                label = "E-mail",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(
                value = viewModel.password,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = "Senha",
                isPassword = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(64.dp))

            PrimaryButton(
                text = "LOGIN",
                onClick = { viewModel.login(viewModel.email, viewModel.password) }
            )

            Spacer(modifier = Modifier.weight(1f))

            // Rodapé de Cadastro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Não tem uma conta? ")
                Text(
                    text = "Cadastre-se",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showAuthentication() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(
                this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)

                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        val debug = ""
                    }
                })

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Título")
            .setSubtitle("Subtítulo")
            .setDescription("Descrição")
            .setNegativeButtonText("Cancelar")
            .build()

        biometricPrompt.authenticate(info)
    }
}
