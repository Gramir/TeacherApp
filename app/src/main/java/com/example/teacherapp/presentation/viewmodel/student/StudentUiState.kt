package com.example.teacherapp.presentation.viewmodel.student

sealed class StudentUiState {
    object Loading : StudentUiState()
    object Success : StudentUiState()
    data class Error(val message: String) : StudentUiState()
}
