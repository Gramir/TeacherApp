package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Teacher
import com.example.teacherapp.domain.repository.TeacherRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TeacherRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : TeacherRepository {

    private var currentTeacher: Teacher? = null

    override suspend fun loginTeacher(username: String, password: String): Result<Teacher> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(Exception("Usuario y contraseña son requeridos"))
        }

        return try {
            firebaseDataSource.verifyTeacher(username, password).also { result ->
                result.onSuccess { teacher ->
                    currentTeacher = teacher
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentTeacher(): Flow<Teacher?> {
        return callbackFlow {
            if (currentTeacher == null) {
                trySend(null)
                close()
                return@callbackFlow
            }

            val listener = firebaseDataSource.getTeacher(currentTeacher?.username ?: "")
                .collect { teacher ->
                    trySend(teacher)
                }

            awaitClose { }
        }
    }
}