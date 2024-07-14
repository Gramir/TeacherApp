package com.example.teacherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teacherapp.data.model.Teacher
import com.example.teacherapp.data.repository.TeacherRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: TeacherRepository) : ViewModel() {
    private val _loginResult = MutableLiveData<Teacher?>()
    val loginResult: LiveData<Teacher?> = _loginResult

    fun login(username: String, password: String) {
        viewModelScope.launch {
            val teacher = repository.login(username, password)
            Log.d("LoginViewModel", "Login attempt: username=$username, password=$password, result=$teacher")
            _loginResult.value = teacher
        }
    }
}