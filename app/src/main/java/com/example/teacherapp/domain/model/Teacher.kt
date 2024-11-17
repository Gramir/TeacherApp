package com.example.teacherapp.domain.model


data class Teacher(
    val id: String = "",
    val username: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "", //No se enviara a firebase
)