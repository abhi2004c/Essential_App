package com.example.subcountdown.checklist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class TodoItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    var isDone: Boolean = false
)

class ChecklistViewModel : ViewModel() {
    val items = mutableStateListOf<TodoItem>()

    fun addItem(title: String) {
        if (title.isNotBlank()) {
            items.add(TodoItem(title = title))
        }
    }

    fun toggleItem(item: TodoItem) {
        val index = items.indexOf(item)
        if (index != -1) {
            items[index] = item.copy(isDone = !item.isDone)
        }
    }

    fun removeItem(item: TodoItem) {
        items.remove(item)
    }
}
