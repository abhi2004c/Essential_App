package com.example.subcountdown.notes

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis()
)

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    val notes = mutableStateListOf<Note>()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        val json = prefs.getString("notes_list", null)
        if (json != null) {
            val type = object : TypeToken<List<Note>>() {}.type
            val savedNotes: List<Note> = gson.fromJson(json, type)
            notes.clear()
            notes.addAll(savedNotes)
        }
    }

    private fun saveNotes() {
        val json = gson.toJson(notes.toList())
        prefs.edit().putString("notes_list", json).apply()
    }

    fun addNote(title: String, content: String) {
        if (title.isNotBlank() || content.isNotBlank()) {
            notes.add(0, Note(title = title, content = content))
            saveNotes()
        }
    }

    fun removeNote(note: Note) {
        notes.remove(note)
        saveNotes()
    }
}
