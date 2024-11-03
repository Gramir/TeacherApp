package com.example.teacherapp.presentation.viewmodel.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.service.NotificationService
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
    private val deleteAssignmentUseCase: DeleteAssignmentUseCase,
    private val notificationService: NotificationService
) : ViewModel() {

    private val _assignmentsState = MutableStateFlow<AssignmentsState>(AssignmentsState.Loading)
    val assignmentsState: StateFlow<AssignmentsState> = _assignmentsState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    fun getAssignments(courseId: String) {
        viewModelScope.launch {
            try {
                getAssignmentsUseCase(courseId).collect { assignments ->
                    _assignmentsState.value = AssignmentsState.Success(assignments)
                }
            } catch (e: Exception) {
                _assignmentsState.value = AssignmentsState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                createAssignmentUseCase(assignment).fold(
                    onSuccess = {
                        _actionState.value = ActionState.Success
                        notificationService.showNewAssignmentNotification(assignment)
                    },
                    onFailure = { e ->
                        _actionState.value = ActionState.Error(e.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                updateAssignmentUseCase(assignment).fold(
                    onSuccess = {
                        _actionState.value = ActionState.Success
                    },
                    onFailure = { e ->
                        _actionState.value = ActionState.Error(e.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                deleteAssignmentUseCase(assignmentId).fold(
                    onSuccess = {
                        _actionState.value = ActionState.Success
                    },
                    onFailure = { e ->
                        _actionState.value = ActionState.Error(e.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetActionState() {
        _actionState.value = ActionState.Idle
    }
}

sealed class AssignmentsState {
    object Loading : AssignmentsState()
    data class Success(val assignments: List<Assignment>) : AssignmentsState()
    data class Error(val message: String) : AssignmentsState()
}

sealed class ActionState {
    object Idle : ActionState()
    object Loading : ActionState()
    object Success : ActionState()
    data class Error(val message: String) : ActionState()
}