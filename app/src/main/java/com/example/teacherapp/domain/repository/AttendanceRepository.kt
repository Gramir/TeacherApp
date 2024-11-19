package com.example.teacherapp.domain.repository

import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceWithStudent
import kotlinx.coroutines.flow.Flow

interface AttendanceRepository {
    fun getAttendanceForCourseAndDate(courseId: String, date: String): Flow<List<AttendanceWithStudent>>
    suspend fun saveAttendance(attendance: List<AttendanceWithStudent>): Result<Unit>
    suspend fun saveMultipleAttendance(attendanceList: List<Attendance>): Result<Unit>
}