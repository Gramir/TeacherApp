package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Student

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE courseId = :courseId")
    suspend fun getStudentsForCourse(courseId: Int): List<Student>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(students: List<Student>)
    @Update
    suspend fun update(student: Student)
    @Delete
    suspend fun delete(student: Student)
    @Query("DELETE FROM students WHERE courseId = :courseId")
    suspend fun deleteStudentsForCourse(courseId: Int)
    @Query("DELETE FROM students")
    suspend fun deleteAll()
}