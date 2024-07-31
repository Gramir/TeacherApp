package com.example.teacherapp.ui.assignment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.model.Assignment
import com.example.teacherapp.data.repository.AssignmentRepository
import com.example.teacherapp.databinding.FragmentAssignmentListBinding
import com.example.teacherapp.viewmodel.AssignmentViewModel
import com.example.teacherapp.viewmodel.AssignmentViewModelFactory

class AssignmentListFragment : Fragment() {
    private var _binding: FragmentAssignmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AssignmentViewModel
    private lateinit var adapter: AssignmentAdapter
    private var courseId: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseId = arguments?.getInt("COURSE_ID") ?: -1
        if (courseId == -1) {
            Log.e("AssignmentListFragment", "Invalid courseId received")
            return
        }

        setupViewModel()
        setupRecyclerView()
        observeAssignments()

        binding.addAssignmentButton.setOnClickListener {
            showAddAssignmentDialog()
        }
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = AssignmentRepository(database.assignmentDao())
        val factory = AssignmentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AssignmentViewModel::class.java]
        viewModel.getAssignmentsForCourse(courseId)
    }

    private fun setupRecyclerView() {
        adapter = AssignmentAdapter(
            onEditClick = { assignment -> showEditAssignmentDialog(assignment) },
            onDeleteClick = { assignment -> deleteAssignment(assignment) }
        )
        binding.assignmentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.assignmentRecyclerView.adapter = adapter
    }

    private fun observeAssignments() {
        viewModel.assignments.observe(viewLifecycleOwner) { assignments ->
            adapter.submitList(assignments)
        }
    }

    private fun showAddAssignmentDialog() {
        val dialog = AddEditAssignmentDialogFragment.newInstance(courseId)
        dialog.setOnAssignmentSavedListener { newAssignment ->
            viewModel.saveAssignment(newAssignment)
        }
        dialog.show(childFragmentManager, "AddAssignmentDialog")
    }

    private fun showEditAssignmentDialog(assignment: Assignment) {
        val dialog = AddEditAssignmentDialogFragment.newInstance(courseId, assignment)
        dialog.setOnAssignmentSavedListener { updatedAssignment ->
            viewModel.updateAssignment(updatedAssignment)
        }
        dialog.show(childFragmentManager, "EditAssignmentDialog")
    }

    private fun deleteAssignment(assignment: Assignment) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Assignment")
            .setMessage("Are you sure you want to delete this assignment?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.deleteAssignment(assignment)
            }
            .setNegativeButton("No", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}