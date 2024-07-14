package com.example.teacherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.model.Teacher
import com.example.teacherapp.data.repository.TeacherRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: TeacherRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val teacher = repository.login(username, password)
            if (teacher != null) {
                _loginResult.value = LoginResult.Success(teacher)
            } else {
                _loginResult.value = LoginResult.Error
            }
        }
    }
}

sealed class LoginResult {
    data class Success(val teacher: Teacher) : LoginResult()
    data object Error : LoginResult()
}