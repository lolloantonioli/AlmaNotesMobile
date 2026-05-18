package com.example.almanotesmobile.ui.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object AuthGraph : Route

    @Serializable
    data object Registration : Route

    @Serializable
    data object Login : Route

    @Serializable
    data object MainGraph : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Upload : Route

    @Serializable
    data object Favourites : Route

    @Serializable
    data object Reviews : Route

    @Serializable
    data object DownloadedFiles : Route

    @Serializable
    data object UploadedFiles : Route

    @Serializable
    data object Theme : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data class PdfViewer(val noteId: Long) : Route
}
