package com.example.teacherapp.domain.model

data class CourseStudent(
    val id: String = "",
    val courseId: String = "",
    val studentId: String = "",
    val enrollmentDate: Long = System.currentTimeMillis()
)