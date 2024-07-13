package com.example.teacherapp.ui.student

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val courseId = arguments?.getInt("COURSE_ID") ?: -1

        val database = AppDatabase.getDatabase(requireContext())
        val repository = StudentRepository(database.studentDao())
        val factory = StudentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(StudentViewModel::class.java)

        adapter = StudentAdapter()
        binding.studentRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.studentRecyclerView.adapter = adapter

        viewModel.getStudentsForCourse(courseId)

        viewModel.students.observe(viewLifecycleOwner) { students ->
            adapter.submitList(students)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}