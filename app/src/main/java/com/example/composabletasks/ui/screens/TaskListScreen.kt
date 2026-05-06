package com.example.composabletasks.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.composabletasks.R
import com.example.composabletasks.service.model.TaskModel
import com.example.composabletasks.ui.components.TaskItem
import com.example.composabletasks.viewmodel.TaskListViewModel
import kotlinx.coroutines.launch

// 1. Função Stateless (Totalmente visual, não conhece ViewModel nem Banco de Dados)
@Composable
fun TaskListContent(
    tasks: List<TaskModel>,
    onTaskClick: (Int) -> Unit,
    onTaskLongClick: (Int) -> Unit,
    onToggleStatus: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskClick = onTaskClick,
                onToggleStatus = onToggleStatus,
                onTaskLongClick = { onTaskLongClick(task.id) }
            )
        }
    }
}

// 2. Função Stateful (Injeta o ViewModel e passa os estados para o Content)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    filter: Int,
    onTaskClick: (Int) -> Unit
) {
    val tasks by viewModel.tasks.observeAsState(emptyList())
    val scope = rememberCoroutineScope() // Necessário para resetar o swipe

    // Estados para o diálogo
    var showDialog by remember { mutableStateOf(false) }
    var taskToDelete by remember { mutableStateOf<TaskModel?>(null) }

    // Guardamos o estado do swipe que precisa ser resetado caso o usuário cancele
    var swipeStateToReset by remember { mutableStateOf<SwipeToDismissBoxState?>(null) }

    // DIÁLOGO DE REMOÇÃO
    if (showDialog && taskToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                scope.launch { swipeStateToReset?.reset() }
                showDialog = false
            },
            title = { Text(text = stringResource(id = R.string.title_task_removal)) },
            text = { Text(text = stringResource(id = R.string.label_remove_task)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(taskToDelete!!.id)
                    showDialog = false
                    taskToDelete = null
                }) {
                    Text(text = stringResource(id = R.string.button_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    // O PULO DO GATO: Resetar o swipe via coroutine
                    scope.launch {
                        swipeStateToReset?.reset()
                        swipeStateToReset = null
                    }
                    showDialog = false
                    taskToDelete = null
                }) {
                    Text(text = stringResource(id = R.string.button_cancel))
                }
            }
        )
    }

    // Obtém o ciclo de vida da Activity atual (MainActivity)
    val lifecycleOwner = LocalLifecycleOwner.current

    // Ouve mudanças no ciclo de vida ou no filtro
    DisposableEffect(lifecycleOwner, filter) {
        val observer = LifecycleEventObserver { _, event ->
            // Dispara a chamada na API quando a tela volta a ficar visível
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.list(filter)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tasks, key = { it.id }) { task ->

            val dismissState = rememberSwipeToDismissBoxState()

            if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
                taskToDelete = task
                swipeStateToReset = dismissState
                showDialog = true
            }

            SwipeToDismissBox(
                state = dismissState,
                enableDismissFromStartToEnd = false,
                backgroundContent = {
                    // Usamos targetValue para uma transição de cor mais suave
                    val color = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart)
                        Color.Red else Color.Transparent
                    Box(modifier = Modifier.fillMaxSize().background(color))
                }
            ) {
                TaskItem(
                    task = task,
                    onTaskClick = onTaskClick,
                    onToggleStatus = { id -> viewModel.status(id, !task.complete) },
                    onTaskLongClick = {
                        taskToDelete = task
                        showDialog = true
                        // Se quiser resetar o swipe ao abrir pelo clique longo:
                        swipeStateToReset = dismissState
                    }
                )
            }
        }
    }
    DisposableEffect(showDialog) {
        onDispose {
            if (!showDialog) {
                scope.launch {
                    swipeStateToReset?.reset()
                    swipeStateToReset = null
                }
            }
        }
    }
}

// 3. O Preview
@Preview(showBackground = true)
@Composable
fun TaskListScreenPreview() {
    // Mock de dados para o Android Studio renderizar a lista
    val mockTasks = listOf(
        TaskModel(
            id = 1,
            priorityId = 1,
            description = "Estudar Jetpack Compose",
            dueDate = "05/05/2026",
            complete = false,
            priorityDescription = "Alta"
        ),
        TaskModel(
            id = 2,
            priorityId = 2,
            description = "Revisar ACBrLib",
            dueDate = "10/05/2026",
            complete = true,
            priorityDescription = "Média"
        ),
        TaskModel(
            id = 3,
            priorityId = 3,
            description = "Treino de freestyle",
            dueDate = "12/05/2026",
            complete = false,
            priorityDescription = "Baixa"
        ),
        TaskModel(
            id = 4,
            priorityId = 1,
            description = "Sair com a Sabrina",
            dueDate = "09/05/2026",
            complete = false,
            priorityDescription = "Crítica"
        )
    )

    TaskListContent(
        tasks = mockTasks,
        onTaskClick = {},
        onToggleStatus = {},
        onTaskLongClick = {}
    )
}