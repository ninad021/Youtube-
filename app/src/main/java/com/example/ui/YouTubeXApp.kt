package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.ui.components.MiniPlayerBar
import com.example.ui.navigation.YouTubeXRoute
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.library.LibraryScreen
import com.example.ui.screens.library.PlaylistDetailScreen
import com.example.ui.screens.library.SubscriptionsScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.player.VideoPlayerOverlay
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.theme.YouTubeXTheme
import com.example.ui.viewmodel.YouTubeXViewModel

private data class TopLevelNavItem(
    val label: String,
    val route: YouTubeXRoute,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun YouTubeXApp(
    viewModel: YouTubeXViewModel
) {
    val preferences by viewModel.userPreferences.collectAsStateWithLifecycle()
    val homeState by viewModel.homeUiState.collectAsStateWithLifecycle()
    val searchState by viewModel.searchUiState.collectAsStateWithLifecycle()
    val libraryState by viewModel.libraryUiState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val allVideos by viewModel.allVideos.collectAsStateWithLifecycle()
    val comments by viewModel.activeComments.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val unreadNotifs by viewModel.unreadNotificationCount.collectAsStateWithLifecycle()
    val downloadingIds by viewModel.downloadingIds.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    val topLevelItems = remember {
        listOf(
            TopLevelNavItem(
                label = "Home",
                route = YouTubeXRoute.Home,
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                testTag = "nav_home"
            ),
            TopLevelNavItem(
                label = "Search",
                route = YouTubeXRoute.Search,
                selectedIcon = Icons.Filled.Search,
                unselectedIcon = Icons.Outlined.Search,
                testTag = "nav_search"
            ),
            TopLevelNavItem(
                label = "Library",
                route = YouTubeXRoute.Library,
                selectedIcon = Icons.Filled.VideoLibrary,
                unselectedIcon = Icons.Outlined.VideoLibrary,
                testTag = "nav_library"
            ),
            TopLevelNavItem(
                label = "Profile",
                route = YouTubeXRoute.Profile,
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                testTag = "nav_profile"
            )
        )
    }

    fun navigateTopLevel(route: YouTubeXRoute) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    YouTubeXTheme(darkTheme = preferences.isDarkTheme) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val useNavigationRail = maxWidth >= 720.dp

            Box(modifier = Modifier.fillMaxSize()) {
                Row(modifier = Modifier.fillMaxSize()) {
                    if (useNavigationRail && !playbackState.isFullScreen) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            topLevelItems.forEach { item ->
                                val selected = currentDestination?.hasRoute(item.route::class) == true
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { navigateTopLevel(item.route) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label
                                        )
                                    },
                                    label = { Text(item.label) },
                                    colors = NavigationRailItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                    ),
                                    modifier = Modifier.testTag(item.testTag)
                                )
                            }
                        }
                    }

                    Scaffold(
                        modifier = Modifier.weight(1f),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                        bottomBar = {
                            if (!playbackState.isFullScreen) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Docked Mini-Player above Bottom Nav
                                    AnimatedVisibility(
                                        visible = playbackState.isPlayerVisible && playbackState.isMinimized,
                                        enter = slideInVertically { it } + fadeIn(),
                                        exit = slideOutVertically { it } + fadeOut()
                                    ) {
                                        MiniPlayerBar(
                                            playbackState = playbackState,
                                            onExpand = { viewModel.playerManager.expandFromMiniPlayer() },
                                            onTogglePlayPause = { viewModel.playerManager.togglePlayPause() },
                                            onPlayNext = { viewModel.playerManager.playNext() },
                                            onClose = { viewModel.playerManager.closePlayer() }
                                        )
                                    }

                                    if (!useNavigationRail) {
                                        NavigationBar(
                                            containerColor = MaterialTheme.colorScheme.surface,
                                            tonalElevation = 4.dp
                                        ) {
                                            topLevelItems.forEach { item ->
                                                val selected = currentDestination?.hasRoute(item.route::class) == true
                                                NavigationBarItem(
                                                    selected = selected,
                                                    onClick = { navigateTopLevel(item.route) },
                                                    icon = {
                                                        Icon(
                                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                            contentDescription = item.label
                                                        )
                                                    },
                                                    label = {
                                                        Text(
                                                            text = item.label,
                                                            style = MaterialTheme.typography.labelSmall
                                                        )
                                                    },
                                                    colors = NavigationBarItemDefaults.colors(
                                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                                    ),
                                                    modifier = Modifier.testTag(item.testTag)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = YouTubeXRoute.Home,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            composable<YouTubeXRoute.Home> {
                                HomeScreen(
                                    uiState = homeState,
                                    unreadNotificationCount = unreadNotifs,
                                    playlists = libraryState.playlists,
                                    onSelectCategory = viewModel::selectHomeCategory,
                                    onRefresh = viewModel::refreshHome,
                                    onSearchClick = { navigateTopLevel(YouTubeXRoute.Search) },
                                    onNotificationsClick = {
                                        navController.navigate(YouTubeXRoute.Notifications)
                                    },
                                    onPlayVideo = { item -> viewModel.playVideo(item) },
                                    onToggleWatchLater = viewModel::toggleWatchLater,
                                    onToggleFavorite = viewModel::toggleFavorite,
                                    onToggleVideoInPlaylist = viewModel::toggleVideoInPlaylist,
                                    onCreatePlaylist = viewModel::createPlaylist
                                )
                            }

                            composable<YouTubeXRoute.Search> {
                                SearchScreen(
                                    uiState = searchState,
                                    onQueryChange = viewModel::updateSearchQuery,
                                    onSubmitSearch = viewModel::submitSearch,
                                    onSelectCategory = viewModel::selectSearchCategory,
                                    onDeleteHistoryItem = viewModel::deleteSearchHistoryItem,
                                    onClearHistory = viewModel::clearSearchHistory,
                                    onPlayVideo = { item -> viewModel.playVideo(item) }
                                )
                            }

                            composable<YouTubeXRoute.Library> {
                                LibraryScreen(
                                    uiState = libraryState,
                                    onPlayVideo = { item -> viewModel.playVideo(item) },
                                    onOpenSystemCollection = { type ->
                                        navController.navigate(
                                            YouTubeXRoute.PlaylistDetail(playlistId = -1L, systemType = type)
                                        )
                                    },
                                    onOpenPlaylist = { playlistId ->
                                        navController.navigate(
                                            YouTubeXRoute.PlaylistDetail(playlistId = playlistId, systemType = null)
                                        )
                                    },
                                    onOpenSubscriptions = {
                                        navController.navigate(YouTubeXRoute.Subscriptions)
                                    },
                                    onCreatePlaylist = { name, desc -> viewModel.createPlaylist(name, desc) },
                                    onDeletePlaylist = viewModel::deletePlaylist
                                )
                            }

                            composable<YouTubeXRoute.Profile> {
                                ProfileScreen(
                                    preferences = preferences,
                                    libraryState = libraryState,
                                    onUpdateProfile = viewModel::updateProfile,
                                    onToggleDarkTheme = viewModel::setDarkTheme,
                                    onToggleAutoPlay = viewModel::setAutoPlayEnabled,
                                    onSelectQuality = viewModel::setPreferredQuality,
                                    onToggleSubtitles = viewModel::setSubtitlesEnabled,
                                    onOpenCollection = { type ->
                                        navController.navigate(
                                            YouTubeXRoute.PlaylistDetail(playlistId = -1L, systemType = type)
                                        )
                                    },
                                    onOpenSubscriptions = {
                                        navController.navigate(YouTubeXRoute.Subscriptions)
                                    },
                                    onOpenLibrary = { navigateTopLevel(YouTubeXRoute.Library) }
                                )
                            }

                            composable<YouTubeXRoute.Notifications> {
                                NotificationsScreen(
                                    notifications = notifications,
                                    onBackClick = { navController.popBackStack() },
                                    onNotificationClick = { notif ->
                                        viewModel.markNotificationRead(notif.id)
                                        viewModel.playVideoById(notif.videoId)
                                    },
                                    onMarkAllRead = viewModel::markAllNotificationsRead
                                )
                            }

                            composable<YouTubeXRoute.PlaylistDetail> { backStackEntry ->
                                val route = backStackEntry.toRoute<YouTubeXRoute.PlaylistDetail>()
                                PlaylistDetailScreen(
                                    playlistId = route.playlistId,
                                    systemType = route.systemType,
                                    libraryState = libraryState,
                                    allVideos = allVideos,
                                    onBackClick = { navController.popBackStack() },
                                    onPlayVideo = { item, queue -> viewModel.playVideo(item, queue) },
                                    onRemoveFromPlaylist = viewModel::toggleVideoInPlaylist,
                                    onRemoveFromWatchLater = viewModel::toggleWatchLater,
                                    onRemoveFavorite = viewModel::toggleFavorite,
                                    onRemoveLike = viewModel::toggleLike,
                                    onRemoveHistory = viewModel::removeFromHistory,
                                    onClearHistory = viewModel::clearWatchHistory
                                )
                            }

                            composable<YouTubeXRoute.Subscriptions> {
                                SubscriptionsScreen(
                                    subscriptions = libraryState.subscriptions,
                                    allVideos = allVideos,
                                    onBackClick = { navController.popBackStack() },
                                    onPlayLatestFromChannel = { item -> viewModel.playVideo(item) },
                                    onUnsubscribe = viewModel::unsubscribeChannel
                                )
                            }
                        }
                    }
                }

                // Expanded Video Player Overlay
                AnimatedVisibility(
                    visible = playbackState.isPlayerVisible && !playbackState.isMinimized,
                    enter = slideInVertically { it / 2 } + fadeIn(),
                    exit = slideOutVertically { it / 2 } + fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    VideoPlayerOverlay(
                        playbackState = playbackState,
                        playerManager = viewModel.playerManager,
                        allVideos = allVideos,
                        comments = comments,
                        playlists = libraryState.playlists,
                        downloadingIds = downloadingIds,
                        onToggleLike = viewModel::toggleLike,
                        onToggleDislike = viewModel::toggleDislike,
                        onToggleFavorite = viewModel::toggleFavorite,
                        onToggleWatchLater = viewModel::toggleWatchLater,
                        onToggleSubscribe = viewModel::toggleSubscribe,
                        onDownloadClick = viewModel::downloadOrRemoveVideo,
                        onAddComment = viewModel::addComment,
                        onToggleCommentLike = viewModel::toggleCommentLike,
                        onToggleVideoInPlaylist = viewModel::toggleVideoInPlaylist,
                        onCreatePlaylist = viewModel::createPlaylist,
                        onSelectRelatedVideo = { related ->
                            viewModel.playVideo(related, playbackState.queue)
                        }
                    )
                }
            }
        }
    }
}
