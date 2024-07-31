package com.example.teacherapp.ui.assignment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.model.Assignment
import com.example.teacherapp.data.repository.AssignmentRepository
import com.example.teacherapp.databinding.DialogAddEditAssignmentBinding
import com.example.teacherapp.viewmodel.AssignmentViewModel
import com.example.teacherapp.viewmodel.AssignmentViewModelFactory
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class AddEditAssignmentDialogFragment : DialogFragment() {

    private var _binding: DialogAddEditAssignmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AssignmentViewModel
    private var courseId: Int = -1
    private var assignment: Assignment? = null

    private var onAssignmentSavedListener: ((Assignment) -> Unit)? = null

    companion object {
        fun newInstance(courseId: Int, assignment: Assignment? = null): AddEditAssignmentDialogFragment {
            val fragment = AddEditAssignmentDialogFragment()
            fragment.courseId = courseId
            fragment.assignment = assignment
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCanceledOnTouchOutside(false)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddEditAssignmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = AppDatabase.getDatabase(requireContext())
        val repository = AssignmentRepository(database.assignmentDao())
        val factory = AssignmentViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(AssignmentViewModel::class.java)

        binding.dueDateEditText.setOnClickListener {
            showDatePicker()
        }

        binding.saveButton.setOnClickListener {
            saveAssignment()
        }

        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        assignment?.let { populateFields(it) }
    }

    private fun populateFields(assignment: Assignment) {
        binding.titleEditText.setText(assignment.title)
        binding.descriptionEditText.setText(assignment.description)
        binding.dueDateEditText.setText(assignment.dueDate)
        binding.statusSpinner.setSelection(getStatusPosition(assignment.status))
    }

    private fun getStatusPosition(status: String): Int {
        return when (status) {
            "pending" -> 0
            "submitted" -> 1
            "graded" -> 2
            else -> 0
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select due date")
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            binding.dueDateEditText.setText(dateFormat.format(Date(selection)))
        }

        datePicker.show(childFragmentManager, "DATE_PICKER")
    }

    private fun saveAssignment() {
        val title = binding.titleEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val dueDate = binding.dueDateEditText.text.toString()
        val status = binding.statusSpinner.selectedItem.toString()

        if (title.isBlank() || description.isBlank() || dueDate.isBlank()) {

            return
        }

        val newAssignment = Assignment(
            id = assignment?.id ?: 0,
            title = title,
            description = description,
            dueDate = dueDate,
            courseId = courseId,
            status = status
        )

        if (assignment == null) {
            viewModel.saveAssignment(newAssignment)
        } else {
            viewModel.updateAssignment(newAssignment)
        }
        onAssignmentSavedListener?.invoke(newAssignment)

        dismiss()
    }
    fun setOnAssignmentSavedListener(listener: (Assignment) -> Unit) {
        onAssignmentSavedListener = listener
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}