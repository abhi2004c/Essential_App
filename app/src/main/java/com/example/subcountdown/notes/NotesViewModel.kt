package com.example.subcountdown.notes

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val date: Long = System.currentTimeMillis()
)

class NotesViewModel : ViewModel() {
    val notes = mutableStateListOf<Note>()

    fun addNote(title: String, content: String) {
        if (title.isNotBlank() || content.isNotBlank()) {
            notes.add(0, Note(title = title, content = content))
        }
    }

    fun removeNote(note: Note) {
        notes.remove(note)
    }
}
