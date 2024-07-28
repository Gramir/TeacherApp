package com.example.teacherapp.data.model

import androidx.room.Entity

@Entity(tableName = "attendances", primaryKeys = ["studentId", "courseId", "date"])
data class Attendance(
    val studentId: Int,
    val courseId: Int,
    val date: String,
    var present: Boolean
)