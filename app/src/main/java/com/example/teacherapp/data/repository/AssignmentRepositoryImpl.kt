package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Assignment
import com.example.teacherapp.domain.repository.AssignmentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssignmentRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : AssignmentRepository {

    override fun getAssignmentsForCourse(courseId: String): Flow<List<Assignment>> {
        return firebaseDataSource.getAssignmentsForCourse(courseId)
    }

    override suspend fun addAssignment(assignment: Assignment): Result<Unit> {
        return firebaseDataSource.addAssignment(assignment)
    }

    override suspend fun updateAssignment(assignment: Assignment): Result<Unit> {
        return firebaseDataSource.updateAssignment(assignment)
    }

    override suspend fun deleteAssignment(assignmentId: String): Result<Unit> {
        return firebaseDataSource.deleteAssignment(assignmentId)
    }
}