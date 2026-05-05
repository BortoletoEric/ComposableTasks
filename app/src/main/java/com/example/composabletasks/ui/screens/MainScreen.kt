package com.example.composabletasks.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.viewmodel.MainViewModel
import com.example.composabletasks.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    taskListViewModel: TaskListViewModel,
    onLogout: () -> Unit
) {
    val userName by mainViewModel.name.observeAsState("")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado local para controlar qual filtro está ativo no momento
    var currentFilter by remember { mutableIntStateOf(TaskConstants.FILTER.ALL) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = Color(0xFF303030)) {
                // ... (Header com userName) ...

                NavigationDrawerItem(
                    label = { Text("Todas as tarefas") },
                    selected = currentFilter == TaskConstants.FILTER.ALL,
                    onClick = {
                        currentFilter = TaskConstants.FILTER.ALL //
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Próximos 7 dias") },
                    selected = currentFilter == TaskConstants.FILTER.NEXT,
                    onClick = {
                        currentFilter = TaskConstants.FILTER.NEXT //[cite: 9]
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Expiradas") },
                    selected = currentFilter == TaskConstants.FILTER.EXPIRED,
                    onClick = {
                        currentFilter = 123 // Note: use a constante correta do seu else no VM[cite: 9]
                        scope.launch { drawerState.close() }
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {
                        onLogout()
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(when(currentFilter) {
                            TaskConstants.FILTER.NEXT -> "Próximos 7 dias"
                            TaskConstants.FILTER.ALL -> "Todas as tarefas"
                            else -> "Expiradas"
                        })
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = { /* Navegar para TaskFormActivity */ }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // A TaskListScreen agora recebe o filtro dinâmico
                TaskListScreen(
                    viewModel = taskListViewModel,
                    filter = currentFilter,
                    onTaskClick = { taskId -> /* Abrir edição */ }
                )
            }
        }
    }
}