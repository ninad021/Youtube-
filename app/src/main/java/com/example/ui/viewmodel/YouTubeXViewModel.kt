package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.SampleCatalog
import com.example.data.local.UserPreferences
import com.example.data.model.ChannelInfo
import com.example.data.model.CommentUiModel
import com.example.data.model.NotificationUiModel
import com.example.data.model.PlaylistUiModel
import com.example.data.model.VideoCategory
import com.example.data.model.VideoItem
import com.example.data.model.VideoWithInteraction
import com.example.data.repository.VideoRepository
import com.example.player.PlaybackUiState
import com.example.player.PlayerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

data class HomeUiState(
    val isLoading: Boolean = true,
    val selectedCategory: VideoCategory = VideoCategory.ALL,
    val featuredVideo: VideoWithInteraction? = null,
    val continueWatching: List<VideoWithInteraction> = emptyList(),
    val feedVideos: List<VideoWithInteraction> = emptyList()
)

data class SearchUiState(
    val query: String = "",
    val selectedCategory: VideoCategory = VideoCategory.ALL,
    val recentSearches: List<String> = emptyList(),
    val suggestions: List<String> = emptyList(),
    val results: List<VideoWithInteraction> = emptyList()
)

data class LibraryUiState(
    val playlists: List<PlaylistUiModel> = emptyList(),
    val watchLaterVideos: List<VideoWithInteraction> = emptyList(),
    val favoriteVideos: List<VideoWithInteraction> = emptyList(),
    val likedVideos: List<VideoWithInteraction> = emptyList(),
    val downloadedVideos: List<VideoWithInteraction> = emptyList(),
    val watchHistory: List<VideoWithInteraction> = emptyList(),
    val subscriptions: List<ChannelInfo> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class YouTubeXViewModel(application: Application) : AndroidViewModel(application) {

    val repository = VideoRepository.getInstance(application)
    val playerManager = PlayerManager(application, repository)

    val playbackState: StateFlow<PlaybackUiState> = playerManager.uiState

    val userPreferences: StateFlow<UserPreferences> = repository.userPreferences.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = UserPreferences()
    )

    val allVideos: StateFlow<List<VideoWithInteraction>> = repository.videosWithInteractions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    private val _selectedHomeCategory = MutableStateFlow(VideoCategory.ALL)
    private val _isHomeLoading = MutableStateFlow(true)

    val homeUiState: StateFlow<HomeUiState> = combine(
        _isHomeLoading,
        _selectedHomeCategory,
        repository.videosWithInteractions,
        repository.continueWatchingVideos
    ) { isLoading, category, videos, continueWatching ->
        val filtered = repository.filterVideos(videos, category)
        val featured = if (category == VideoCategory.ALL) filtered.firstOrNull() else null
        HomeUiState(
            isLoading = isLoading,
            selectedCategory = category,
            featuredVideo = featured,
            continueWatching = continueWatching,
            feedVideos = filtered
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    private val _searchQuery = MutableStateFlow("")
    private val _searchCategory = MutableStateFlow(VideoCategory.ALL)

    val searchUiState: StateFlow<SearchUiState> = combine(
        _searchQuery,
        _searchCategory,
        repository.videosWithInteractions,
        repository.searchHistory
    ) { query, category, videos, history ->
        val trimmed = query.trim()
        val suggestions = if (trimmed.isEmpty()) {
            listOf(
                "Tears of Steel 4K",
                "Synthwave Live Mix",
                "Unreal Engine Raytracing",
                "Media3 ExoPlayer",
                "Rally Challenge",
                "Big Buck Bunny"
            )
        } else {
            val matching = videos.flatMap { item ->
                listOf(item.video.title, item.video.channelName) + item.video.tags
            }.distinct().filter { it.contains(trimmed, ignoreCase = true) }.take(6)
            matching
        }
        val results = if (trimmed.isEmpty() && category == VideoCategory.ALL) {
            videos
        } else {
            repository.filterVideos(videos, category, trimmed)
        }
        SearchUiState(
            query = query,
            selectedCategory = category,
            recentSearches = history,
            suggestions = suggestions,
            results = results
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchUiState()
    )

    val libraryUiState: StateFlow<LibraryUiState> = combine(
        repository.playlists,
        repository.videosWithInteractions,
        repository.watchHistoryVideos,
        repository.subscriptions
    ) { playlists, videos, history, subs ->
        LibraryUiState(
            playlists = playlists,
            watchLaterVideos = videos.filter { it.isWatchLater },
            favoriteVideos = videos.filter { it.isFavorite },
            likedVideos = videos.filter { it.isLiked },
            downloadedVideos = videos.filter { it.isDownloaded },
            watchHistory = history,
            subscriptions = subs
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LibraryUiState()
    )

    val notifications: StateFlow<List<NotificationUiModel>> = repository.notifications.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val unreadNotificationCount: StateFlow<Int> = repository.unreadNotificationCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    val activeComments: StateFlow<List<CommentUiModel>> = playbackState
        .flatMapLatest { state ->
            val videoId = state.currentVideo?.id
            if (videoId == null) flowOf(emptyList())
            else repository.observeComments(videoId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _downloadingIds = MutableStateFlow<Set<String>>(emptySet())
    val downloadingIds: StateFlow<Set<String>> = _downloadingIds.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            delay(450L)
            _isHomeLoading.value = false
        }
    }

    fun selectHomeCategory(category: VideoCategory) {
        if (_selectedHomeCategory.value == category) return
        _selectedHomeCategory.value = category
        viewModelScope.launch {
            _isHomeLoading.value = true
            delay(180L)
            _isHomeLoading.value = false
        }
    }

    fun refreshHome() {
        viewModelScope.launch {
            _isHomeLoading.value = true
            delay(400L)
            _isHomeLoading.value = false
        }
    }

    fun playVideo(videoWithInteraction: VideoWithInteraction, queue: List<VideoItem>? = null) {
        val resumeMs = if (videoWithInteraction.watchProgressMs > 3_000L &&
            videoWithInteraction.progressFraction < 0.95f
        ) {
            videoWithInteraction.watchProgressMs
        } else 0L
        playerManager.playVideo(
            video = videoWithInteraction.video,
            startPositionMs = resumeMs,
            customQueue = queue
        )
    }

    fun playVideoById(videoId: String, queue: List<VideoItem>? = null) {
        val match = allVideos.value.find { it.video.id == videoId }
        if (match != null) {
            playVideo(match, queue)
        } else {
            SampleCatalog.getVideoById(videoId)?.let {
                playerManager.playVideo(it, 0L, queue)
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun submitSearch(query: String) {
        val clean = query.trim()
        _searchQuery.value = clean
        if (clean.isNotEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.addSearchQuery(clean)
            }
        }
    }

    fun selectSearchCategory(category: VideoCategory) {
        _searchCategory.value = category
    }

    fun deleteSearchHistoryItem(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSearchQuery(query)
        }
    }

    fun clearSearchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearSearchHistory()
        }
    }

    fun toggleLike(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.toggleLike(videoId) }
    }

    fun toggleDislike(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) { repository.toggleDislike(videoId) }
    }

    fun toggleFavorite(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(videoId)
            showMessage("Updated Favorites")
        }
    }

    fun toggleWatchLater(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleWatchLater(videoId)
            showMessage("Updated Watch Later")
        }
    }

    fun toggleSubscribe(video: VideoItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleSubscribe(video)
        }
    }

    fun unsubscribeChannel(channelId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.unsubscribeChannel(channelId)
        }
    }

    fun addComment(videoId: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addComment(videoId, text)
        }
    }

    fun toggleCommentLike(commentId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleCommentLike(commentId)
        }
    }

    fun createPlaylist(name: String, description: String = "", initialVideoId: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val id = repository.createPlaylist(name, description)
            if (id > 0L && initialVideoId != null) {
                repository.addVideoToPlaylist(id, initialVideoId)
            }
            if (id > 0L) {
                showMessage("Created playlist \"$name\"")
            }
        }
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePlaylist(playlistId)
            showMessage("Playlist deleted")
        }
    }

    fun toggleVideoInPlaylist(playlist: PlaylistUiModel, videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (playlist.videoIds.contains(videoId)) {
                repository.removeVideoFromPlaylist(playlist.id, videoId)
                showMessage("Removed from ${playlist.name}")
            } else {
                repository.addVideoToPlaylist(playlist.id, videoId)
                showMessage("Saved to ${playlist.name}")
            }
        }
    }

    fun removeFromHistory(videoId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFromHistory(videoId)
        }
    }

    fun clearWatchHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearWatchHistory()
            showMessage("Watch history cleared")
        }
    }

    fun downloadOrRemoveVideo(videoItem: VideoItem, currentlyDownloaded: Boolean) {
        if (!videoItem.isDownloadable) {
            showMessage("This stream is licensed for online playback only")
            return
        }
        val filesDir = getApplication<Application>().filesDir
        val offlineDir = File(filesDir, "offline_videos").apply { mkdirs() }
        val targetFile = File(offlineDir, "${videoItem.id}.mp4")

        if (currentlyDownloaded) {
            viewModelScope.launch(Dispatchers.IO) {
                if (targetFile.exists()) targetFile.delete()
                repository.setDownloaded(videoItem.id, false)
                showMessage("Removed offline download")
            }
            return
        }

        if (_downloadingIds.value.contains(videoItem.id)) return
        _downloadingIds.value = _downloadingIds.value + videoItem.id
        showMessage("Downloading \"${videoItem.title.take(24)}…\"")

        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                try {
                    val conn = (URL(videoItem.videoUrl).openConnection() as HttpURLConnection).apply {
                        connectTimeout = 10_000
                        readTimeout = 15_000
                    }
                    conn.inputStream.use { input ->
                        targetFile.outputStream().use { output ->
                            val buffer = ByteArray(16 * 1024)
                            var totalBytes = 0L
                            val maxCacheBytes = 6L * 1024L * 1024L // Cache clean offline preview up to 6MB
                            while (totalBytes < maxCacheBytes) {
                                val read = input.read(buffer)
                                if (read == -1) break
                                output.write(buffer, 0, read)
                                totalBytes += read
                            }
                        }
                    }
                    conn.disconnect()
                    true
                } catch (_: Exception) {
                    if (targetFile.exists()) targetFile.delete()
                    false
                }
            }
            _downloadingIds.value = _downloadingIds.value - videoItem.id
            if (success) {
                withContext(Dispatchers.IO) {
                    repository.setDownloaded(videoItem.id, true)
                }
                showMessage("Saved for offline viewing")
            } else {
                showMessage("Download failed — check connection")
            }
        }
    }

    fun markNotificationRead(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAllNotificationsRead()
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { repository.setDarkTheme(enabled) }
    }

    fun setAutoPlayEnabled(enabled: Boolean) {
        playerManager.setAutoPlayNext(enabled)
    }

    fun setPreferredQuality(quality: String) {
        playerManager.setQuality(quality)
    }

    fun setSubtitlesEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSubtitlesEnabled(enabled)
        }
        if (playbackState.value.subtitlesEnabled != enabled) {
            playerManager.toggleSubtitles()
        }
    }

    fun updateProfile(username: String, handle: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateProfile(username, handle)
            showMessage("Profile updated")
        }
    }

    fun clearMessage() {
        _snackbarMessage.value = null
    }

    private fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
