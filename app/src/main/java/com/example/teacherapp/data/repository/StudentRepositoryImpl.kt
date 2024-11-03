package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Student
import com.example.teacherapp.domain.repository.StudentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StudentRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : StudentRepository {

    override fun getStudentsForCourse(courseId: String): Flow<List<Student>> {
        return firebaseDataSource.getStudentsForCourse(courseId)
    }
}