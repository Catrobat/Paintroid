/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
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
package org.catrobat.paintroid.test.junit.tools

import android.graphics.Bitmap
import android.graphics.PointF
import android.util.DisplayMetrics
import android.view.ViewGroup
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.ContextCallback.ScreenOrientation
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape
import org.catrobat.paintroid.tools.implementation.MINIMAL_BOX_SIZE
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.catrobat.paintroid.ui.Perspective
import org.hamcrest.Matchers
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.stubbing.Answer
import com.nhaarman.mockitokotlin2.any
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@RunWith(MockitoJUnitRunner::class)
class BaseToolWithRectangleShapeToolTest {
    private var screenWidth = 0
    private var screenHeight = 0
    private var rectWidth = 0f
    private var rectHeight = 0f
    private var rotation = 0f
    private var symbolDistance = 0f
    private var toolPosition: PointF? = null

    @JvmField
    @Rule
    var launchActivityRule = ActivityTestRule(
            MainActivity::class.java
    )

    @Mock
    private val commandManager: CommandManager? = null

    @Mock
    private val toolOptionsViewController: ToolOptionsViewController? = null

    @Mock
    private val contextCallback: ContextCallback? = null

    @Mock
    private val workspace: Workspace? = null

    @Mock
    private val toolPaint: ToolPaint? = null

    @Mock
    private val metrics: DisplayMetrics? = null
    private var toolToTest: BaseToolWithRectangleShape? = null
    private var idlingResource: CountingIdlingResource? = null
    private val viewMock = Mockito.mock(ViewGroup::class.java)
    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        metrics!!.heightPixels = 1920
        metrics.widthPixels = 1080
        metrics.density = 1f
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
        Mockito.`when`(contextCallback!!.orientation).thenReturn(ScreenOrientation.PORTRAIT)
        Mockito.`when`(contextCallback.displayMetrics).thenReturn(metrics)
        Mockito.`when`(workspace!!.scale).thenReturn(1f)
        Mockito.`when`(workspace.width).thenReturn(screenWidth)
        Mockito.`when`(workspace.perspective).thenReturn(Perspective(screenWidth, screenHeight))
        Mockito.`when`(workspace.height).thenReturn(screenHeight)
        Mockito.`when`(workspace.contains(any<PointF>())).thenAnswer(
                Answer { invocation ->
                    val point = invocation.getArgument<PointF>(0)
                    point.x >= 0f && point.y >= 0f && point.x < screenWidth && point.y < screenHeight
                })
        toolToTest = BaseToolWithRectangleShapeImpl(
            contextCallback,
            toolOptionsViewController,
            toolPaint,
            workspace,
            idlingResource,
            commandManager
        )
        toolPosition = (toolToTest as BaseToolWithRectangleShapeImpl).toolPosition
        rectWidth = (toolToTest as BaseToolWithRectangleShapeImpl).boxWidth
        rectHeight = (toolToTest as BaseToolWithRectangleShapeImpl).boxHeight
        rotation = (toolToTest as BaseToolWithRectangleShapeImpl).boxRotation
        symbolDistance = (toolToTest as BaseToolWithRectangleShapeImpl).rotationSymbolDistance
        Mockito.`when`(toolOptionsViewController!!.toolSpecificOptionsLayout).thenReturn(viewMock)
        Mockito.`when`(toolOptionsViewController.toolSpecificOptionsLayout).thenReturn(viewMock)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testResizeRectangleMinimumSizeBiggerThanMargin() {
        Mockito.`when`(workspace!!.contains(any<PointF>()))
                .thenReturn(true)
        val rectWidth = toolToTest!!.boxWidth
        val rectHeight = toolToTest!!.boxHeight
        val rectPosition = toolToTest!!.toolPosition
        val dragFromX = rectPosition.x - rectWidth / 2
        val dragToX = dragFromX + rectWidth + RESIZE_MOVE_DISTANCE
        val dragFromY = rectPosition.y - rectHeight / 2
        val dragToY = dragFromY + rectHeight + RESIZE_MOVE_DISTANCE
        toolToTest!!.handleDown(PointF(dragFromX, dragFromY))
        toolToTest!!.handleMove(PointF(dragToX, dragToY))
        toolToTest!!.handleUp(PointF(dragToX, dragToY))
        val newWidth = toolToTest!!.boxWidth
        val newHeight = toolToTest!!.boxHeight
        val boxResizeMargin: Float = MINIMAL_BOX_SIZE.toFloat()
        Assert.assertThat(newHeight, Matchers.`is`(Matchers.greaterThanOrEqualTo(boxResizeMargin)))
        Assert.assertThat(newWidth, Matchers.`is`(Matchers.greaterThanOrEqualTo(boxResizeMargin)))
    }

    @Test
    fun testMoveRectangle() {
        val rectWidth = toolToTest!!.boxWidth
        val rectHeight = toolToTest!!.boxHeight
        val rectPosition = toolToTest!!.toolPosition
        val dragFromX = rectPosition.x
        val dragToX = dragFromX + RESIZE_MOVE_DISTANCE
        val dragFromY = rectPosition.y
        val dragToY = dragFromY + RESIZE_MOVE_DISTANCE
        toolToTest!!.handleDown(PointF(dragFromX, dragFromY))
        toolToTest!!.handleMove(PointF(dragToX, dragToY))
        toolToTest!!.handleUp(PointF(dragToX, dragToY))
        val newWidth = toolToTest!!.boxWidth
        val newHeight = toolToTest!!.boxHeight
        val newPosition = toolToTest!!.toolPosition
        Assert.assertEquals("width should be the same", rectWidth, newWidth, Float.MIN_VALUE)
        Assert.assertEquals("height should be the same", rectHeight, newHeight, Float.MIN_VALUE)
        Assert.assertTrue(
                "position should have moved",
                newPosition.x == dragToX && newPosition.y == dragToY
        )
    }

    @Test
    fun testRectangleSizeMaximumWhenZoomed() {
        Mockito.`when`(workspace!!.scale).thenReturn(0.8f, 0.15f, 0.1f)
        toolToTest = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        var width = (toolToTest as BaseToolWithRectangleShapeImpl).boxWidth
        var height = (toolToTest as BaseToolWithRectangleShapeImpl).boxHeight
        Assert.assertEquals(
                "Width and Height should be the same with activating Rectangletool on low zoom out",
                width.toDouble(),
                height.toDouble(),
                Double.MIN_VALUE
        )
        toolToTest = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        width = (toolToTest as BaseToolWithRectangleShapeImpl).boxWidth
        height = (toolToTest as BaseToolWithRectangleShapeImpl).boxHeight
        Assert.assertNotSame(
                "With zooming out a lot, height and width should not be the same anymore and adjust the ratio to the drawinSurface",
                width, height
        )
        toolToTest = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        val newWidth = (toolToTest as BaseToolWithRectangleShapeImpl).boxWidth
        val newHeight = (toolToTest as BaseToolWithRectangleShapeImpl).boxHeight
        Assert.assertEquals(
                "After zooming out a little more (from already beeing zoomed out a lot), width should stay the same",
                newWidth.toDouble(), width.toDouble(), Double.MIN_VALUE
        )
        Assert.assertEquals(
                "After zooming out a little more (from already beeing zoomed out a lot), height should stay the same",
                newHeight.toDouble(), height.toDouble(), Double.MIN_VALUE
        )
    }

    @Test
    fun testRectangleSizeChangeWhenZoomedLevel1ToLevel2() {
        Mockito.`when`(workspace!!.scale).thenReturn(1f, 2f)
        val rectTool1: BaseToolWithRectangleShape = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        val rectTool2: BaseToolWithRectangleShape = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        Assert.assertTrue(
                "rectangle should be smaller with scale 2", rectTool1.boxWidth > rectTool2.boxWidth && rectTool1.boxHeight > rectTool2.boxHeight
        )
    }

    @Test
    fun testRectangleSizeChangeWhenZoomedLevel1ToLevel05() {
        Mockito.`when`(workspace!!.scale).thenReturn(1f, 0.5f)
        val rectTool1: BaseToolWithRectangleShape = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        val rectTool05: BaseToolWithRectangleShape = BaseToolWithRectangleShapeImpl(
            contextCallback, toolOptionsViewController, toolPaint,
            workspace, idlingResource, commandManager
        )
        Assert.assertThat(rectTool1.boxWidth, Matchers.`is`(Matchers.lessThan(rectTool05.boxWidth)))
        Assert.assertThat(
                rectTool1.boxHeight,
                Matchers.`is`(Matchers.lessThan(rectTool05.boxHeight))
        )
    }

    @Test
    fun testRotateRectangleRight() {
        toolToTest!!.rotationEnabled = true
        toolToTest!!.handleDown(toolPosition)
        toolToTest!!.handleUp(toolPosition)
        val topLeftRotationPoint = PointF(
                toolPosition!!.x - rectWidth / 2 - symbolDistance / 2,
                toolPosition!!.y - rectHeight / 2 - symbolDistance / 2
        )

        // try rotate right
        toolToTest!!.handleDown(topLeftRotationPoint)
        toolToTest!!.handleMove(PointF(screenWidth / 2f, topLeftRotationPoint.y))
        toolToTest!!.handleUp(PointF(screenWidth / 2f, topLeftRotationPoint.y))
        val newRotation = toolToTest!!.boxRotation
        Assert.assertThat(newRotation, Matchers.`is`(Matchers.greaterThan(rotation)))
    }

    @Test
    fun testRotateRectangleLeft() {
        toolToTest!!.rotationEnabled = true
        toolToTest!!.handleDown(toolPosition)
        toolToTest!!.handleUp(toolPosition)
        val topLeftRotationPoint = PointF(
                toolPosition!!.x - rectWidth / 2 - symbolDistance / 2,
                toolPosition!!.y - rectHeight / 2 - symbolDistance / 2
        )

        // try rotate left
        toolToTest!!.handleDown(topLeftRotationPoint)
        toolToTest!!.handleMove(PointF(topLeftRotationPoint.x, screenHeight / 2f))
        toolToTest!!.handleUp(PointF(topLeftRotationPoint.x, screenHeight / 2f))
        val newRotation = toolToTest!!.boxRotation
        Assert.assertThat(newRotation, Matchers.`is`(Matchers.lessThan(rotation)))
    }

    @Test
    fun testRotateRectangle() {
        toolToTest!!.rotationEnabled = true
        toolToTest!!.handleDown(toolPosition)
        toolToTest!!.handleUp(toolPosition)
        val topLeftRotationPoint = PointF(
                toolPosition!!.x - rectWidth / 2 - symbolDistance / 2,
                toolPosition!!.y - rectHeight / 2 - symbolDistance / 2
        )
        val topRightRotationPoint = PointF(
                toolPosition!!.x + toolPosition!!.x - topLeftRotationPoint.x,
                topLeftRotationPoint.y
        )
        val bottomRightRotationPoint = PointF(
                topRightRotationPoint.x,
                toolPosition!!.y + rectHeight / 2 + symbolDistance / 2
        )
        val bottomLeftRotationPoint = PointF(
                topLeftRotationPoint.x,
                bottomRightRotationPoint.y
        )
        var currentPosition: PointF? = topLeftRotationPoint
        var newPosition = PointF(topRightRotationPoint.x, topRightRotationPoint.y)
        toolToTest!!.handleDown(currentPosition)
        toolToTest!!.handleMove(newPosition)
        toolToTest!!.handleUp(newPosition)
        var newRotation = toolToTest!!.boxRotation
        Assert.assertEquals(
                "Rotation value should be 90 degree.",
                newRotation,
                90f,
                Float.MIN_VALUE
        )
        currentPosition = newPosition
        newPosition = PointF(bottomRightRotationPoint.x, bottomRightRotationPoint.y)
        toolToTest!!.handleDown(currentPosition)
        toolToTest!!.handleMove(newPosition)
        toolToTest!!.handleUp(newPosition)
        newRotation = toolToTest!!.boxRotation
        Assert.assertEquals(
                "Rotation value should be 180 degree.",
                newRotation,
                180f,
                Float.MIN_VALUE
        )
        currentPosition = newPosition
        newPosition = PointF(bottomLeftRotationPoint.x, bottomLeftRotationPoint.y)
        toolToTest!!.handleDown(currentPosition)
        toolToTest!!.handleMove(newPosition)
        toolToTest!!.handleUp(newPosition)
        newRotation = toolToTest!!.boxRotation
        Assert.assertEquals(
                "Rotation value should be -90 degree.",
                newRotation,
                -90f,
                Float.MIN_VALUE
        )
        currentPosition = newPosition
        newPosition = PointF(topLeftRotationPoint.x, topLeftRotationPoint.y)
        toolToTest!!.handleDown(currentPosition)
        toolToTest!!.handleMove(newPosition)
        toolToTest!!.handleUp(newPosition)
        newRotation = toolToTest!!.boxRotation
        Assert.assertEquals("Rotation value should be 0 degree.", newRotation, 0f, Float.MIN_VALUE)
    }

    @Test
    fun testRotateOnlyNearCorner() {
        toolToTest!!.rotationEnabled = true
        toolToTest!!.handleDown(toolPosition)
        toolToTest!!.handleUp(toolPosition)
        val noRotationPoint = PointF(
                toolPosition!!.x - rectWidth / 2 - symbolDistance,
                toolPosition!!.y - rectHeight / 2 - symbolDistance
        )
        val topLeftRotationPoint = PointF(noRotationPoint.x + 1, noRotationPoint.y + 1)
        val destinationPoint = PointF(noRotationPoint.x + 10, noRotationPoint.y)
        toolToTest!!.handleDown(noRotationPoint)
        toolToTest!!.handleMove(destinationPoint)
        var newRotation = toolToTest!!.boxRotation
        Assert.assertEquals("Rectangle should not rotate.", newRotation, 0f, Float.MIN_VALUE)
        toolToTest!!.handleMove(noRotationPoint)
        toolToTest!!.handleUp(noRotationPoint)
        toolToTest!!.handleDown(topLeftRotationPoint)
        toolToTest!!.handleMove(destinationPoint)
        toolToTest!!.handleUp(destinationPoint)
        newRotation = toolToTest!!.boxRotation
        Assert.assertNotEquals("Rectangle should rotate.", newRotation, 0)
    }

    @Test
    fun testIsClickInsideBoxCalculatedCorrect() {
        val topLeftCorner = PointF(
                toolPosition!!.x - rectWidth / 2 + 20,
                toolPosition!!.y - rectHeight / 2 + 20
        )
        val pointInRotatedRectangle = PointF(
                toolPosition!!.x,
                toolPosition!!.y - rectHeight / 2
        )
        val topLeftRotationPoint = PointF(
                toolPosition!!.x - rectWidth / 2 - symbolDistance / 2,
                toolPosition!!.y - rectHeight / 2 - symbolDistance / 2
        )
        toolToTest!!.rotationEnabled = true
        toolToTest!!.handleDown(toolPosition)
        toolToTest!!.handleUp(toolPosition)
        Assert.assertTrue(toolToTest!!.boxContainsPoint(topLeftCorner))
        Assert.assertFalse(toolToTest!!.boxContainsPoint(pointInRotatedRectangle))

        // rotate right
        toolToTest!!.handleDown(topLeftRotationPoint)
        toolToTest!!.handleMove(PointF(screenWidth / 2f, topLeftRotationPoint.y))
        toolToTest!!.handleUp(PointF(screenWidth / 2f, topLeftRotationPoint.y))
        Assert.assertFalse(toolToTest!!.boxContainsPoint(topLeftCorner))
        Assert.assertTrue(toolToTest!!.boxContainsPoint(pointInRotatedRectangle))
    }

    @Test
    fun testToolPreciseMovementTest() {
        val initialToolPositionX = toolToTest!!.toolPosition.x
        val initialToolPositionY = toolToTest!!.toolPosition.y
        toolToTest!!.handleDown(PointF(initialToolPositionX, initialToolPositionY))
        toolToTest!!.handleMove(PointF(initialToolPositionX + 9, initialToolPositionY + 9))
        toolToTest!!.handleUp(PointF(initialToolPositionX + 9, initialToolPositionY + 9))
        Assert.assertEquals(toolToTest!!.toolPosition.x, initialToolPositionX + 9, 0f)
        Assert.assertEquals(toolToTest!!.toolPosition.y, initialToolPositionY + 9, 0f)
    }

    private inner class BaseToolWithRectangleShapeImpl(
        contextCallback: ContextCallback?,
        toolOptionsViewController: ToolOptionsViewController?,
        toolPaint: ToolPaint?, layerModelWrapper: Workspace?,
        idlingResource: CountingIdlingResource?,
        commandManager: CommandManager?
    ) : BaseToolWithRectangleShape(
            contextCallback!!,
            toolOptionsViewController!!,
            toolPaint!!, layerModelWrapper!!, idlingResource!!, commandManager!!
    ) {
        override fun onClickOnButton() {
            drawingBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
        }

        override val toolType: ToolType
            get() = ToolType.BRUSH

        override var drawTime: Long
            get() = 0
            set(drawTime) {}

        override fun toolPositionCoordinates(coordinate: PointF) = coordinate
    }

    companion object {
        private const val RESIZE_MOVE_DISTANCE = 50
    }
}
