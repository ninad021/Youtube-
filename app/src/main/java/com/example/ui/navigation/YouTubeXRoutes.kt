package com.example.ui.navigation

import kotlinx.serialization.Serializable

sealed interface YouTubeXRoute {
    @Serializable
    data object Home : YouTubeXRoute

    @Serializable
    data object Search : YouTubeXRoute

    @Serializable
    data object Library : YouTubeXRoute

    @Serializable
    data object Profile : YouTubeXRoute

    @Serializable
    data object Notifications : YouTubeXRoute

    @Serializable
    data class PlaylistDetail(
        val playlistId: Long = -1L,
        val systemType: String? = null
    ) : YouTubeXRoute

    @Serializable
    data object Subscriptions : YouTubeXRoute
}
