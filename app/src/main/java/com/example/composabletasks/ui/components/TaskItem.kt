package com.example.composabletasks.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composabletasks.R
import com.example.composabletasks.service.model.TaskModel

@Composable
fun TaskItem(
    task: TaskModel,
    onTaskClick: (Int) -> Unit,
    onToggleStatus: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onTaskClick(task.id) }
            .padding(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Ícone de Status (Check ou Vazio)
            IconButton(onClick = { onToggleStatus(task.id) }) {
                Icon(
                    painter = painterResource(
                        id = if (task.complete) R.drawable.ic_done else R.drawable.ic_todo
                    ),
                    contentDescription = null,
                    tint = if (task.complete) Color(0xFF4CAF50) else Color.Gray
                )
            }

            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = task.description,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = task.priorityDescription, // Você já tem isso no TaskModel[cite: 4]
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }

            Text(
                text = task.dueDate,
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Top)
            )
        }
        HorizontalDivider(
            modifier = Modifier.padding(top = 8.dp),
            thickness = 0.5.dp,
            color = Color.Gray
        )
    }
}