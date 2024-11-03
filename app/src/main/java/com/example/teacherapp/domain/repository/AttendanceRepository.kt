package com.example.teacherapp.domain.repository

import com.example.teacherapp.domain.model.Attendance
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getAttendanceForCourseAndDate(courseId: String, date: String): Flow<List<Attendance>>
    suspend fun saveAttendance(attendance: Attendance): Result<Unit>
    suspend fun saveMultipleAttendance(attendanceList: List<Attendance>): Result<Unit>
}