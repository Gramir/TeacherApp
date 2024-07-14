package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Attendance

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendances WHERE courseId = :courseId AND date = :date")
    suspend fun getAttendanceForCourseAndDate(courseId: Int, date: String): List<Attendance>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: Attendance)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendances: List<Attendance>)

    @Query("DELETE FROM attendances WHERE courseId = :courseId AND date = :date")
    suspend fun deleteAttendanceForCourseAndDate(courseId: Int, date: String)

    @Update
    suspend fun update(attendance: Attendance)
}