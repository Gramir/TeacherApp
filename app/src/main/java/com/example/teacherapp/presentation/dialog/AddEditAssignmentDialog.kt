package com.example.teacherapp.presentation.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.R
import com.example.teacherapp.databinding.DialogAddEditAssignmentBinding
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.presentation.viewmodel.assigment.AssignmentViewModel
import com.example.teacherapp.presentation.viewmodel.assigment.ActionState
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditAssignmentDialog : DialogFragment() {

    private var _binding: DialogAddEditAssignmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentViewModel by activityViewModels()
    private lateinit var courseId: String
    private var assignmentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        courseId = arguments?.getString("courseId") ?: ""
        assignmentId = arguments?.getString("assignmentId")
        // Log para depuración
        println("DEBUG: onCreate Dialog - courseId: $courseId, assignmentId: $assignmentId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupSpinner()

        assignmentId?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                kotlinx.coroutines.delay(100) // Pequeño retraso para asegurar que los datos estén disponibles
                viewModel.getAssignment(id)
            }
        }

        observeStates()
    }

    private fun setupViews() {
        binding.toolbar.apply {
            setNavigationOnClickListener { dismiss() }
            title = if (assignmentId == null) "Agregar Tarea" else "Editar Tarea"
            inflateMenu(R.menu.menu_add_edit_assignment)
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_save -> {
                        saveAssignment()
                        true
                    }
                    else -> false
                }
            }
        }

        binding.dueDateEditText.setOnClickListener {
            showDatePicker()
        }
    }

    private fun setupSpinner() {
        val statusArray = arrayOf("Pendiente", "En Progreso", "Completado")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusArray
        )
        binding.statusAutoComplete.setAdapter(adapter)
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.selectedAssignment.collect { assignment ->
                    println("DEBUG: Assignment recibido: $assignment")
                    assignment?.let {
                        println("DEBUG: Actualizando campos con assignment: $it")
                        binding.titleEditText.setText(it.title)
                        binding.descriptionEditText.setText(it.description)
                        binding.dueDateEditText.setText(it.dueDate)
                        binding.statusAutoComplete.setText(it.status, false)
                    }
                }
            }
            viewModel.actionState.collect { state ->
                when (state) {
                    is ActionState.Success -> {
                        Snackbar.make(
                            binding.root,
                            if (assignmentId == null) "Tarea creada" else "Tarea actualizada",
                            Snackbar.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
                    is ActionState.Error -> {
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        // Handle other states if needed
                    }
                }
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Seleccionar Fecha de Entrega")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dueDateEditText.setText(dateFormat.format(Date(selection)))
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun saveAssignment() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val dueDate = binding.dueDateEditText.text.toString()
        val status = binding.statusAutoComplete.text.toString()

        if (title.isBlank() || dueDate.isBlank()) {
            if (title.isBlank()) binding.titleTextInputLayout.error = "El título es requerido"
            if (dueDate.isBlank()) binding.dueDateTextInputLayout.error = "La fecha de entrega es requerida"
            return
        }

        val assignment = Assignment(
            id = assignmentId ?: UUID.randomUUID().toString(),
            title = title,
            description = description,
            dueDate = dueDate,
            courseId = courseId,
            status = status,
            createdAt = System.currentTimeMillis()
        )

        if (assignmentId == null) {
            viewModel.createAssignment(assignment)
        } else {
            viewModel.updateAssignment(assignment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(courseId: String, assignment: Assignment? = null): AddEditAssignmentDialog {
            println("DEBUG: Creando nuevo dialog - courseId: $courseId, assignment: $assignment")
            return AddEditAssignmentDialog().apply {
                arguments = Bundle().apply {
                    putString("courseId", courseId)
                    putString("assignmentId", assignment?.id)
                }
            }
        }
    }
}