package com.example.almanotesmobile.data.repositories

import com.example.almanotesmobile.data.notifications.AppNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationRepository {
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    fun publish(title: String, message: String, isPushCandidate: Boolean = false) {
        val notification = AppNotification(
            title = title,
            message = message,
            isPushCandidate = isPushCandidate
        )
        _notifications.value = listOf(notification) + _notifications.value
    }
}