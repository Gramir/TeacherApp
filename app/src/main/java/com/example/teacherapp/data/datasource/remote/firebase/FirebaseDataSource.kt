package com.example.teacherapp.data.datasource.remote.firebase

import com.example.teacherapp.domain.model.*
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceWithStudent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth : FirebaseAuth
) {
    suspend fun verifyTeacher(username: String, password: String): Result<Teacher> {
        return try {
            // 1. Buscar el profesor por username
            val teacherQuery = firestore.collection("teachers")
                .whereEqualTo("username", username)
                .get()
                .await()

            val teacherDoc = teacherQuery.documents.firstOrNull()
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val teacher = teacherDoc.toObject(Teacher::class.java)
                ?: return Result.failure(Exception("Error al obtener datos del usuario"))

            // 2. Convertir username a email para Firebase Auth
            val email = "${username.lowercase()}@teacherapp.com"

            try {
                // 3. Verificar credenciales con Firebase Auth
                auth.signInWithEmailAndPassword(email, password).await()
                Result.success(teacher)
            } catch (e: Exception) {
                Result.failure(Exception("Contraseña incorrecta"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTeacher(username: String, password: String, name: String): Result<Teacher> {
        return try {
            // 1. Verificar si el username ya existe
            val existingTeacher = firestore.collection("teachers")
                .whereEqualTo("username", username)
                .get()
                .await()
                .documents
                .firstOrNull()

            if (existingTeacher != null) {
                return Result.failure(Exception("El nombre de usuario ya existe"))
            }

            // 2. Crear usuario en Firebase Auth
            val email = "${username.lowercase()}@teacherapp.com"
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw Exception("Error al crear usuario")

            // 3. Crear documento en Firestore
            val teacher = Teacher(
                id = userId,
                username = username,
                name = name
            )

            firestore.collection("teachers")
                .document(userId)
                .set(teacher)
                .await()

            Result.success(teacher)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getTeacher(username: String): Flow<Teacher?> = callbackFlow {
        val subscription = firestore.collection("teachers")
            .whereEqualTo("username", username)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val teacher = snapshot?.documents?.firstOrNull()?.toObject(Teacher::class.java)
                trySend(teacher)
            }

        awaitClose { subscription.remove() }
    }

    fun getCoursesForTeacher(teacherId: String): Flow<List<Course>> = callbackFlow {
        val subscription = firestore.collection("courses")
            .whereEqualTo("teacherId", teacherId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val courses = snapshot?.documents?.mapNotNull {
                    it.toObject(Course::class.java)
                } ?: emptyList()
                trySend(courses)
            }

        awaitClose { subscription.remove() }
    }

    fun getStudentsForCourse(courseId: String): Flow<List<Student>> = callbackFlow {
        println("DEBUG_APP: ===== INICIO BÚSQUEDA DE ESTUDIANTES =====")
        println("DEBUG_APP: Buscando estudiantes para courseId: $courseId")

        // Verificar que la colección course_students existe y tiene el documento
        val courseStudentsCheck = firestore.collection("course_students")
            .whereEqualTo("courseId", courseId)
            .get()
            .await()

        println("DEBUG_APP: Documentos en course_students: ${courseStudentsCheck.documents.size}")
        courseStudentsCheck.documents.forEach { doc ->
            println("DEBUG_APP: Documento course_students:")
            println("DEBUG_APP: - ID: ${doc.id}")
            println("DEBUG_APP: - Data: ${doc.data}")
        }

        val subscription = firestore.collection("course_students")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("DEBUG_APP: Error en listener: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                println("DEBUG_APP: Snapshot recibido, documentos: ${snapshot?.documents?.size}")

                val studentIds = snapshot?.documents?.mapNotNull { doc ->
                    val data = doc.data
                    println("DEBUG_APP: Documento encontrado:")
                    data?.forEach { (key, value) ->
                        println("DEBUG_APP: - $key: $value")
                    }
                    doc.getString("studentId")
                } ?: emptyList()

                println("DEBUG_APP: StudentIds encontrados: $studentIds")

                if (studentIds.isEmpty()) {
                    println("DEBUG_APP: No se encontraron studentIds")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                // Buscar en students
                firestore.collection("students")
                    .whereIn("id", studentIds)
                    .get()
                    .addOnSuccessListener { studentsSnapshot ->
                        println("DEBUG_APP: Búsqueda en students completada")
                        println("DEBUG_APP: Documentos encontrados: ${studentsSnapshot.documents.size}")

                        studentsSnapshot.documents.forEach { doc ->
                            println("DEBUG_APP: Documento student:")
                            println("DEBUG_APP: - ID: ${doc.id}")
                            println("DEBUG_APP: - Data: ${doc.data}")
                        }

                        val students = studentsSnapshot.documents.mapNotNull {
                            it.toObject(Student::class.java)
                        }
                        println("DEBUG_APP: Estudiantes convertidos: ${students.size}")
                        trySend(students)
                    }
                    .addOnFailureListener { e ->
                        println("DEBUG_APP: Error al obtener students: ${e.message}")
                        close(e)
                    }
            }

        awaitClose {
            println("DEBUG_APP: Cerrando flujo")
            subscription.remove()
        }
    }
    fun getAssignmentsForCourse(courseId: String): Flow<List<Assignment>> = callbackFlow {
        val subscription = firestore.collection("assignments")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val assignments = snapshot?.documents?.mapNotNull {
                    it.toObject(Assignment::class.java)
                } ?: emptyList()
                trySend(assignments)
            }

        awaitClose { subscription.remove() }
    }

    fun getAttendanceForCourseAndDate(courseId: String, date: String): Flow<List<AttendanceWithStudent>> = callbackFlow {
        println("DEBUG_APP: Buscando asistencia para courseId: $courseId, fecha: $date")

        try {
            // 1. Primero obtener todos los estudiantes del curso
            val courseStudents = firestore.collection("course_students")
                .whereEqualTo("courseId", courseId)
                .get()
                .await()

            val studentIds = courseStudents.documents.mapNotNull { it.getString("studentId") }
            println("DEBUG_APP: Encontrados ${studentIds.size} estudiantes en el curso")

            if (studentIds.isEmpty()) {
                println("DEBUG_APP: No hay estudiantes en el curso")
                trySend(emptyList())
                return@callbackFlow
            }

            // 2. Obtener los datos de los estudiantes
            val studentsSnapshot = firestore.collection("students")
                .whereIn("id", studentIds)
                .get()
                .await()

            val studentsMap = studentsSnapshot.documents.mapNotNull { doc ->
                val student = doc.toObject(Student::class.java)
                if (student != null) {
                    student.id to student
                } else null
            }.toMap()

            println("DEBUG_APP: Datos de estudiantes obtenidos: ${studentsMap.size}")

            // 3. Obtener registros de asistencia existentes para la fecha
            val subscription = firestore.collection("attendance")
                .whereEqualTo("courseId", courseId)
                .whereEqualTo("date", date)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        println("DEBUG_APP: Error: ${error.message}")
                        close(error)
                        return@addSnapshotListener
                    }

                    // Convertir registros existentes a un mapa
                    val existingAttendance = snapshot?.documents?.mapNotNull {
                        it.toObject(Attendance::class.java)
                    }?.associateBy { it.studentId } ?: emptyMap()

                    println("DEBUG_APP: Registros de asistencia existentes: ${existingAttendance.size}")

                    // 4. Crear lista final de AttendanceWithStudent
                    val attendanceList = studentIds.mapNotNull { studentId ->
                        val student = studentsMap[studentId] ?: return@mapNotNull null
                        val attendance = existingAttendance[studentId]

                        AttendanceWithStudent(
                            id = attendance?.id ?: "${courseId}_${studentId}_${date}",
                            studentId = studentId,
                            studentName = "${student.name} ${student.lastName}",
                            courseId = courseId,
                            date = date,
                            present = attendance?.present ?: false // Por defecto no presente
                        )
                    }

                    println("DEBUG_APP: Enviando lista de asistencia, tamaño: ${attendanceList.size}")
                    trySend(attendanceList)
                }

            awaitClose {
                println("DEBUG_APP: Cerrando flujo de asistencia")
                subscription.remove()
            }

        } catch (e: Exception) {
            println("DEBUG_APP: Error en getAttendanceForCourseAndDate: ${e.message}")
            close(e)
        }
    }
    suspend fun addAssignment(assignment: Assignment): Result<Unit> = try {
        val document = firestore.collection("assignments").document()
        val assignmentWithId = assignment.copy(id = document.id)
        document.set(assignmentWithId).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun updateAssignment(assignment: Assignment): Result<Unit> = try {
        firestore.collection("assignments")
            .document(assignment.id)
            .set(assignment)
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun deleteAssignment(assignmentId: String): Result<Unit> = try {
        firestore.collection("assignments")
            .document(assignmentId)
            .delete()
            .await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun saveAttendance(attendance: List<Attendance>): Result<Unit> = try {
        val batch = firestore.batch()

        attendance.forEach { record ->
            val docRef = firestore.collection("attendance")
                .document("${record.courseId}_${record.studentId}_${record.date}")
            batch.set(docRef, record)
        }

        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}