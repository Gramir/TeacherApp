package com.example.teacherapp.domain.usecase.course

import com.example.teacherapp.domain.model.Course
import com.example.teacherapp.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCoursesUseCase @Inject constructor(
    private val repository: CourseRepository
) {
    operator fun invoke(teacherId: String): Flow<List<Course>> {
        return repository.getCoursesForTeacher(teacherId)
    }
}