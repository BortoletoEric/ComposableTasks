package com.example.composabletasks.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.composabletasks.view.components.CustomTextField
import com.example.composabletasks.view.components.PrimaryButton
import com.example.composabletasks.viewmodel.RegisterViewModel

class RegisterActivity : AppCompatActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                RegisterScreen(
                    viewModel = viewModel,
                    onRegisterSuccess = {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E0B4B)) // Mesma cor do login
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        CustomTextField(
            value = viewModel.name,
            onValueChange = { viewModel.onNameChange(it) },
            label = "Nome",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

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