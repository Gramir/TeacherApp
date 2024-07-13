package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Course

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE teacherId = :teacherId")
    suspend fun getCoursesForTeacher(teacherId: Int): List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course)
}