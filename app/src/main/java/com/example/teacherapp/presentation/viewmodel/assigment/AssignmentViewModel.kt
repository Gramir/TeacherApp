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
    val assignmentsState: StateFlow<AssignmentsState> = _assignmentsState.asStateFlow()

    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    private val _selectedAssignment = MutableStateFlow<Assignment?>(null)
    val selectedAssignment: StateFlow<Assignment?> = _selectedAssignment.asStateFlow()

    private val assignments = mutableListOf<Assignment>()

    fun getAssignments(courseId: String) {
        viewModelScope.launch {
            _assignmentsState.value = AssignmentsState.Loading
            try {
                getAssignmentsUseCase(courseId).collect { newAssignments ->
                    assignments.clear()
                    assignments.addAll(newAssignments)
                    _assignmentsState.value = AssignmentsState.Success(newAssignments)
                }
            } catch (e: Exception) {
                _assignmentsState.value = AssignmentsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAssignment(assignmentId: String) {
        println("DEBUG: Buscando assignment con ID: $assignmentId")
        println("DEBUG: Assignments disponibles: ${assignments.size}")
        viewModelScope.launch {
            val assignment = assignments.find { it.id == assignmentId }
            println("DEBUG: Assignment encontrado: $assignment")
            _selectedAssignment.value = assignment
        }
    }

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                createAssignmentUseCase(assignment).onSuccess {
                    _actionState.value = ActionState.Success
                    getAssignments(assignment.courseId)
                }.onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Error al crear la tarea")
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                updateAssignmentUseCase(assignment).onSuccess {
                    _actionState.value = ActionState.Success
                    getAssignments(assignment.courseId)
                }.onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Error al actualizar la tarea")
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun deleteAssignment(assignmentId: String) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                deleteAssignmentUseCase(assignmentId).onSuccess {
                    _actionState.value = ActionState.Success
                    val courseId = assignments.find { it.id == assignmentId }?.courseId
                    courseId?.let { getAssignments(it) }
                }.onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Error al eliminar la tarea")
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun clearSelectedAssignment() {
        println("DEBUG: Limpiando assignment seleccionado")
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