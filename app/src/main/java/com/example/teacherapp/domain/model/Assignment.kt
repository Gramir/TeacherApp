package com.example.teacherapp.domain.model

data class Assignment(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val dueDate: String = "",
    val courseId: String = "",
    val status: String = "pending"
)