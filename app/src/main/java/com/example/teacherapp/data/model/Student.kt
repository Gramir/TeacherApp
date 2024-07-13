package com.example.teacherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: Int,
    val name: String,
    val lastName: String,
    val birthDate: String,
    val phone: String,
    val email: String,
    val courseId: Int
)