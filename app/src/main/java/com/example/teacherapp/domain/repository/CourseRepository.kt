package com.example.teacherapp.domain.repository

import com.example.teacherapp.domain.model.Course
import kotlinx.coroutines.flow.Flow

interface CourseRepository {
    fun getCoursesForTeacher(teacherId: String): Flow<List<Course>>
}