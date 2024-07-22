package com.example.teacherapp.ui.course

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.R
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.CourseRepository
import com.example.teacherapp.databinding.FragmentCourseListBinding
import com.example.teacherapp.viewmodel.CourseViewModel
import com.example.teacherapp.viewmodel.CourseViewModelFactory

class CourseListFragment : Fragment(), CourseAdapter.CourseClickListener {

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
        viewModel = ViewModelProvider(this, CourseViewModelFactory(repository))[CourseViewModel::class.java]

        adapter = CourseAdapter(this)
        binding.courseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.courseRecyclerView.adapter = adapter

        val teacherId = activity?.intent?.getIntExtra("TEACHER_ID", -1) ?: -1
        viewModel.getCoursesForTeacher(teacherId)

        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            adapter.submitList(courses)
        }
    }

    override fun onAttendanceClick(courseId: Int) {
        Log.d("CourseListFragment", "Navigating to AttendanceFragment with courseId: $courseId")
        val bundle = Bundle().apply {
            putInt("COURSE_ID", courseId)
        }
        findNavController().navigate(R.id.action_courseListFragment_to_attendanceFragment, bundle)
    }

    override fun onAssignmentsClick(courseId: Int) {
        Log.d("CourseListFragment", "Navigating to AssignmentListFragment with courseId: $courseId")
        val bundle = Bundle().apply {
            putInt("COURSE_ID", courseId)
        }
        findNavController().navigate(R.id.action_courseListFragment_to_assignmentListFragment, bundle)
    }

    override fun onStudentsClick(courseId: Int) {
        Log.d("CourseListFragment", "onStudentsClick called with courseId: $courseId")
        try {
            val action = CourseListFragmentDirections.actionCourseListFragmentToStudentListFragment(courseId)
            findNavController().navigate(action)
        } catch (e: Exception) {
            Log.e("CourseListFragment", "Navigation failed", e)
            val bundle = Bundle().apply {
                putInt("courseId", courseId)
            }
            findNavController().navigate(R.id.studentListFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}