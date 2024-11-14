package com.example.teacherapp.presentation.fragment.student

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.databinding.FragmentStudentListBinding
import com.example.teacherapp.presentation.adapter.StudentAdapter
import com.example.teacherapp.presentation.viewmodel.student.StudentUiState
import com.example.teacherapp.presentation.viewmodel.student.StudentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StudentListFragment : Fragment() {
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StudentViewModel by viewModels()
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("COURSE_ID")
        if (courseId == null) {
            Log.e(TAG, "Invalid courseId received")
            Toast.makeText(context, "Error: Invalid course ID", Toast.LENGTH_LONG).show()
            return
        }

        setupRecyclerView()
        observeStudents()
        viewModel.getStudentsForCourse(courseId)
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@StudentListFragment.adapter
        }
    }

    private fun observeStudents() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.students.collect { students ->
                Log.d(TAG, "Received ${students.size} students")
                adapter.submitList(students)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                handleUiState(state)
            }
        }
    }

    private fun handleUiState(state: StudentUiState) {
        binding.progressBar.isVisible = state is StudentUiState.Loading

        when (state) {
            is StudentUiState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
            is StudentUiState.Loading -> {
                // Progress bar ya está manejado arriba
            }
            is StudentUiState.Success -> {
                // Lista ya actualizada por el collect de students
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG = "StudentListFragment"
    }
}