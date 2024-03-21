/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso

import android.graphics.Color
import android.webkit.WebView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.web.sugar.Web.onWebView
import androidx.test.espresso.web.webdriver.DriverAtoms.webClick
import androidx.test.espresso.web.webdriver.DriverAtoms.findElement
import androidx.test.espresso.web.webdriver.Locator
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ImportToolOptionsViewInteraction.Companion.onImportToolOptionsViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.Companion.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.test.testsuites.annotations.MediaGalleryTests
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.categories.Category
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@Category(MediaGalleryTests::class)
class MediaGalleryWebViewClientIntegrationTest {
    companion object {
        private const val TOP_APP_BAR_TITLE = "top-app-bar__title"
        private const val TOP_APP_BAR_BUTTON_SIDEBAR_TOGGLE = "top-app-bar__btn-sidebar-toggle"
        private const val LOGO = "logo"
        private const val MEDIA_FILE = "mediafile-335"
    }

    @get:Rule
    val launchActivityRule = ActivityTestRule(
            MainActivity::class.java
    )

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    private lateinit var mainActivity: MainActivity
    private lateinit var idlingResource: IdlingResource

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        idlingResource = mainActivity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testClickingOnCatrobatCommunityClosesWebView() {
        onToolBarView().performSelectTool(ToolType.IMPORTPNG)
        onImportToolOptionsViewInteraction().performOpenStickers()
        clickElementInWebView(120_000, Locator.ID, TOP_APP_BAR_TITLE)
        checkIfViewDisappears(120_000, R.id.webview)
    }

    @Test
    fun testClickingOnCatrobatLogoClosesWebView() {
        onToolBarView().performSelectTool(ToolType.IMPORTPNG)
        onImportToolOptionsViewInteraction().performOpenStickers()
        clickElementInWebView(120_000, Locator.ID, TOP_APP_BAR_BUTTON_SIDEBAR_TOGGLE)
        clickElementInWebView(120_000, Locator.CLASS_NAME, LOGO)
        checkIfViewDisappears(120_000, R.id.webview)
    }

    @Test
    fun testImportSticker() {
        runBlocking {
            delay(3000)
        }
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onToolBarView().performSelectTool(ToolType.IMPORTPNG)
        onImportToolOptionsViewInteraction().performOpenStickers()
        clickElementInWebView(120_000, Locator.ID, MEDIA_FILE)
        checkIfViewDisappears(120_000, R.id.webview)
        runBlocking {
            delay(5000)
        }
        TopBarViewInteraction.onTopBarView().performClickCheckmark()
        DrawingSurfaceInteraction.onDrawingSurfaceView().checkPixelColorIsNotTransparent(BitmapLocationProvider.MIDDLE)
    }

    @SuppressWarnings("SwallowedException")
    private fun clickElementInWebView(maxWaitingTimeMs: Int, locator: Locator, name: String) {
        val endTime = System.currentTimeMillis() + maxWaitingTimeMs
        do {
            try {
                onWebView().withElement(findElement(locator, name)).perform(webClick())
                return
            } catch (e: java.lang.RuntimeException) {
                runBlocking {
                    delay(1000)
                }
                continue
            }
        } while (System.currentTimeMillis() <= endTime)
        onWebView().withElement(findElement(locator, name)).perform(webClick())
    }

    private fun checkIfViewDisappears(maxWaitingTimeMs: Int, viewId: Int) {
        val endTime = System.currentTimeMillis() + maxWaitingTimeMs
        do {
            val view = mainActivity.findViewById<WebView>(viewId)
        } while (view != null && System.currentTimeMillis() <= endTime)
        onView(withId(viewId)).check(ViewAssertions.doesNotExist())
    }
}
