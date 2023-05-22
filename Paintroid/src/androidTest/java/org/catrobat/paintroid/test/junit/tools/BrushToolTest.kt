/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 20f10f-20f15 The Catrobat Team
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

import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.view.View
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.rule.ActivityTestRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertTrue
import org.catrobat.paintroid.MainActivity
import org.catrobat.paintroid.command.Command
import org.catrobat.paintroid.command.CommandManager
import org.catrobat.paintroid.command.implementation.PathCommand
import org.catrobat.paintroid.command.implementation.PointCommand
import org.catrobat.paintroid.test.junit.stubs.PathStub
import org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPaintEquals
import org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPathEquals
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
import org.junit.Before
import org.junit.Rule
import org.junit.Assert
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
    private val commandManager: CommandManager? = null

    @Mock
    private val toolPaint: ToolPaint? = null

    @Mock
    private val brushToolOptionsView: BrushToolOptionsView? = null

    @Mock
    private val toolOptionsViewController: ToolOptionsViewController? = null

    @Mock
    private val workspace: Workspace? = null

    @Mock
    private val contextCallback: ContextCallback? = null
    private var paint: Paint? = null
    private var toolToTest: BrushTool? = null
    private var idlingResource: CountingIdlingResource? = null

    @JvmField
    @Rule
    var launchActivityRule = ActivityTestRule(MainActivity::class.java)
    @Before
    fun setUp() {
        idlingResource = launchActivityRule.activity.idlingResource
        IdlingRegistry.getInstance().register(idlingResource)
        toolToTest = BrushTool(brushToolOptionsView!!, contextCallback!!, toolOptionsViewController!!, toolPaint!!, workspace!!, idlingResource!!, commandManager!!, 0)
        paint = Paint()
        paint!!.color = Color.BLACK
        paint!!.strokeCap = Paint.Cap.ROUND
        paint!!.strokeWidth = STROKE_25
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(idlingResource)
    }

    @Test
    fun testShouldReturnCorrectToolType() {
        val toolType = toolToTest!!.toolType
        Assert.assertEquals(ToolType.BRUSH, toolType)
    }

    @Test
    fun testShouldMovePathOnDownEvent() {
        val event = PointF(0f, 0f)
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub
        val returnValue = toolToTest!!.handleDown(event)
        Assert.assertTrue(returnValue)
        val stub = pathStub.getStub()
        Mockito.verify(stub).moveTo(event.x, event.y)
    }

    @Test
    fun testShouldNotAddCommandOnDownEvent() {
        val event = PointF(0f, 0f)
        val returnValue = toolToTest!!.handleDown(event)
        Assert.assertTrue(returnValue)
        Mockito.verify(commandManager, Mockito.never())!!.addCommand(ArgumentMatchers.any(Command::class.java))
    }

    @Test
    fun testShouldNotStartPathIfNoCoordinateOnDownEvent() {
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub
        val returnValue = toolToTest!!.handleDown(null)
        Assert.assertFalse(returnValue)
        val stub = pathStub.getStub()
        Mockito.verify(stub, Mockito.never()).reset()
        Mockito.verify(stub, Mockito.never()).moveTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testShouldMovePathOnMoveEvent() {
        val event1 = PointF(0f, 0f)
        val event2 = PointF(5f, 6f)
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub
        toolToTest!!.handleDown(event1)
        val returnValue = toolToTest!!.handleMove(event2)
        Assert.assertTrue(returnValue)
        val stub = pathStub.getStub()
        Mockito.verify(stub).moveTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
        Mockito.verify(stub).quadTo(event1.x, event1.y, event2.x, event2.y)
    }

    @Test
    fun testShouldNotAddCommandOnMoveEvent() {
        val event = PointF(0f, 0f)
        toolToTest!!.handleDown(event)
        val returnValue = toolToTest!!.handleMove(event)
        Assert.assertTrue(returnValue)
        Mockito.verify(commandManager, Mockito.never())!!.addCommand(ArgumentMatchers.any(Command::class.java))
    }

    @Test
    fun testShouldNotMovePathIfNoCoordinateOnMoveEvent() {
        val event = PointF(0f, 0f)
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub
        toolToTest!!.handleDown(event)
        val returnValue = toolToTest!!.handleMove(null)
        Assert.assertFalse(returnValue)
        Mockito.verify(pathStub.getStub(), Mockito.never()).quadTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testShouldMovePathOnUpEvent() {
        val viewMock = Mockito.mock(View::class.java)
        Mockito.`when`(viewMock.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(brushToolOptionsView!!.getTopToolOptions()).thenReturn(viewMock)
        Mockito.`when`(brushToolOptionsView.getBottomToolOptions()).thenReturn(viewMock)
        whenever(workspace!!.contains(any<PointF>())).thenReturn(true)
        whenever(toolPaint!!.paint).thenReturn(paint)
        val event1 = PointF(0f, 0f)
        val event2 = PointF(MOVE_TOLERANCE, MOVE_TOLERANCE)
        val event3 = PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE)
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub

        toolToTest!!.handleDown(event1)
        toolToTest!!.handleMove(event2)
        val returnValue: Boolean = toolToTest!!.handleUp(event3)

        assertTrue(returnValue)
        val stub = pathStub.stub
        verify(stub).moveTo(any(), any())
        verify(stub).quadTo(any(), any(), any(), any())
        verify(stub).lineTo(event3.x, event3.y)
    }

    @Test
    fun testShouldNotMovePathIfNoCoordinateOnUpEvent() {
        val event = PointF(0f, 0f)
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub
        toolToTest!!.handleDown(event)
        toolToTest!!.handleMove(event)
        val returnValue = toolToTest!!.handleUp(null)
        Assert.assertFalse(returnValue)
        Mockito.verify(pathStub.getStub(), Mockito.never()).lineTo(ArgumentMatchers.anyFloat(), ArgumentMatchers.anyFloat())
    }

    @Test
    fun testShouldAddCommandOnUpEvent() {
        val viewMock = Mockito.mock(View::class.java)
        Mockito.`when`(viewMock.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(brushToolOptionsView!!.getTopToolOptions()).thenReturn(viewMock)
        Mockito.`when`(brushToolOptionsView.getBottomToolOptions()).thenReturn(viewMock)

        whenever(workspace!!.contains(any())).thenReturn(true)
        whenever(toolPaint!!.paint).thenReturn(paint)
        val event = PointF(0f, 0f)
        val event1 = PointF(MOVE_TOLERANCE + 0.1f, 0f)
        val event2 = PointF(MOVE_TOLERANCE + 2f, MOVE_TOLERANCE + 2f)
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub

        toolToTest!!.handleDown(event)
        toolToTest!!.handleMove(event1)
        val returnValue = toolToTest!!.handleUp(event2)

        assertTrue(returnValue)
        val argument = argumentCaptor<Command>()
        verify(commandManager)!!.addCommand(argument.capture())
        val command = argument.firstValue
        if (command is PathCommand) {
            assertPathEquals(pathStub, command.path)
            assertPaintEquals(paint!!, command.paint)
        }
    }

    @Test
    fun testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
        val event = PointF(0f, 0f)
        toolToTest!!.handleDown(event)
        toolToTest!!.handleMove(event)
        val returnValue = toolToTest!!.handleUp(null)
        Assert.assertFalse(returnValue)
        Mockito.verify(commandManager, Mockito.never())!!.addCommand(ArgumentMatchers.any(Command::class.java))
    }

    @Test
    fun testShouldAddCommandOnTapEvent() {
        val viewMock = Mockito.mock(View::class.java)
        Mockito.`when`(viewMock.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(brushToolOptionsView!!.getTopToolOptions()).thenReturn(viewMock)
        Mockito.`when`(brushToolOptionsView.getBottomToolOptions()).thenReturn(viewMock)

        Mockito.`when`(workspace!!.contains(any<PointF>())).thenReturn(true)
        Mockito.`when`(toolPaint!!.paint).thenReturn(paint)
        val tap = PointF(5f, 5f)
        val returnValue1 = toolToTest!!.handleDown(tap)
        val returnValue2 = toolToTest!!.handleUp(tap)
        Assert.assertTrue(returnValue1)
        Assert.assertTrue(returnValue2)
        val argument = ArgumentCaptor.forClass(PointCommand::class.java)
        Mockito.verify(commandManager)!!.addCommand(argument.capture())
        val command = argument.value
        Assert.assertEquals(tap, command.point)
        assertPaintEquals(paint!!, command.paint)
    }

    @Test
    fun testShouldAddCommandOnTapWithinToleranceEvent() {
        val viewMock = Mockito.mock(View::class.java)
        Mockito.`when`(viewMock.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(brushToolOptionsView!!.getTopToolOptions()).thenReturn(viewMock)
        Mockito.`when`(brushToolOptionsView.getBottomToolOptions()).thenReturn(viewMock)

        val tap1 = PointF(0f, 0f)
        val tap2 = PointF(MOVE_TOLERANCE - .1f, 0f)
        val tap3 = PointF(MOVE_TOLERANCE - 0.1f, MOVE_TOLERANCE - 0.1f)
        Mockito.`when`(workspace!!.contains(any<PointF>())).thenReturn(true)
        Mockito.`when`(toolPaint!!.paint).thenReturn(paint)
        val returnValue1 = toolToTest!!.handleDown(tap1)
        val returnValue2 = toolToTest!!.handleMove(tap2)
        val returnValue3 = toolToTest!!.handleUp(tap3)
        Assert.assertTrue(returnValue1)
        Assert.assertTrue(returnValue2)
        Assert.assertTrue(returnValue3)
        val argument = ArgumentCaptor.forClass(PointCommand::class.java)
        Mockito.verify(commandManager)!!.addCommand(argument.capture())
        val command = argument.value
        Assert.assertEquals(tap1, command.point)
        assertPaintEquals(paint!!, command.paint)
    }

    @Test
    fun testShouldAddPathCommandOnMultipleMovesWithinToleranceEvent() {
        val viewMock = Mockito.mock(View::class.java)
        Mockito.`when`(viewMock.visibility).thenReturn(View.VISIBLE)
        Mockito.`when`(brushToolOptionsView!!.getTopToolOptions()).thenReturn(viewMock)
        Mockito.`when`(brushToolOptionsView.getBottomToolOptions()).thenReturn(viewMock)

        Mockito.`when`(workspace!!.contains(any<PointF>())).thenReturn(true)
        Mockito.`when`(toolPaint!!.paint).thenReturn(paint)
        val tap1 = PointF(7f, 7f)
        val tap2 = PointF(7f, MOVE_TOLERANCE - 0.1f)
        val tap3 = PointF(7f, 7f)
        val tap4 = PointF(7f, -MOVE_TOLERANCE + 0.1f)
        val tap5 = PointF(7f, 7f)
        toolToTest!!.handleDown(tap1)
        toolToTest!!.handleMove(tap2)
        toolToTest!!.handleMove(tap3)
        toolToTest!!.handleMove(tap4)
        toolToTest!!.handleUp(tap5)
        Mockito.verify(commandManager)!!.addCommand(ArgumentMatchers.isA(PathCommand::class.java))
    }

    @Test
    fun testShouldRewindPathOnAppliedToBitmap() {
        val pathStub = PathStub()
        toolToTest!!.pathToDraw = pathStub
        toolToTest!!.resetInternalState(StateChange.RESET_INTERNAL_STATE)
        Mockito.verify(pathStub.getStub()).rewind()
    }

    @Test
    fun testShouldChangePaintFromBrushPicker() {
        val argument = argumentCaptor<OnBrushChangedListener>()
        Mockito.verify(brushToolOptionsView)!!.setBrushChangedListener(argument.capture())
        val onBrushChangedListener = argument.firstValue
        onBrushChangedListener.setCap(Paint.Cap.ROUND)
        onBrushChangedListener.setStrokeWidth(15)
        Mockito.verify(toolPaint)!!.strokeCap = Paint.Cap.ROUND
        Mockito.verify(toolPaint)!!.strokeWidth = 15f
    }

    companion object {
        private const val MOVE_TOLERANCE = 5f
    }
}
