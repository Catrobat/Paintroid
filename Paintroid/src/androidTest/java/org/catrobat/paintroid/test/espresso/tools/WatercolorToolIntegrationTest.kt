/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.test.espresso.tools

import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import junit.framework.Assert.assertTrue
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WatercolorToolIntegrationTest {

    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Before
    fun setUp() {
        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.WATERCOLOR)
    }

    private fun getCurrentToolBitmapPaint(): Paint? {
        return launchActivityRule.activity.toolPaint.paint
    }

    private fun getCurrentToolCanvasPaint(): Paint? {
        return launchActivityRule.activity.toolPaint.previewPaint
    }

    @Test
    fun testSwitchingBetweenBrushAndWatercolorandEraser() {
        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        assertTrue("Paint has no maskfilter", launchActivityRule.activity.toolPaint.paint.maskFilter != null)

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.ERASER)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.BRUSH)

        BottomNavigationViewInteraction.onBottomNavigationView()
            .checkShowsCurrentTool(ToolType.BRUSH)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        ToolBarViewInteraction.onToolBarView()
            .performSelectTool(ToolType.PIPETTE)

        DrawingSurfaceInteraction.onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))

        assertTrue("Paint still has maskfilter", launchActivityRule.activity.toolPaint.paint.maskFilter == null)

    }

}