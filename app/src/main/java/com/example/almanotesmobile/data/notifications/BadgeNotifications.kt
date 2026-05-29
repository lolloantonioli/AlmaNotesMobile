package com.example.almanotesmobile.data.notifications

import com.example.almanotesmobile.data.repositories.AuthRepository
import com.example.almanotesmobile.data.repositories.NotificationRepository

data class BadgeDefinition(
    val id: String,
    val title: String,
    val message: String
)

val uploadBadgeDefinitions = listOf(
    BadgeDefinition("upload_1", "Uploader Lv.1", "Hai caricato il tuo primo documento."),
    BadgeDefinition("upload_5", "Uploader Lv.2", "Hai caricato 5 documenti."),
    BadgeDefinition("upload_10", "Uploader Lv.3", "Hai caricato 10 documenti.")
)

val downloadBadgeDefinitions = listOf(
    BadgeDefinition("download_1", "Downloader Lv.1", "Hai scaricato il tuo primo documento."),
    BadgeDefinition("download_5", "Downloader Lv.2", "Hai scaricato 5 documenti."),
    BadgeDefinition("download_10", "Downloader Lv.3", "Hai scaricato 10 documenti.")
)

val reviewBadgeDefinitions = listOf(
    BadgeDefinition("review_1", "Recensore Lv.1", "Hai pubblicato la tua prima recensione."),
    BadgeDefinition("review_5", "Recensore Lv.2", "Hai pubblicato 5 recensioni."),
    BadgeDefinition("review_10", "Recensore Lv.3", "Hai pubblicato 10 recensioni.")
)

val profileImageBadgeDefinition = BadgeDefinition(
    id = "profile_image",
    title = "Camera",
    message = "Hai aggiunto la foto profilo."
)

suspend fun publishBadgeIfNew(
    authRepository: AuthRepository,
    notificationRepository: NotificationRepository,
    badge: BadgeDefinition
) {
    if (authRepository.markBadgeAwardedIfNew(badge.id)) {
        notificationRepository.publish(
            title = "Badge ottenuto: ${badge.title}",
            message = badge.message,
            sendPush = true
        )
    }
}

suspend fun publishCountBadgesIfNew(
    authRepository: AuthRepository,
    notificationRepository: NotificationRepository,
    count: Int,
    badges: List<BadgeDefinition>
) {
    badges.forEach { badge ->
        val threshold = badge.id.substringAfterLast('_').toIntOrNull() ?: return@forEach
        if (count >= threshold) {
            publishBadgeIfNew(authRepository, notificationRepository, badge)
        }
    }
}