package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.domain.repository.AttendanceRepository
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceWithStudent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : AttendanceRepository {

    override fun getAttendanceForCourseAndDate(courseId: String, date: String): Flow<List<AttendanceWithStudent>> {
        return firebaseDataSource.getAttendanceForCourseAndDate(courseId, date)
    }

    override suspend fun saveAttendance(attendanceList: List<AttendanceWithStudent>): Result<Unit> {
        return firebaseDataSource.saveAttendance(attendanceList.map { it.toAttendance() })
    }

    override suspend fun saveMultipleAttendance(attendanceLists: List<Attendance>): Result<Unit> {
        return firebaseDataSource.saveAttendance(attendanceLists)
    }
}