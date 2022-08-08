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

package org.catrobat.paintroid.test.espresso.tools

import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction.onLayerMenuView
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView
import org.catrobat.paintroid.test.espresso.util.wrappers.StampToolViewInteraction.Companion.onStampToolViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.StampTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StampToolIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private lateinit var workspace: Workspace
    private lateinit var perspective: Perspective
    private lateinit var toolReference: ToolReference
    private lateinit var mainActivity: MainActivity

    @Before
    fun setUp() {
        onToolBarView().performSelectTool(ToolType.BRUSH)
        mainActivity = launchActivityRule.activity
        workspace = mainActivity.workspace
        perspective = mainActivity.perspective
        toolReference = mainActivity.toolReference
    }

    @Test
    fun testBorders() {
        onToolBarView().performSelectTool(ToolType.SHAPE)
        onShapeToolOptionsView()
            .performSelectShape(DrawableShape.RECTANGLE)
            .performSelectShapeDrawType(DrawableStyle.STROKE)
        onTopBarView().performClickCheckmark()
        onToolBarView().performSelectTool(ToolType.STAMP)

        val stampTool = toolReference.tool as StampTool
        stampTool.boxHeight -= 25f
        stampTool.boxWidth -= 25f
        onStampToolViewInteraction().performCopy()

        val topLeft = stampTool.drawingBitmap?.getPixel(0, 0)
        val topRight =
            (stampTool.drawingBitmap?.width)?.minus(1)
                ?.let { stampTool.drawingBitmap?.getPixel(it, 0) }
        val bottomLeft =
            (stampTool.drawingBitmap?.height)?.minus(1)?.let {
                stampTool.drawingBitmap?.getPixel(0, it)
            }
        val bottomRight = (stampTool.drawingBitmap?.width)?.minus(1)?.let {
            stampTool.drawingBitmap?.height?.minus(1)
                ?.let { it1 -> stampTool.drawingBitmap?.getPixel(it, it1) }
        }

        if (topLeft != null) { Assert.assertEquals(topLeft.toLong(), Color.BLACK.toLong()) }
        if (topRight != null) { Assert.assertEquals(topRight.toLong(), Color.BLACK.toLong()) }
        if (bottomLeft != null) { Assert.assertEquals(bottomLeft.toLong(), Color.BLACK.toLong()) }

        Assert.assertEquals(bottomRight?.toLong(), Color.BLACK.toLong())
    }

    @Test
    fun testCopyPixel() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.STAMP)
        onStampToolViewInteraction().performCopy()

        val stampTool = toolReference.tool as StampTool?
        if (stampTool != null) { stampTool.toolPosition[stampTool.toolPosition.x] = stampTool.toolPosition.y * .5f }

        onStampToolViewInteraction().performPaste()
        stampTool?.toolPosition?.let { onDrawingSurfaceView().checkPixelColor(Color.BLACK, it.x, stampTool.toolPosition.y) }
    }

    @Test
    fun testCutAndPastePixel() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.STAMP)
        onStampToolViewInteraction().performCut()

        val stampTool = toolReference.tool as StampTool?
        stampTool?.toolPosition?.x?.let {
            onDrawingSurfaceView()
                .checkPixelColor(
                    Color.TRANSPARENT,
                    it,
                    stampTool.toolPosition.y)
        }
        onStampToolViewInteraction().performPaste()
        stampTool?.toolPosition?.let { onDrawingSurfaceView().checkPixelColor(Color.BLACK, it.x, stampTool.toolPosition.y) }
    }

    @Test
    fun testStampToolNotCapturingOtherLayers() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.STAMP)
        onLayerMenuView()
            .performOpen()
            .performAddLayer()
        onLayerMenuView().performClose()
        onStampToolViewInteraction().performCopy()
        val stampTool = toolReference.tool as StampTool?
        if (stampTool != null) { stampTool.toolPosition[stampTool.toolPosition.x] = stampTool.toolPosition.y * .5f }
        onStampToolViewInteraction().performPaste()
        if (stampTool != null) {
            onDrawingSurfaceView()
                .checkPixelColor(
                    Color.TRANSPARENT,
                    stampTool.toolPosition.x,
                    stampTool.toolPosition.y * .5f
                )
        }
    }

    @Test
    fun testStampOutsideDrawingSurface() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val bitmapWidth = workspace.width
        val bitmapHeight = workspace.height

        perspective.scale = SCALE_25
        onToolBarView().performSelectTool(ToolType.STAMP)
        val stampTool = toolReference.tool as StampTool?
        val toolPosition = PointF(perspective.surfaceCenterX, perspective.surfaceCenterY)

        stampTool?.toolPosition?.set(toolPosition)
        if (stampTool != null) { stampTool.boxWidth = (bitmapWidth * STAMP_RESIZE_FACTOR) }
        if (stampTool != null) { stampTool.boxHeight = (bitmapHeight * STAMP_RESIZE_FACTOR) }
        onStampToolViewInteraction().performPaste()
        if (stampTool != null) { Assert.assertNotNull(stampTool.drawingBitmap) }
    }

    @Test
    fun testBitmapSavedOnOrientationChange() {
        onDrawingSurfaceView().perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.STAMP)
        val emptyBitmap =
            (toolReference.tool as BaseToolWithRectangleShape?)?.drawingBitmap?.let {
                Bitmap.createBitmap(it)
            }
        onStampToolViewInteraction().performCopy()
        val expectedBitmap =
            (toolReference.tool as BaseToolWithRectangleShape?)?.drawingBitmap?.let {
                Bitmap.createBitmap(it)
            }
        if (expectedBitmap != null) {
            Assert.assertFalse(expectedBitmap.sameAs(emptyBitmap))
        }
        mainActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val actualBitmap =
            (toolReference.tool as BaseToolWithRectangleShape?)?.drawingBitmap?.let {
                Bitmap.createBitmap(it)
            }
        if (expectedBitmap != null) {
            Assert.assertTrue(expectedBitmap.sameAs(actualBitmap))
        }
    }

    @Test
    fun testStampToolDoesNotResetPerspectiveScale() {
        val scale = 2.0f
        perspective.scale = scale
        perspective.surfaceTranslationX = 50f
        perspective.surfaceTranslationY = 200f
        mainActivity.refreshDrawingSurface()
        onToolBarView().performSelectTool(ToolType.STAMP)
        Assert.assertEquals(scale, perspective.scale, 0.0001f)
    }

    companion object {
        private const val SCALE_25 = 0.25f
        private const val STAMP_RESIZE_FACTOR = 1.5f
    }
}
