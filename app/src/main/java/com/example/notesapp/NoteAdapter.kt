package com.example.notesapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.databinding.ItemNoteBinding

class NoteHolder(private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(note: Note, onNoteClicked: (noteId: String) -> Unit, onDeleteClicked: (noteId: String) -> Unit) {
        binding.noteTitle.text = note.title

        binding.root.setOnClickListener {
            onNoteClicked(note.id)
        }

        binding.deleteButton.setOnClickListener {
            onDeleteClicked(note.id)
        }
    }
}

class NoteAdapter(
    private var notes: List<Note>,
    private val onNoteClicked: (noteId: String) -> Unit,
    private val onDeleteClicked: (noteId: String) -> Unit
) : RecyclerView.Adapter<NoteHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNoteBinding.inflate(inflater, parent, false)
        return NoteHolder(binding)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        val note = notes[position]
        holder.bind(note, onNoteClicked, onDeleteClicked)
    }
}

