package com.example.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.PlaylistUiModel
import com.example.data.model.VideoItem
import com.example.data.model.VideoWithInteraction
import com.example.ui.components.CompactVideoRow
import com.example.ui.viewmodel.LibraryUiState

@Composable
fun PlaylistDetailScreen(
    playlistId: Long,
    systemType: String?,
    libraryState: LibraryUiState,
    allVideos: List<VideoWithInteraction>,
    onBackClick: () -> Unit,
    onPlayVideo: (VideoWithInteraction, List<VideoItem>) -> Unit,
    onRemoveFromPlaylist: (PlaylistUiModel, String) -> Unit,
    onRemoveFromWatchLater: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit,
    onRemoveLike: (String) -> Unit,
    onRemoveHistory: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customPlaylist = if (systemType == null) {
        libraryState.playlists.find { it.id == playlistId }
    } else null

    val title: String
    val subtitle: String
    val videos: List<VideoWithInteraction>

    when (systemType) {
        "watch_later" -> {
            title = "Watch Later"
            subtitle = "Videos queued for later viewing"
            videos = libraryState.watchLaterVideos
        }
        "favorites" -> {
            title = "Favorites"
            subtitle = "Your top favorite streams"
            videos = libraryState.favoriteVideos
        }
        "liked" -> {
            title = "Liked Videos"
            subtitle = "Videos you've given a thumbs up"
            videos = libraryState.likedVideos
        }
        "downloads" -> {
            title = "Downloads"
            subtitle = "Saved offline for playback anywhere"
            videos = libraryState.downloadedVideos
        }
        "history" -> {
            title = "Watch History"
            subtitle = "Recently watched streams"
            videos = libraryState.watchHistory
        }
        else -> {
            val map = allVideos.associateBy { it.video.id }
            title = customPlaylist?.name ?: "Playlist"
            subtitle = customPlaylist?.description?.ifEmpty { "Custom playlist" } ?: "Custom playlist"
            videos = customPlaylist?.videoIds?.mapNotNull { map[it] } ?: emptyList()
        }
    }

    val queue = videos.map { it.video }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("playlist_detail_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick, modifier = Modifier.testTag("playlist_back_btn")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            if (systemType == "history" && videos.isNotEmpty()) {
                TextButton(
                    onClick = onClearHistory,
                    modifier = Modifier.testTag("clear_watch_history_btn")
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Summary Header Card
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${videos.size} video${if (videos.size == 1) "" else "s"}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                if (videos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { onPlayVideo(videos.first(), queue) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.weight(1f).testTag("playlist_play_all_btn")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play All", color = Color.White)
                        }
                        OutlinedButton(
                            onClick = {
                                val shuffled = videos.shuffled()
                                onPlayVideo(shuffled.first(), shuffled.map { it.video })
                            },
                            shape = RoundedCornerShape(22.dp),
                            modifier = Modifier.weight(1f).testTag("playlist_shuffle_btn")
                        ) {
                            Icon(Icons.Filled.Shuffle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shuffle")
                        }
                    }
                }
            }
        }

        if (videos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Nothing here yet",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Videos added to $title will appear right here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 84.dp)
            ) {
                items(videos, key = { "pd_${it.video.id}" }) { item ->
                    CompactVideoRow(
                        item = item,
                        onClick = { onPlayVideo(item, queue) },
                        trailingAction = {
                            IconButton(
                                onClick = {
                                    when (systemType) {
                                        "watch_later" -> onRemoveFromWatchLater(item.video.id)
                                        "favorites" -> onRemoveFavorite(item.video.id)
                                        "liked" -> onRemoveLike(item.video.id)
                                        "history" -> onRemoveHistory(item.video.id)
                                        null -> customPlaylist?.let { onRemoveFromPlaylist(it, item.video.id) }
                                    }
                                }
                            ) {
                                if (systemType != "downloads") {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Remove from $title",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
