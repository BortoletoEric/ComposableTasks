package com.example.composabletasks.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.composabletasks.ui.components.TaskItem
import com.example.composabletasks.viewmodel.TaskListViewModel

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    filter: Int,
    onTaskClick: (Int) -> Unit
) {
    // 1. Observa a lista de tarefas real vinda do repositório
    val tasks by viewModel.tasks.observeAsState(emptyList())
    val context = LocalContext.current

    // 2. Dispara a busca sempre que o filtro (All, Next, Overdue) mudar
    LaunchedEffect(filter) {
        viewModel.list(filter)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tasks) { task ->
            TaskItem(
                task = task,
                onTaskClick = onTaskClick,
                onToggleStatus = { id ->
                    // Inverte o status atual usando a função status() do seu VM
                    viewModel.status(id, !task.complete)
                }
            )
        }
    }
}