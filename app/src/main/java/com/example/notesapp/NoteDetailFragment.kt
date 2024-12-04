package com.example.notesapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.notesapp.databinding.FragmentNoteDetailBinding
import kotlinx.coroutines.launch
import java.util.UUID

class NoteDetailFragment : Fragment() {

    private val args: NoteDetailFragmentArgs by navArgs()
    private val noteDetailViewModel: NoteDetailViewModel by viewModels {
        NoteDetailViewModelFactory(args.noteId.toString())
    }
    private var _binding: FragmentNoteDetailBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access the view because it is null."
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            titleEditText.doOnTextChanged { text, _, _, _ ->
                noteDetailViewModel.updateNote { it.copy(title = text.toString()) }
            }
            descriptionEditText.doOnTextChanged { text, _, _, _ ->
                noteDetailViewModel.updateNote { it.copy(description = text.toString()) }
            }
        }

        binding.saveButton.setOnClickListener {
            val note = Note(
                id = args.noteId ?: UUID.randomUUID().toString(),
                title = binding.titleEditText.text.toString(),
                description = binding.descriptionEditText.text.toString()
            )
            noteDetailViewModel.saveNote(note)
            findNavController().navigate(NoteDetailFragmentDirections.saveNote())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            noteDetailViewModel.note.collect { note ->
                note?.let { updateUi(it) }
            }
        }
    }

    private fun updateUi(note: Note) {
        binding.apply {
            if (titleEditText.text.toString() != note.title) {
                titleEditText.setText(note.title)
            }
            if (descriptionEditText.text.toString() != note.description) {
                descriptionEditText.setText(note.description)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
