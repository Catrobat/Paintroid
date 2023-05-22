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
import org.catrobat.paintroid.test.espresso.util.wrappers.ClipboardToolViewInteraction.Companion.onClipboardToolViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView
import org.catrobat.paintroid.test.espresso.util.wrappers.LayerMenuViewInteraction
import org.catrobat.paintroid.test.espresso.util.wrappers.ShapeToolOptionsViewInteraction.onShapeToolOptionsView
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView
import org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.tools.ToolReference
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.drawable.DrawableShape
import org.catrobat.paintroid.tools.drawable.DrawableStyle
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.ClipboardTool
import org.catrobat.paintroid.ui.Perspective
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ClipboardToolIntegrationTest {
    @get:Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var workspace: Workspace? = null
    private var perspective: Perspective? = null
    private var toolReference: ToolReference? = null
    private var mainActivity: MainActivity? = null
    @Before
    fun setUp() {
        onToolBarView().performSelectTool(ToolType.BRUSH)
        mainActivity = launchActivityRule.activity
        workspace = mainActivity?.workspace
        perspective = mainActivity?.perspective
        toolReference = mainActivity?.toolReference
    }

    @Test
    fun testBorders() {
        onToolBarView().performSelectTool(ToolType.SHAPE)
        onShapeToolOptionsView()
            .performSelectShape(DrawableShape.RECTANGLE)
            .performSelectShapeDrawType(DrawableStyle.STROKE)
        onTopBarView().performClickCheckmark()
        onToolBarView().performSelectTool(ToolType.CLIPBOARD)
        val stampTool = toolReference?.tool as ClipboardTool?
        if (stampTool != null) {
            stampTool.boxHeight = stampTool.boxHeight.minus(25f)
        }
        if (stampTool != null) {
            stampTool.boxWidth = stampTool.boxWidth.minus(25f)
        }
        onClipboardToolViewInteraction()
            .performCopy()
        val topLeft = stampTool?.drawingBitmap?.getPixel(0, 0)
        val topRight = stampTool?.drawingBitmap?.getPixel(stampTool.drawingBitmap?.width?.minus(1) ?: 0, 0)
        val bottomLeft = stampTool?.drawingBitmap?.getPixel(
            0,
            stampTool.drawingBitmap?.height?.minus(1) ?: 0
        )
        val bottomRight = stampTool?.drawingBitmap?.getPixel(
            stampTool.drawingBitmap?.width?.minus(1) ?: 0,
            stampTool.drawingBitmap?.height?.minus(1) ?: 0
        )
        Assert.assertEquals(topLeft?.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(topRight?.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(bottomLeft?.toLong(), Color.BLACK.toLong())
        Assert.assertEquals(bottomRight?.toLong(), Color.BLACK.toLong())
    }

    @Test
    fun testCopyPixel() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView().performSelectTool(ToolType.CLIPBOARD)
        onClipboardToolViewInteraction().performCopy()
        val stampTool = toolReference?.tool as ClipboardTool?
        stampTool?.toolPosition?.set(stampTool.toolPosition.x, stampTool.toolPosition.y * .5f)
        onClipboardToolViewInteraction().performPaste()
        stampTool?.toolPosition?.let {
            onDrawingSurfaceView()
                .checkPixelColor(Color.BLACK, stampTool.toolPosition.x, it.y)
        }
    }

    @Test
    fun testCutAndPastePixel() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        onClipboardToolViewInteraction()
            .performCut()
        val stampTool = toolReference?.tool as ClipboardTool?
        stampTool?.toolPosition?.let {
            onDrawingSurfaceView()
                .checkPixelColor(
                    Color.TRANSPARENT,
                    stampTool.toolPosition.x,
                    it.y
                )
        }
        onClipboardToolViewInteraction().performPaste()
        stampTool?.toolPosition?.x?.let {
            onDrawingSurfaceView()
                .checkPixelColor(Color.BLACK, it, stampTool.toolPosition.y)
        }
    }

    @Test
    fun testStampToolNotCapturingOtherLayers() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        LayerMenuViewInteraction.onLayerMenuView()
            .performOpen()
            .performAddLayer()
        LayerMenuViewInteraction.onLayerMenuView()
            .performClose()
        onClipboardToolViewInteraction()
            .performCopy()
        val stampTool = toolReference?.tool as ClipboardTool?
        stampTool!!.toolPosition[stampTool.toolPosition.x] = stampTool.toolPosition.y * .5f
        onClipboardToolViewInteraction()
            .performPaste()
        onDrawingSurfaceView()
            .checkPixelColor(
                Color.TRANSPARENT,
                stampTool.toolPosition.x,
                stampTool.toolPosition.y * .5f
            )
    }

    @Test
    fun testStampOutsideDrawingSurface() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        val bitmapWidth = workspace?.width
        val bitmapHeight = workspace?.height
        perspective?.scale = SCALE_25
        onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        val stampTool = toolReference?.tool as ClipboardTool?
        val toolPosition = perspective?.surfaceCenterX?.let {
            perspective?.surfaceCenterY?.let {
                    it1 ->
                PointF(it, it1)
            }
        }
        if (toolPosition != null) {
            stampTool?.toolPosition?.set(toolPosition)
        }
        if (bitmapWidth != null) {
            stampTool?.boxWidth = bitmapWidth * STAMP_RESIZE_FACTOR
        }
        if (bitmapHeight != null) {
            stampTool?.boxHeight = bitmapHeight * STAMP_RESIZE_FACTOR
        }
        onClipboardToolViewInteraction().performPaste()
        Assert.assertNotNull(stampTool?.drawingBitmap)
    }

    @Test
    fun testBitmapSavedOnOrientationChange() {
        onDrawingSurfaceView()
            .perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE))
        onToolBarView()
            .performSelectTool(ToolType.CLIPBOARD)
        val emptyBitmap =
            (toolReference?.tool as BaseToolWithRectangleShape?)?.drawingBitmap?.let {
                Bitmap.createBitmap(it)
            }
        onClipboardToolViewInteraction().performCopy()
        val expectedBitmap =
            (toolReference?.tool as BaseToolWithRectangleShape?)?.drawingBitmap?.let {
                Bitmap.createBitmap(it)
            }
        if (expectedBitmap != null) {
            Assert.assertFalse(expectedBitmap.sameAs(emptyBitmap))
        }
        mainActivity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val actualBitmap =
            (toolReference?.tool as BaseToolWithRectangleShape?)?.drawingBitmap?.let {
                Bitmap.createBitmap(it)
            }
        if (expectedBitmap != null) {
            Assert.assertTrue(expectedBitmap.sameAs(actualBitmap))
        }
    }

    @Test
    fun testStampToolDoesNotResetPerspectiveScale() {
        val scale = 2.0f
        perspective?.scale = scale
        perspective?.surfaceTranslationX = 50F
        perspective?.surfaceTranslationY = 200F
        mainActivity?.refreshDrawingSurface()
        onToolBarView().performSelectTool(ToolType.CLIPBOARD)
        perspective?.scale?.let { Assert.assertEquals(scale, it, 0.0001f) }
    }

    companion object {
        private const val SCALE_25 = 0.25f
        private const val STAMP_RESIZE_FACTOR = 1.5f
    }
}
