package com.example.subcountdown.checklist

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class TodoItem(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val isDone: Boolean = false
)

class ChecklistViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("checklist_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    val items = mutableStateListOf<TodoItem>()

    init {
        loadItems()
    }

    private fun loadItems() {
        val json = prefs.getString("checklist_list", null)
        if (json != null) {
            val type = object : TypeToken<List<TodoItem>>() {}.type
            val savedItems: List<TodoItem> = gson.fromJson(json, type)
            items.clear()
            items.addAll(savedItems)
        }
    }

    private fun saveItems() {
        val json = gson.toJson(items.toList())
        prefs.edit().putString("checklist_list", json).apply()
    }

    fun addItem(title: String) {
        if (title.isNotBlank()) {
            items.add(TodoItem(title = title))
            saveItems()
        }
    }

    fun toggleItem(item: TodoItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item.copy(isDone = !item.isDone)
            saveItems()
        }
    }

    fun removeItem(item: TodoItem) {
        items.removeIf { it.id == item.id }
        saveItems()
    }
}
