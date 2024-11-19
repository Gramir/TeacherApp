package com.example.teacherapp.domain.usecase.attendance

import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.domain.repository.AttendanceRepository
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceWithStudent
import javax.inject.Inject

class SaveAttendanceUseCase @Inject constructor(
    private val repository: AttendanceRepository
) {
    suspend operator fun invoke(attendanceList: List<AttendanceWithStudent>): Result<Unit> {
        if (attendanceList.isEmpty()) {
            return Result.failure(IllegalArgumentException("La lista de asistencia no puede estar vacía"))
        }
        return repository.saveAttendance(attendanceList)
    }
}