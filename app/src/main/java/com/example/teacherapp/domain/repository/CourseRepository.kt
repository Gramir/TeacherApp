package com.example.teacherapp.domain.repository

import com.example.teacherapp.data.datasource.local.dao.CourseDao

class CourseRepository(private val courseDao: CourseDao) {
    suspend fun getCoursesForTeacher(teacherId: Int): List<Course> {
        return courseDao.getCoursesForTeacher(teacherId)
    }

    suspend fun insert(course: Course) {
        courseDao.insert(course)
    }
}