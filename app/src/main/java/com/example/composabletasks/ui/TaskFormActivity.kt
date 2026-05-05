package com.example.composabletasks.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.ui.screens.TaskFormScreen
import com.example.composabletasks.ui.themes.ComposableTasksTheme
import com.example.composabletasks.viewmodel.TaskFormViewModel

class TaskFormActivity : AppCompatActivity() {

    private val viewModel: TaskFormViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Resgata o ID enviado pela MainActivity
        val taskId = intent.getIntExtra(TaskConstants.BUNDLE.TASKID, 0)
        if (taskId != 0) {
            viewModel.load(taskId)
        }

        setContent {
            ComposableTasksTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text(if (taskId == 0) "Nova Tarefa" else "Editar Tarefa") })
                    }
                ) { paddingValues ->
                    Box(modifier = Modifier.padding(paddingValues)) {
                        TaskFormScreen(
                            viewModel = viewModel,
                            onSaveSuccess = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}