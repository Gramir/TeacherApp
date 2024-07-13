package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Assignment

@Dao
interface AssignmentDao {
    @Query("SELECT * FROM assignments WHERE courseId = :courseId")
    suspend fun getAssignmentsForCourse(courseId: Int): List<Assignment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(assignment: Assignment)

    @Update
    suspend fun update(assignment: Assignment)

    @Delete
    suspend fun delete(assignment: Assignment)
}