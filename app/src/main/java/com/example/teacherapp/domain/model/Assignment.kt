package com.example.teacherapp.domain.model


data class Assignment(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val courseId: String = "",
    val dueDate: String = "",
    val status: String = "pending",
    val createdAt: Long = System.currentTimeMillis()
)