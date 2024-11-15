package com.example.teacherapp.presentation.viewmodel.course

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.domain.model.Course
import com.example.teacherapp.domain.usecase.course.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase
) : ViewModel() {

    private val _coursesState = MutableStateFlow<CoursesState>(CoursesState.Loading)
    val coursesState: StateFlow<CoursesState> = _coursesState.asStateFlow()

    fun getCourses(teacherId: String) {
        viewModelScope.launch {
            try {
                getCoursesUseCase(teacherId).collect { courses ->
                    _coursesState.value = CoursesState.Success(courses)
                }
            } catch (e: Exception) {
                _coursesState.value = CoursesState.Error(e.message ?: "Unknown error")
            }
        }
    }
}

sealed class CoursesState {
    data object Loading : CoursesState()
    data class Success(val courses: List<Course>) : CoursesState()
    data class Error(val message: String) : CoursesState()
}