package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Student

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE courseId = :courseId")
    suspend fun getStudentsForCourse(courseId: Int): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)
}