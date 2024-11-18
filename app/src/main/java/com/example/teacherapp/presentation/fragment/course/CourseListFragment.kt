package com.example.teacherapp.presentation.fragment.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.teacherapp.R
import com.example.teacherapp.databinding.FragmentCourseListBinding
import com.example.teacherapp.presentation.adapter.CourseAdapter
import com.example.teacherapp.presentation.viewmodel.course.CourseViewModel
import com.example.teacherapp.presentation.viewmodel.course.CoursesState
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CourseListFragment : Fragment() {

    private var _binding: FragmentCourseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels()
    private lateinit var adapter: CourseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DEBUG_APP: CourseListFragment - onViewCreated")

        setupRecyclerView()
        observeState()

        val teacherId = requireActivity().intent.getStringExtra("TEACHER_ID") ?: return
        println("DEBUG_APP: TeacherId: $teacherId")
        viewModel.getCourses(teacherId)
    }

    private fun setupRecyclerView() {
        println("DEBUG_APP: CourseListFragment - setupRecyclerView")
        adapter = CourseAdapter(
            onAssignmentsClick = { courseId ->
                println("DEBUG_APP: Click en Assignments, courseId: $courseId")
                val bundle = Bundle().apply {
                    putString("COURSE_ID", courseId)
                }
                findNavController().navigate(R.id.assignmentListFragment, bundle)
            },
            onAttendanceClick = { courseId ->
                println("DEBUG_APP: Click en Attendance, courseId: $courseId")
                val bundle = Bundle().apply {
                    putString("COURSE_ID", courseId)
                }
                findNavController().navigate(R.id.attendanceFragment, bundle)
            },
            onStudentsClick = { courseId ->
                println("DEBUG_APP: Click en Students, courseId: $courseId")
                val bundle = Bundle().apply {
                    putString("COURSE_ID", courseId)
                }
                findNavController().navigate(R.id.studentListFragment, bundle)
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.coursesState.collect { state ->
                when (state) {
                    is CoursesState.Loading -> {
                        println("DEBUG_APP: Estado Loading")
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CoursesState.Success -> {
                        println("DEBUG_APP: Estado Success con ${state.courses.size} cursos")
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.courses)
                    }
                    is CoursesState.Error -> {
                        println("DEBUG_APP: Estado Error: ${state.message}")
                        binding.progressBar.visibility = View.GONE
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}