package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.UserPreferencesDataStore
import com.example.data.local.database.YouTubeXDatabase
import com.example.data.repository.VideoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VideoRepositoryTest {

    private lateinit var db: YouTubeXDatabase
    private lateinit var repository: VideoRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, YouTubeXDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = VideoRepository(
            dao = db.youTubeXDao(),
            preferencesDataStore = UserPreferencesDataStore(context)
        )
    }

    @After
    fun tearDown() = runBlocking {
        repository.seedingJob.join()
        db.close()
    }

    @Test
    fun `repository persists likes favorites playlists comments watch history and search history`() = runBlocking {
        repository.seedingJob.join()
        val videoId = "vid_tears_of_steel"

        repository.toggleLike(videoId)
        repository.toggleFavorite(videoId)
        repository.recordWatchProgress(videoId, positionMs = 45_000L, durationMs = 734_000L)

        val afterToggle = repository.videosWithInteractions.first().first { it.video.id == videoId }
        assertTrue(afterToggle.isLiked)
        assertFalse(afterToggle.isDisliked)
        assertTrue(afterToggle.isFavorite)
        assertTrue(afterToggle.watchProgressMs == 45_000L)

        val playlistId = repository.createPlaylist("Sci-Fi Favorites", "Best cyberpunk films")
        assertTrue(playlistId > 0L)
        repository.addVideoToPlaylist(playlistId, videoId)
        val playlists = repository.playlists.first()
        val created = playlists.firstOrNull { it.id == playlistId }
        assertTrue(created != null && created.videoIds.contains(videoId))

        repository.addComment(videoId, "Incredible VFX work!")
        val comments = repository.observeComments(videoId).first()
        assertTrue(comments.any { it.text == "Incredible VFX work!" })

        repository.addSearchQuery("Tears of Steel")
        val searches = repository.searchHistory.first()
        assertTrue(searches.contains("Tears of Steel"))
    }
}
