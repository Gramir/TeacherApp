package com.example.teacherapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.teacherapp.data.repository.AttendanceRepository

class AttendanceViewModelFactory(private val repository: AttendanceRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AttendanceViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AttendanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}