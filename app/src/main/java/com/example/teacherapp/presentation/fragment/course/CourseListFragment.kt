package com.example.teacherapp.presentation.fragment

import android.os.Bundle
import android.view.View
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
class CourseListFragment : Fragment(R.layout.fragment_course_list) {

    private var _binding: FragmentCourseListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CourseViewModel by viewModels()
    private lateinit var adapter: CourseAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCourseListBinding.bind(view)

        setupRecyclerView()
        observeState()

        val teacherId = requireActivity().intent.getStringExtra("TEACHER_ID") ?: return
        viewModel.getCourses(teacherId)
    }

    private fun setupRecyclerView() {
        adapter = CourseAdapter(
            onAssignmentsClick = { courseId ->
                findNavController().navigate(
                    CourseListFragmentDirections.actionCourseListToAssignments(courseId)
                )
            },
            onAttendanceClick = { courseId ->
                findNavController().navigate(
                    CourseListFragmentDirections.actionCourseListToAttendance(courseId)
                )
            },
            onStudentsClick = { courseId ->
                findNavController().navigate(
                    CourseListFragmentDirections.actionCourseListToStudents(courseId)
                )
            }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.coursesState.collect { state ->
                when (state) {
                    is CoursesState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is CoursesState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.courses)
                    }
                    is CoursesState.Error -> {
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