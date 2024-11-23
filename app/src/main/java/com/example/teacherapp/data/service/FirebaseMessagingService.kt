package com.example.teacherapp.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.teacherapp.domain.model.Course
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TeacherAppMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.let { data ->
            // Solo procesar notificación si es un nuevo curso
            if (data["type"] == "new_course") {
                val course = Course(
                    id = data["id"] ?: "",
                    name = data["name"] ?: "",
                    teacherId = data["teacherId"] ?: "",
                    code = data["code"] ?: "",
                    semester = data["semester"] ?: "",
                    schedule = data["schedule"] ?: ""
                )

                notificationService.showNewCourseNotification(course)
            }
        }
    }

    override fun onNewToken(token: String) {
        // Aquí enviarías el token al servidor para actualizarlo
        // Esto es necesario para enviar notificaciones a dispositivos específicos
    }
}