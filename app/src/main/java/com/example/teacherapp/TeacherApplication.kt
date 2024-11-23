package com.example.teacherapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp
import kotlin.system.exitProcess

@HiltAndroidApp
class TeacherApplication : Application() {

    override fun onCreate() {
        try {
            super.onCreate()
            // Inicializar Firebase
            FirebaseApp.initializeApp(this)
            // Inicializar Analytics
            FirebaseAnalytics.getInstance(this)

            // Limpiar caché de la aplicación al inicio
            clearAppCache()

        } catch (e: Exception) {
            e.printStackTrace()
            // En caso de error fatal, terminar la aplicación
            exitProcess(1)
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }

    private fun clearAppCache() {
        try {
            cacheDir.deleteRecursively()
            codeCacheDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}