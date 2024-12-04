package com.example.notesapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "NoteDetailViewModel"

class NoteDetailViewModel(noteId: String?) : ViewModel() {
    private val repository = NoteRepository.get()

    private val _note = MutableStateFlow(Note())
    val note: StateFlow<Note> get() = _note

    init {
        if (noteId != null) {
            fetchNoteById(noteId)
        }
    }

    private fun fetchNoteById(noteId: String) {
        viewModelScope.launch {
            try {
                val fetchedNote = repository.getNoteById(noteId)
                _note.value = fetchedNote ?: Note()
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching note", e)
            }
        }
    }

    fun updateNote(update: (Note) -> Note) {
        _note.value = update(_note.value)
    }

    fun saveNote(note: Note) {
        viewModelScope.launch {
            try {
                repository.addOrUpdateNote(note)
                Log.d(TAG, "Note saved successfully")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving note", e)
            }
        }
    }
}

class NoteDetailViewModelFactory(private val noteId: String?) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailViewModel::class.java)) {
            return NoteDetailViewModel(noteId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
