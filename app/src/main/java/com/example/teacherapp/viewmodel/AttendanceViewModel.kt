package com.example.teacherapp.viewmodel

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

    fun saveAttendance(attendance: Attendance) {
        viewModelScope.launch {
            repository.insert(attendance)
        }
    }

    fun deleteAttendanceForCourseAndDate(courseId: Int, date: String) {
        viewModelScope.launch {
            repository.deleteAttendanceForCourseAndDate(courseId, date)
        }
    }
}