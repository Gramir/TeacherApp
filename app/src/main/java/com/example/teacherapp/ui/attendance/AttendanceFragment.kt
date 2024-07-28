package com.example.teacherapp.ui.attendance

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.model.Attendance
import com.example.teacherapp.data.repository.AttendanceRepository
import com.example.teacherapp.databinding.FragmentAttendanceBinding
import com.example.teacherapp.ui.util.DatePickerFragment
import com.example.teacherapp.viewmodel.AttendanceViewModel
import com.example.teacherapp.viewmodel.AttendanceViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment() {
    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AttendanceViewModel
    private lateinit var adapter: AttendanceAdapter

    private var courseId: Int = -1
    private lateinit var currentDate: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        courseId = arguments?.getInt("COURSE_ID") ?: -1
        if (courseId == -1) {
            Log.e("AttendanceFragment", "Invalid courseId received")
            Toast.makeText(context, "Error: Invalid course ID", Toast.LENGTH_LONG).show()
            return
        }

        currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        setupViewModel()
        setupRecyclerView()
        setupDatePicker()
        loadAttendanceData()

        binding.saveAttendanceButton.setOnClickListener {
            saveAttendance()
        }
        observeAttendances()

    }
    private fun observeAttendances() {
        viewModel.attendances.observe(viewLifecycleOwner) { attendances ->
            Log.d("AttendanceFragment", "Received ${attendances.size} attendances")
            if (attendances.isEmpty()) {
                Log.d("AttendanceFragment", "No attendances received")
            } else {
                Log.d("AttendanceFragment", "First attendance: ${attendances[0]}")
            }
            adapter.submitList(attendances)
        }
    }
    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(requireContext())
        val repository = AttendanceRepository(database.attendanceDao())
        val factory = AttendanceViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AttendanceViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = AttendanceAdapter { updatedAttendance ->
            Log.d("AttendanceFragment", "Attendance changed for ${updatedAttendance.studentName}")
        }
        binding.attendanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.attendanceRecyclerView.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.dateEditText.setText(currentDate)
        binding.dateEditText.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerFragment { day, month, year ->
            currentDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            binding.dateEditText.setText(currentDate)
            loadAttendanceData()
        }
        datePicker.show(childFragmentManager, "datePicker")
    }

    private fun loadAttendanceData() {
        viewModel.getStudentsWithAttendanceForCourseAndDate(courseId, currentDate)
        viewModel.attendances.observe(viewLifecycleOwner) { attendances ->
            Log.d("AttendanceFragment", "Received ${attendances.size} attendances")
            adapter.submitList(attendances)
        }
    }

    private fun saveAttendance() {
        val attendances = adapter.currentList
        lifecycleScope.launch {
            attendances.forEach { attendanceWithName ->
                val attendance = Attendance(
                    studentId = attendanceWithName.studentId,
                    courseId = courseId,
                    date = currentDate,
                    present = attendanceWithName.present
                )
                viewModel.saveAttendance(attendance)
            }
            Toast.makeText(context, "Attendance saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}