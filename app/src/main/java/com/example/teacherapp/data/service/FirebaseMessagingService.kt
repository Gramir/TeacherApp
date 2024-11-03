package com.example.teacherapp.data.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.example.teacherapp.domain.model.Assignment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TeacherAppMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationService: NotificationService

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.data.let { data ->
            // Only process notification if it's a new assignment
            if (data["type"] == "new_assignment") {
                val assignment = Assignment(
                    id = data["id"] ?: "",
                    title = data["title"] ?: "",
                    description = data["description"] ?: "",
                    dueDate = data["dueDate"] ?: "",
                    courseId = data["courseId"] ?: "",
                    status = data["status"] ?: "pending"
                )

                notificationService.showNewAssignmentNotification(assignment)
            }
        }
    }

    override fun onNewToken(token: String) {
        // Here you would typically send this token to your server
        // This is required for sending notifications to specific devices
    }
}