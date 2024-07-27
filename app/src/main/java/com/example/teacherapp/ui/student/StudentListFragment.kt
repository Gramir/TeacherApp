package com.example.teacherapp.ui.student

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.StudentRepository
import com.example.teacherapp.databinding.FragmentStudentListBinding
import com.example.teacherapp.viewmodel.StudentViewModel
import com.example.teacherapp.viewmodel.StudentViewModelFactory

class StudentListFragment : Fragment() {
    private var _binding: FragmentStudentListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: StudentViewModel
    private lateinit var adapter: StudentAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStudentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getInt("courseId") ?: -1
        if (courseId == -1) {
            Log.e("StudentListFragment", "Invalid courseId received")
            Toast.makeText(context, "Error: Invalid course ID", Toast.LENGTH_LONG).show()
            return
        }

        setupViewModel(courseId)
        setupRecyclerView()
        observeStudents()
    }

    private fun setupViewModel(courseId: Int) {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = StudentRepository(database.studentDao())
        val factory = StudentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StudentViewModel::class.java]
        viewModel.getStudentsForCourse(courseId)
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter()
        binding.studentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.studentRecyclerView.adapter = adapter
    }

    private fun observeStudents() {
        viewModel.students.observe(viewLifecycleOwner) { students ->
            Log.d("StudentListFragment", "Received ${students.size} students")
            adapter.submitList(students)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}