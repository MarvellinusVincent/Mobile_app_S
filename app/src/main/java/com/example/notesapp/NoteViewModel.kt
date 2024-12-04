package com.example.notesapp

// NoteViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val repository =    NoteRepository.get()

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> get() = _notes

    init {
        fetchNotes()
    }

    private fun fetchNotes() {
        viewModelScope.launch {
            val fetchedNotes = repository.getNotes()
            _notes.value = fetchedNotes
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            repository.deleteNote(noteId)
            fetchNotes()
        }
    }
}
