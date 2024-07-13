package com.example.teacherapp.data.repository

import com.example.teacherapp.data.dao.CourseDao
import com.example.teacherapp.data.model.Course

class CourseRepository(private val courseDao: CourseDao) {
    suspend fun getCoursesForTeacher(teacherId: Int): List<Course> {
        return courseDao.getCoursesForTeacher(teacherId)
    }

    suspend fun insert(course: Course) {
        courseDao.insert(course)
    }
}