package com.example.teacherapp.presentation.fragment.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.R
import com.example.teacherapp.databinding.FragmentAssignmentListBinding
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.presentation.adapter.AssignmentAdapter
import com.example.teacherapp.presentation.dialog.AddEditAssignmentDialog
import com.example.teacherapp.presentation.viewmodel.assigment.ActionState
import com.example.teacherapp.presentation.viewmodel.assigment.AssignmentViewModel
import com.example.teacherapp.presentation.viewmodel.assigment.AssignmentsState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AssignmentListFragment : Fragment() {

    private var _binding: FragmentAssignmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentViewModel by viewModels()
    private lateinit var adapter: AssignmentAdapter
    private lateinit var courseId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        courseId = arguments?.getString("COURSE_ID") ?: run {
            Toast.makeText(context, "Error: No se encontró el ID del curso", Toast.LENGTH_LONG).show()
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAssignmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupViews()
        observeStates()
        loadAssignments()
    }

    private fun loadAssignments() {
        println("DEBUG_FRAGMENT: Cargando tareas para curso: $courseId")
        viewModel.getAssignments(courseId)
    }

    private fun setupRecyclerView() {
        adapter = AssignmentAdapter(
            onEditClick = { assignment ->
                println("DEBUG_FRAGMENT: Click en editar tarea: $assignment")
                showAddEditDialog(assignment)
            },
            onDeleteClick = { assignment ->
                showDeleteConfirmation(assignment)
            }
        )
        binding.assignmentRecyclerView.adapter = adapter
    }

    private fun setupViews() {
        binding.addAssignmentButton.setOnClickListener {
            showAddEditDialog(null)
        }
    }

    private fun showAddEditDialog(assignment: Assignment?) {
        println("DEBUG_FRAGMENT: Mostrando diálogo para tarea: $assignment")
        AddEditAssignmentDialog.newInstance(courseId, assignment)
            .show(childFragmentManager, "assignment_dialog")
    }

    private fun showDeleteConfirmation(assignment: Assignment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.confirm_delete_title)
            .setMessage(R.string.confirm_delete_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                viewModel.deleteAssignment(assignment.id)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.assignmentsState.collect { state ->
                when (state) {
                    is AssignmentsState.Loading -> {
                        binding.assignmentProgressBar.visibility = View.VISIBLE
                    }
                    is AssignmentsState.Success -> {
                        binding.assignmentProgressBar.visibility = View.GONE
                        adapter.submitList(state.assignments)
                    }
                    is AssignmentsState.Error -> {
                        binding.assignmentProgressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actionState.collect { state ->
                when (state) {
                    is ActionState.Success -> {
                        // Recargar la lista después de una acción exitosa
                        loadAssignments()
                        viewModel.resetActionState()
                    }
                    is ActionState.Error -> {
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                        viewModel.resetActionState()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "AssignmentListFragment"
    }
}