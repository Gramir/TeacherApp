package com.example.teacherapp.data.dao

import androidx.room.*
import com.example.teacherapp.data.model.Attendance

@Dao
interface AttendanceDao {
    @Query("SELECT s.id as studentId, :courseId as courseId, :date as date, " +
            "CASE WHEN a.present IS NULL THEN 0 ELSE a.present END as present, " +
            "s.name || ' ' || s.lastName as studentName " +
            "FROM students s " +
            "LEFT JOIN attendances a ON s.id = a.studentId AND a.courseId = :courseId AND a.date = :date " +
            "WHERE s.courseId = :courseId")
    suspend fun getStudentsWithAttendanceForCourseAndDate(courseId: Int, date: String): List<AttendanceWithStudentName>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAttendance(attendance: Attendance)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(attendances: List<Attendance>)

    @Query("DELETE FROM attendances WHERE courseId = :courseId AND date = :date")
    suspend fun deleteAttendanceForCourseAndDate(courseId: Int, date: String)

    @Update
    suspend fun update(attendance: Attendance)
}
data class AttendanceWithStudentName(
    val studentId: Int,
    val courseId: Int,
    val date: String,
    var present: Boolean,
    val studentName: String
)