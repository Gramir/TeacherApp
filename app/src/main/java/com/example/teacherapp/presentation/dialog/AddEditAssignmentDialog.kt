package com.example.teacherapp.presentation.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.R
import com.example.teacherapp.databinding.DialogAddEditAssignmentBinding
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.presentation.viewmodel.assigment.ActionState
import com.example.teacherapp.presentation.viewmodel.assigment.AssignmentViewModel
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
    private var assignment: Assignment? = null
    private var isDialogShown = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        courseId = arguments?.getString(ARG_COURSE_ID) ?: ""
        assignment = arguments?.getParcelable(ARG_ASSIGNMENT)
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

        // Si tenemos una tarea, llenar los campos directamente
        assignment?.let { assignment ->
            updateFormFields(assignment)
        }

        // Observar el estado de las acciones
        observeActionState()
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            if (!isDialogShown) {
                val ft = manager.beginTransaction()
                ft.add(this, tag)
                ft.commitAllowingStateLoss()
                isDialogShown = true
            }
        } catch (e: Exception) {
            println("DEBUG_DIALOG: Error al mostrar diálogo: ${e.message}")
        }
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.let { window ->
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    private fun observeActionState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actionState.collect { state ->
                when (state) {
                    is ActionState.Success -> {
                        Snackbar.make(
                            binding.root,
                            if (assignment == null) R.string.assignment_saved
                            else R.string.assignment_updated,
                            Snackbar.LENGTH_SHORT
                        ).show()
                        dismiss()
                    }
                    is ActionState.Error -> {
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                    else -> {
                        // No hacer nada para otros estados
                    }
                }
            }
        }
    }

    private fun updateFormFields(assignment: Assignment) {
        binding.apply {
            titleEditText.setText(assignment.title)
            descriptionEditText.setText(assignment.description)
            dueDateEditText.setText(assignment.dueDate)
        }
    }

    private fun setupViews() {
        binding.toolbar.apply {
            setNavigationOnClickListener { dismiss() }
            title = if (assignment == null)
                getString(R.string.add_assignment)
            else
                getString(R.string.edit_assignment)

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

    private fun saveAssignment() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val dueDate = binding.dueDateEditText.text.toString()
        val status = "Pendiente" // Estado por defecto para nuevas asignaciones

        if (title.isBlank() || dueDate.isBlank()) {
            if (title.isBlank())
                binding.titleTextInputLayout.error = getString(R.string.error_title_required)
            if (dueDate.isBlank())
                binding.dueDateTextInputLayout.error = getString(R.string.error_date_required)
            return
        }

        val updatedAssignment = Assignment(
            id = assignment?.id ?: "",
            title = title,
            description = description,
            dueDate = dueDate,
            courseId = courseId,
            status = if (assignment == null) status else assignment?.status ?: status,
            createdAt = assignment?.createdAt ?: System.currentTimeMillis()
        )

        if (assignment == null) {
            viewModel.createAssignment(updatedAssignment)
        } else {
            viewModel.updateAssignment(updatedAssignment)
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.due_date))
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dueDateEditText.setText(dateFormat.format(Date(selection)))
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isDialogShown = false
        viewModel.clearSelectedAssignment()
        viewModel.resetActionState()
    }

    override fun dismiss() {
        if (isDialogShown) {
            isDialogShown = false
            try {
                super.dismissAllowingStateLoss()
            } catch (e: Exception) {
                println("DEBUG_DIALOG: Error al cerrar diálogo: ${e.message}")
            }
        }
    }

    companion object {
        private const val ARG_COURSE_ID = "courseId"
        private const val ARG_ASSIGNMENT = "assignment"

        fun newInstance(courseId: String, assignment: Assignment? = null): AddEditAssignmentDialog {
            return AddEditAssignmentDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_COURSE_ID, courseId)
                    putParcelable(ARG_ASSIGNMENT, assignment)
                }
            }
        }
    }
}