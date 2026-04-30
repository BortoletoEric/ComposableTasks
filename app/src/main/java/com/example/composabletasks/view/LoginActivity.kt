package com.example.composabletasks.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.composabletasks.viewmodel.LoginViewModel
import java.util.concurrent.Executor

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.verifyAuthentication()

// Removemos o ViewBinding e usamos o setContent
        setContent {
            // Aqui chamamos a estrutura da tela que criámos
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    // Lógica de navegação que estava no teu observador
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                onRequireBiometrics = {
                    // Chamamos a tua função de biometria original
                    showAuthentication()
                }
            )
        }
    }

    @Composable
    fun LoginScreen(
        viewModel: LoginViewModel,
        onLoginSuccess: () -> Unit,
        onRequireBiometrics: () -> Unit // Novo callback
    ) {
        val context = LocalContext.current
        val loginResult by viewModel.login.observeAsState()
        val loggedUser by viewModel.loggedUser.observeAsState()

        // Efeito 1: Resultado do clique no botão Login
        LaunchedEffect(loginResult) {
            loginResult?.let {
                if (it.status()) {
                    onLoginSuccess()
                } else {
                    Toast.makeText(context, it.message(), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Efeito 2: Verificação de usuário já logado na abertura do app
        LaunchedEffect(loggedUser) {
            if (loggedUser == true) {
                onRequireBiometrics()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Logo
            Spacer(modifier = Modifier.height(64.dp))

            // CustomTextField (E-mail)
            Spacer(modifier = Modifier.height(16.dp))

            // CustomTextField (Senha)
            Spacer(modifier = Modifier.height(64.dp))

            // PrimaryButton (Login)

            // Empurra o rodapé para o final da tela
            Spacer(modifier = Modifier.weight(1f))

            // Rodapé com elementos lado a lado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center // Centraliza os textos na linha
            ) {
                Text(text = "Não tem uma conta? ")
                Text(
                    text = "Cadastre-se",
                    modifier = Modifier.clickable {
                        // Ação para ir para a tela de registro
                    }
                )
            }
        }
    }
    private fun showAuthentication() {
        val executor: Executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt =
            BiometricPrompt(this, executor,
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
