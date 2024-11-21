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

    // StateFlow para mantener la lista de tareas
    private val _assignments = MutableStateFlow<List<Assignment>>(emptyList())

    init {
        println("DEBUG_VM: ViewModel inicializado")
    }

    fun getAssignments(courseId: String) {
        viewModelScope.launch {
            println("DEBUG_VM: Iniciando obtención de tareas para curso: $courseId")
            _assignmentsState.value = AssignmentsState.Loading

            try {
                getAssignmentsUseCase(courseId).collect { newAssignments ->
                    println("DEBUG_VM: Recibidas ${newAssignments.size} tareas")
                    newAssignments.forEach { assignment ->
                        println("DEBUG_VM: Tarea recibida - ID: ${assignment.id}, Título: ${assignment.title}")
                    }

                    _assignments.value = newAssignments
                    _assignmentsState.value = AssignmentsState.Success(newAssignments)
                }
            } catch (e: Exception) {
                println("DEBUG_VM: Error al obtener tareas: ${e.message}")
                _assignmentsState.value = AssignmentsState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun getAssignment(assignmentId: String) {
        println("DEBUG_VM: Buscando tarea con ID: $assignmentId")
        val currentAssignments = _assignments.value
        println("DEBUG_VM: Tareas en memoria: ${currentAssignments.size}")

        if (currentAssignments.isEmpty()) {
            println("DEBUG_VM: No hay tareas en memoria, recargando...")
            // Si no hay tareas en memoria, volvemos a cargarlas
            viewModelScope.launch {
                getAssignmentsUseCase(assignmentId.substringBefore('_')).collect { assignments ->
                    println("DEBUG_VM: Tareas recargadas: ${assignments.size}")
                    _assignments.value = assignments
                    val found = assignments.find { it.id == assignmentId }
                    println("DEBUG_VM: Tarea encontrada después de recargar: $found")
                    _selectedAssignment.value = found
                }
            }
            return
        }

        // Si hay tareas en memoria, buscar normalmente
        currentAssignments.forEach { assignment ->
            println("DEBUG_VM: Verificando tarea en memoria - ID: ${assignment.id}, Título: ${assignment.title}")
        }

        val assignment = currentAssignments.find { it.id == assignmentId }
        println("DEBUG_VM: Tarea encontrada: $assignment")

        if (assignment != null) {
            println("DEBUG_VM: Estableciendo tarea seleccionada")
            _selectedAssignment.value = assignment
        } else {
            println("DEBUG_VM: No se encontró la tarea $assignmentId")
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                updateAssignmentUseCase(assignment).onSuccess {
                    // Actualizar la lista en memoria
                    val currentList = _assignments.value.toMutableList()
                    val index = currentList.indexOfFirst { it.id == assignment.id }
                    if (index != -1) {
                        currentList[index] = assignment
                        _assignments.value = currentList
                    }
                    _actionState.value = ActionState.Success
                }.onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Error al actualizar la tarea")
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun createAssignment(assignment: Assignment) {
        viewModelScope.launch {
            _actionState.value = ActionState.Loading
            try {
                createAssignmentUseCase(assignment).onSuccess {
                    // Agregar a la lista en memoria
                    val currentList = _assignments.value.toMutableList()
                    currentList.add(assignment)
                    _assignments.value = currentList
                    _actionState.value = ActionState.Success
                }.onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Error al crear la tarea")
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
                    // Eliminar de la lista en memoria
                    val currentList = _assignments.value.toMutableList()
                    currentList.removeAll { it.id == assignmentId }
                    _assignments.value = currentList
                    _actionState.value = ActionState.Success
                }.onFailure { error ->
                    _actionState.value = ActionState.Error(error.message ?: "Error al eliminar la tarea")
                }
            } catch (e: Exception) {
                _actionState.value = ActionState.Error(e.message ?: "Error desconocido")
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