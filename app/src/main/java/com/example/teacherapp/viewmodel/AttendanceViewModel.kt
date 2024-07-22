package com.example.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.model.Attendance
import com.example.teacherapp.data.repository.AttendanceRepository
import kotlinx.coroutines.launch

class AttendanceViewModel(private val repository: AttendanceRepository) : ViewModel() {
    private val _attendances = MutableLiveData<List<Attendance>>()
    val attendances: LiveData<List<Attendance>> = _attendances

    fun getAttendanceForCourseAndDate(courseId: Int, date: String) {
        viewModelScope.launch {
            _attendances.value = repository.getAttendanceForCourseAndDate(courseId, date)
        }
    }
    fun getAttendanceForCourse(courseId: Int) {
        viewModelScope.launch {
            val attendanceList = repository.getAttendanceForCourse(courseId)
            Log.d("AttendanceViewModel", "Retrieved ${attendanceList.size} attendances for course $courseId")
            _attendances.value = attendanceList
        }
    }

    fun saveAttendances(attendances: List<Attendance>) {
        viewModelScope.launch {
            attendances.forEach { attendance ->
                repository.insert(attendance)
            }
        }
    }

    fun deleteAttendanceForCourseAndDate(courseId: Int, date: String) {
        viewModelScope.launch {
            repository.deleteAttendanceForCourseAndDate(courseId, date)
        }
    }
}