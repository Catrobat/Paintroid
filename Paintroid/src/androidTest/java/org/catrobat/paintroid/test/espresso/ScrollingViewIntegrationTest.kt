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

import android.R
import org.junit.runner.RunWith
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule
import org.catrobat.paintroid.ui.Perspective
import org.junit.Before
import org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction
import android.graphics.PointF
import org.catrobat.paintroid.tools.ToolType
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.catrobat.paintroid.test.espresso.util.UiInteractions
import org.junit.Assert
import org.junit.Rule
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class ScrollingViewIntegrationTest {
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Rule
    var screenshotOnFailRule = ScreenshotOnFailRule()
    private var drawerEdgeSize = 0
    private lateinit var perspective: Perspective
    private lateinit var mainActivity: MainActivity

    @Before
    fun setUp() {
        val activity = launchActivityRule.activity
        val displayDensity = activity.resources.displayMetrics.density
        perspective = activity.perspective
        drawerEdgeSize = (20 * displayDensity + 0.5f).toInt()
        mainActivity = launchActivityRule.activity
    }

    @Test
    fun testScrollingViewDrawTool() {
        ToolBarViewInteraction.onToolBarView().performCloseToolOptionsView()
        val perspectiveScale = 5
        perspective.scale = perspectiveScale.toFloat()
        val surfaceWidth = perspective.surfaceWidth.toFloat()
        val surfaceHeight = perspective.surfaceHeight.toFloat()
        val xRight = surfaceWidth - 1 - drawerEdgeSize
        val xLeft = (1 + drawerEdgeSize).toFloat()
        val xMiddle = surfaceWidth / 2
        var statusBarHeight = 0
        val resourceId = mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }
        val actionBarHeight: Int
        val styledAttributes = mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val yMiddle = surfaceHeight / 2 + actionBarHeight + statusBarHeight
        val yTop = (actionBarHeight + statusBarHeight).toFloat()
        val yBottom = surfaceHeight + yTop - 1
        val middle = PointF(xMiddle, yMiddle)
        val rightMiddle = PointF(xRight, yMiddle)
        val leftMiddle = PointF(xLeft, yMiddle)
        val topMiddle = PointF(xMiddle, yTop)
        val bottomMiddle = PointF(xMiddle, yBottom)
        val topLeft = PointF(xLeft, yTop)
        val bottomRight = PointF(xRight, yBottom)
        val bottomLeft = PointF(xLeft, yBottom)
        val topRight = PointF(xRight, yTop)

        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.BRUSH)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(middle)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(rightMiddle)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(leftMiddle)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(topMiddle)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(bottomMiddle)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(bottomRight)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(topLeft)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(bottomLeft)
        longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(topRight)
    }

    @Test
    fun testScrollingViewCursorTool() {
        val perspectiveScale = 5
        perspective.scale = perspectiveScale.toFloat()
        ToolBarViewInteraction.onToolBarView().performCloseToolOptionsView()
        val surfaceWidth = perspective.surfaceWidth.toFloat()
        val surfaceHeight = perspective.surfaceHeight.toFloat()
        var statusBarHeight = 0
        val resourceId =
            mainActivity.resources?.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId != null) {
            if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }
        }
        val actionBarHeight: Int
        val styledAttributes =
            mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val xRight = surfaceWidth - 100
        val xLeft = 1f
        val xMiddle = surfaceWidth / 2
        val yMiddle = surfaceHeight / 2 + actionBarHeight + statusBarHeight
        val yTop = (actionBarHeight + statusBarHeight).toFloat()
        val yBottom = surfaceHeight + yTop - 1
        val middle = PointF(xMiddle, yMiddle)
        val rightMiddle = PointF(xRight, yMiddle)
        val leftMiddle = PointF(xLeft, yMiddle)
        val topMiddle = PointF(xMiddle, yTop)
        val bottomMiddle = PointF(xMiddle, yBottom)
        val topLeft = PointF(xLeft, yTop)
        val bottomRight = PointF(xRight, yBottom)
        val bottomLeft = PointF(xLeft, yBottom)
        val topRight = PointF(xRight, yTop)

        ToolBarViewInteraction.onToolBarView().performSelectTool(ToolType.CURSOR)
        ToolBarViewInteraction.onToolBarView().performCloseToolOptionsView()
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(rightMiddle)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(leftMiddle)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(topMiddle)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomMiddle)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomRight)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(topLeft)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomLeft)
        longpressOnPointAndCheckIfCanvasPointHasNotChanged(topRight)
        dragAndCheckIfCanvasHasMovedInXOrY(bottomMiddle, topMiddle)
        dragAndCheckIfCanvasHasMovedInXOrY(topMiddle, middle)
        dragAndCheckIfCanvasHasMovedInXOrY(topMiddle, bottomMiddle)
        dragAndCheckIfCanvasHasMovedInXOrY(bottomMiddle, middle)
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.touchCenterMiddle())
    }

    fun longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(clickPoint: PointF) {
        var statusBarHeight = 0
        val resourceId =
            mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId)
        }
        val actionBarHeight: Int
        val styledAttributes = mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val startPointSurface = PointF(
            perspective.surfaceCenterX,
            perspective.surfaceCenterY + actionBarHeight + statusBarHeight
        )
        val startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Espresso.onView(ViewMatchers.isRoot())
            .perform(UiInteractions.touchLongAt(clickPoint.x, clickPoint.y))
        val endPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        val delta = 0.5f

        Assert.assertNotEquals("view should scroll in x", startPointCanvas.x, endPointCanvas.x, delta)
        Assert.assertNotEquals("view should scroll in y", startPointCanvas.y, endPointCanvas.y, delta)
    }

    fun longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(clickPoint: PointF) {
        var statusBarHeight = 0
        val resourceId = mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }
        val actionBarHeight: Int
        val styledAttributes =
            mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val startPointSurface = PointF(
            perspective.surfaceCenterX,
            perspective.surfaceCenterY + actionBarHeight + statusBarHeight
        )
        val startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Espresso.onView(ViewMatchers.isRoot())
            .perform(UiInteractions.touchLongAt(clickPoint.x, clickPoint.y))
        val endPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Assert.assertTrue(
            "scrolling did not work",
            startPointCanvas.x != endPointCanvas.x || startPointCanvas.y != endPointCanvas.y
        )
    }

    fun longpressOnPointAndCheckIfCanvasPointHasNotChanged(clickPoint: PointF) {
        var statusBarHeight = 0
        val resourceId =
            mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }
        val actionBarHeight: Int
        val styledAttributes =
            mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val startPointSurface = PointF(
            perspective.surfaceCenterX,
            perspective.surfaceCenterY + actionBarHeight + statusBarHeight
        )
        val startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Espresso.onView(ViewMatchers.isRoot())
            .perform(UiInteractions.touchLongAt(clickPoint.x, clickPoint.y))
        val endPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        val delta = 0.5f

        Assert.assertEquals("view should not scroll in x", startPointCanvas.x, endPointCanvas.x, delta)
        Assert.assertEquals("view should not scroll in y", startPointCanvas.y, endPointCanvas.y, delta)
    }

    fun dragAndCheckIfCanvasHasMovedInXAndY(fromPoint: PointF, toPoint: PointF?) {
        var statusBarHeight = 0
        val resourceId =
            mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }
        val actionBarHeight: Int
        val styledAttributes =
            mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val startPointSurface = PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight)
        val startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint))
        val endPointSurface = PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight)
        val endPointCanvas = perspective.getCanvasPointFromSurfacePoint(endPointSurface)

        Assert.assertNotEquals("scrolling did not work in x", startPointCanvas.x, endPointCanvas.x)
        Assert.assertNotEquals("scrolling did not work in y", startPointCanvas.y, endPointCanvas.y)
    }

    fun dragAndCheckIfCanvasHasMovedInXOrY(fromPoint: PointF, toPoint: PointF?) {
        var statusBarHeight = 0
        val resourceId =
            mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")

        if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }

        val actionBarHeight: Int
        val styledAttributes =
            mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val startPointSurface = PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight)
        val startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint))
        val endPointSurface = PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight)
        val endPointCanvas = perspective.getCanvasPointFromSurfacePoint(endPointSurface)
        val message = ("startX(" + startPointCanvas.x + ") != endX(" + endPointCanvas.x
            + ") || startY(" + startPointCanvas.y + ") != endY(" + endPointCanvas.y + ")")
        Assert.assertTrue(
            message,
            startPointCanvas.x != endPointCanvas.x || startPointCanvas.y != endPointCanvas.y
        )
    }

    fun dragAndCheckIfCanvasHasNotMoved(fromPoint: PointF, toPoint: PointF?) {
        var statusBarHeight = 0
        val resourceId =
            mainActivity.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) { statusBarHeight = mainActivity.resources.getDimensionPixelSize(resourceId) }
        val actionBarHeight: Int
        val styledAttributes = mainActivity.theme.obtainStyledAttributes(intArrayOf(R.attr.actionBarSize))
        actionBarHeight = styledAttributes.getDimension(0, 0f).toInt()
        val startPointSurface = PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight)
        val startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface)
        Espresso.onView(ViewMatchers.isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint))
        val endPointSurface = PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight)
        val endPointCanvas = perspective.getCanvasPointFromSurfacePoint(endPointSurface)
        val delta = 0.5f

        Assert.assertEquals(
            "view should not scroll but did it in x direction",
            startPointCanvas.x,
            endPointCanvas.x,
            delta
        )
        Assert.assertEquals(
            "view should not scroll but did it in y direction",
            startPointCanvas.y,
            endPointCanvas.y,
            delta
        )
    }
}
