package com.example.composabletasks.view

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.composabletasks.R
import com.example.composabletasks.service.model.TaskModel
import com.example.composabletasks.viewmodel.TaskListViewModel

@Composable
fun AllTasksScreen(
    viewModel: TaskListViewModel,
    taskFilter: Int,
    onTaskClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observação de Estado
    val tasks by viewModel.tasks.observeAsState(initial = emptyList<TaskModel>())
    val taskDeleted by viewModel.taskDeleted.observeAsState()
    val taskStatus by viewModel.taskStatus.observeAsState()

    // Estado local para controle do Dialog de exclusão
    var taskIdToDelete by remember { mutableStateOf<Int?>(null) }

    // Gerenciamento de Ciclo de Vida
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.list(taskFilter)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Side Effects
    LaunchedEffect(taskDeleted) {
        taskDeleted?.let { validation ->
            val msg =
                if (validation.status()) context.getString(R.string.msg_task_removed) else validation.message()
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(taskStatus) {
        taskStatus?.let { validation ->
            if (!validation.status()) {
                Toast.makeText(context, validation.message(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Dialog de confirmação
    if (taskIdToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskIdToDelete = null },
            title = { Text(stringResource(R.string.title_task_removal)) },
            text = { Text(stringResource(R.string.label_remove_task)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.delete(taskIdToDelete!!)
                    taskIdToDelete = null
                }) {
                    Text(stringResource(R.string.button_yes))
                }
            },
            dismissButton = {
                TextButton(onClick = { taskIdToDelete = null }) {
                    Text(stringResource(R.string.button_cancel))
                }
            }
        )
    }

    // Lista de Tarefas
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(tasks, key = { it.id }) { task ->
            TaskItemRow(
                task = task,
                onClick = { onTaskClick(task.id) },
                onDeleteClick = { taskIdToDelete = task.id },
                onCompleteClick = { viewModel.status(task.id, true) },
                onUndoClick = { viewModel.status(task.id, false) }
            )
        }
    }
}

@Composable
fun TaskItemRow(
    task: TaskModel,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onUndoClick: () -> Unit
) {
    // Implementação básica para evitar o TODO e permitir compilação/teste
    Text(text = task.description)
}
