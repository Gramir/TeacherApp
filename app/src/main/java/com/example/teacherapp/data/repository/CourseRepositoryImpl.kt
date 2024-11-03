package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Course
import com.example.teacherapp.domain.repository.CourseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CourseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : CourseRepository {

    override fun getCoursesForTeacher(teacherId: String): Flow<List<Course>> {
        return firebaseDataSource.getCoursesForTeacher(teacherId)
    }
}