package com.example.teacherapp.domain.repository

import android.util.Log
import com.example.teacherapp.data.datasource.local.dao.AttendanceDao
import com.example.teacherapp.data.datasource.local.dao.AttendanceWithStudentName

class AttendanceRepository(private val attendanceDao: AttendanceDao) {

    suspend fun getStudentsWithAttendanceForCourseAndDate(courseId: Int, date: String): List<AttendanceWithStudentName> {
        Log.d("AttendanceRepository", "Fetching attendances for course $courseId on date $date")
        val attendances = attendanceDao.getStudentsWithAttendanceForCourseAndDate(courseId, date)
        Log.d("AttendanceRepository", "Retrieved ${attendances.size} attendances")
        if (attendances.isEmpty()) {
            Log.d("AttendanceRepository", "No attendances retrieved")
        } else {
            Log.d("AttendanceRepository", "First attendance: ${attendances[0]}")
        }
        return attendances
    }

    suspend fun saveAttendance(attendance: Attendance) {
        attendanceDao.insertOrUpdateAttendance(attendance)
    }
}