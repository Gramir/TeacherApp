package com.example.teacherapp.ui.course

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.CourseRepository
import com.example.teacherapp.databinding.FragmentCourseListBinding
import com.example.teacherapp.viewmodel.CourseViewModel
import com.example.teacherapp.viewmodel.CourseViewModelFactory

class CourseListFragment : Fragment() {

    private var _binding: FragmentCourseListBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourseViewModel
    private lateinit var adapter: CourseAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCourseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = CourseRepository(database.courseDao())
        val factory = CourseViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(CourseViewModel::class.java)

        adapter = CourseAdapter()
        binding.courseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.courseRecyclerView.adapter = adapter

        val teacherId = activity?.intent?.getIntExtra("TEACHER_ID", -1) ?: -1
        viewModel.getCoursesForTeacher(teacherId)

        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            adapter.submitList(courses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}