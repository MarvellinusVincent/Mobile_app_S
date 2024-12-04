package com.example.notesapp

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapp.databinding.FragmentNotesListBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class NoteListFragment : Fragment() {

    private var _binding: FragmentNotesListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access the view because it is null."
        }

    private val noteViewModel: NoteViewModel by viewModels()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                noteViewModel.notes.collect { notes ->
                    val mutableNotes = notes.toMutableList()
                    binding.recyclerView.adapter = NoteAdapter(
                        mutableNotes,
                        onNoteClicked = { noteId ->
                            findNavController().navigate(
                                NoteListFragmentDirections.showNoteDetail(noteId)
                            )
                        },
                        onDeleteClicked = { noteId ->
                            showDeleteConfirmation(noteId)
                        }
                    )
                }
            }
        }

        binding.userButton.setOnClickListener {
            if (auth.currentUser != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes") { _, _ ->
                        auth.signOut()
                        findNavController().navigate(R.id.loginFragment)
                    }
                    .setNegativeButton("No", null)
                    .show()
            } else {
                findNavController().navigate(R.id.action_noteListFragment_to_loginFragment)
            }
        }

        binding.addNoteButton.setOnClickListener {
            findNavController().navigate(NoteListFragmentDirections.showNoteDetail(null))
        }
    }

    private fun showDeleteConfirmation(noteId: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Note")
            .setMessage("Are you sure you want to delete this note?")
            .setPositiveButton("Yes") { _, _ ->
                noteViewModel.deleteNote(noteId)
            }
            .setNegativeButton("No", null)
            .create()
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
