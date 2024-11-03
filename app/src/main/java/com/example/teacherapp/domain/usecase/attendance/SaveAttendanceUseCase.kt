package com.example.teacherapp.domain.usecase.attendance

import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.domain.repository.AttendanceRepository
import javax.inject.Inject

class SaveAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(attendanceList: List<Attendance>): Result<Unit> {
        if (attendanceList.isEmpty()) {
            return Result.failure(IllegalArgumentException("Attendance list cannot be empty"))
        }
        return repository.saveMultipleAttendance(attendanceList)
    }
}