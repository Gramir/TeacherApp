package com.example.teacherapp.presentation.fragment.attendance

import android.annotation.SuppressLint
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.teacherapp.databinding.FragmentAttendanceBinding
import com.example.teacherapp.presentation.adapter.AttendanceAdapter
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceUiState
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceViewModel
import com.example.teacherapp.util.DatePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class AttendanceFragment : Fragment() {

    private var _binding: FragmentAttendanceBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AttendanceViewModel by viewModels()
    private lateinit var adapter: AttendanceAdapter
    private lateinit var courseId: String

    // Inicializar con la fecha actual
    private var currentDate: String = LocalDate.now().format(DateTimeFormatter.ISO_DATE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("DEBUG_APP: AttendanceFragment - onCreate")
        println("DEBUG_APP: Arguments: ${arguments?.toString()}")

        courseId = arguments?.getString("COURSE_ID")?.also { id ->
            println("DEBUG_APP: CourseId obtenido: $id")
        } ?: run {
            println("DEBUG_APP: ERROR - CourseId es null")
            Toast.makeText(context, "Error: Course ID not found", Toast.LENGTH_LONG).show()
            return
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAttendanceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("DEBUG_APP: AttendanceFragment - onViewCreated")
        println("DEBUG_APP: CourseId: $courseId")
        println("DEBUG_APP: Fecha inicial: $currentDate")

        setupRecyclerView()
        setupDatePicker()
        observeAttendances()
        setupSaveButton()

        // Cargar datos iniciales con la fecha actual
        binding.dateEditText.setText(currentDate)
        loadAttendanceData()
    }

    private fun setupRecyclerView() {
        println("DEBUG_APP: Configurando RecyclerView")
        adapter = AttendanceAdapter { studentId, isPresent ->
            println("DEBUG_APP: Actualizando asistencia - estudiante: $studentId, presente: $isPresent")
            viewModel.updateAttendanceStatus(studentId, isPresent)
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@AttendanceFragment.adapter
        }
    }

    private fun setupDatePicker() {
        binding.dateEditText.apply {
            setText(currentDate)
            setOnClickListener {
                showDatePickerDialog()
            }
        }
    }

    private fun loadAttendanceData() {
        println("DEBUG_APP: Cargando asistencia - courseId: $courseId, fecha: $currentDate")
        viewModel.getStudentsWithAttendanceForCourseAndDate(courseId, currentDate)
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog() {
        val datePickerFragment = DatePickerFragment { day, month, year ->
            currentDate = String.format("%04d-%02d-%02d", year, month + 1, day)
            binding.dateEditText.setText(currentDate)
            loadAttendanceData()
        }
        datePickerFragment.show(childFragmentManager, "datePicker")
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            println("DEBUG_APP: Guardando asistencia - courseId: $courseId, fecha: $currentDate")
            viewModel.saveAttendance(adapter.currentList)
        }
    }

    private fun observeAttendances() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.attendances.collect { attendances ->
                println("DEBUG_APP: Recibidos ${attendances.size} registros de asistencia")
                adapter.submitList(attendances)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                handleUiState(state)
            }
        }
    }

    private fun handleUiState(state: AttendanceUiState) {
        binding.progressBar.isVisible = state is AttendanceUiState.Loading

        when (state) {
            is AttendanceUiState.Success -> {
                println("DEBUG_APP: Estado Success")
                binding.saveButton.isEnabled = true
            }
            is AttendanceUiState.Error -> {
                println("DEBUG_APP: Estado Error: ${state.message}")
                binding.saveButton.isEnabled = true
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }
            is AttendanceUiState.Loading -> {
                println("DEBUG_APP: Estado Loading")
                binding.saveButton.isEnabled = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}