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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.composabletasks.R
import com.example.composabletasks.service.model.PriorityModel
import com.example.composabletasks.ui.components.BasicTextField
import com.example.composabletasks.ui.components.PrimaryButton
import com.example.composabletasks.viewmodel.TaskFormViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// 1. Função Stateless (Totalmente visual, recebe apenas valores e eventos)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormContent(
    description: String,
    onDescriptionChange: (String) -> Unit,
    dueDate: String,
    onDueDateChange: (String) -> Unit,
    priorityId: Int,
    onPriorityChange: (Int) -> Unit,
    onTaskCompletedChange: (Boolean) -> Unit,
    isCompleted: Boolean,
    priorities: List<PriorityModel>,
    onSaveClick: () -> Unit
) {
    // 1. Estado para controlar a exibição do calendário
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // 2. Lógica do DatePickerDialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // Formatação correta com fuso UTC para evitar o bug de "-1 dia"
                        val formatter =
                            SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
                        formatter.timeZone = TimeZone.getTimeZone("UTC")
                        val formattedDate = formatter.format(Date(millis))

                        onDueDateChange(formattedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

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

        // 3. Botão que substitui o BasicTextField original
        OutlinedButton(
            onClick = { showDatePicker = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp) // Altura padrão de campos de texto no Material Design
        ) {
            Text(
                // Exibe a data selecionada ou o texto padrão se estiver vazio
                text = dueDate.ifEmpty { "Data Limite" }// Ajuste para a cor do seu tema se necessário
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Status da Tarefa", modifier = Modifier.align(Alignment.CenterHorizontally))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Spacer(modifier = Modifier.weight(1f))

                // Ícone de Status (Mesmo comportamento do TaskItem)
                IconButton(onClick = {
                    // Inverte o estado atual (se era true vira false e vice-versa)
                    onTaskCompletedChange(!isCompleted)
                }) {
                    Icon(
                        painter = painterResource(
                            id = if (isCompleted) R.drawable.ic_done else R.drawable.ic_todo
                        ),
                        contentDescription = null,
                        tint = if (isCompleted) Color(0xFF4CAF50) else Color.DarkGray
                    )
                }
                // Texto descritivo ao lado do ícone
                Text(text = if (isCompleted) "Tarefa Completa" else "Tarefa Pendente")

                Spacer(modifier = Modifier.weight(1f))
            }
        }

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
        onSaveClick = { viewModel.save() },
        onTaskCompletedChange = { viewModel.onTaskCompletedChange(it) },
        isCompleted = viewModel.complete
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
        onSaveClick = {},
        onTaskCompletedChange = {},
        isCompleted = true
    )
}