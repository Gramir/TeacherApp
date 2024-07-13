package com.example.teacherapp.data.repository

import com.example.teacherapp.data.dao.AssignmentDao
import com.example.teacherapp.data.model.Assignment

class AssignmentRepository(private val assignmentDao: AssignmentDao) {
    suspend fun getAssignmentsForCourse(courseId: Int): List<Assignment> {
        return assignmentDao.getAssignmentsForCourse(courseId)
    }

    suspend fun insert(assignment: Assignment) {
        assignmentDao.insert(assignment)
    }

    suspend fun update(assignment: Assignment) {
        assignmentDao.update(assignment)
    }

    suspend fun delete(assignment: Assignment) {
        assignmentDao.delete(assignment)
    }
}