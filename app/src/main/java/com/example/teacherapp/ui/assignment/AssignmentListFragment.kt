package com.example.teacherapp.ui.assignment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.AssignmentRepository
import com.example.teacherapp.databinding.FragmentAssignmentListBinding
import com.example.teacherapp.viewmodel.AssignmentViewModel
import com.example.teacherapp.viewmodel.AssignmentViewModelFactory

class AssignmentListFragment : Fragment() {

    private var _binding: FragmentAssignmentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AssignmentViewModel
    private lateinit var adapter: AssignmentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAssignmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getInt("COURSE_ID") ?: -1

        val database = AppDatabase.getDatabase(requireContext())
        val repository = AssignmentRepository(database.assignmentDao())
        val factory = AssignmentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AssignmentViewModel::class.java)

        adapter = AssignmentAdapter(
            onEditClick = { assignment -> editAssignment(assignment) },
            onDeleteClick = { assignment -> deleteAssignment(assignment) }
        )
        binding.assignmentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.assignmentRecyclerView.adapter = adapter

        binding.addAssignmentButton.setOnClickListener {
            showAddAssignmentDialog(courseId)
        }

        viewModel.getAssignmentsForCourse(courseId)

        viewModel.assignments.observe(viewLifecycleOwner) { assignments ->
            adapter.submitList(assignments)
        }
    }

    private fun showAddAssignmentDialog(courseId: Int) {
        val dialog = AddEditAssignmentDialogFragment.newInstance(courseId)
        dialog.show(childFragmentManager, "AddAssignmentDialog")
    }

    private fun editAssignment(assignment: Assignment) {
        val dialog = AddEditAssignmentDialogFragment.newInstance(assignment.courseId, assignment)
        dialog.show(childFragmentManager, "EditAssignmentDialog")
    }

    private fun deleteAssignment(assignment: Assignment) {
        viewModel.deleteAssignment(assignment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}