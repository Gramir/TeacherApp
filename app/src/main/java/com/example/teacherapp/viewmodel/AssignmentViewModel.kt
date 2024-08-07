package com.example.teacherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.model.Assignment
import com.example.teacherapp.data.repository.AssignmentRepository
import kotlinx.coroutines.launch

class AssignmentViewModel(private val repository: AssignmentRepository) : ViewModel() {
    private val _assignments = MutableLiveData<List<Assignment>>()
    val assignments: LiveData<List<Assignment>> = _assignments

    fun getAssignmentsForCourse(courseId: Int) {
        viewModelScope.launch {
            _assignments.value = repository.getAssignmentsForCourse(courseId)
        }
    }

    fun saveAssignment(assignment: Assignment) {
        viewModelScope.launch {
            val newId = repository.getNextId()
            val newAssignment = assignment.copy(id = newId)
            repository.insert(newAssignment)
            getAssignmentsForCourse(assignment.courseId)
        }
    }

    fun updateAssignment(assignment: Assignment) {
        viewModelScope.launch {
            repository.update(assignment)
            getAssignmentsForCourse(assignment.courseId)
        }
    }

    fun deleteAssignment(assignment: Assignment) {
        viewModelScope.launch {
            repository.delete(assignment)
            getAssignmentsForCourse(assignment.courseId)
        }
    }
}