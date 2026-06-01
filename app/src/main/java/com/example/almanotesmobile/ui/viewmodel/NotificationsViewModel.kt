package com.example.almanotesmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.almanotesmobile.data.repositories.NotificationRepository

class NotificationsViewModel(
    notificationRepository: NotificationRepository
) : ViewModel() {
    val notifications = notificationRepository.notifications
}