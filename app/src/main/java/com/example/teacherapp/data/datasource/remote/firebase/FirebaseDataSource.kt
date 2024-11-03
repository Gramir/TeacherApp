package com.example.teacherapp.data.datasource.remote.firebase

import com.example.teacherapp.domain.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSource @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

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
        val subscription = firestore.collection("students")
            .whereEqualTo("courseId", courseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val students = snapshot?.documents?.mapNotNull {
                    it.toObject(Student::class.java)
                } ?: emptyList()
                trySend(students)
            }

        awaitClose { subscription.remove() }
    }

    fun getAssignmentsForCourse(courseId: String): Flow<List<Assignment>> = callbackFlow {
        val subscription = firestore.collection("assignments")
            .whereEqualTo("courseId", courseId)
            .orderBy("dueDate", Query.Direction.ASCENDING)
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

    fun getAttendanceForCourseAndDate(courseId: String, date: String): Flow<List<Attendance>> = callbackFlow {
        val subscription = firestore.collection("attendance")
            .whereEqualTo("courseId", courseId)
            .whereEqualTo("date", date)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val attendance = snapshot?.documents?.mapNotNull {
                    it.toObject(Attendance::class.java)
                } ?: emptyList()
                trySend(attendance)
            }

        awaitClose { subscription.remove() }
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