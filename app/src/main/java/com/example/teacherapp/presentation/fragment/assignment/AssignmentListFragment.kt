package com.example.teacherapp.presentation.fragment.assignment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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
class AssignmentListFragment : Fragment(R.layout.fragment_assignment_list) {

    private var _binding: FragmentAssignmentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentViewModel by viewModels()
    private val args: AssignmentListFragmentArgs by navArgs()
    private lateinit var adapter: AssignmentAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAssignmentListBinding.bind(view)

        setupRecyclerView()
        setupViews()
        observeStates()

        viewModel.getAssignments(args.courseId)
    }

    private fun setupRecyclerView() {
        adapter = AssignmentAdapter(
            onEditClick = { assignment ->
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

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch {
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

            launch {
                viewModel.actionState.collect { state ->
                    when (state) {
                        is ActionState.Loading -> {
                            binding.assignmentProgressBar.visibility = View.VISIBLE
                        }
                        is ActionState.Success -> {
                            binding.assignmentProgressBar.visibility = View.GONE
                            viewModel.getAssignments(args.courseId)
                            viewModel.resetActionState()
                        }
                        is ActionState.Error -> {
                            binding.assignmentProgressBar.visibility = View.GONE
                            Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                            viewModel.resetActionState()
                        }
                        is ActionState.Idle -> {
                            binding.assignmentProgressBar.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun showAddEditDialog(assignment: Assignment?) {
        AddEditAssignmentDialog.newInstance(args.courseId, assignment)
            .show(childFragmentManager, "assignment_dialog")
    }

    private fun showDeleteConfirmation(assignment: Assignment) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Assignment")
            .setMessage("Are you sure you want to delete this assignment?")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAssignment(assignment.id)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}