package com.example.teacherapp.presentation.viewmodel.student


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.domain.model.Student
import com.example.teacherapp.domain.usecase.student.GetStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase
) : ViewModel() {
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private val _uiState = MutableStateFlow<StudentUiState>(StudentUiState.Loading)
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    fun getStudentsForCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.value = StudentUiState.Loading
            try {
                getStudentsUseCase(courseId).collect { students ->
                    _students.value = students
                    _uiState.value = StudentUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = StudentUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}