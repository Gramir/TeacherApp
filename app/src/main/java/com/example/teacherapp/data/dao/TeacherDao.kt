package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Teacher

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): Teacher?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(teacher: Teacher)
}