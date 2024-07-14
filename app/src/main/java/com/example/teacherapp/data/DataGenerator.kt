package com.example.teacherapp.data

import com.example.teacherapp.data.model.*

object DataGenerator {
    fun generateTestData(): TestData {
        val teacher = Teacher(1, "teacher1", "123456", "John Doe", "john.doe@example.com")

        val courses = listOf(
            Course(1, "Mathematics", 1),
            Course(2, "Physics", 1),
            Course(3, "Chemistry", 1)
        )

        val students = listOf(
            Student(1, "Alice", "Johnson", "2000-05-15", "123-456-7890", "alice@example.com", 1),
            Student(2, "Bob", "Smith", "2001-03-22", "234-567-8901", "bob@example.com", 1),
            Student(3, "Charlie", "Brown", "2000-11-30", "345-678-9012", "charlie@example.com", 1),
            Student(4, "David", "Wilson", "2001-07-18", "456-789-0123", "david@example.com", 2),
            Student(5, "Eva", "Taylor", "2000-09-25", "567-890-1234", "eva@example.com", 2),
            Student(6, "Frank", "Anderson", "2001-01-10", "678-901-2345", "frank@example.com", 3)
        )

        val assignments = listOf(
            Assignment(1, "Math Homework 1", "Complete exercises 1-5", "2023-05-15", 1, "pending"),
            Assignment(2, "Physics Lab Report", "Write a report on the pendulum experiment", "2023-05-20", 2, "pending"),
            Assignment(3, "Chemistry Quiz", "Prepare for the quiz on chemical bonds", "2023-05-18", 3, "pending")
        )

        val attendances = listOf(
            Attendance(1, 1, "2023-05-10", true, "Alice Johnson"),
            Attendance(2, 1, "2023-05-10", false, "Bob Smith"),
            Attendance(3, 1, "2023-05-10", true, "Charlie Brown"),
            Attendance(4, 2, "2023-05-11", true, "David Wilson"),
            Attendance(5, 2, "2023-05-11", true, "Eva Taylor"),
            Attendance(6, 3, "2023-05-12", false, "Frank Anderson")
        )

        return TestData(teacher, courses, students, assignments, attendances)
    }
}

data class TestData(
    val teacher: Teacher,
    val courses: List<Course>,
    val students: List<Student>,
    val assignments: List<Assignment>,
    val attendances: List<Attendance>
)