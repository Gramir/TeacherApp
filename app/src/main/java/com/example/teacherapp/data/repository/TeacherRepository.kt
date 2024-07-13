package com.example.teacherapp.data.repository

import com.example.teacherapp.data.dao.TeacherDao
import com.example.teacherapp.data.model.Teacher

class TeacherRepository(private val teacherDao: TeacherDao) {
    suspend fun login(username: String, password: String): Teacher? {
        return teacherDao.login(username, password)
    }

    suspend fun insert(teacher: Teacher) {
        teacherDao.insert(teacher)
    }
}