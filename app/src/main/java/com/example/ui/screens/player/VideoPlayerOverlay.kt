package com.example.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.ClosedCaptionOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.ui.PlayerView
import com.example.data.local.SampleCatalog
import com.example.data.model.CommentUiModel
import com.example.data.model.PlaylistUiModel
import com.example.data.model.VideoItem
import com.example.data.model.VideoWithInteraction
import com.example.player.PlaybackUiState
import com.example.player.PlayerManager
import com.example.ui.components.AddToPlaylistBottomSheet
import com.example.ui.components.ChannelAvatar
import com.example.ui.components.CompactVideoRow
import com.example.ui.components.formatDurationMs
import com.example.ui.components.shareVideo
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerOverlay(
    playbackState: PlaybackUiState,
    playerManager: PlayerManager,
    allVideos: List<VideoWithInteraction>,
    comments: List<CommentUiModel>,
    playlists: List<PlaylistUiModel>,
    downloadingIds: Set<String>,
    onToggleLike: (String) -> Unit,
    onToggleDislike: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleWatchLater: (String) -> Unit,
    onToggleSubscribe: (VideoItem) -> Unit,
    onDownloadClick: (VideoItem, Boolean) -> Unit,
    onAddComment: (String, String) -> Unit,
    onToggleCommentLike: (Long) -> Unit,
    onToggleVideoInPlaylist: (PlaylistUiModel, String) -> Unit,
    onCreatePlaylist: (String, String, String) -> Unit,
    onSelectRelatedVideo: (VideoWithInteraction) -> Unit
) {
    val currentVideo = playbackState.currentVideo ?: return
    val videoInteraction = remember(allVideos, currentVideo.id) {
        allVideos.find { it.video.id == currentVideo.id } ?: VideoWithInteraction(currentVideo)
    }
    val relatedVideos = remember(allVideos, currentVideo.id) {
        val relatedOrder = SampleCatalog.getRelatedVideos(currentVideo).map { it.id }
        val map = allVideos.associateBy { it.video.id }
        relatedOrder.mapNotNull { map[it] }
    }

    val context = LocalContext.current
    val activity = context as? Activity

    var controlsVisible by remember { mutableStateOf(true) }
    var showVolumeSlider by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showPlaylistSheet by remember { mutableStateOf(false) }
    var descriptionExpanded by remember { mutableStateOf(false) }
    var commentDraft by remember { mutableStateOf("") }
    var seekFeedbackText by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    LaunchedEffect(currentVideo.id) {
        listState.scrollToItem(0)
        descriptionExpanded = false
        controlsVisible = true
    }

    LaunchedEffect(controlsVisible, playbackState.isPlaying, showVolumeSlider) {
        if (controlsVisible && playbackState.isPlaying && !showVolumeSlider) {
            delay(3_600L)
            controlsVisible = false
        }
    }

    LaunchedEffect(seekFeedbackText) {
        if (seekFeedbackText != null) {
            delay(800L)
            seekFeedbackText = null
        }
    }

    // Manage Full-screen Orientation & System Bars cleanly
    DisposableEffect(playbackState.isFullScreen) {
        if (activity != null) {
            val window = activity.window
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            if (playbackState.isFullScreen) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
        onDispose {
            if (activity != null && playbackState.isFullScreen) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                val controller = WindowCompat.getInsetsController(activity.window, activity.window.decorView)
                controller.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    BackHandler(enabled = !playbackState.isMinimized) {
        if (playbackState.isFullScreen) {
            playerManager.exitFullScreen()
        } else {
            playerManager.minimizeToMiniPlayer()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("video_player_overlay")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (playbackState.isFullScreen) Modifier
                    else Modifier.statusBarsPadding()
                )
        ) {
            // 1. Video Surface + Custom Compose HUD
            Box(
                modifier = Modifier
                    .then(
                        if (playbackState.isFullScreen) Modifier.fillMaxSize()
                        else Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                    )
                    .background(Color.Black)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { controlsVisible = !controlsVisible },
                            onDoubleTap = { offset ->
                                val width = size.width
                                if (offset.x < width * 0.4f) {
                                    playerManager.seekBy(-10_000L)
                                    seekFeedbackText = "-10s"
                                } else if (offset.x > width * 0.6f) {
                                    playerManager.seekBy(10_000L)
                                    seekFeedbackText = "+10s"
                                } else {
                                    playerManager.togglePlayPause()
                                }
                            }
                        )
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            useController = false
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            player = playerManager.exoPlayer
                        }
                    },
                    update = { view ->
                        if (view.player !== playerManager.exoPlayer) {
                            view.player = playerManager.exoPlayer
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Subtitles / Captions Overlay
                if (playbackState.subtitlesEnabled && !playbackState.activeCaptionText.isNullOrEmpty()) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.78f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(
                                horizontal = 20.dp,
                                vertical = if (controlsVisible) 58.dp else 18.dp
                            )
                            .testTag("caption_overlay")
                    ) {
                        Text(
                            text = playbackState.activeCaptionText,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                // Double-tap seek feedback pill
                androidx.compose.animation.AnimatedVisibility(
                    visible = seekFeedbackText != null,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = seekFeedbackText.orEmpty(),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                        )
                    }
                }

                // Buffering Spinner
                if (playbackState.isBuffering && !playbackState.hasError) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(44.dp)
                    )
                }

                // Playback Error State
                if (playbackState.hasError) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.84f))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ErrorOutline,
                            contentDescription = "Playback error",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = playbackState.errorMessage ?: "Video playback error",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { playerManager.retryPlayback() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry")
                        }
                    }
                }

                // Auto-Play Next Countdown Overlay
                if (playbackState.autoPlayCountdownSeconds != null && playbackState.hasNextVideo) {
                    val nextVideo = playbackState.queue.getOrNull(
                        playbackState.queue.indexOfFirst { it.id == currentVideo.id } + 1
                    )
                    if (nextVideo != null) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.88f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Up Next in ${playbackState.autoPlayCountdownSeconds}s",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = nextVideo.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = { playerManager.cancelAutoPlayCountdown() },
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                                    ) {
                                        Text("Cancel", color = Color.White)
                                    }
                                    Button(
                                        onClick = { playerManager.playNext() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text("Play Now")
                                    }
                                }
                            }
                        }
                    }
                }

                // Player Controls HUD
                androidx.compose.animation.AnimatedVisibility(
                    visible = controlsVisible && !playbackState.hasError,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    PlayerControlsHud(
                        playbackState = playbackState,
                        showVolumeSlider = showVolumeSlider,
                        onToggleVolumeSlider = { showVolumeSlider = !showVolumeSlider },
                        onMinimizeOrExitFullScreen = {
                            if (playbackState.isFullScreen) playerManager.exitFullScreen()
                            else playerManager.minimizeToMiniPlayer()
                        },
                        onTogglePlayPause = { playerManager.togglePlayPause() },
                        onSeekBackward = {
                            playerManager.seekBy(-10_000L)
                            seekFeedbackText = "-10s"
                        },
                        onSeekForward = {
                            playerManager.seekBy(10_000L)
                            seekFeedbackText = "+10s"
                        },
                        onPrevious = { playerManager.playPrevious() },
                        onNext = { playerManager.playNext() },
                        onSeekTo = { playerManager.seekTo(it) },
                        onVolumeChange = { playerManager.setVolume(it) },
                        onToggleMute = { playerManager.toggleMute() },
                        onToggleSubtitles = { playerManager.toggleSubtitles() },
                        onToggleAutoPlay = { playerManager.setAutoPlayNext(it) },
                        onOpenSettings = { showSettingsSheet = true },
                        onToggleFullScreen = {
                            if (playbackState.isFullScreen) playerManager.exitFullScreen()
                            else playerManager.enterFullScreen()
                        }
                    )
                }
            }

            // 2. Scrollable Video Details, Interactions, Comments & Related Videos (Portrait)
            if (!playbackState.isFullScreen) {
                val isDownloading = downloadingIds.contains(currentVideo.id)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),
                    contentPadding = PaddingValues(bottom = 28.dp)
                ) {
                    // Title & Stats Header
                    item(key = "details_header") {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .animateContentSize()
                        ) {
                            Text(
                                text = currentVideo.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${currentVideo.viewCountFormatted} • ${currentVideo.uploadDateLabel}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = currentVideo.category.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (descriptionExpanded) "Show less" else "…more",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .clickable { descriptionExpanded = !descriptionExpanded }
                                        .padding(4.dp)
                                        .testTag("expand_description_btn")
                                )
                            }

                            if (descriptionExpanded) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = currentVideo.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(10.dp))
                                        Text(
                                            text = "License: ${currentVideo.licenseNote}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        if (currentVideo.tags.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = currentVideo.tags.joinToString(" ") { "#$it" },
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Channel Row + Subscribe
                    item(key = "channel_row") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ChannelAvatar(
                                channelName = currentVideo.channelName,
                                colorHex = currentVideo.channelAvatarColorHex,
                                size = 42.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentVideo.channelName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = currentVideo.subscriberCountFormatted,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            val subscribed = videoInteraction.isSubscribed
                            Button(
                                onClick = { onToggleSubscribe(currentVideo) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (subscribed) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.primary,
                                    contentColor = if (subscribed) MaterialTheme.colorScheme.onSurface else Color.White
                                ),
                                shape = RoundedCornerShape(22.dp),
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("subscribe_button")
                            ) {
                                if (subscribed) {
                                    Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                }
                                Text(
                                    text = if (subscribed) "Subscribed" else "Subscribe",
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }

                    // Action Pill Bar: Like / Dislike / Share / Watch Later / Save / Favorite / Download
                    item(key = "actions_bar") {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            item {
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                    shape = RoundedCornerShape(22.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Row(
                                            modifier = Modifier
                                                .clickable { onToggleLike(currentVideo.id) }
                                                .padding(start = 14.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
                                                .testTag("like_button"),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (videoInteraction.isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                                contentDescription = "Like video",
                                                tint = if (videoInteraction.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Text(
                                                text = formatCompactCount(videoInteraction.displayLikesCount),
                                                style = MaterialTheme.typography.labelLarge,
                                                color = if (videoInteraction.isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                        Box(
                                            modifier = Modifier
                                                .width(1.dp)
                                                .height(18.dp)
                                                .background(MaterialTheme.colorScheme.outline)
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clickable { onToggleDislike(currentVideo.id) }
                                                .padding(start = 10.dp, end = 14.dp, top = 8.dp, bottom = 8.dp)
                                                .testTag("dislike_button")
                                        ) {
                                            Icon(
                                                imageVector = if (videoInteraction.isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown,
                                                contentDescription = "Dislike video",
                                                tint = if (videoInteraction.isDisliked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                ActionPillButton(
                                    icon = Icons.Filled.Share,
                                    label = "Share",
                                    active = false,
                                    onClick = { shareVideo(context, currentVideo) },
                                    testTag = "share_button"
                                )
                            }

                            item {
                                ActionPillButton(
                                    icon = if (videoInteraction.isWatchLater) Icons.Filled.WatchLater else Icons.Outlined.WatchLater,
                                    label = if (videoInteraction.isWatchLater) "In Watch Later" else "Watch Later",
                                    active = videoInteraction.isWatchLater,
                                    onClick = { onToggleWatchLater(currentVideo.id) },
                                    testTag = "watch_later_button"
                                )
                            }

                            item {
                                ActionPillButton(
                                    icon = if (playlists.any { !it.isSystemWatchLater && it.videoIds.contains(currentVideo.id) }) {
                                        Icons.Filled.BookmarkAdded
                                    } else {
                                        Icons.AutoMirrored.Filled.PlaylistAdd
                                    },
                                    label = "Save",
                                    active = playlists.any { it.videoIds.contains(currentVideo.id) },
                                    onClick = { showPlaylistSheet = true },
                                    testTag = "save_to_playlist_button"
                                )
                            }

                            item {
                                ActionPillButton(
                                    icon = if (videoInteraction.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    label = if (videoInteraction.isFavorite) "Favorited" else "Favorite",
                                    active = videoInteraction.isFavorite,
                                    onClick = { onToggleFavorite(currentVideo.id) },
                                    testTag = "favorite_button"
                                )
                            }

                            if (currentVideo.isDownloadable) {
                                item {
                                    ActionPillButton(
                                        icon = if (videoInteraction.isDownloaded) Icons.Filled.DownloadDone else Icons.Filled.Download,
                                        label = when {
                                            isDownloading -> "Saving…"
                                            videoInteraction.isDownloaded -> "Saved Offline"
                                            else -> "Download"
                                        },
                                        active = videoInteraction.isDownloaded,
                                        onClick = { onDownloadClick(currentVideo, videoInteraction.isDownloaded) },
                                        testTag = "download_button"
                                    )
                                }
                            }
                        }
                    }

                    // Comments Section Header & Composer
                    item(key = "comments_section") {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Comments (${comments.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Public thread",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = commentDraft,
                                        onValueChange = { commentDraft = it },
                                        placeholder = { Text("Add a comment…") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(24.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("comment_input")
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    IconButton(
                                        onClick = {
                                            if (commentDraft.trim().isNotEmpty()) {
                                                onAddComment(currentVideo.id, commentDraft)
                                                commentDraft = ""
                                            }
                                        },
                                        enabled = commentDraft.trim().isNotEmpty(),
                                        modifier = Modifier.testTag("send_comment_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Post comment",
                                            tint = if (commentDraft.trim().isNotEmpty()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                if (comments.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    comments.take(5).forEachIndexed { idx, comment ->
                                        if (idx > 0) {
                                            HorizontalDivider(
                                                color = MaterialTheme.colorScheme.outlineVariant,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            )
                                        }
                                        CommentItemRow(
                                            comment = comment,
                                            onLikeClick = { onToggleCommentLike(comment.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Related Video Recommendations
                    item(key = "related_header") {
                        Text(
                            text = "Up Next & Related",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )
                    }

                    items(relatedVideos, key = { "rel_${it.video.id}" }) { related ->
                        CompactVideoRow(
                            item = related,
                            onClick = { onSelectRelatedVideo(related) }
                        )
                    }
                }
            }
        }
    }

    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ) {
            PlayerSettingsSheetContent(
                playbackState = playbackState,
                onSelectSpeed = { playerManager.setPlaybackSpeed(it) },
                onSelectQuality = { playerManager.setQuality(it) },
                onToggleSubtitles = { playerManager.toggleSubtitles() },
                onToggleAutoPlay = { playerManager.setAutoPlayNext(it) },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }

    if (showPlaylistSheet) {
        AddToPlaylistBottomSheet(
            videoId = currentVideo.id,
            playlists = playlists,
            onTogglePlaylist = { playlist -> onToggleVideoInPlaylist(playlist, currentVideo.id) },
            onCreateNewPlaylist = { name, desc -> onCreatePlaylist(name, desc, currentVideo.id) },
            onDismiss = { showPlaylistSheet = false }
        )
    }
}

@Composable
private fun PlayerControlsHud(
    playbackState: PlaybackUiState,
    showVolumeSlider: Boolean,
    onToggleVolumeSlider: () -> Unit,
    onMinimizeOrExitFullScreen: () -> Unit,
    onTogglePlayPause: () -> Unit,
    onSeekBackward: () -> Unit,
    onSeekForward: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleSubtitles: () -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onOpenSettings: () -> Unit,
    onToggleFullScreen: () -> Unit
) {
    val video = playbackState.currentVideo ?: return
    val playScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1f else 1.06f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "playScale"
    )

    var isScrubbing by remember { mutableStateOf(false) }
    var scrubProgress by remember { mutableFloatStateOf(0f) }

    val totalMs = playbackState.durationMs.coerceAtLeast(1L)
    val sliderValue = if (isScrubbing) {
        scrubProgress
    } else {
        (playbackState.currentPositionMs.toFloat() / totalMs.toFloat()).coerceIn(0f, 1f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.72f),
                        Color.Black.copy(alpha = 0.25f),
                        Color.Black.copy(alpha = 0.80f)
                    )
                )
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Top HUD Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onMinimizeOrExitFullScreen,
                modifier = Modifier.testTag("player_minimize_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (playbackState.isFullScreen) "Exit fullscreen" else "Minimize player",
                    tint = Color.White
                )
            }

            if (playbackState.isFullScreen) {
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Auto",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = playbackState.autoPlayNext,
                    onCheckedChange = onToggleAutoPlay,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.scale(0.75f).testTag("player_autoplay_switch")
                )
            }

            IconButton(
                onClick = onToggleSubtitles,
                modifier = Modifier.testTag("player_subtitles_button")
            ) {
                Icon(
                    imageVector = if (playbackState.subtitlesEnabled) Icons.Filled.ClosedCaption else Icons.Outlined.ClosedCaptionOff,
                    contentDescription = "Toggle captions",
                    tint = if (playbackState.subtitlesEnabled) MaterialTheme.colorScheme.primary else Color.White
                )
            }

            IconButton(
                onClick = onOpenSettings,
                modifier = Modifier.testTag("player_settings_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Playback speed and quality settings",
                    tint = Color.White
                )
            }
        }

        // Center Playback Transport Row
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Previous video",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(
                onClick = onSeekBackward,
                modifier = Modifier.testTag("player_rewind_10s")
            ) {
                Icon(
                    imageVector = Icons.Filled.Replay10,
                    contentDescription = "Rewind 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .scale(playScale)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.58f))
                    .clickable(onClick = onTogglePlayPause)
                    .testTag("player_play_pause_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (playbackState.isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = onSeekForward,
                modifier = Modifier.testTag("player_forward_10s")
            ) {
                Icon(
                    imageVector = Icons.Filled.Forward10,
                    contentDescription = "Forward 10 seconds",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = onNext) {
                Icon(
                    imageVector = Icons.Filled.SkipNext,
                    contentDescription = "Next video",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        // Volume Slider Popover
        if (showVolumeSlider) {
            Surface(
                color = Color.Black.copy(alpha = 0.85f),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 54.dp)
                    .width(210.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onToggleMute, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (playbackState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute toggle",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Slider(
                        value = playbackState.volume,
                        onValueChange = onVolumeChange,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Bottom HUD Bar: Time, Scrubber, Volume, Quality Chip, Fullscreen
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${formatDurationMs((sliderValue * totalMs).toLong())} / ${formatDurationMs(totalMs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                Surface(
                    color = Color.White.copy(alpha = 0.16f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .clickable(onClick = onOpenSettings)
                        .padding(horizontal = 2.dp)
                ) {
                    Text(
                        text = "${playbackState.selectedQuality} • ${playbackState.playbackSpeed}x",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }

                IconButton(
                    onClick = onToggleVolumeSlider,
                    modifier = Modifier.size(36.dp).testTag("player_volume_button")
                ) {
                    Icon(
                        imageVector = if (playbackState.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Volume control",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onToggleFullScreen,
                    modifier = Modifier.size(36.dp).testTag("player_fullscreen_button")
                ) {
                    Icon(
                        imageVector = if (playbackState.isFullScreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                        contentDescription = if (playbackState.isFullScreen) "Exit fullscreen" else "Fullscreen",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Slider(
                value = sliderValue,
                onValueChange = {
                    isScrubbing = true
                    scrubProgress = it
                },
                onValueChangeFinished = {
                    isScrubbing = false
                    onSeekTo((scrubProgress * totalMs).toLong())
                },
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = Color.White.copy(alpha = 0.35f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(26.dp)
                    .testTag("player_seek_slider")
            )
        }
    }
}

@Composable
private fun ActionPillButton(
    icon: ImageVector,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        onClick = onClick,
        color = if (active) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(22.dp),
        modifier = Modifier.testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CommentItemRow(
    comment: CommentUiModel,
    onLikeClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        ChannelAvatar(
            channelName = comment.authorName,
            colorHex = comment.avatarColorHex,
            size = 32.dp
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = comment.timestampLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable(onClick = onLikeClick)
                    .padding(vertical = 2.dp)
            ) {
                Icon(
                    imageVector = if (comment.isLikedByUser) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                    contentDescription = "Like comment",
                    tint = if (comment.isLikedByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${comment.likesCount}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (comment.isLikedByUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun PlayerSettingsSheetContent(
    playbackState: PlaybackUiState,
    onSelectSpeed: (Float) -> Unit,
    onSelectQuality: (String) -> Unit,
    onToggleSubtitles: () -> Unit,
    onToggleAutoPlay: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val speeds = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    val qualities = playbackState.currentVideo?.availableQualities
        ?: listOf("Auto", "1080p", "720p", "480p", "360p")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Playback Settings",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Speed
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Speed, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text("Playback Speed", style = MaterialTheme.typography.titleMedium)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(speeds) { speed ->
                    val selected = playbackState.playbackSpeed == speed
                    Surface(
                        onClick = { onSelectSpeed(speed) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            text = "${speed}x",
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Quality
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Filled.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                Text("Video Quality", style = MaterialTheme.typography.titleMedium)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(qualities) { quality ->
                    val selected = playbackState.selectedQuality == quality
                    Surface(
                        onClick = { onSelectQuality(quality) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh
                    ) {
                        Text(
                            text = quality,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Subtitles / Closed Captions", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = playbackState.subtitlesEnabled,
                onCheckedChange = { onToggleSubtitles() }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Auto-play next video", style = MaterialTheme.typography.titleMedium)
            Switch(
                checked = playbackState.autoPlayNext,
                onCheckedChange = onToggleAutoPlay
            )
        }

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Close")
        }
    }
}

private fun formatCompactCount(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000f)
        count >= 1_000 -> String.format("%.1fK", count / 1_000f)
        else -> count.toString()
    }
}
