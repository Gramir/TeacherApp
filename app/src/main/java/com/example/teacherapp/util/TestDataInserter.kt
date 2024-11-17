package com.example.teacherapp.util

import com.example.teacherapp.data.datasource.remote.firebase.FirebaseDataSource
import javax.inject.Inject

class TestDataInserter @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) {
    suspend fun insertTestData() {
        try {
            // Crear profesor de prueba
            val teacherResult = firebaseDataSource.createTeacher(
                username = "teacher1",
                password = "123456",
                name = "Juan Pérez"
            )

            if (teacherResult.isSuccess) {
                println("""
                    ¡Datos de prueba creados exitosamente!
                    Usuario: teacher1
                    Contraseña: 123456
                """.trimIndent())
            } else {
                println("Error al crear datos de prueba: ${teacherResult.exceptionOrNull()?.message}")
            }

        } catch (e: Exception) {
            println("Error al insertar datos de prueba: ${e.message}")
        }
    }
}