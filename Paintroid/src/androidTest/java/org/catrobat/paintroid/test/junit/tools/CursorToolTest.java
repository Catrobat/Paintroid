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

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandFactory;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.common.Constants;
import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.catrobat.paintroid.tools.options.BrushToolOptionsContract;
import org.catrobat.paintroid.tools.options.ToolOptionsControllerContract;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.catrobat.paintroid.test.utils.ToolPositionMatcher.eqToolPosition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CursorToolTest {
	private static final float MOVE_TOLERANCE = Constants.MOVE_TOLERANCE;
	@Mock
	public CommandManager commandManager;
	@Mock
	public BrushToolOptionsContract brushToolOptions;
	@Mock
	public ContextCallback contextCallback;
	@Mock
	public ToolOptionsControllerContract toolOptionsController;
	@Mock
	public CommandFactory commandFactory;
	@Mock
	public ToolPaint toolPaint;
	@Mock
	public Workspace workspace;
	@Mock
	public Paint paint;
	@Mock
	public Command command;
	@InjectMocks
	public CursorTool toolToTest;

	@Before
	public void setUp() {
		when(toolPaint.getPaint()).thenReturn(paint);

		when(workspace.getWidth()).thenReturn(1920);
		when(workspace.getHeight()).thenReturn(1080);
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = toolToTest.getToolType();

		assertEquals(ToolType.CURSOR, toolType);
	}

	@Test
	public void testShouldActivateCursorOnTapEvent() {
		PointF point = new PointF(5, 5);
		Command command = mock(Command.class);
		when(workspace.contains(point)).thenReturn(true);
		when(workspace.contains(eqToolPosition(toolToTest))).thenReturn(true);
		when(commandFactory.createPointCommand(eq(paint), eqToolPosition(toolToTest))).thenReturn(command);

		assertTrue(toolToTest.handleDown(point));
		assertTrue(toolToTest.handleUp(point));

		verify(commandManager).addCommand(command);
		assertTrue(toolToTest.toolInDrawMode);
	}

	@Test
	public void testShouldActivateCursorOnTapEventOutsideDrawingSurface() {
		PointF point = new PointF(-5, -5);
		when(workspace.contains(point)).thenReturn(false);
		when(workspace.contains(eqToolPosition(toolToTest))).thenReturn(true);
		when(commandFactory.createPointCommand(eq(paint), eqToolPosition(toolToTest))).thenReturn(command);

		assertTrue(toolToTest.handleDown(point));
		assertTrue(toolToTest.handleUp(point));

		verify(commandManager).addCommand(command);
		assertTrue(toolToTest.toolInDrawMode);
	}

	@Test
	public void testShouldNotActivateCursorOnTapEvent() {
		when(commandFactory.createPointCommand(eq(paint), any(PointF.class))).thenReturn(command);

		PointF pointDown = new PointF(0, 0);
		PointF pointUp = new PointF(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y + MOVE_TOLERANCE + 1);

		// +/+
		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(command);
		assertFalse(toolToTest.toolInDrawMode);

		// +/0
		pointUp.set(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y);

		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(command);

		assertFalse(toolToTest.toolInDrawMode);

		// 0/+
		pointUp.set(pointDown.x, pointDown.y + MOVE_TOLERANCE + 1);

		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(command);
		assertFalse(toolToTest.toolInDrawMode);

		// -/-
		pointUp.set(pointDown.x - MOVE_TOLERANCE - 1, pointDown.y - MOVE_TOLERANCE - 1);

		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(command);
		assertFalse(toolToTest.toolInDrawMode);
	}

	@Test
	public void testShouldMovePathOnUpEvent() {
		when(workspace.getSurfacePointFromCanvasPoint(any(PointF.class)))
				.thenAnswer(new Answer<PointF>() {
					@Override
					public PointF answer(InvocationOnMock invocation) {
						return invocation.getArgument(0);
					}
				});

		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE, MOVE_TOLERANCE);
		PointF event3 = new PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE);
		PointF testCursorPosition = new PointF();
		PointF actualCursorPosition = toolToTest.toolPosition;
		assertNotNull(actualCursorPosition);
		testCursorPosition.set(actualCursorPosition);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;
		assertFalse(toolToTest.toolInDrawMode);

		// e1
		boolean returnValue = toolToTest.handleDown(event1);
		assertTrue(returnValue);
		assertFalse(toolToTest.toolInDrawMode);
		returnValue = toolToTest.handleUp(event1);
		assertTrue(toolToTest.toolInDrawMode);
		assertTrue(returnValue);
		assertEquals(testCursorPosition.x, actualCursorPosition.x, Double.MIN_VALUE);
		assertEquals(testCursorPosition.y, actualCursorPosition.y, Double.MIN_VALUE);
		// e2
		returnValue = toolToTest.handleMove(event2);
		float vectorCX = event2.x - event1.x;
		float vectorCY = event2.y - event1.y;
		testCursorPosition.set(testCursorPosition.x + vectorCX, testCursorPosition.y + vectorCY);
		assertEquals(testCursorPosition.x, actualCursorPosition.x, Double.MIN_VALUE);
		assertEquals(testCursorPosition.y, actualCursorPosition.y, Double.MIN_VALUE);
		assertTrue(toolToTest.toolInDrawMode);
		assertTrue(returnValue);
		// e3
		returnValue = toolToTest.handleUp(event3);
		assertTrue(toolToTest.toolInDrawMode);
		assertTrue(returnValue);
		assertEquals(testCursorPosition.x, actualCursorPosition.x, Double.MIN_VALUE);
		assertEquals(testCursorPosition.y, actualCursorPosition.y, Double.MIN_VALUE);

		Path stub = pathStub.getStub();
		verify(stub).moveTo(anyFloat(), anyFloat());
		verify(stub).quadTo(anyFloat(), anyFloat(), anyFloat(), anyFloat());
		verify(stub).lineTo(testCursorPosition.x, testCursorPosition.y);
	}

	@Test
	public void testShouldCheckIfColorChangesIfToolIsActive() {

		boolean checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertFalse(checkIfInDrawMode);

		PointF point = new PointF(200, 200);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint = toolPaint.getPaint();
		int testmSecondaryShapeColor = toolToTest.cursorToolSecondaryShapeColor;

		assertEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint = toolPaint.getPaint();
		testmSecondaryShapeColor = toolToTest.cursorToolSecondaryShapeColor;
		assertNotEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		toolToTest.changePaintColor(Color.GREEN);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint2 = toolPaint.getPaint();
		int testmSecondaryShapeColor2 = toolToTest.cursorToolSecondaryShapeColor;
		assertEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint2 = toolPaint.getPaint();
		testmSecondaryShapeColor2 = toolToTest.cursorToolSecondaryShapeColor;
		assertNotEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		// test if color also changes if cursor already active
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);
		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertTrue(checkIfInDrawMode);

		toolToTest.changePaintColor(Color.CYAN);

		Paint testmBitmapPaint3 = toolPaint.getPaint();
		int testmSecondaryShapeColor3 = toolToTest.cursorToolSecondaryShapeColor;
		assertEquals("If cursor already active and color gets changed, cursortool should change color immediately",
				testmBitmapPaint3.getColor(), testmSecondaryShapeColor3);
	}
}
