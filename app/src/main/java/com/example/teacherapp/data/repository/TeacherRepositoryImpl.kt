package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Teacher
import com.example.teacherapp.domain.repository.TeacherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : TeacherRepository {

    private var currentTeacher: Teacher? = null

    override suspend fun loginTeacher(username: String, password: String): Result<Teacher> {
        return try {
            val teacher = firebaseDataSource.getTeacher(username).first()
            if (teacher != null) {
                currentTeacher = teacher
                Result.success(teacher)
            } else {
                Result.failure(Exception("Invalid credentials"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentTeacher(): Flow<Teacher?> {
        return firebaseDataSource.getTeacher(currentTeacher?.username ?: "")
    }
}