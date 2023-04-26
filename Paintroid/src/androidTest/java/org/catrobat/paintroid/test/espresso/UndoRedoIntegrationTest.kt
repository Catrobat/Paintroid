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

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso

import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.MainActivityHelper
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.UiMatcher
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.test.utils.TestUtils.Companion.selectColorInDialog
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.ui.Perspective
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UndoRedoIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var activityHelper: MainActivityHelper? = null
    private var perspective: Perspective? = null

    @Before
    fun setUp() {
        activityHelper = MainActivityHelper(launchActivityRule.activity)
        activityHelper?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        perspective = launchActivityRule.activity.perspective
        onToolBarView().performSelectTool(ToolType.BRUSH)
    }

    @Suppress("LongMethod")
    @Test
    fun testUndoRedoIconsWhenSwitchToLandscapeMode() {
        Assert.assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT.toLong(),
            activityHelper?.screenOrientation?.toLong()
        )
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_TOP_LEFT))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_RIGHT))
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onTopBarView().performUndo()
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        activityHelper?.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Assert.assertEquals(
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE.toLong(),
            activityHelper?.screenOrientation?.toLong()
        )
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo),
                        ViewMatchers.isEnabled()
                    )
                )
            )
            .perform(ViewActions.click())
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo),
                        ViewMatchers.isEnabled()
                    )
                )
            ).perform(ViewActions.click())
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_RIGHT)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_LEFT)
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_btn_top_undo))
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
    }

    @Test
    fun testDisableEnableUndo() {
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo),
                        ViewMatchers.isEnabled()
                    )
                )
            )
        onTopBarView().performUndo()
        onTopBarView().onUndoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_undo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
    }

    @Test
    fun testDisableEnableRedo() {
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo_disabled),
                        Matchers.not(ViewMatchers.isEnabled())
                    )
                )
            )
        onTopBarView().performUndo()
        onTopBarView().onRedoButton()
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        UiMatcher.withDrawable(R.drawable.ic_pocketpaint_redo),
                        ViewMatchers.isEnabled()
                    )
                )
            )
    }

    @Test
    fun testPreserveZoomAndMoveAfterUndo() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val scale = .5f
        val translationX = 10
        val translationY = 15

        perspective?.scale = scale
        perspective?.surfaceTranslationX = translationX.toFloat()
        perspective?.surfaceTranslationY = translationY.toFloat()

        onTopBarView().performUndo()
        perspective?.scale?.let { Assert.assertEquals(scale, it, Float.MIN_VALUE) }
        perspective?.surfaceTranslationX?.let { Assert.assertEquals(translationX.toFloat(), it, Float.MIN_VALUE) }
        perspective?.surfaceTranslationY?.let { Assert.assertEquals(translationY.toFloat(), it, Float.MIN_VALUE) }
    }

    @Test
    fun testPreserveZoomAndMoveAfterRedo() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onTopBarView().performUndo()
        val scale = .5f
        val translationX = 10
        val translationY = 15

        perspective?.scale = scale
        perspective?.surfaceTranslationX = translationX.toFloat()
        perspective?.surfaceTranslationY = translationY.toFloat()

        onTopBarView().performRedo()
        perspective?.scale?.let { Assert.assertEquals(scale, it, Float.MIN_VALUE) }
        perspective?.surfaceTranslationX?.let { Assert.assertEquals(translationX.toFloat(), it, Float.MIN_VALUE) }
        perspective?.surfaceTranslationY?.let { Assert.assertEquals(translationY.toFloat(), it, Float.MIN_VALUE) }
    }

    @Test
    fun testUndoDoesNotResetLayerVisibility() {
        onDrawingSurfaceView().perform(
            UiInteractions.swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
        )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE
                )
            )
        onLayerMenuView().performOpen().performToggleLayerVisibility(0).performClose()
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
    }

    @Test
    fun testUndoRedoDoesNotResetLayerVisibility() {
        onDrawingSurfaceView().perform(
            UiInteractions.swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
        )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE
                )
            )
            .perform(
                UiInteractions.swipeAccurate(
                    DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE,
                    DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE
                )
            )
        onLayerMenuView().performOpen().performToggleLayerVisibility(0).performClose()
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onTopBarView()
            .performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_TOP_MIDDLE)
        onTopBarView().performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE)
        onTopBarView().performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE)
        onTopBarView().performRedo()
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
        onLayerMenuView().performOpen().performToggleLayerVisibility(0).performClose()
        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_LEFT_MIDDLE)
    }

    @Test
    fun testChangeColorUndoRedo() {
        selectColorInDialog(0)
        selectColorInDialog(1)
        selectColorInDialog(2)
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onColorPickerView().performOpenColorPicker()
        onColorPickerView().checkCurrentViewColor(Color.BLACK)
        onColorPickerView()
            .onNegativeButton()
            .perform(ViewActions.click())
        onTopBarView().performRedo()
        onTopBarView().performRedo()
        onTopBarView().performRedo()
        onDrawingSurfaceView().perform(
            UiInteractions.swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
        )
        onDrawingSurfaceView().checkPixelColor(
            Color.parseColor("#FF078707"),
            BitmapLocationProvider.HALFWAY_TOP_MIDDLE
        )
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onDrawingSurfaceView().perform(
            UiInteractions.swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE
            )
        )
        onDrawingSurfaceView().checkPixelColor(
            Color.parseColor("#FF0074CD"),
            BitmapLocationProvider.HALFWAY_BOTTOM_MIDDLE
        )
        onTopBarView().performUndo()
        onTopBarView().performUndo()
        onTopBarView().performRedo()
        onDrawingSurfaceView().perform(
            UiInteractions.swipeAccurate(
                DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE,
                DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE
            )
        )
        onDrawingSurfaceView().checkPixelColor(
            Color.parseColor("#FF0074CD"),
            BitmapLocationProvider.HALFWAY_TOP_MIDDLE
        )
    }
}
