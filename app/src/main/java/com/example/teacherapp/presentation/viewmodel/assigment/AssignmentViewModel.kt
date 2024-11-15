package com.example.teacherapp.presentation.viewmodel.assigment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.domain.usecase.assignment.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssignmentViewModel @Inject constructor(
    private val getAssignmentsUseCase: GetAssignmentsUseCase,
    private val createAssignmentUseCase: CreateAssignmentUseCase,
    private val updateAssignmentUseCase: UpdateAssignmentUseCase,
    private val deleteAssignmentUseCase: DeleteAssignmentUseCase
) : ViewModel() {

    private val _assignmentsState = MutableStateFlow<AssignmentsState>(AssignmentsState.Loading)
    val assignmentsState: StateFlow<AssignmentsState> = _assignmentsState

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState

    private val _selectedAssignment = MutableStateFlow<Assignment?>(null)
    val selectedAssignment: StateFlow<Assignment?> = _selectedAssignment

    private val assignmentsList = mutableListOf<Assignment>()

    fun getAssignments(courseId: String) {
        viewModelScope.launch {
            _assignmentsState.value = AssignmentsState.Loading
            try {
                getAssignmentsUseCase(courseId).collect { assignments ->
                    assignmentsList.clear()
                    assignmentsList.addAll(assignments)
                    _assignmentsState.value = AssignmentsState.Success(assignments)
                }
            } catch (e: Exception) {
                _assignmentsState.value = AssignmentsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getAssignment(assignmentId: String) {
        _selectedAssignment.value = assignmentsList.find { it.id == assignmentId }
    }

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                createAssignmentUseCase(assignment)
                _actionState.value = ActionState.Success
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                updateAssignmentUseCase(assignment)
                _actionState.value = ActionState.Success
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                deleteAssignmentUseCase(assignmentId)
                _actionState.value = ActionState.Success
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearSelectedAssignment() {
        _selectedAssignment.value = null
    }

    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }
}

sealed class AssignmentsState {
    data object Loading : AssignmentsState()
    data class Success(val assignments: List<Assignment>) : AssignmentsState()
    data class Error(val message: String) : AssignmentsState()
}

sealed class ActionState {
    data object Idle : ActionState()
    data object Loading : ActionState()
    data object Success : ActionState()
    data class Error(val message: String) : ActionState()
}