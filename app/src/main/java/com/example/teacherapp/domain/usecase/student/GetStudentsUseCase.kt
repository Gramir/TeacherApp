package com.example.teacherapp.domain.usecase.student

import com.example.teacherapp.domain.model.Student
import com.example.teacherapp.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStudentsUseCase @Inject constructor(
    private val repository: StudentRepository
) {
    operator fun invoke(courseId: String): Flow<List<Student>> {
        return repository.getStudentsForCourse(courseId)
    }
}