package com.example.teacherapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey val id: Int,
    val username: String,
    val password: String,
    val name: String,
    val email: String
)