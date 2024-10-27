package com.example.teacherapp.domain.repository

import com.example.teacherapp.data.datasource.local.dao.TeacherDao

class TeacherRepository(private val teacherDao: TeacherDao) {
    suspend fun login(username: String, password: String): Teacher? {
        return teacherDao.login(username, password)
    }

    suspend fun insert(teacher: Teacher) {
        teacherDao.insert(teacher)
    }
}