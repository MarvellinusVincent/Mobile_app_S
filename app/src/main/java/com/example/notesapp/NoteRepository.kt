package com.example.notesapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class NoteRepository private constructor() {

    private val firestore = FirebaseFirestore.getInstance()
    private val notesCollection = firestore.collection("notes")
    private val auth = FirebaseAuth.getInstance()

    suspend fun getNotes(): List<Note> {
        val userId = auth.currentUser?.uid
        return if (userId != null) {
            try {
                val snapshot = notesCollection.whereEqualTo("userId", userId).get().await()
                snapshot.toObjects(Note::class.java)
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    suspend fun getNoteById(id: String): Note? {
        return try {
            val snapshot = notesCollection.document(id).get().await()
            snapshot.toObject(Note::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun addOrUpdateNote(note: Note) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            note.userId = userId
            notesCollection.document(note.id).set(note)
                .addOnSuccessListener {
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }

    fun deleteNote(id: String) {
        notesCollection.document(id).delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
                e.printStackTrace()
            }
    }

    companion object {
        private var INSTANCE: NoteRepository? = null

        fun get(): NoteRepository {
            if (INSTANCE == null) {
                INSTANCE = NoteRepository()
            }
            return INSTANCE!!
        }
    }
}
