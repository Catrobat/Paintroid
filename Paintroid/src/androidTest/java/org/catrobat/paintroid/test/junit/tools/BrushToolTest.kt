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

package org.catrobat.paintroid.test.junit.tools

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.PathCommand
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.test.junit.stubs.PathStub
import org.catrobat.paintroid.test.utils.PaintroidAsserts
import org.catrobat.paintroid.tools.ContextCallback
import org.catrobat.paintroid.tools.Tool.StateChange
import org.catrobat.paintroid.tools.ToolPaint
import org.catrobat.paintroid.tools.ToolType
import org.catrobat.paintroid.tools.Workspace
import org.catrobat.paintroid.tools.implementation.BrushTool
import org.catrobat.paintroid.tools.implementation.STROKE_25
import org.catrobat.paintroid.tools.options.BrushToolOptionsView
import org.catrobat.paintroid.tools.options.BrushToolOptionsView.OnBrushChangedListener
import org.catrobat.paintroid.tools.options.ToolOptionsViewController
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class BrushToolTest {
    @Mock
    private lateinit var commandManager: CommandManager

    @Mock
    private lateinit var toolPaint: ToolPaint

    @Mock
    private lateinit var brushToolOptionsView: BrushToolOptionsView

    @Mock
    private lateinit var toolOptionsViewController: ToolOptionsViewController

    @Mock
    private lateinit var workspace: Workspace

    @Mock
    private lateinit var contextCallback: ContextCallback
    private lateinit var paint: Paint
    private lateinit var toolToTest: BrushTool
    private lateinit var idlingResource: CountingIdlingResource

    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)

    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        toolToTest = BrushTool(
            brushToolOptionsView,
            contextCallback, toolOptionsViewController, toolPaint, workspace,
            idlingResource, commandManager, 0
        )
        paint = Paint()
        paint.color = Color.BLACK
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = STROKE_25
    }

    @After
    fun tearDown() { IdlingRegistry.getInstance().unregister(idlingResource) }

    @Test
    fun testShouldReturnCorrectToolType() {
        val toolType = toolToTest.toolType
        Assert.assertEquals(ToolType.BRUSH, toolType)
    }

    @Test
    fun testShouldMovePathOnDownEvent() {
        val event = PointF(0f, 0f)
        val pathStub = PathStub()
        toolToTest.pathToDraw = pathStub
        val returnValue = toolToTest.handleDown(event)
        Assert.assertTrue(returnValue)
        val stub = pathStub.stub
        Mockito.verify(stub).moveTo(event.x, event.y)
    }

    @Test
    fun testShouldNotAddCommandOnDownEvent() {
        val event = PointF(0f, 0f)
        val returnValue = toolToTest.handleDown(event)
        Assert.assertTrue(returnValue)
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
    }

    @Test
    fun testShouldNotStartPathIfNoCoordinateOnDownEvent() {
        val pathStub = PathStub()
        toolToTest.pathToDraw = pathStub
        val returnValue = toolToTest.handleDown(null)
        Assert.assertFalse(returnValue)
        val stub = pathStub.stub
        Mockito.verify(stub, Mockito.never()).reset()
        Mockito.verify(stub, Mockito.never()).moveTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testShouldMovePathOnMoveEvent() {
        val event1 = PointF(0f, 0f)
        val event2 = PointF(5f, 6f)
        val pathStub = PathStub()

        toolToTest.pathToDraw = pathStub
        toolToTest.handleDown(event1)

        val returnValue = toolToTest.handleMove(event2)
        Assert.assertTrue(returnValue)
        val stub = pathStub.stub
        Mockito.verify(stub).moveTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
        Mockito.verify(stub).quadTo(event1.x, event1.y, event2.x, event2.y)
    }

    @Test
    fun testShouldNotAddCommandOnMoveEvent() {
        val event = PointF(0f, 0f)
        toolToTest.handleDown(event)
        val returnValue = toolToTest.handleMove(event)
        Assert.assertTrue(returnValue)
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
    }

    @Test
    fun testShouldNotMovePathIfNoCoordinateOnMoveEvent() {
        val event = PointF(0f, 0f)
        val pathStub = PathStub()

        toolToTest.pathToDraw = pathStub
        toolToTest.handleDown(event)

        val returnValue = toolToTest.handleMove(null)
        Assert.assertFalse(returnValue)
        Mockito.verify(pathStub.stub, Mockito.never()).quadTo(
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat()
        )
    }

    @Test
    fun testShouldMovePathOnUpEvent() {
        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java))).thenReturn(true)
        Mockito.`when`(toolPaint.paint).thenReturn(paint)

        val event1 = PointF(0f, 0f)
        val event2 = PointF(MOVE_TOLERANCE, MOVE_TOLERANCE)
        val event3 = PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE)
        val pathStub = PathStub()

        toolToTest.pathToDraw = pathStub
        toolToTest.handleDown(event1)
        toolToTest.handleMove(event2)

        val returnValue = toolToTest.handleUp(event3)
        Assert.assertTrue(returnValue)
        val stub = pathStub.stub

        Mockito.verify(stub).moveTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
        Mockito.verify(stub).quadTo(
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat(),
            ArgumentMatchers.anyFloat()
        )
        Mockito.verify(stub).lineTo(event3.x, event3.y)
    }

    @Test
    fun testShouldNotMovePathIfNoCoordinateOnUpEvent() {
        val event = PointF(0f, 0f)
        val pathStub = PathStub()

        toolToTest.pathToDraw = pathStub
        toolToTest.handleDown(event)
        toolToTest.handleMove(event)

        val returnValue = toolToTest.handleUp(null)
        Assert.assertFalse(returnValue)
        Mockito.verify(pathStub.stub, Mockito.never()).lineTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testShouldAddCommandOnUpEvent() {
        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java))).thenReturn(true)
        Mockito.`when`(toolPaint.paint).thenReturn(paint)

        val event = PointF(0f, 0f)
        val event1 = PointF(MOVE_TOLERANCE + 0.1f, 0f)
        val event2 = PointF(MOVE_TOLERANCE + 2, MOVE_TOLERANCE + 2)
        val pathStub = PathStub()

        toolToTest.pathToDraw = pathStub
        toolToTest.handleDown(event)
        toolToTest.handleMove(event1)
        val returnValue = toolToTest.handleUp(event2)
        Assert.assertTrue(returnValue)
        val argument = ArgumentCaptor.forClass(PathCommand::class.java)
        Mockito.verify(commandManager).addCommand(argument.capture())
        val command = argument.value
        PaintroidAsserts.assertPathEquals(pathStub, command.path)
        PaintroidAsserts.assertPaintEquals(paint, command.paint)
    }

    @Test
    fun testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
        val event = PointF(0f, 0f)
        toolToTest.handleDown(event)
        toolToTest.handleMove(event)
        val returnValue = toolToTest.handleUp(null)
        Assert.assertFalse(returnValue)
        Mockito.verify(commandManager, Mockito.never()).addCommand(ArgumentMatchers.any(Command::class.java))
    }

    @Test
    fun testShouldAddCommandOnTapEvent() {
        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java))).thenReturn(true)
        Mockito.`when`(toolPaint.paint).thenReturn(paint)

        val tap = PointF(5f, 5f)
        val returnValue1 = toolToTest.handleDown(tap)
        val returnValue2 = toolToTest.handleUp(tap)

        Assert.assertTrue(returnValue1)
        Assert.assertTrue(returnValue2)
        val argument = ArgumentCaptor.forClass(PointCommand::class.java)
        Mockito.verify(commandManager).addCommand(argument.capture())
        val command = argument.value
        Assert.assertEquals(tap, command.point)
        PaintroidAsserts.assertPaintEquals(paint, command.paint)
    }

    @Test
    fun testShouldAddCommandOnTapWithinToleranceEvent() {
        val tap1 = PointF(0f, 0f)
        val tap2 = PointF(MOVE_TOLERANCE - 0.1f, 0f)
        val tap3 = PointF(MOVE_TOLERANCE - 0.1f, MOVE_TOLERANCE - 0.1f)

        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java))).thenReturn(true)
        Mockito.`when`(toolPaint.paint).thenReturn(paint)

        val returnValue1 = toolToTest.handleDown(tap1)
        val returnValue2 = toolToTest.handleMove(tap2)
        val returnValue3 = toolToTest.handleUp(tap3)

        Assert.assertTrue(returnValue1)
        Assert.assertTrue(returnValue2)
        Assert.assertTrue(returnValue3)

        val argument = ArgumentCaptor.forClass(PointCommand::class.java)
        Mockito.verify(commandManager).addCommand(argument.capture())
        val command = argument.value
        Assert.assertEquals(tap1, command.point)
        PaintroidAsserts.assertPaintEquals(paint, command.paint)
    }

    @Test
    fun testShouldAddPathCommandOnMultipleMovesWithinToleranceEvent() {
        Mockito.`when`(workspace.contains(ArgumentMatchers.any(PointF::class.java))).thenReturn(true)
        Mockito.`when`(toolPaint.paint).thenReturn(paint)

        val tap1 = PointF(7f, 7f)
        val tap2 = PointF(7f, MOVE_TOLERANCE - 0.1f)
        val tap3 = PointF(7f, 7f)
        val tap4 = PointF(7f, -MOVE_TOLERANCE + 0.1f)
        val tap5 = PointF(7f, 7f)

        toolToTest.handleDown(tap1)
        toolToTest.handleMove(tap2)
        toolToTest.handleMove(tap3)
        toolToTest.handleMove(tap4)
        toolToTest.handleUp(tap5)
        Mockito.verify(commandManager).addCommand(ArgumentMatchers.isA(PathCommand::class.java))
    }

    @Test
    fun testShouldRewindPathOnAppliedToBitmap() {
        val pathStub = PathStub()
        toolToTest.pathToDraw = pathStub
        toolToTest.resetInternalState(StateChange.RESET_INTERNAL_STATE)
        Mockito.verify(pathStub.stub).rewind()
    }

    @Test
    fun testShouldChangePaintFromBrushPicker() {
        val argumentCaptor = ArgumentCaptor.forClass(OnBrushChangedListener::class.java)
        Mockito.verify(brushToolOptionsView).setBrushChangedListener(argumentCaptor.capture())
        val onBrushChangedListener = argumentCaptor.value
        onBrushChangedListener.setCap(Paint.Cap.ROUND)
        onBrushChangedListener.setStrokeWidth(15)
        Mockito.verify(toolPaint).strokeCap = Paint.Cap.ROUND
        Mockito.verify(toolPaint).strokeWidth = 15f
    }

    companion object {
        private const val MOVE_TOLERANCE = 0.1f
    }
}
