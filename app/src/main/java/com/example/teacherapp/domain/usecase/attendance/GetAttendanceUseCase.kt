package com.example.teacherapp.domain.usecase.attendance

import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.domain.repository.AttendanceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    operator fun invoke(courseId: String, date: String): Flow<List<Attendance>> {
        return repository.getAttendanceForCourseAndDate(courseId, date)
    }
}