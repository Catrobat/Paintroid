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

import android.graphics.Color
import android.graphics.Paint
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.R
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.OffsetLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.ShapeTool
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShapeToolIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var toolReference: ToolReference? = null
    private lateinit var mainActivity: MainActivity

    @Before
    fun setUp() {
        mainActivity = launchActivityRule.activity
        toolReference = mainActivity.toolReference

        onToolBarView().performSelectTool(ToolType.SHAPE)
    }

    private fun getCurrentToolBitmapPaint(): Paint? = (toolReference?.tool as ShapeTool?)?.shapeBitmapPaint

    private fun getToolPaint(): Paint = mainActivity.toolPaint.paint

    @Test
    fun testEllipseIsDrawnOnBitmap() {
        onShapeToolOptionsView().performSelectShape(DrawableShape.OVAL)

        val ellipseTool = toolReference?.tool as BaseToolWithRectangleShape?
        val rectHeight = ellipseTool?.boxHeight

        onToolBarView().performCloseToolOptionsView()
        onTopBarView().performClickCheckmark()
        if (rectHeight != null) {
            onDrawingSurfaceView()
                .checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
                .checkPixelColor(
                    Color.BLACK,
                    OffsetLocationProvider.withOffset(
                        BitmapLocationProvider.MIDDLE,
                        (rectHeight / 2.5f).toInt(),
                        0
                    )
                )
                .checkPixelColor(
                    Color.TRANSPARENT,
                    OffsetLocationProvider.withOffset(
                        BitmapLocationProvider.MIDDLE,
                        (rectHeight / 2.5f).toInt(),
                        (rectHeight / 2.5f).toInt()
                    )
                )
        }
    }

    @Test
    fun testUndoRedo() {
        onToolBarView().performCloseToolOptionsView()
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
        onTopBarView().performUndo()
        onDrawingSurfaceView().checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
        onTopBarView().performRedo()
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testFilledRectChangesColor() {
        onToolBarView().performCloseToolOptionsView()
        onToolProperties().setColorResource(R.color.pocketpaint_color_picker_brown1)
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView()
            .checkPixelColorResource(
                R.color.pocketpaint_color_picker_brown1,
                BitmapLocationProvider.MIDDLE
            )
    }

    @Test
    fun testDrawWithHeartShape() {
        onShapeToolOptionsView().performSelectShape(DrawableShape.HEART)
        onToolBarView().performCloseToolOptionsView()
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView().checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
    }

    @Test
    fun testAntiAliasingIsOffIfShapeOutlineWidthIsOne() {
        onToolBarView().performSelectTool(ToolType.SHAPE)
        onShapeToolOptionsView().performSelectShapeDrawType(DrawableStyle.STROKE)
        onShapeToolOptionsView().performSetOutlineWidth(UiInteractions.touchCenterLeft())
        drawShape()

        val bitmapPaint: Paint? = getCurrentToolBitmapPaint()
        val toolPaint: Paint = getToolPaint()

        if (bitmapPaint != null) {
            Assert.assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias)
        }
        Assert.assertTrue("TOOL_PAINT antialiasing should be on", toolPaint.isAntiAlias)
    }

    @Test
    fun testDoNotUseRegularToolPaintInShapeTool() {
        onToolBarView().performSelectTool(ToolType.SHAPE)
        onShapeToolOptionsView().performSelectShapeDrawType(DrawableStyle.FILL)
        drawShape()

        val bitmapPaint: Paint? = getCurrentToolBitmapPaint()
        val toolPaint: Paint = getToolPaint()

        Assert.assertNotEquals("bitmapPaint and toolPaint should differ", bitmapPaint, toolPaint)
    }

    @Test
    fun testShapeWithOutlineAlsoWorksWithTransparentColor() {
        onToolBarView().performSelectTool(ToolType.SHAPE)
        onShapeToolOptionsView().performSelectShape(DrawableShape.RECTANGLE)
        onShapeToolOptionsView().performSelectShapeDrawType(DrawableStyle.FILL)
        onToolProperties().setColor(Color.BLACK)
        drawShape()
        onToolBarView().performClickSelectedToolButton()
        onShapeToolOptionsView().performSelectShape(DrawableShape.OVAL)
        onShapeToolOptionsView().performSelectShapeDrawType(DrawableStyle.STROKE)
        onToolProperties().setColor(Color.TRANSPARENT)
        drawShape()

        onDrawingSurfaceView()
            .checkPixelColor(Color.BLACK, DrawingSurfaceLocationProvider.TOOL_POSITION)
        onDrawingSurfaceView()
            .checkPixelColor(Color.TRANSPARENT, DrawingSurfaceLocationProvider.TOP_MIDDLE)
    }

    @Test
    fun testShapeToolBoxGetsPlacedCorrectWhenZoomedIn() {
        onToolBarView().performSelectTool(ToolType.BRUSH)

        mainActivity.perspective.surfaceTranslationY = 200f
        mainActivity.perspective.surfaceTranslationX = 50f
        mainActivity.perspective.scale = 2.0f
        mainActivity.refreshDrawingSurface()

        onToolBarView().performSelectTool(ToolType.SHAPE)
        onShapeToolOptionsView().performSelectShape(DrawableShape.RECTANGLE)
        onShapeToolOptionsView().performSelectShapeDrawType(DrawableStyle.FILL)
        onTopBarView().performClickCheckmark()
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.BLACK,
                mainActivity.perspective.surfaceCenterX - mainActivity.perspective.surfaceTranslationX,
                mainActivity.perspective.surfaceCenterY - mainActivity.perspective.surfaceTranslationY
            )
    }

    private fun drawShape() {
        onToolBarView().performCloseToolOptionsView()
        onTopBarView().performClickCheckmark()
    }
}
