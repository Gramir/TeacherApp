package com.example.teacherapp.presentation.viewmodel.attendance

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.domain.model.Attendance
import com.example.teacherapp.domain.usecase.attendance.GetAttendanceUseCase
import com.example.teacherapp.domain.usecase.attendance.SaveAttendanceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AttendanceViewModel"

@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val getAttendanceUseCase: GetAttendanceUseCase,
    private val saveAttendanceUseCase: SaveAttendanceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Loading)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    private val _attendances = MutableStateFlow<List<AttendanceWithStudent>>(emptyList())
    val attendances: StateFlow<List<AttendanceWithStudent>> = _attendances.asStateFlow()

    fun getStudentsWithAttendanceForCourseAndDate(courseId: String, date: String) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                getAttendanceUseCase(courseId, date).collect { attendanceList ->
                    _attendances.value = attendanceList
                    _uiState.value = AttendanceUiState.Success
                }
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    fun updateAttendanceStatus(studentId: String, isPresent: Boolean) {
        val currentList = _attendances.value.toMutableList()
        val index = currentList.indexOfFirst { it.studentId == studentId }
        if (index != -1) {
            currentList[index] = currentList[index].copy(present = isPresent)
            _attendances.value = currentList
        }
    }

    fun saveAttendance(attendanceList: List<AttendanceWithStudent>) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            try {
                val result = saveAttendanceUseCase(attendanceList)
                result.fold(
                    onSuccess = { _uiState.value = AttendanceUiState.Success },
                    onFailure = { error ->
                        _uiState.value = AttendanceUiState.Error(error.message ?: "Error al guardar")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }
}

sealed class AttendanceUiState {
    data object Loading : AttendanceUiState()
    data object Success : AttendanceUiState()
    data class Error(val message: String) : AttendanceUiState()
}

data class AttendanceWithStudent(
    val id: String = "",
    val studentId: String,
    val studentName: String,
    val courseId: String,
    val date: String,
    val present: Boolean
) {
    fun toAttendance() = Attendance(
        id = id,
        studentId = studentId,
        courseId = courseId,
        date = date,
        present = present
    )
}

private fun Attendance.toAttendanceWithStudent() = AttendanceWithStudent(
    id = id,
    studentId = studentId,
    studentName = "", // Se obtendrá del estudiante en Firebase
    courseId = courseId,
    date = date,
    present = present
)