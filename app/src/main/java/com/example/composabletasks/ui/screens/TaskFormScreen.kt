package com.example.composabletasks.ui.screens

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
import com.example.composabletasks.service.model.PriorityModel
import com.example.composabletasks.ui.components.BasicTextField
import com.example.composabletasks.ui.components.PrimaryButton
import com.example.composabletasks.viewmodel.TaskFormViewModel

// 1. Função Stateless (Totalmente visual, recebe apenas valores e eventos)
@Composable
fun TaskFormContent(
    description: String,
    onDescriptionChange: (String) -> Unit,
    dueDate: String,
    onDueDateChange: (String) -> Unit,
    priorityId: Int,
    onPriorityChange: (Int) -> Unit,
    priorities: List<PriorityModel>,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BasicTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Descrição da tarefa",
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
                        selected = priorityId == priority.id,
                        onClick = { onPriorityChange(priority.id) }
                    )
                    Text(text = priority.description)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = dueDate,
            onValueChange = onDueDateChange,
            label = "Data limite",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = "SALVAR",
            onClick = onSaveClick
        )
    }
}

// 2. Função Stateful (Injeta o ViewModel e passa os estados para o Content)
@Composable
fun TaskFormScreen(
    viewModel: TaskFormViewModel,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
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

    // Chama a interface visual repassando os estados do ViewModel[cite: 11]
    TaskFormContent(
        description = viewModel.description,
        onDescriptionChange = { viewModel.onDescriptionChange(it) },
        dueDate = viewModel.dueDate,
        onDueDateChange = { viewModel.onDueDateChange(it) },
        priorityId = viewModel.priorityId,
        onPriorityChange = { viewModel.onPriorityChange(it) },
        priorities = priorities,
        onSaveClick = { viewModel.save() }
    )
}

// 3. O Preview
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskFormScreenPreview() {
    TaskFormContent(
        description = "Estudar Jetpack Compose",
        onDescriptionChange = {},
        dueDate = "05/05/2026",
        onDueDateChange = {},
        priorityId = 2,
        onPriorityChange = {},
        // Passamos uma lista falsa para renderizar os RadioButtons no Android Studio
        priorities = listOf(
            PriorityModel(id = 1, description = "Baixa"),
            PriorityModel(id = 2, description = "Média"),
            PriorityModel(id = 3, description = "Alta")
        ),
        onSaveClick = {}
    )
}