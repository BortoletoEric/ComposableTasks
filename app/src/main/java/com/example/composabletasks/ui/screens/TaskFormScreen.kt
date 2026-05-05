package com.example.composabletasks.ui.screens

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composabletasks.ui.components.BasicTextField
import com.example.composabletasks.ui.components.PrimaryButton
import com.example.composabletasks.viewmodel.TaskFormViewModel

@Composable
fun TaskFormScreen(
    viewModel: TaskFormViewModel,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current

    // Observa os LiveDatas originais do ViewModel
    val saveResult by viewModel.taskSaved.observeAsState()
    val priorities by viewModel.priorityList.observeAsState(emptyList())

    LaunchedEffect(saveResult) {
        saveResult?.let {
            if (it.status()) {
                onSaveSuccess()
            } else {
                Toast.makeText(context, it.message(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = viewModel.description,
            onValueChange = { viewModel.onDescriptionChange(it) },
            label = "Descrição da tarefa",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = viewModel.dueDate,
            onValueChange = { viewModel.onDueDateChange(it) },
            label = "Data de vencimento (Ex: 31/12/2026)",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Prioridade", modifier = Modifier.align(Alignment.Start))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            priorities.forEach { priority ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = viewModel.priorityId == priority.id,
                        onClick = { viewModel.onPriorityChange(priority.id) }
                    )
                    Text(text = priority.description)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "SALVAR",
            onClick = { viewModel.save() }
        )
    }
}