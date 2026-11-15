package com.example.data.model

import androidx.annotation.DrawableRes

enum class VideoCategory(val displayName: String) {
    ALL("All"),
    MUSIC("Music"),
    GAMING("Gaming"),
    TECHNOLOGY("Technology"),
    EDUCATION("Education"),
    NEWS("News"),
    SPORTS("Sports"),
    ENTERTAINMENT("Entertainment")
}

data class CaptionCue(
    val startMs: Long,
    val endMs: Long,
    val text: String
)

data class VideoItem(
    val id: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    @DrawableRes val fallbackDrawableRes: Int? = null,
    val videoUrl: String,
    val durationSeconds: Int,
    val durationLabel: String,
    val viewCount: Long,
    val viewCountFormatted: String,
    val uploadDateLabel: String,
    val category: VideoCategory,
    val channelId: String,
    val channelName: String,
    val channelAvatarColorHex: Long,
    val channelHandle: String,
    val subscriberCount: Long,
    val subscriberCountFormatted: String,
    val baseLikes: Int,
    val isDownloadable: Boolean = true,
    val licenseNote: String = "CC0 / Public Sample Stream",
    val availableQualities: List<String> = listOf("Auto", "1080p", "720p", "480p", "360p"),
    val tags: List<String> = emptyList(),
    val captions: List<CaptionCue> = emptyList()
)

data class ChannelInfo(
    val id: String,
    val name: String,
    val handle: String,
    val avatarColorHex: Long,
    val subscriberCountFormatted: String,
    val isVerified: Boolean = true
)

data class CommentUiModel(
    val id: Long,
    val videoId: String,
    val authorName: String,
    val avatarColorHex: Long,
    val text: String,
    val timestampLabel: String,
    val likesCount: Int,
    val isLikedByUser: Boolean
)

data class VideoWithInteraction(
    val video: VideoItem,
    val isLiked: Boolean = false,
    val isDisliked: Boolean = false,
    val isFavorite: Boolean = false,
    val isWatchLater: Boolean = false,
    val isDownloaded: Boolean = false,
    val isSubscribed: Boolean = false,
    val watchProgressMs: Long = 0L,
    val watchDurationMs: Long = 0L
) {
    val progressFraction: Float
        get() = if (watchDurationMs > 0L) {
            (watchProgressMs.toFloat() / watchDurationMs.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val displayLikesCount: Int
        get() = video.baseLikes + if (isLiked) 1 else 0
}

data class PlaylistUiModel(
    val id: Long,
    val name: String,
    val description: String,
    val videoIds: List<String>,
    val coverThumbnailUrl: String?,
    val isSystemWatchLater: Boolean = false,
    val createdAt: Long = 0L
)

data class NotificationUiModel(
    val id: String,
    val channelName: String,
    val title: String,
    val message: String,
    val timeAgo: String,
    val thumbnailUrl: String,
    val videoId: String,
    val isRead: Boolean
)
