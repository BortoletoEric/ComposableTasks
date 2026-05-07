package com.example.composabletasks.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composabletasks.R
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.viewmodel.MainViewModel
import com.example.composabletasks.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    taskListViewModel: TaskListViewModel,
    onLogout: () -> Unit,
    onNavigateToTaskForm: (Int) -> Unit // Callback de navegação
) {
    val userName by mainViewModel.name.observeAsState("")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Estado local para controlar qual filtro está ativo no momento
    var currentFilter by remember { mutableIntStateOf(TaskConstants.FILTER.ALL) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val drawerItemColors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFF424242), // Cor do fundo cinza quando selecionado
                unselectedContainerColor = Color.Transparent, // Fundo transparente quando não selecionado
                selectedTextColor = Color(0xFFBB86FC), // Cor do texto roxo quando selecionado (ajuste o HEX exato se precisar)
                selectedIconColor = Color(0xFFBB86FC), // Cor do ícone roxo quando selecionado
                unselectedTextColor = Color.White, // Branco quando não selecionado
                unselectedIconColor = Color.White // Branco quando não selecionado
            )
            ModalDrawerSheet(drawerContainerColor = Color(0xFF303030)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush
                                .linearGradient(
                                    colors = listOf(
                                        Color.Black,
                                        Color(0xFF6200EE),
                                    ),
                                    start = Offset.Zero,
                                    end = Offset.Infinite
                                )
                        )
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logotipo), // O logotipo do seu XML
                        contentDescription = "Logotipo",
                        modifier = Modifier.height(40.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Olá, $userName",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Icon de evento") },
                    label = {
                        Text(text = "Todas as tarefas")
                    },
                    selected = currentFilter == TaskConstants.FILTER.ALL,
                    onClick = {
                        currentFilter = TaskConstants.FILTER.ALL //
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Icon de evento") },
                    label = {
                        Text(text = "Próximos 7 dias")
                    },
                    selected = currentFilter == TaskConstants.FILTER.NEXT,
                    onClick = {
                        currentFilter = TaskConstants.FILTER.NEXT //[cite: 9]
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AlarmOff, contentDescription = "Icon de evento") },
                    label = {
                        Text("Expiradas")
                    },
                    selected = currentFilter == TaskConstants.FILTER.EXPIRED,
                    onClick = {
                        currentFilter =
                            TaskConstants.FILTER.EXPIRED // Note: use a constante correta do seu else no VM[cite: 9]
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Icon de evento"
                        )
                    },
                    label = {
                        Text("Sair")
                    },
                    selected = false,
                    onClick = {
                        onLogout()
                        scope.launch { drawerState.close() }
                    },
                    colors = drawerItemColors
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            when (currentFilter) {
                                TaskConstants.FILTER.NEXT -> "Próximos 7 dias"
                                TaskConstants.FILTER.ALL -> "Todas as tarefas"
                                else -> "Expiradas"
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = null)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    onNavigateToTaskForm(0)
                }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        ) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                // A TaskListScreen agora recebe o filtro dinâmico
                TaskListScreen(
                    viewModel = taskListViewModel,
                    filter = currentFilter,
                    onTaskClick = { taskId ->
                        onNavigateToTaskForm(taskId) // Repassa o ID da tarefa clicada
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ModalDrawerSheetOpenedPreview() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            val drawerItemColors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = Color(0xFF424242), // Cor do fundo cinza quando selecionado
                unselectedContainerColor = Color.Transparent, // Fundo transparente quando não selecionado
                selectedTextColor = Color(0xFFBB86FC), // Cor do texto roxo quando selecionado (ajuste o HEX exato se precisar)
                selectedIconColor = Color(0xFFBB86FC), // Cor do ícone roxo quando selecionado
                unselectedTextColor = Color.White, // Branco quando não selecionado
                unselectedIconColor = Color.White // Branco quando não selecionado
            )
            ModalDrawerSheet(drawerContainerColor = Color(0xFF303030)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush
                                .linearGradient(
                                    colors = listOf(
                                        // purple_500 do seu XML
                                        Color.Black,
                                        Color(0xFF6200EE),
                                    ),
                                    start = Offset.Zero,
                                    end = Offset.Infinite
                                )
                        )
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logotipo), // O logotipo do seu XML
                        contentDescription = "Logotipo",
                        modifier = Modifier.height(40.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Olá, Usuário", // Nome fixo para bater com a sua imagem de exemplo
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Menu, contentDescription = "Icon de evento") },
                    label = { Text(text = "Todas as tarefas") },
                    selected = true,
                    onClick = {},
                    colors = drawerItemColors
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Event, contentDescription = "Icon de evento") },
                    label = { Text(text = "Próximos 7 dias") },
                    selected = false,
                    onClick = {},
                    colors = drawerItemColors
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.AlarmOff, contentDescription = "Icon de evento") },
                    label = { Text("Expiradas") },
                    selected = false,
                    onClick = {},
                    colors = drawerItemColors
                )
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Icon de evento"
                        )
                    },
                    label = { Text("Sair") },
                    selected = false,
                    onClick = {},
                    colors = drawerItemColors
                )
            }
        }
    ) {}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ModalDrawerSheetClosedPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Todas as tarefas"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* Mock handler for drawer icon click */ }) {
                        Icon(Icons.Default.Menu, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Mock handler for FAB click */ }) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            // A TaskListScreen agora recebe o filtro dinâmico
            TaskListScreenPreview()
        }
    }
}