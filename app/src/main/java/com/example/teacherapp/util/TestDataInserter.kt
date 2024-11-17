package com.example.teacherapp.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.teacherapp.domain.model.*
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalDate.now
import javax.inject.Inject

class TestDataInserter @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend fun insertTestData() {
        try {
            // 1. Crear profesor de prueba
            val email = "teacher1@teacherapp.com"
            val password = "123456"

            // Intentar crear usuario en Auth
            val authResult = try {
                auth.createUserWithEmailAndPassword(email, password).await()
            } catch (e: Exception) {
                // Si el usuario ya existe, intentar obtenerlo
                auth.signInWithEmailAndPassword(email, password).await()
            }

            val teacherId = authResult.user?.uid ?: throw Exception("Error al obtener userId")

            // Crear o actualizar en Firestore
            val teacher = Teacher(
                id = teacherId,
                username = "teacher1",
                name = "Juan Pérez",
                email = email
            )

            firestore.collection("teachers")
                .document(teacherId)
                .set(teacher)
                .await()

            // 2. Crear cursos
            val course1 = createCourse(
                name = "Matemáticas 101",
                teacherId = teacherId,
                code = "MAT101",
                semester = "2024-1",
                schedule = "Lunes y Miércoles 9:00-11:00"
            )

            val course2 = createCourse(
                name = "Física Básica",
                teacherId = teacherId,
                code = "FIS101",
                semester = "2024-1",
                schedule = "Martes y Jueves 11:00-13:00"
            )

            // 3. Crear estudiantes
            val student1 = createStudent(
                name = "Ana",
                lastName = "García",
                birthDate = "1999-05-15",
                phone = "555-0001",
                email = "ana.garcia@email.com"
            )

            val student2 = createStudent(
                name = "Carlos",
                lastName = "Martínez",
                birthDate = "2000-03-20",
                phone = "555-0002",
                email = "carlos.martinez@email.com"
            )

            // 4. Matricular estudiantes en cursos
            enrollStudentInCourse(student1.id, course1.id)
            enrollStudentInCourse(student1.id, course2.id)
            enrollStudentInCourse(student2.id, course1.id)

            // 5. Crear asignaciones para los cursos
            createAssignment(
                title = "Límites y Derivadas",
                description = "Ejercicios del capítulo 3",
                courseId = course1.id,
                dueDate = "2024-02-20"
            )

            createAssignment(
                title = "Vectores y Fuerzas",
                description = "Problemas de física vectorial",
                courseId = course2.id,
                dueDate = "2024-02-25"
            )

            // 6. Crear registros de asistencia
            val today = now().toString()
            createAttendance(student1.id, course1.id, today, true)
            createAttendance(student2.id, course1.id, today, true)
            createAttendance(student1.id, course2.id, today, false)

        } catch (e: Exception) {
            throw Exception("Error al crear datos de prueba: ${e.message}")
        }
    }

    private suspend fun createCourse(
        name: String,
        teacherId: String,
        code: String,
        semester: String,
        schedule: String
    ): Course {
        val courseRef = firestore.collection("courses").document()
        val course = Course(
            id = courseRef.id,
            name = name,
            teacherId = teacherId,
            code = code,
            semester = semester,
            schedule = schedule
        )
        courseRef.set(course).await()
        return course
    }

    private suspend fun createStudent(
        name: String,
        lastName: String,
        birthDate: String,
        phone: String,
        email: String
    ): Student {
        val studentRef = firestore.collection("students").document()
        val student = Student(
            id = studentRef.id,
            name = name,
            lastName = lastName,
            birthDate = birthDate,
            phone = phone,
            email = email
        )
        studentRef.set(student).await()
        return student
    }

    private suspend fun enrollStudentInCourse(studentId: String, courseId: String) {
        val enrollmentRef = firestore.collection("course_students").document()
        val enrollment = CourseStudent(
            id = enrollmentRef.id,
            studentId = studentId,
            courseId = courseId
        )
        enrollmentRef.set(enrollment).await()
    }

    private suspend fun createAssignment(
        title: String,
        description: String,
        courseId: String,
        dueDate: String
    ) {
        val assignmentRef = firestore.collection("assignments").document()
        val assignment = Assignment(
            id = assignmentRef.id,
            title = title,
            description = description,
            courseId = courseId,
            dueDate = dueDate
        )
        assignmentRef.set(assignment).await()
    }

    private suspend fun createAttendance(
        studentId: String,
        courseId: String,
        date: String,
        present: Boolean
    ) {
        val attendanceRef = firestore.collection("attendance").document()
        val attendance = Attendance(
            id = attendanceRef.id,
            studentId = studentId,
            courseId = courseId,
            date = date,
            present = present
        )
        attendanceRef.set(attendance).await()
    }
}