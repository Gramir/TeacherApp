package com.example.teacherapp.data.repository

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AttendanceRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : AttendanceRepository {

    override fun getAttendanceForCourseAndDate(courseId: String, date: String): Flow<List<Attendance>> {
        return firebaseDataSource.getAttendanceForCourseAndDate(courseId, date)
    }

    override suspend fun saveAttendance(attendance: Attendance): Result<Unit> {
        return firebaseDataSource.saveAttendance(listOf(attendance))
    }

    override suspend fun saveMultipleAttendance(attendanceList: List<Attendance>): Result<Unit> {
        return firebaseDataSource.saveAttendance(attendanceList)
    }
}