package com.example.teacherapp.domain.repository

import com.example.teacherapp.domain.model.Teacher
import kotlinx.coroutines.flow.Flow

interface TeacherRepository {
    suspend fun loginTeacher(username: String, password: String): Result<Teacher>
    fun getCurrentTeacher(): Flow<Teacher?>
}