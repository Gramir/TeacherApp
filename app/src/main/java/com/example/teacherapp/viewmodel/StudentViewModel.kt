package com.example.teacherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.model.Student
import com.example.teacherapp.data.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _students = MutableLiveData<List<Student>>()
    val students: LiveData<List<Student>> = _students

    fun getStudentsForCourse(courseId: Int) {
        viewModelScope.launch {
            _students.value = repository.getStudentsForCourse(courseId)
        }
    }
}