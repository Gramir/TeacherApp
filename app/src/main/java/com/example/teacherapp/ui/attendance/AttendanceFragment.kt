package com.example.teacherapp.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.AttendanceRepository
import com.example.teacherapp.databinding.FragmentAttendanceBinding
import com.example.teacherapp.viewmodel.AttendanceViewModel
import com.example.teacherapp.viewmodel.AttendanceViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AttendanceViewModel
    private lateinit var adapter: AttendanceAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getInt("COURSE_ID") ?: -1

        val database = AppDatabase.getDatabase(requireContext())
        val repository = AttendanceRepository(database.attendanceDao())
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)

        adapter = AttendanceAdapter()
        binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.attendanceRecyclerView.adapter = adapter

        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        binding.dateEditText.setText(currentDate)

        binding.dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.saveAttendanceButton.setOnClickListener {
            saveAttendance(courseId)
        }

        viewModel.getAttendanceForCourseAndDate(courseId, currentDate)

        viewModel.attendances.observe(viewLifecycleOwner) { attendances ->
            adapter.submitList(attendances)
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun onDateSelected(day: Int, month: Int, year: Int) {
        val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
        binding.dateEditText.setText(selectedDate)
        val courseId = arguments?.getInt("COURSE_ID") ?: -1
        viewModel.getAttendanceForCourseAndDate(courseId, selectedDate)
    }

    private fun saveAttendance(courseId: Int) {
        val date = binding.dateEditText.text.toString()
        viewModel.saveAttendance(adapter.currentList, courseId, date)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}