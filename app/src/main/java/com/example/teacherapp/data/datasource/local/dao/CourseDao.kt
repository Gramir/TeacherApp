package com.example.teacherapp.data.datasource.local.dao

import androidx.room.*

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses WHERE teacherId = :teacherId")
    suspend fun getCoursesForTeacher(teacherId: Int): List<Course>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(course: Course)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<Course>)

}