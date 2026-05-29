package com.example.almanotesmobile.data.repositories

import com.example.almanotesmobile.data.notifications.AndroidPushNotifier
import com.example.almanotesmobile.data.notifications.AppNotification
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepository(
    private val pushNotifier: AndroidPushNotifier
) {
    private val nextNotificationId = AtomicLong(System.currentTimeMillis())
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun publish(title: String, message: String, sendPush: Boolean = false) {
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
            isPushNotification = pushSent
        )
        _notifications.value = listOf(notification) + _notifications.value
    }
}