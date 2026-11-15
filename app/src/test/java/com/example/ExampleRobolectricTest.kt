package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `app_name is YouTubeX`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("YouTubeX", appName)
    }

    @Test
    fun `navigation across bottom tabs and search empty state work`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("home_screen").assertIsDisplayed()

        // Switch to Search tab
        composeTestRule.onNodeWithTag("nav_search").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("search_screen").assertIsDisplayed()

        // Search for nonexistent query to verify empty state
        composeTestRule.onNodeWithTag("search_text_field").performTextInput("zzzz_no_match_999")
        composeTestRule.waitUntil(timeoutMillis = 3_000L) {
            composeTestRule.onAllNodesWithTag("empty_search_state").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("empty_search_state").performScrollTo().assertIsDisplayed()

        // Clear search
        composeTestRule.onNodeWithTag("clear_search_input_btn").performClick()
        composeTestRule.waitForIdle()

        // Switch to Library tab
        composeTestRule.onNodeWithTag("nav_library").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("library_screen").assertIsDisplayed()
        composeTestRule.onNodeWithText("Watch Later").assertIsDisplayed()

        // Switch to Profile tab
        composeTestRule.onNodeWithTag("nav_profile").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("profile_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("profile_screen").performScrollToNode(hasTestTag("dark_theme_switch"))
        composeTestRule.onNodeWithTag("dark_theme_switch").assertIsDisplayed()
    }

    @Test
    fun `notifications screen opens from home top bar`() {
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("notifications_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("notifications_screen").assertIsDisplayed()
    }
}
