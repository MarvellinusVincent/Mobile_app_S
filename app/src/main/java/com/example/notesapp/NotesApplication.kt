package com.example.notesapp

import android.app.Application

class NotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NoteRepository.get()
    }
}