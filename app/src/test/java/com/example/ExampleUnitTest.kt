package com.example

import com.example.data.local.SampleCatalog
import com.example.data.model.VideoCategory
import com.example.data.model.VideoWithInteraction
import com.example.ui.components.formatDurationMs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `sample catalog covers all video categories`() {
        val categoriesInCatalog = SampleCatalog.videos.map { it.category }.toSet()
        VideoCategory.entries
            .filter { it != VideoCategory.ALL }
            .forEach { category ->
                assertTrue("Missing category: $category", categoriesInCatalog.contains(category))
            }
    }

    @Test
    fun `related videos exclude current video`() {
        val current = SampleCatalog.videos.first()
        val related = SampleCatalog.getRelatedVideos(current)
        assertTrue(related.none { it.id == current.id })
        assertEquals(SampleCatalog.videos.size - 1, related.size)
    }

    @Test
    fun `formatDurationMs formats seconds minutes and hours cleanly`() {
        assertEquals("0:15", formatDurationMs(15_000L))
        assertEquals("12:14", formatDurationMs(734_000L))
        assertEquals("1:01:05", formatDurationMs(3_665_000L))
    }

    @Test
    fun `video interaction progressFraction clamps cleanly`() {
        val video = SampleCatalog.videos.first()
        val item = VideoWithInteraction(video = video, watchProgressMs = 50_000L, watchDurationMs = 100_000L)
        assertEquals(0.5f, item.progressFraction, 0.001f)
    }
}
