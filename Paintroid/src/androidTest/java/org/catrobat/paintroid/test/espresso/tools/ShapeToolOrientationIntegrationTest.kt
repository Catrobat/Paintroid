/*
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2022 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

@file:Suppress("DEPRECATION")

package org.catrobat.paintroid.test.espresso.tools

import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ShapeToolOrientationIntegrationTest {
    @get:Rule
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()

    @Parameterized.Parameter
    var shape: DrawableShape? = null

    @Parameterized.Parameter(1)
    var shapeId = 0
    private var workspace: Workspace? = null
    @Before
    fun setUp() {
        workspace = activityTestRule.activity.workspace
        onToolBarView().performSelectTool(ToolType.SHAPE)
    }

    @Test
    fun testRememberShapeAfterOrientationChange() {
        onShapeToolOptionsView().performSelectShape(shape)
        Espresso.onView(ViewMatchers.withId(shapeId))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onToolBarView().performCloseToolOptionsView()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        val expectedBitmap = workspace?.bitmapOfCurrentLayer
        onTopBarView().performUndo()
        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Espresso.onView(ViewMatchers.withId(shapeId))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onToolBarView().performCloseToolOptionsView()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        expectedBitmap?.sameAs(workspace?.bitmapOfCurrentLayer)?.let { Assert.assertTrue(it) }
    }

    @Test
    fun testRememberOutlineShapeAfterOrientationChange() {
        onShapeToolOptionsView()
            .performSelectShape(shape)
            .performSelectShapeDrawType(DrawableStyle.STROKE)
        Espresso.onView(ViewMatchers.withId(shapeId))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_shape_ibtn_outline))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onToolBarView()
            .performCloseToolOptionsView()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        val expectedBitmap = workspace?.bitmapOfCurrentLayer
        onTopBarView()
            .performUndo()
        activityTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        Espresso.onView(ViewMatchers.withId(shapeId))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        Espresso.onView(ViewMatchers.withId(R.id.pocketpaint_shape_ibtn_outline))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
        onToolBarView()
            .performCloseToolOptionsView()
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.TOOL_POSITION))
        if (expectedBitmap != null) {
            Assert.assertTrue(expectedBitmap.sameAs(workspace?.bitmapOfCurrentLayer))
        }
    }

    companion object {
        @Parameterized.Parameters(name = "{0}")
        fun data(): Iterable<Array<Any>> {
            return listOf(
                arrayOf(
                    DrawableShape.RECTANGLE,
                    R.id.pocketpaint_shapes_square_btn
                ),
                arrayOf(
                    DrawableShape.OVAL,
                    R.id.pocketpaint_shapes_circle_btn
                ),
                arrayOf(
                    DrawableShape.HEART,
                    R.id.pocketpaint_shapes_heart_btn
                ),
                arrayOf(
                    DrawableShape.STAR,
                    R.id.pocketpaint_shapes_star_btn
                )
            )
        }
    }
}
