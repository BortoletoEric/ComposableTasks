package com.example.composabletasks.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.composabletasks.service.model.TaskModel
import com.example.composabletasks.ui.components.TaskItem
import com.example.composabletasks.viewmodel.TaskListViewModel

// 1. Função Stateless (Totalmente visual, não conhece ViewModel nem Banco de Dados)
@Composable
fun TaskListContent(
    tasks: List<TaskModel>,
    onTaskClick: (Int) -> Unit,
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
                onToggleStatus = onToggleStatus
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

    // Obtém o ciclo de vida da Activity atual (MainActivity)
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

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
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskClick = onTaskClick,
                onToggleStatus = { id ->
                    viewModel.status(id, !task.complete)
                }
            )
        }
    }
}

// 3. O Preview
@Preview(showBackground = true, showSystemUi = true)
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
        onToggleStatus = {}
    )
}