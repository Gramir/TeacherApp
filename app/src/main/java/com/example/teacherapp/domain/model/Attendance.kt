package com.example.teacherapp.domain.model

data class Attendance(
    val id: String = "",
    val studentId: String = "",
    val courseId: String = "",
    val date: String = "",
    val present: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)