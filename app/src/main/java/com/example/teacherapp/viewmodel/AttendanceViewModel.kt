package com.example.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.dao.AttendanceWithStudentName
import com.example.teacherapp.data.model.Attendance
import com.example.teacherapp.data.repository.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {
    private val _attendances = MutableLiveData<List<AttendanceWithStudentName>>()
    val attendances: LiveData<List<AttendanceWithStudentName>> = _attendances

    fun getStudentsWithAttendanceForCourseAndDate(courseId: Int, date: String) {
        viewModelScope.launch {
            Log.d("AttendanceViewModel", "Fetching attendances for course $courseId on date $date")
            val attendanceList = repository.getStudentsWithAttendanceForCourseAndDate(courseId, date)
            Log.d("AttendanceViewModel", "Retrieved ${attendanceList.size} attendances")
            if (attendanceList.isEmpty()) {
                Log.d("AttendanceViewModel", "No attendances retrieved")
            } else {
                Log.d("AttendanceViewModel", "First attendance: ${attendanceList[0]}")
            }
            _attendances.value = attendanceList
        }
    }

    fun saveAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.saveAttendance(attendance)
        }
    }
}