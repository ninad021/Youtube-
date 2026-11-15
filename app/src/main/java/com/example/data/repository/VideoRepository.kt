package com.example.data.repository

import android.content.Context
import com.example.data.local.SampleCatalog
import com.example.data.local.UserPreferences
import com.example.data.local.UserPreferencesDataStore
import com.example.data.local.database.ChannelSubscriptionEntity
import com.example.data.local.database.CommentEntity
import com.example.data.local.database.NotificationEntity
import com.example.data.local.database.PlaylistEntity
import com.example.data.local.database.PlaylistVideoCrossRef
import com.example.data.local.database.SearchHistoryEntity
import com.example.data.local.database.VideoInteractionEntity
import com.example.data.local.database.WatchHistoryEntity
import com.example.data.local.database.YouTubeXDao
import com.example.data.local.database.YouTubeXDatabase
import com.example.data.model.ChannelInfo
import com.example.data.model.CommentUiModel
import com.example.data.model.NotificationUiModel
import com.example.data.model.PlaylistUiModel
import com.example.data.model.VideoCategory
import com.example.data.model.VideoItem
import com.example.data.model.VideoWithInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class VideoRepository(
    private val dao: YouTubeXDao,
    private val preferencesDataStore: UserPreferencesDataStore
) {
    private val initScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    internal val seedingJob = initScope.launch {
        seedInitialDataIfNeeded()
    }

    val userPreferences: Flow<UserPreferences> = preferencesDataStore.preferencesFlow

    val videosWithInteractions: Flow<List<VideoWithInteraction>> = combine(
        dao.observeAllInteractions(),
        dao.observeSubscriptions(),
        dao.observeWatchHistory()
    ) { interactions, subscriptions, history ->
        val interactionMap = interactions.associateBy { it.videoId }
        val subscribedChannelIds = subscriptions.filter { it.isSubscribed }.map { it.channelId }.toSet()
        val historyMap = history.associateBy { it.videoId }

        SampleCatalog.videos.map { video ->
            val inter = interactionMap[video.id]
            val hist = historyMap[video.id]
            VideoWithInteraction(
                video = video,
                isLiked = inter?.isLiked ?: false,
                isDisliked = inter?.isDisliked ?: false,
                isFavorite = inter?.isFavorite ?: false,
                isWatchLater = inter?.isWatchLater ?: false,
                isDownloaded = inter?.isDownloaded ?: false,
                isSubscribed = subscribedChannelIds.contains(video.channelId),
                watchProgressMs = hist?.lastPositionMs ?: 0L,
                watchDurationMs = hist?.durationMs ?: (video.durationSeconds * 1000L)
            )
        }
    }

    val watchHistoryVideos: Flow<List<VideoWithInteraction>> = combine(
        dao.observeWatchHistory(),
        videosWithInteractions
    ) { history, allVideos ->
        val videoMap = allVideos.associateBy { it.video.id }
        history.mapNotNull { entry -> videoMap[entry.videoId] }
    }

    val continueWatchingVideos: Flow<List<VideoWithInteraction>> = watchHistoryVideos.map { list ->
        list.filter { item ->
            item.watchProgressMs > 3_000L && item.progressFraction < 0.96f
        }
    }

    val playlists: Flow<List<PlaylistUiModel>> = combine(
        dao.observePlaylists(),
        dao.observeAllPlaylistVideos()
    ) { playlists, refs ->
        val grouped = refs.groupBy { it.playlistId }
        playlists.map { entity ->
            val videoIds = grouped[entity.id]?.map { it.videoId } ?: emptyList()
            val cover = videoIds.firstOrNull()?.let { SampleCatalog.getVideoById(it)?.thumbnailUrl }
            PlaylistUiModel(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                videoIds = videoIds,
                coverThumbnailUrl = cover,
                isSystemWatchLater = entity.isSystemWatchLater,
                createdAt = entity.createdAt
            )
        }
    }

    val subscriptions: Flow<List<ChannelInfo>> = dao.observeSubscriptions().map { list ->
        list.filter { it.isSubscribed }.map {
            ChannelInfo(
                id = it.channelId,
                name = it.channelName,
                handle = it.channelHandle,
                avatarColorHex = it.avatarColorHex,
                subscriberCountFormatted = it.subscriberCountFormatted,
                isVerified = true
            )
        }
    }

    val searchHistory: Flow<List<String>> = dao.observeSearchHistory().map { items ->
        items.map { it.query }
    }

    val notifications: Flow<List<NotificationUiModel>> = dao.observeNotifications().map { list ->
        list.map {
            NotificationUiModel(
                id = it.id,
                channelName = it.channelName,
                title = it.title,
                message = it.message,
                timeAgo = it.timeAgo,
                thumbnailUrl = it.thumbnailUrl,
                videoId = it.videoId,
                isRead = it.isRead
            )
        }
    }

    val unreadNotificationCount: Flow<Int> = dao.observeUnreadNotificationCount()

    fun observeComments(videoId: String): Flow<List<CommentUiModel>> {
        return dao.observeCommentsForVideo(videoId).map { list ->
            list.map {
                CommentUiModel(
                    id = it.id,
                    videoId = it.videoId,
                    authorName = it.authorName,
                    avatarColorHex = it.avatarColorHex,
                    text = it.text,
                    timestampLabel = it.timestampLabel,
                    likesCount = it.likesCount,
                    isLikedByUser = it.isLikedByUser
                )
            }
        }
    }

    suspend fun ensureCommentsForVideo(videoId: String) {
        if (dao.getCommentCountForVideo(videoId) == 0) {
            val video = SampleCatalog.getVideoById(videoId)
            val seeded = listOf(
                CommentEntity(
                    videoId = videoId,
                    authorName = "Maya Lin",
                    avatarColorHex = 0xFF06B6D4,
                    text = "The pacing and visual clarity on ${video?.channelName ?: "this channel"} are top tier. Smooth 60fps playback too!",
                    timestampLabel = "3 hours ago",
                    likesCount = 142,
                    createdAt = System.currentTimeMillis() - 10_800_000L
                ),
                CommentEntity(
                    videoId = videoId,
                    authorName = "Devon Brooks",
                    avatarColorHex = 0xFFF59E0B,
                    text = "Saved straight to my Watch Later queue. Love the sound design around the 0:10 mark.",
                    timestampLabel = "1 day ago",
                    likesCount = 58,
                    createdAt = System.currentTimeMillis() - 86_400_000L
                ),
                CommentEntity(
                    videoId = videoId,
                    authorName = "Priya Nair",
                    avatarColorHex = 0xFF8B5CF6,
                    text = "This deserves way more views. Subscribed right away!",
                    timestampLabel = "2 days ago",
                    likesCount = 29,
                    createdAt = System.currentTimeMillis() - 172_800_000L
                )
            )
            dao.insertComments(seeded)
        }
    }

    suspend fun addComment(videoId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        val prefs = preferencesDataStore.preferencesFlow.first()
        dao.insertComment(
            CommentEntity(
                videoId = videoId,
                authorName = prefs.username,
                avatarColorHex = 0xFFFF1E2D,
                text = trimmed,
                timestampLabel = "Just now",
                likesCount = 0,
                isLikedByUser = false,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleCommentLike(commentId: Long) {
        val comment = dao.getCommentById(commentId) ?: return
        val nowLiked = !comment.isLikedByUser
        dao.insertComment(
            comment.copy(
                isLikedByUser = nowLiked,
                likesCount = if (nowLiked) comment.likesCount + 1 else (comment.likesCount - 1).coerceAtLeast(0)
            )
        )
    }

    suspend fun toggleLike(videoId: String) {
        val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
        val newLiked = !current.isLiked
        dao.upsertInteraction(
            current.copy(
                isLiked = newLiked,
                isDisliked = if (newLiked) false else current.isDisliked,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleDislike(videoId: String) {
        val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
        val newDisliked = !current.isDisliked
        dao.upsertInteraction(
            current.copy(
                isDisliked = newDisliked,
                isLiked = if (newDisliked) false else current.isLiked,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleFavorite(videoId: String) {
        val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
        dao.upsertInteraction(
            current.copy(
                isFavorite = !current.isFavorite,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleWatchLater(videoId: String) {
        val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
        val nextWatchLater = !current.isWatchLater
        dao.upsertInteraction(
            current.copy(
                isWatchLater = nextWatchLater,
                updatedAt = System.currentTimeMillis()
            )
        )
        val watchLaterPlaylist = dao.getWatchLaterPlaylist() ?: run {
            val newId = dao.insertPlaylist(
                PlaylistEntity(
                    name = "Watch Later",
                    description = "Videos saved to watch later",
                    isSystemWatchLater = true
                )
            )
            PlaylistEntity(id = newId, name = "Watch Later", isSystemWatchLater = true)
        }
        if (nextWatchLater) {
            dao.addVideoToPlaylist(PlaylistVideoCrossRef(watchLaterPlaylist.id, videoId))
        } else {
            dao.removeVideoFromPlaylist(watchLaterPlaylist.id, videoId)
        }
    }

    suspend fun setDownloaded(videoId: String, downloaded: Boolean) {
        val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
        dao.upsertInteraction(
            current.copy(
                isDownloaded = downloaded,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun toggleSubscribe(video: VideoItem) {
        val existing = dao.getSubscription(video.channelId)
        val nextSubscribed = !(existing?.isSubscribed ?: false)
        dao.upsertSubscription(
            ChannelSubscriptionEntity(
                channelId = video.channelId,
                channelName = video.channelName,
                channelHandle = video.channelHandle,
                avatarColorHex = video.channelAvatarColorHex,
                subscriberCountFormatted = video.subscriberCountFormatted,
                isSubscribed = nextSubscribed,
                subscribedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun unsubscribeChannel(channelId: String) {
        val existing = dao.getSubscription(channelId) ?: return
        dao.upsertSubscription(existing.copy(isSubscribed = false))
    }

    suspend fun recordWatchProgress(videoId: String, positionMs: Long, durationMs: Long) {
        val safeDuration = if (durationMs > 0L) durationMs else {
            (SampleCatalog.getVideoById(videoId)?.durationSeconds ?: 0) * 1000L
        }
        dao.upsertWatchHistory(
            WatchHistoryEntity(
                videoId = videoId,
                lastPositionMs = positionMs.coerceAtLeast(0L),
                durationMs = safeDuration,
                watchedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun removeFromHistory(videoId: String) {
        dao.removeWatchHistoryItem(videoId)
    }

    suspend fun clearWatchHistory() {
        dao.clearWatchHistory()
    }

    suspend fun createPlaylist(name: String, description: String = ""): Long {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) return -1L
        return dao.insertPlaylist(
            PlaylistEntity(
                name = cleanName,
                description = description.trim(),
                isSystemWatchLater = false,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun deletePlaylist(playlistId: Long) {
        dao.deletePlaylistVideos(playlistId)
        dao.deletePlaylist(playlistId)
    }

    suspend fun addVideoToPlaylist(playlistId: Long, videoId: String) {
        dao.addVideoToPlaylist(PlaylistVideoCrossRef(playlistId = playlistId, videoId = videoId))
        val watchLater = dao.getWatchLaterPlaylist()
        if (watchLater != null && watchLater.id == playlistId) {
            val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
            dao.upsertInteraction(current.copy(isWatchLater = true))
        }
    }

    suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: String) {
        dao.removeVideoFromPlaylist(playlistId = playlistId, videoId = videoId)
        val watchLater = dao.getWatchLaterPlaylist()
        if (watchLater != null && watchLater.id == playlistId) {
            val current = dao.getInteraction(videoId) ?: VideoInteractionEntity(videoId = videoId)
            dao.upsertInteraction(current.copy(isWatchLater = false))
        }
    }

    suspend fun addSearchQuery(query: String) {
        val clean = query.trim()
        if (clean.isNotEmpty()) {
            dao.upsertSearchQuery(SearchHistoryEntity(query = clean, searchedAt = System.currentTimeMillis()))
        }
    }

    suspend fun deleteSearchQuery(query: String) {
        dao.deleteSearchQuery(query)
    }

    suspend fun clearSearchHistory() {
        dao.clearSearchHistory()
    }

    suspend fun markNotificationRead(id: String) {
        dao.markNotificationRead(id)
    }

    suspend fun markAllNotificationsRead() {
        dao.markAllNotificationsRead()
    }

    suspend fun setDarkTheme(enabled: Boolean) = preferencesDataStore.setDarkTheme(enabled)
    suspend fun setAutoPlayEnabled(enabled: Boolean) = preferencesDataStore.setAutoPlayEnabled(enabled)
    suspend fun setPreferredQuality(quality: String) = preferencesDataStore.setPreferredQuality(quality)
    suspend fun setPreferredSpeed(speed: Float) = preferencesDataStore.setPreferredSpeed(speed)
    suspend fun setSubtitlesEnabled(enabled: Boolean) = preferencesDataStore.setSubtitlesEnabled(enabled)
    suspend fun updateProfile(username: String, handle: String) = preferencesDataStore.updateProfile(username, handle)

    fun filterVideos(
        videos: List<VideoWithInteraction>,
        category: VideoCategory,
        query: String = ""
    ): List<VideoWithInteraction> {
        val byCategory = if (category == VideoCategory.ALL) {
            videos
        } else {
            videos.filter { it.video.category == category }
        }
        val cleanQuery = query.trim().lowercase()
        if (cleanQuery.isEmpty()) return byCategory
        return byCategory.filter { item ->
            val v = item.video
            v.title.lowercase().contains(cleanQuery) ||
                v.channelName.lowercase().contains(cleanQuery) ||
                v.category.displayName.lowercase().contains(cleanQuery) ||
                v.description.lowercase().contains(cleanQuery) ||
                v.tags.any { it.lowercase().contains(cleanQuery) }
        }
    }

    private suspend fun seedInitialDataIfNeeded() {
        if (dao.getWatchLaterPlaylist() == null) {
            val watchLaterId = dao.insertPlaylist(
                PlaylistEntity(
                    name = "Watch Later",
                    description = "Saved videos to watch anytime",
                    isSystemWatchLater = true
                )
            )
            dao.addVideoToPlaylist(PlaylistVideoCrossRef(watchLaterId, "vid_sintel"))
            dao.upsertInteraction(
                VideoInteractionEntity(videoId = "vid_sintel", isWatchLater = true)
            )
        }
        if (dao.getNotificationsCount() == 0) {
            dao.insertNotifications(
                SampleCatalog.initialNotifications.mapIndexed { idx, item ->
                    NotificationEntity(
                        id = item.id,
                        channelName = item.channelName,
                        title = item.title,
                        message = item.message,
                        timeAgo = item.timeAgo,
                        thumbnailUrl = item.thumbnailUrl,
                        videoId = item.videoId,
                        isRead = item.isRead,
                        createdAt = System.currentTimeMillis() - idx * 3_600_000L
                    )
                }
            )
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: VideoRepository? = null

        fun getInstance(context: Context): VideoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: VideoRepository(
                    dao = YouTubeXDatabase.getInstance(context).youTubeXDao(),
                    preferencesDataStore = UserPreferencesDataStore(context.applicationContext)
                ).also { INSTANCE = it }
            }
        }
    }
}
