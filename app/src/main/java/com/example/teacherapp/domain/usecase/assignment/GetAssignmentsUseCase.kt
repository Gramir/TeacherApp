package com.example.teacherapp.domain.usecase.assignment

import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAssignmentsUseCase @Inject constructor(
    private val repository: AssignmentRepository
) {
    operator fun invoke(courseId: String): Flow<List<Assignment>> {
        return repository.getAssignmentsForCourse(courseId)
    }
}
