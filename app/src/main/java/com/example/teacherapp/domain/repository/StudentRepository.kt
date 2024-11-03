package com.example.teacherapp.domain.repository

import com.example.teacherapp.domain.model.Student
import kotlinx.coroutines.flow.Flow

interface StudentRepository {
    fun getStudentsForCourse(courseId: String): Flow<List<Student>>
    suspend fun addStudent(student: Student): Result<Unit>
    suspend fun updateStudent(student: Student): Result<Unit>
    suspend fun deleteStudent(studentId: String): Result<Unit>
}