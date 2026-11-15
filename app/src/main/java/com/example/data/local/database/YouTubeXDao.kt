package com.example.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface YouTubeXDao {

    // Video Interactions
    @Query("SELECT * FROM video_interactions")
    fun observeAllInteractions(): Flow<List<VideoInteractionEntity>>

    @Query("SELECT * FROM video_interactions WHERE videoId = :videoId LIMIT 1")
    suspend fun getInteraction(videoId: String): VideoInteractionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertInteraction(entity: VideoInteractionEntity)

    // Channel Subscriptions
    @Query("SELECT * FROM channel_subscriptions WHERE isSubscribed = 1 ORDER BY subscribedAt DESC")
    fun observeSubscriptions(): Flow<List<ChannelSubscriptionEntity>>

    @Query("SELECT * FROM channel_subscriptions WHERE channelId = :channelId LIMIT 1")
    suspend fun getSubscription(channelId: String): ChannelSubscriptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSubscription(entity: ChannelSubscriptionEntity)

    // Watch History & Continue Watching
    @Query("SELECT * FROM watch_history ORDER BY watchedAt DESC")
    fun observeWatchHistory(): Flow<List<WatchHistoryEntity>>

    @Query("SELECT * FROM watch_history WHERE videoId = :videoId LIMIT 1")
    suspend fun getWatchHistoryItem(videoId: String): WatchHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertWatchHistory(entity: WatchHistoryEntity)

    @Query("DELETE FROM watch_history WHERE videoId = :videoId")
    suspend fun removeWatchHistoryItem(videoId: String)

    @Query("DELETE FROM watch_history")
    suspend fun clearWatchHistory()

    // Playlists
    @Query("SELECT * FROM playlists ORDER BY isSystemWatchLater DESC, createdAt DESC")
    fun observePlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_videos ORDER BY addedAt DESC")
    fun observeAllPlaylistVideos(): Flow<List<PlaylistVideoCrossRef>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId AND isSystemWatchLater = 0")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId")
    suspend fun deletePlaylistVideos(playlistId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addVideoToPlaylist(crossRef: PlaylistVideoCrossRef)

    @Query("DELETE FROM playlist_videos WHERE playlistId = :playlistId AND videoId = :videoId")
    suspend fun removeVideoFromPlaylist(playlistId: Long, videoId: String)

    @Query("SELECT * FROM playlists WHERE isSystemWatchLater = 1 LIMIT 1")
    suspend fun getWatchLaterPlaylist(): PlaylistEntity?

    // Search History
    @Query("SELECT * FROM search_history ORDER BY searchedAt DESC LIMIT 15")
    fun observeSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearchQuery(entity: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE `query` = :query")
    suspend fun deleteSearchQuery(query: String)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    // Comments
    @Query("SELECT * FROM comments WHERE videoId = :videoId ORDER BY createdAt DESC")
    fun observeCommentsForVideo(videoId: String): Flow<List<CommentEntity>>

    @Query("SELECT COUNT(*) FROM comments WHERE videoId = :videoId")
    suspend fun getCommentCountForVideo(videoId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComments(comments: List<CommentEntity>)

    @Query("SELECT * FROM comments WHERE id = :commentId LIMIT 1")
    suspend fun getCommentById(commentId: Long): CommentEntity?

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun observeNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun observeUnreadNotificationCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM notifications")
    suspend fun getNotificationsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(items: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: String)

    @Query("UPDATE notifications SET isRead = 1")
    suspend fun markAllNotificationsRead()
}
