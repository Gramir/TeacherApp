package com.example.teacherapp.presentation.viewmodel.student

sealed class StudentUiState {
    data object Loading : StudentUiState()
    data object Success : StudentUiState()
    data class Error(val message: String) : StudentUiState()
}
