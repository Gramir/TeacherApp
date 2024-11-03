package com.example.teacherapp.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.teacherapp.R
import com.example.teacherapp.databinding.DialogAddEditAssignmentBinding
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.presentation.viewmodel.assignment.AssignmentViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddEditAssignmentDialog : DialogFragment() {

    private var _binding: DialogAddEditAssignmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AssignmentViewModel by activityViewModels()
    private var courseId: String? = null
    private var assignment: Assignment? = null

    companion object {
        private const val ARG_COURSE_ID = "course_id"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
        courseId = arguments?.getString(ARG_COURSE_ID)
        assignment = arguments?.getParcelable(ARG_ASSIGNMENT)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setWindowAnimations(R.style.DialogAnimation)
        }
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
        populateAssignment()
    }

    private fun setupViews() {
        binding.toolbar.apply {
            setNavigationOnClickListener { dismiss() }
            title = if (assignment == null) "Add Assignment" else "Edit Assignment"
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_save -> {
                        saveAssignment()
                        true
                    }
                    else -> false
                }
            }
        }

        binding.dueDateInput.setOnClickListener {
            showDatePicker()
        }
    }

    private fun populateAssignment() {
        assignment?.let { assignment ->
            binding.apply {
                titleInput.setText(assignment.title)
                descriptionInput.setText(assignment.description)
                dueDateInput.setText(assignment.dueDate)
                statusSpinner.setSelection(getStatusPosition(assignment.status))
            }
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Due Date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dueDateInput.setText(dateFormat.format(Date(selection)))
        }

        datePicker.show(childFragmentManager, "date_picker")
    }

    private fun saveAssignment() {
        val title = binding.titleInput.text.toString()
        val description = binding.descriptionInput.text.toString()
        val dueDate = binding.dueDateInput.text.toString()
        val status = binding.statusSpinner.selectedItem.toString()

        if (title.isBlank() || dueDate.isBlank()) {
            binding.titleInputLayout.error = if (title.isBlank()) "Required" else null
            binding.dueDateInputLayout.error = if (dueDate.isBlank()) "Required" else null
            return
        }

        val newAssignment = Assignment(
            id = assignment?.id ?: UUID.randomUUID().toString(),
            title = title,
            description = description,
            dueDate = dueDate,
            courseId = courseId ?: "",
            status = status
        )

        if (assignment == null) {
            viewModel.createAssignment(newAssignment)
        } else {
            viewModel.updateAssignment(newAssignment)
        }

        dismiss()
    }

    private fun getStatusPosition(status: String): Int {
        return when (status.lowercase()) {
            "pending" -> 0
            "in progress" -> 1
            "completed" -> 2
            else -> 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}