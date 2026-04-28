package com.example.composabletasks

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                MainDrawerScreen(
                    viewModel = viewModel,
                    onLogout = {
                        viewModel.logout()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    },
                    onNewTask = {
                        //startActivity(Intent(this, TaskFormActivity::class.java))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawerScreen(
    viewModel: MainViewModel,
    onLogout: () -> Unit,
    onNewTask: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val userName by viewModel.name.observeAsState("Usuário")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Olá, $userName", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                // Rotas do menu
                NavigationDrawerItem(
                    label = { Text("Todas as Tarefas") },
                    selected = false, // Aqui entrará a lógica de rota ativa futuramente
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Próximas Tarefas") },
                    selected = false,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Sair") },
                    selected = false,
                    onClick = { onLogout() }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Tarefas") },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onNewTask) {
                    Icon(Icons.Default.Add, contentDescription = "Nova Tarefa")
                }
            }
        ) { paddingValues ->
            // O conteúdo da tela principal (seu NavHost ou a AllTasksScreen) vai aqui.
            // Temporariamente, renderizamos uma tela vazia apenas com o padding do Scaffold.
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                Text("Conteúdo da tela aqui", modifier = Modifier.padding(16.dp))
            }
        }
    }
}
