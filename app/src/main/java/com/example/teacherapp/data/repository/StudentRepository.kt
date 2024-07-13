package com.example.teacherapp.data.repository

import com.example.teacherapp.data.dao.StudentDao
import com.example.teacherapp.data.model.Student

class StudentRepository(private val studentDao: StudentDao) {
    suspend fun getStudentsForCourse(courseId: Int): List<Student> {
        return studentDao.getStudentsForCourse(courseId)
    }

    suspend fun insert(student: Student) {
        studentDao.insert(student)
    }
}