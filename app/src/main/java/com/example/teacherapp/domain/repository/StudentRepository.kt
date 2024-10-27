package com.example.teacherapp.domain.repository

import com.example.teacherapp.data.datasource.local.dao.StudentDao

class StudentRepository(private val studentDao: StudentDao) {
    suspend fun getStudentsForCourse(courseId: Int): List<Student> {
        return studentDao.getStudentsForCourse(courseId)
    }

    suspend fun insert(student: Student) {
        studentDao.insert(student)
    }
}