package com.example.composabletasks.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.example.composabletasks.R
import com.example.composabletasks.ui.components.CustomTextField
import com.example.composabletasks.ui.components.PrimaryButton
import com.example.composabletasks.ui.screens.RegisterScreen
import com.example.composabletasks.ui.themes.ComposableTasksTheme
import com.example.composabletasks.viewmodel.RegisterViewModel

class RegisterActivity : ComponentActivity() {

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposableTasksTheme {
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