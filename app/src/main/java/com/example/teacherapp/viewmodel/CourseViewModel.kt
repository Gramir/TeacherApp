package com.example.teacherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.model.Course
import com.example.teacherapp.data.repository.CourseRepository
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {
    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    fun getCoursesForTeacher(teacherId: Int) {
        viewModelScope.launch {
            _courses.value = repository.getCoursesForTeacher(teacherId)
        }
    }
}