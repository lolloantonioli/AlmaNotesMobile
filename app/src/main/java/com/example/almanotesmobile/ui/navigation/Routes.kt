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
    data object Favourites : Route

    @Serializable
    data object Rewards : Route

    @Serializable
    data object Theme : Route

    @Serializable
    data object Profile : Route
}
