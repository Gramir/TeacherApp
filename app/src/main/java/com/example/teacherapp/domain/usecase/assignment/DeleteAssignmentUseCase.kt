package com.example.teacherapp.domain.usecase.assignment

import com.example.teacherapp.domain.repository.AssignmentRepository
import javax.inject.Inject

class DeleteAssignmentUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    suspend operator fun invoke(assignmentId: String): Result<Unit> {
        if (assignmentId.isBlank()) {
            return Result.failure(IllegalArgumentException("Assignment ID cannot be empty"))
        }
        return repository.deleteAssignment(assignmentId)
    }
}