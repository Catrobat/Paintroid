/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.content.Intent
import android.graphics.Color
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.TEMP_IMAGE_PATH
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.ui.Perspective
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

private const val THREAD_WAITING_TIME: Long = 2500
private const val THREAD_SHORT_WAITING_TIME: Long = 1000

@RunWith(AndroidJUnit4::class)
class TemporaryFileSavingTest {

    @get:Rule
    val launchActivityRule = ActivityTestRule(MainActivity::class.java)

    private lateinit var workspace: Workspace
    private lateinit var perspective: Perspective
    private lateinit var toolReference: ToolReference
    private lateinit var mainActivity: MainActivity
    private lateinit var intent: Intent

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)
        intent = Intent().putExtra("isTemporaryFileSavingTest", true)
        mainActivity = launchActivityRule.launchActivity(intent)
        workspace = mainActivity.workspace
        perspective = mainActivity.perspective
        toolReference = mainActivity.toolReference
        val file = File(mainActivity.filesDir, TEMP_IMAGE_PATH)
        if (file.exists()) {
            file.delete()
        }
    }

    @Test
    fun testOneUserInteraction() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        Thread.sleep(THREAD_WAITING_TIME)
        launchActivityRule.finishActivity()
        launchActivityRule.launchActivity(intent)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testTooShortWaitingTime() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        Thread.sleep(THREAD_SHORT_WAITING_TIME)
        launchActivityRule.finishActivity()
        launchActivityRule.launchActivity(intent)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testMultipleUserInteractions() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        Thread.sleep(THREAD_WAITING_TIME)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        Thread.sleep(THREAD_WAITING_TIME)
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_LEFT))
        launchActivityRule.finishActivity()
        launchActivityRule.launchActivity(intent)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_LEFT)
    }

    @Test
    fun testTempFilesDeletedAfterCreatingNewImage() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        Thread.sleep(THREAD_WAITING_TIME)
        TopBarViewInteraction.onTopBarView()
            .performOpenMoreOptions()
        Espresso.onView(ViewMatchers.withText(R.string.menu_new_image))
            .perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withText(R.string.discard_button_text))
            .perform(ViewActions.click())
        launchActivityRule.finishActivity()
        launchActivityRule.launchActivity(intent)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
    }
}
