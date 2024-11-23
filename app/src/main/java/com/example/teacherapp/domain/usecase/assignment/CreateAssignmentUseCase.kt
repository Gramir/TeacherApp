package com.example.teacherapp.domain.usecase.assignment

import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.domain.repository.AssignmentRepository
import javax.inject.Inject

class CreateAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignment: Assignment): Result<Unit> {
        if (assignment.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Assignment title cannot be empty"))
        }
        if (assignment.dueDate.isBlank()) {
            return Result.failure(IllegalArgumentException("Due date cannot be empty"))
        }

        // Asegurar que el estado sea "Pendiente" para nuevas asignaciones
        val newAssignment = assignment.copy(status = "Pendiente")
        return repository.addAssignment(newAssignment)
    }
}