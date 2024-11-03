package com.example.teacherapp.domain.usecase.auth

import com.example.teacherapp.domain.model.Teacher
import com.example.teacherapp.domain.repository.TeacherRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: TeacherRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Teacher> {
        if (username.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Username and password cannot be empty"))
        }
        return repository.loginTeacher(username, password)
    }
}