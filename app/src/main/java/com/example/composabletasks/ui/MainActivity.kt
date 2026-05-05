package com.example.composabletasks.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.composabletasks.service.constants.TaskConstants
import com.example.composabletasks.ui.screens.MainScreen
import com.example.composabletasks.ui.themes.ComposableTasksTheme
import com.example.composabletasks.viewmodel.MainViewModel
import com.example.composabletasks.viewmodel.TaskListViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()
    private val taskListViewModel: TaskListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mainViewModel.loadUserName()

        setContent {
            ComposableTasksTheme {
                MainScreen(
                    mainViewModel = mainViewModel,
                    onLogout = {
                        mainViewModel.logout()
                        startActivity(Intent(applicationContext, LoginActivity::class.java))
                        finish()
                    },
                    onNavigateToTaskForm = { taskId ->
                        val intent = Intent(applicationContext, TaskFormActivity::class.java)
                        // Se o ID for diferente de 0, envia para edição
                        if (taskId != 0) {
                            intent.putExtra(TaskConstants.BUNDLE.TASKID, taskId)
                        }
                        startActivity(intent)
                    },
                    taskListViewModel = taskListViewModel
                )
            }
        }
    }
}