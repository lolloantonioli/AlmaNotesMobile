package com.example.almanotesmobile.data.repositories

import com.example.almanotesmobile.data.notifications.AndroidPushNotifier
import com.example.almanotesmobile.data.notifications.AppNotification
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted

class NotificationRepository(
    private val pushNotifier: AndroidPushNotifier,
    private val authRepository: AuthRepository
) {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val nextNotificationId = AtomicLong(System.currentTimeMillis())
    private val allNotifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = combine(
        allNotifications,
        authRepository.username
    ) { notifications, currentUsername ->
        notifications.filter { notification ->
            notification.recipientUsername == currentUsername
        }
    }.stateIn(repositoryScope, SharingStarted.Eagerly, emptyList())

    suspend fun publish(
        title: String,
        message: String,
        sendPush: Boolean = false,
        recipientUsername: String? = null
    ) {
        val targetUsername = recipientUsername ?: authRepository.username.first()
        if (targetUsername.isBlank()) return

        val notificationId = nextNotificationId.incrementAndGet()
        val pushSent = sendPush && pushNotifier.notifyPush(
            notificationId = notificationId.toInt(),
            title = title,
            message = message
        )
        val notification = AppNotification(
            id = notificationId,
            title = title,
            message = message,
            recipientUsername = targetUsername,
            isPushNotification = pushSent
        )
        allNotifications.value = listOf(notification) + allNotifications.value
    }
}