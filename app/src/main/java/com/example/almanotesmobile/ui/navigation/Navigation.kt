package com.example.almanotesmobile.ui

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable data object Login : Route
    @Serializable data object Register : Route
    @Serializable data object Home : Route
    @Serializable data object Theme : Route
    @Serializable data object Profile : Route
}