package com.example.data.local.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "video_interactions")
data class VideoInteractionEntity(
    @PrimaryKey val videoId: String,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isFavorite: Boolean = false,
    val isWatchLater: Boolean = false,
    val isDownloaded: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "channel_subscriptions")
data class ChannelSubscriptionEntity(
    @PrimaryKey val channelId: String,
    val channelName: String,
    val channelHandle: String,
    val avatarColorHex: Long,
    val subscriberCountFormatted: String,
    val isSubscribed: Boolean = true,
    val subscribedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "watch_history")
data class WatchHistoryEntity(
    @PrimaryKey val videoId: String,
    val lastPositionMs: Long,
    val durationMs: Long,
    val watchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String = "",
    val isSystemWatchLater: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "playlist_videos",
    primaryKeys = ["playlistId", "videoId"]
)
data class PlaylistVideoCrossRef(
    val playlistId: Long,
    val videoId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val videoId: String,
    val authorName: String,
    val avatarColorHex: Long,
    val text: String,
    val timestampLabel: String,
    val likesCount: Int,
    val isLikedByUser: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String,
    val channelName: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val thumbnailUrl: String,
    val videoId: String,
    val isRead: Boolean,
    val createdAt: Long = System.currentTimeMillis()
)
