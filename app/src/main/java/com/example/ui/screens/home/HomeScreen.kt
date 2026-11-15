package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WatchLater
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.PlaylistUiModel
import com.example.data.model.VideoCategory
import com.example.data.model.VideoWithInteraction
import com.example.ui.components.AddToPlaylistBottomSheet
import com.example.ui.components.CategoryFilterChips
import com.example.ui.components.VideoCard
import com.example.ui.components.VideoCardSkeleton
import com.example.ui.components.VideoThumbnail
import com.example.ui.components.YouTubeXLogo
import com.example.ui.components.formatDurationMs
import com.example.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    unreadNotificationCount: Int,
    playlists: List<PlaylistUiModel>,
    onSelectCategory: (VideoCategory) -> Unit,
    onRefresh: () -> Unit,
    onSearchClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onPlayVideo: (VideoWithInteraction) -> Unit,
    onToggleWatchLater: (String) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onToggleVideoInPlaylist: (PlaylistUiModel, String) -> Unit,
    onCreatePlaylist: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var playlistSheetVideoId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .testTag("home_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            YouTubeXLogo()
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = onRefresh,
                modifier = Modifier.testTag("home_refresh_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh feed",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.testTag("notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(
                                    text = unreadNotificationCount.toString(),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (unreadNotificationCount > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            IconButton(
                onClick = onSearchClick,
                modifier = Modifier.testTag("search_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search videos",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // Category Filter Chips
        CategoryFilterChips(
            selectedCategory = uiState.selectedCategory,
            onSelectCategory = onSelectCategory
        )

        if (uiState.isLoading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(4) {
                    VideoCardSkeleton()
                }
            }
        } else {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val isWide = maxWidth >= 680.dp
                val columns = if (isWide) 2 else 1

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 84.dp)
                ) {
                    // Featured Hero Premiere Card
                    if (uiState.featuredVideo != null && uiState.selectedCategory == VideoCategory.ALL) {
                        item(span = { GridItemSpan(columns) }, key = "hero_banner") {
                            FeaturedHeroCard(
                                item = uiState.featuredVideo,
                                onPlayClick = { onPlayVideo(uiState.featuredVideo) },
                                onWatchLaterClick = { onToggleWatchLater(uiState.featuredVideo.video.id) },
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Continue Watching Carousel
                    if (uiState.continueWatching.isNotEmpty() && uiState.selectedCategory == VideoCategory.ALL) {
                        item(span = { GridItemSpan(columns) }, key = "continue_watching_section") {
                            Column(modifier = Modifier.padding(top = 10.dp, bottom = 12.dp)) {
                                Text(
                                    text = "Continue Watching",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(uiState.continueWatching, key = { "cw_${it.video.id}" }) { cwItem ->
                                        ContinueWatchingCard(
                                            item = cwItem,
                                            onClick = { onPlayVideo(cwItem) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Section Title
                    item(span = { GridItemSpan(columns) }, key = "feed_header") {
                        Text(
                            text = if (uiState.selectedCategory == VideoCategory.ALL) {
                                "Recommended For You"
                            } else {
                                "${uiState.selectedCategory.displayName} Streams"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }

                    items(uiState.feedVideos, key = { it.video.id }) { videoItem ->
                        VideoCard(
                            item = videoItem,
                            onClick = { onPlayVideo(videoItem) },
                            onWatchLaterClick = { onToggleWatchLater(videoItem.video.id) },
                            onAddToPlaylistClick = { playlistSheetVideoId = videoItem.video.id },
                            onFavoriteClick = { onToggleFavorite(videoItem.video.id) }
                        )
                    }
                }
            }
        }
    }

    playlistSheetVideoId?.let { targetVideoId ->
        AddToPlaylistBottomSheet(
            videoId = targetVideoId,
            playlists = playlists,
            onTogglePlaylist = { playlist -> onToggleVideoInPlaylist(playlist, targetVideoId) },
            onCreateNewPlaylist = { name, desc -> onCreatePlaylist(name, desc, targetVideoId) },
            onDismiss = { playlistSheetVideoId = null }
        )
    }
}

@Composable
private fun FeaturedHeroCard(
    item: VideoWithInteraction,
    onPlayClick: () -> Unit,
    onWatchLaterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onPlayClick)
            .testTag("featured_hero_card"),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_stream),
                contentDescription = item.video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Black.copy(alpha = 0.15f),
                                Color.Black.copy(alpha = 0.45f),
                                Color.Black.copy(alpha = 0.92f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "FEATURED PREMIERE",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.video.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${item.video.channelName} • ${item.video.viewCountFormatted} • ${item.video.durationLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onPlayClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(22.dp),
                        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("hero_watch_now_btn")
                    ) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Watch Now", style = MaterialTheme.typography.labelLarge)
                    }
                    FilledTonalIconButton(onClick = onWatchLaterClick) {
                        Icon(
                            imageVector = if (item.isWatchLater) Icons.Filled.WatchLater else Icons.Outlined.WatchLater,
                            contentDescription = "Save to Watch Later",
                            tint = if (item.isWatchLater) MaterialTheme.colorScheme.primary else Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContinueWatchingCard(
    item: VideoWithInteraction,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(172.dp)
            .clickable(onClick = onClick)
            .testTag("continue_watching_${item.video.id}")
    ) {
        VideoThumbnail(
            video = item.video,
            progressFraction = item.progressFraction,
            isDownloaded = item.isDownloaded,
            cornerRadius = 12.dp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = item.video.title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Resume at ${formatDurationMs(item.watchProgressMs)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}
