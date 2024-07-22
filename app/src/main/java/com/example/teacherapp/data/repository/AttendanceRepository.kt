package com.example.teacherapp.data.repository

import com.example.teacherapp.data.dao.AttendanceDao
import com.example.teacherapp.data.model.Attendance

class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    suspend fun getAttendanceForCourseAndDate(courseId: Int, date: String): List<Attendance> {
        return attendanceDao.getAttendanceForCourseAndDate(courseId, date)
    }

    suspend fun getAttendanceForCourse(courseId: Int): List<Attendance> {
        return attendanceDao.getAttendanceForCourse(courseId)
    }

    suspend fun insert(attendance: Attendance) {
        attendanceDao.insert(attendance)
    }

    suspend fun deleteAttendanceForCourseAndDate(courseId: Int, date: String) {
        attendanceDao.deleteAttendanceForCourseAndDate(courseId, date)
    }
}