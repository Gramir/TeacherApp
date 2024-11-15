package com.example.teacherapp.presentation.dialog

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.R
import com.example.teacherapp.databinding.DialogAddEditAssignmentBinding
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.presentation.viewmodel.assigment.AssignmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditAssignmentDialog : DialogFragment() {

    private var _binding: DialogAddEditAssignmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentViewModel by activityViewModels()
    private var courseId: String = ""

    companion object {
        fun newInstance(courseId: String, assignment: Assignment? = null): AddEditAssignmentDialog {
            return AddEditAssignmentDialog().apply {
                arguments = Bundle().apply {
                    putString("courseId", courseId)
                    putString("assignmentId", assignment?.id)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        courseId = arguments?.getString("courseId") ?: ""
        arguments?.getString("assignmentId")?.let { assignmentId ->
            // Obtener la asignación del ViewModel si es una edición
            viewModel.getAssignment(assignmentId)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupSpinner()
        observeAssignment()
    }

    private fun observeAssignment() {
        // Observar la asignación seleccionada para editar
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.selectedAssignment.collect { assignment ->
                assignment?.let {
                    binding.titleEditText.setText(it.title)
                    binding.descriptionEditText.setText(it.description)
                    binding.dueDateEditText.setText(it.dueDate)
                    setStatusSelection(it.status)
                    binding.toolbar.title = "Edit Assignment"
                } ?: run {
                    binding.toolbar.title = "Add Assignment"
                }
            }
        }
    }

    private fun setStatusSelection(status: String) {
        val position = when (status.lowercase()) {
            "pending" -> 0
            "in progress" -> 1
            "completed" -> 2
            else -> 0
        }
        binding.statusAutoComplete.setText(
            (binding.statusAutoComplete.adapter?.getItem(position) as? String) ?: "Pending",
            false
        )
    }





    private fun setupSpinner() {
        val statusArray = arrayOf("Pending", "In Progress", "Completed")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            statusArray
        )

        binding.statusAutoComplete.apply {
            setAdapter(adapter)
            setText(adapter.getItem(0), false)
        }
    }

    private fun saveAssignment() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val dueDate = binding.dueDateEditText.text.toString()
        val status = binding.statusAutoComplete.text.toString()

        if (title.isBlank() || dueDate.isBlank()) {
            binding.titleTextInputLayout.error = if (title.isBlank()) "Required" else null
            binding.dueDateTextInputLayout.error = if (dueDate.isBlank()) "Required" else null
            return
        }

        val assignment = Assignment(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            dueDate = dueDate,
            courseId = courseId,
            status = status
        )

        viewModel.createAssignment(assignment)
        dismiss()
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Due Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dueDateEditText.setText(dateFormat.format(Date(selection)))
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun setupViews() {
        binding.toolbar.apply {
            setNavigationOnClickListener { dismiss() }
            title = "Add Assignment"
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
}