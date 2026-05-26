package com.example.subcountdown.checklist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.subcountdown.core.ui.GlassCard
import com.example.subcountdown.core.ui.PremiumTextField

@Composable
fun ChecklistScreen(viewModel: ChecklistViewModel = viewModel()) {
    var newTaskText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
    ) {
        Text(
            "Checklist",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PremiumTextField(
                value = newTaskText,
                onValueChange = { newTaskText = it },
                label = "Add a new task...",
                modifier = Modifier.weight(1f)
            )
            FloatingActionButton(
                onClick = {
                    if (newTaskText.isNotBlank()) {
                        viewModel.addItem(newTaskText)
                        newTaskText = ""
                    }
                },
                containerColor = Color(0xFF3F51B5),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(viewModel.items) { item ->
                TodoItemRow(
                    item = item, 
                    onToggle = { viewModel.toggleItem(item) }, 
                    onDelete = { viewModel.removeItem(item) }
                )
            }
        }
    }
}

@Composable
fun TodoItemRow(item: TodoItem, onToggle: () -> Unit, onDelete: () -> Unit) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isDone,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF3F51B5))
            )
            Text(
                text = item.title,
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                color = if (item.isDone) Color.Gray else Color.White,
                textDecoration = if (item.isDone) TextDecoration.LineThrough else null,
                fontSize = 16.sp
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}
