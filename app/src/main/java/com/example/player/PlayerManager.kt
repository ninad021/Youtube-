package com.example.player

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.text.CueGroup
import androidx.media3.exoplayer.ExoPlayer
import com.example.data.local.SampleCatalog
import com.example.data.model.VideoItem
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlaybackUiState(
    val currentVideo: VideoItem? = null,
    val queue: List<VideoItem> = emptyList(),
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val bufferedPercentage: Int = 0,
    val playbackSpeed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val selectedQuality: String = "Auto",
    val subtitlesEnabled: Boolean = false,
    val activeCaptionText: String? = null,
    val autoPlayNext: Boolean = true,
    val autoPlayCountdownSeconds: Int? = null,
    val isFullScreen: Boolean = false,
    val isMinimized: Boolean = false
) {
    val isPlayerVisible: Boolean
        get() = currentVideo != null

    val hasNextVideo: Boolean
        get() = currentVideo != null && queue.indexOfFirst { it.id == currentVideo.id } in 0 until (queue.size - 1)

    val hasPreviousVideo: Boolean
        get() = currentVideo != null && queue.indexOfFirst { it.id == currentVideo.id } > 0
}

class PlayerManager(
    private val context: Context,
    private val repository: VideoRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    private var _exoPlayer: ExoPlayer? = null
    val exoPlayer: ExoPlayer
        get() = _exoPlayer ?: createPlayer().also { _exoPlayer = it }

    private var progressJob: Job? = null
    private var autoPlayCountdownJob: Job? = null
    private var lastSavedPositionMs: Long = 0L
    private var savedVolumeBeforeMute: Float = 1.0f
    private var embeddedCueText: String? = null

    init {
        scope.launch {
            val prefs = repository.userPreferences.first()
            _uiState.update {
                it.copy(
                    autoPlayNext = prefs.autoPlayEnabled,
                    selectedQuality = prefs.preferredQuality,
                    playbackSpeed = prefs.preferredSpeed,
                    subtitlesEnabled = prefs.subtitlesEnabled
                )
            }
        }
    }

    private fun createPlayer(): ExoPlayer {
        return ExoPlayer.Builder(context.applicationContext).build().apply {
            addListener(playerListener)
            val state = _uiState.value
            volume = if (state.isMuted) 0f else state.volume
            playbackParameters = PlaybackParameters(state.playbackSpeed)
            applyQualityTrackConstraints(this, state.selectedQuality)
            applySubtitleTrackPreference(this, state.subtitlesEnabled)
        }
    }

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _uiState.update { it.copy(isPlaying = isPlaying) }
            if (isPlaying) {
                startProgressUpdates()
            } else {
                persistCurrentProgress(force = true)
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_BUFFERING -> {
                    _uiState.update { it.copy(isBuffering = true, hasError = false, errorMessage = null) }
                }
                Player.STATE_READY -> {
                    val dur = _exoPlayer?.duration?.takeIf { it > 0L }
                        ?: ((_uiState.value.currentVideo?.durationSeconds ?: 0) * 1000L)
                    _uiState.update {
                        it.copy(
                            isBuffering = false,
                            hasError = false,
                            errorMessage = null,
                            durationMs = dur
                        )
                    }
                }
                Player.STATE_ENDED -> {
                    _uiState.update { it.copy(isBuffering = false, isPlaying = false) }
                    persistCurrentProgress(force = true)
                    handlePlaybackEnded()
                }
                Player.STATE_IDLE -> {
                    _uiState.update { it.copy(isBuffering = false) }
                }
            }
        }

        override fun onCues(cueGroup: CueGroup) {
            embeddedCueText = cueGroup.cues
                .mapNotNull { it.text?.toString()?.trim() }
                .filter { it.isNotEmpty() }
                .joinToString("\n")
                .ifEmpty { null }
            updateActiveCaption(_exoPlayer?.currentPosition ?: 0L)
        }

        override fun onPlayerError(error: PlaybackException) {
            val online = isNetworkAvailable()
            val message = if (!online) {
                "No internet connection. Connect to Wi-Fi or mobile data and tap Retry."
            } else {
                "Unable to stream this video right now (${error.errorCodeName}). Tap Retry."
            }
            _uiState.update {
                it.copy(
                    isBuffering = false,
                    isPlaying = false,
                    hasError = true,
                    errorMessage = message
                )
            }
        }
    }

    fun playVideo(
        video: VideoItem,
        startPositionMs: Long = 0L,
        customQueue: List<VideoItem>? = null
    ) {
        autoPlayCountdownJob?.cancel()
        val resolvedQueue = customQueue?.takeIf { it.isNotEmpty() }
            ?: (listOf(video) + SampleCatalog.getRelatedVideos(video))

        val player = exoPlayer
        val currentState = _uiState.value

        _uiState.update {
            it.copy(
                currentVideo = video,
                queue = resolvedQueue,
                isBuffering = true,
                hasError = false,
                errorMessage = null,
                currentPositionMs = startPositionMs,
                durationMs = video.durationSeconds * 1000L,
                autoPlayCountdownSeconds = null,
                isMinimized = false
            )
        }

        player.setMediaItem(MediaItem.fromUri(video.videoUrl), startPositionMs)
        player.playbackParameters = PlaybackParameters(currentState.playbackSpeed)
        applyQualityTrackConstraints(player, currentState.selectedQuality)
        applySubtitleTrackPreference(player, currentState.subtitlesEnabled)
        player.prepare()
        player.playWhenReady = true
        lastSavedPositionMs = startPositionMs
        startProgressUpdates()

        scope.launch(Dispatchers.IO) {
            repository.recordWatchProgress(
                videoId = video.id,
                positionMs = startPositionMs,
                durationMs = video.durationSeconds * 1000L
            )
            repository.ensureCommentsForVideo(video.id)
        }
    }

    fun retryPlayback() {
        val video = _uiState.value.currentVideo ?: return
        val resumePos = _uiState.value.currentPositionMs
        playVideo(video, startPositionMs = resumePos, customQueue = _uiState.value.queue)
    }

    fun togglePlayPause() {
        autoPlayCountdownJob?.cancel()
        _uiState.update { it.copy(autoPlayCountdownSeconds = null) }
        val player = exoPlayer
        if (_uiState.value.hasError) {
            retryPlayback()
            return
        }
        if (player.playbackState == Player.STATE_ENDED) {
            player.seekTo(0L)
            player.play()
        } else if (player.isPlaying) {
            player.pause()
        } else {
            player.play()
        }
    }

    fun seekTo(positionMs: Long) {
        val player = exoPlayer
        val maxDur = _uiState.value.durationMs.coerceAtLeast(1L)
        val clamped = positionMs.coerceIn(0L, maxDur)
        player.seekTo(clamped)
        _uiState.update { it.copy(currentPositionMs = clamped) }
        updateActiveCaption(clamped)
        persistCurrentProgress(force = true)
    }

    fun seekBy(deltaMs: Long) {
        val current = exoPlayer.currentPosition
        seekTo(current + deltaMs)
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
        _uiState.update { it.copy(playbackSpeed = speed) }
        scope.launch(Dispatchers.IO) {
            repository.setPreferredSpeed(speed)
        }
    }

    fun setVolume(volume: Float) {
        val clamped = volume.coerceIn(0f, 1f)
        if (clamped > 0f) savedVolumeBeforeMute = clamped
        exoPlayer.volume = clamped
        _uiState.update { it.copy(volume = clamped, isMuted = clamped == 0f) }
    }

    fun toggleMute() {
        val currentlyMuted = _uiState.value.isMuted
        if (currentlyMuted) {
            val restored = savedVolumeBeforeMute.coerceAtLeast(0.5f)
            setVolume(restored)
        } else {
            savedVolumeBeforeMute = _uiState.value.volume.coerceAtLeast(0.5f)
            exoPlayer.volume = 0f
            _uiState.update { it.copy(volume = 0f, isMuted = true) }
        }
    }

    fun setQuality(quality: String) {
        applyQualityTrackConstraints(exoPlayer, quality)
        _uiState.update { it.copy(selectedQuality = quality) }
        scope.launch(Dispatchers.IO) {
            repository.setPreferredQuality(quality)
        }
    }

    fun toggleSubtitles() {
        val next = !_uiState.value.subtitlesEnabled
        applySubtitleTrackPreference(exoPlayer, next)
        _uiState.update { it.copy(subtitlesEnabled = next) }
        updateActiveCaption(_exoPlayer?.currentPosition ?: _uiState.value.currentPositionMs)
        scope.launch(Dispatchers.IO) {
            repository.setSubtitlesEnabled(next)
        }
    }

    fun setAutoPlayNext(enabled: Boolean) {
        _uiState.update { it.copy(autoPlayNext = enabled) }
        if (!enabled) {
            autoPlayCountdownJob?.cancel()
            _uiState.update { it.copy(autoPlayCountdownSeconds = null) }
        }
        scope.launch(Dispatchers.IO) {
            repository.setAutoPlayEnabled(enabled)
        }
    }

    fun playNext() {
        autoPlayCountdownJob?.cancel()
        val state = _uiState.value
        val current = state.currentVideo ?: return
        val index = state.queue.indexOfFirst { it.id == current.id }
        val nextVideo = if (index in 0 until (state.queue.size - 1)) {
            state.queue[index + 1]
        } else {
            SampleCatalog.getRelatedVideos(current).firstOrNull()
        }
        if (nextVideo != null) {
            playVideo(nextVideo, startPositionMs = 0L, customQueue = state.queue)
        }
    }

    fun playPrevious() {
        autoPlayCountdownJob?.cancel()
        val state = _uiState.value
        val current = state.currentVideo ?: return
        if (state.currentPositionMs > 4_000L) {
            seekTo(0L)
            return
        }
        val index = state.queue.indexOfFirst { it.id == current.id }
        if (index > 0) {
            playVideo(state.queue[index - 1], startPositionMs = 0L, customQueue = state.queue)
        } else {
            seekTo(0L)
        }
    }

    fun cancelAutoPlayCountdown() {
        autoPlayCountdownJob?.cancel()
        _uiState.update { it.copy(autoPlayCountdownSeconds = null) }
    }

    fun enterFullScreen() {
        _uiState.update { it.copy(isFullScreen = true, isMinimized = false) }
    }

    fun exitFullScreen() {
        _uiState.update { it.copy(isFullScreen = false) }
    }

    fun minimizeToMiniPlayer() {
        _uiState.update { it.copy(isMinimized = true, isFullScreen = false) }
    }

    fun expandFromMiniPlayer() {
        _uiState.update { it.copy(isMinimized = false) }
    }

    fun closePlayer() {
        autoPlayCountdownJob?.cancel()
        progressJob?.cancel()
        persistCurrentProgress(force = true)
        _exoPlayer?.stop()
        _exoPlayer?.clearMediaItems()
        _uiState.update {
            it.copy(
                currentVideo = null,
                isPlaying = false,
                isBuffering = false,
                isFullScreen = false,
                isMinimized = false,
                autoPlayCountdownSeconds = null,
                activeCaptionText = null
            )
        }
    }

    fun release() {
        autoPlayCountdownJob?.cancel()
        progressJob?.cancel()
        persistCurrentProgress(force = true)
        _exoPlayer?.removeListener(playerListener)
        _exoPlayer?.release()
        _exoPlayer = null
    }

    private fun startProgressUpdates() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (true) {
                val player = _exoPlayer
                if (player != null && _uiState.value.currentVideo != null) {
                    val pos = player.currentPosition.coerceAtLeast(0L)
                    val dur = player.duration.takeIf { it > 0L }
                        ?: _uiState.value.durationMs
                    val buffered = player.bufferedPercentage.coerceIn(0, 100)
                    _uiState.update {
                        it.copy(
                            currentPositionMs = pos,
                            durationMs = dur,
                            bufferedPercentage = buffered
                        )
                    }
                    updateActiveCaption(pos)
                    if (kotlin.math.abs(pos - lastSavedPositionMs) >= 4_000L) {
                        persistCurrentProgress(force = false)
                    }
                }
                delay(350L)
            }
        }
    }

    private fun updateActiveCaption(positionMs: Long) {
        val state = _uiState.value
        if (!state.subtitlesEnabled) {
            if (state.activeCaptionText != null) {
                _uiState.update { it.copy(activeCaptionText = null) }
            }
            return
        }
        val cue = embeddedCueText ?: state.currentVideo?.captions?.firstOrNull {
            positionMs in it.startMs..it.endMs
        }?.text
        if (cue != state.activeCaptionText) {
            _uiState.update { it.copy(activeCaptionText = cue) }
        }
    }

    private fun handlePlaybackEnded() {
        val state = _uiState.value
        if (!state.autoPlayNext || state.currentVideo == null) return
        autoPlayCountdownJob?.cancel()
        autoPlayCountdownJob = scope.launch {
            for (sec in 3 downTo 1) {
                _uiState.update { it.copy(autoPlayCountdownSeconds = sec) }
                delay(1_000L)
            }
            _uiState.update { it.copy(autoPlayCountdownSeconds = null) }
            playNext()
        }
    }

    private fun persistCurrentProgress(force: Boolean) {
        val video = _uiState.value.currentVideo ?: return
        val pos = _exoPlayer?.currentPosition?.coerceAtLeast(0L) ?: _uiState.value.currentPositionMs
        val dur = _exoPlayer?.duration?.takeIf { it > 0L } ?: _uiState.value.durationMs
        if (force || kotlin.math.abs(pos - lastSavedPositionMs) >= 3_000L) {
            lastSavedPositionMs = pos
            scope.launch(Dispatchers.IO) {
                repository.recordWatchProgress(video.id, pos, dur)
            }
        }
    }

    private fun applyQualityTrackConstraints(player: ExoPlayer, quality: String) {
        val builder = player.trackSelectionParameters.buildUpon()
        when (quality) {
            "1080p" -> builder.setMaxVideoSize(1920, 1080)
            "720p" -> builder.setMaxVideoSize(1280, 720)
            "480p" -> builder.setMaxVideoSize(854, 480)
            "360p" -> builder.setMaxVideoSize(640, 360)
            else -> builder.clearVideoSizeConstraints()
        }
        player.trackSelectionParameters = builder.build()
    }

    private fun applySubtitleTrackPreference(player: ExoPlayer, enabled: Boolean) {
        val builder = player.trackSelectionParameters.buildUpon()
        builder.setTrackTypeDisabled(C.TRACK_TYPE_TEXT, !enabled)
        player.trackSelectionParameters = builder.build()
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return true
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
