package com.example.composabletasks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composabletasks.ui.theme.ComposableTasksTheme
import com.example.composabletasks.view.LoginActivity
import com.example.composabletasks.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.loadUserName()

        setContent {
            MaterialTheme {
                // Aplicamos o tema do seu app aqui se já tiver criado (ex: TasksTheme)
                MainScreen(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todas as tarefas") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Abrir Drawer */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Navegar para TaskFormActivity */ },
                containerColor = Color(0xFF03DAC5) // Cor aproximada do seu FAB
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar", tint = Color.White)
            }
        }
    ) { paddingValues ->
        // O conteúdo principal (lista de tarefas) ficará aqui dentro
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            Text("As tarefas aparecerão aqui em breve!", modifier = Modifier.align(Alignment.Center))
        }
    }
}