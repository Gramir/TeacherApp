package com.example.teacherapp.domain.repository

import com.example.teacherapp.domain.model.Assignment
import kotlinx.coroutines.flow.Flow

interface AssignmentRepository {
    fun getAssignmentsForCourse(courseId: String): Flow<List<Assignment>>
    suspend fun addAssignment(assignment: Assignment): Result<Unit>
    suspend fun updateAssignment(assignment: Assignment): Result<Unit>
    suspend fun deleteAssignment(assignmentId: String): Result<Unit>
}