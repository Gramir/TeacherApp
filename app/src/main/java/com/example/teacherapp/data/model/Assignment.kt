package com.example.teacherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    val courseId: Int,
    val status: String
)