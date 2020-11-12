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
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.common.Constants;
import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.catrobat.paintroid.tools.options.BrushToolOptionsView;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CursorToolTest {
	private static final float MOVE_TOLERANCE = Constants.MOVE_TOLERANCE;
	@Mock
	private CommandManager commandManager;
	@Mock
	private ToolPaint toolPaint;
	@Mock
	private Workspace workspace;
	@Mock
	private BrushToolOptionsView brushToolOptionsView;
	@Mock
	private ToolOptionsVisibilityController toolOptionsViewController;
	@Mock
	private ContextCallback contextCallback;

	private CursorTool toolToTest;

	@Before
	public void setUp() {
		Paint paint = new Paint();
		when(toolPaint.getPaint()).thenReturn(paint);
		when(workspace.getHeight()).thenReturn(1920);
		when(workspace.getWidth()).thenReturn(1080);
		when(workspace.getPerspective()).thenReturn(new Perspective(1080, 1920));

		toolToTest = new CursorTool(brushToolOptionsView, contextCallback, toolOptionsViewController, toolPaint, workspace, commandManager);
	}

	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = toolToTest.getToolType();

		assertEquals(ToolType.CURSOR, toolType);
	}

	@Test
	public void testShouldActivateCursorOnTapEvent() {
		PointF point = new PointF(5, 5);
		when(workspace.contains(any(PointF.class))).thenReturn(true);

		assertTrue(toolToTest.handleDown(point));
		assertTrue(toolToTest.handleUp(point));

		verify(commandManager).addCommand(isA(PointCommand.class));
		assertTrue(toolToTest.toolInDrawMode);
	}

	@Test
	public void testShouldActivateCursorOnTapEventOutsideDrawingSurface() {
		when(workspace.contains(toolToTest.toolPosition)).thenReturn(true);
		PointF point = new PointF(-5, -5);

		assertTrue(toolToTest.handleDown(point));
		assertTrue(toolToTest.handleUp(point));

		verify(commandManager).addCommand(isA(PointCommand.class));
		assertTrue(toolToTest.toolInDrawMode);
	}

	@Test
	public void testShouldNotActivateCursorOnTapEvent() {
		PointF pointDown = new PointF(0, 0);
		PointF pointUp = new PointF(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y + MOVE_TOLERANCE + 1);

		// +/+
		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(any(Command.class));
		assertFalse(toolToTest.toolInDrawMode);

		// +/0
		pointUp.set(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y);

		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(any(Command.class));

		assertFalse(toolToTest.toolInDrawMode);

		// 0/+
		pointUp.set(pointDown.x, pointDown.y + MOVE_TOLERANCE + 1);

		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(any(Command.class));
		assertFalse(toolToTest.toolInDrawMode);

		// -/-
		pointUp.set(pointDown.x - MOVE_TOLERANCE - 1, pointDown.y - MOVE_TOLERANCE - 1);

		assertTrue(toolToTest.handleDown(pointDown));
		assertTrue(toolToTest.handleUp(pointUp));

		verify(commandManager, never()).addCommand(any(Command.class));
		assertFalse(toolToTest.toolInDrawMode);
	}

	private static PointF copyPointF(PointF point) {
		return new PointF(point.x, point.y);
	}

	@Test
	public void testShouldMovePathOnUpEvent() {
		when(workspace.getSurfaceHeight()).thenReturn(1920);
		when(workspace.getSurfaceWidth()).thenReturn(1080);
		when(workspace.getSurfacePointFromCanvasPoint(any(PointF.class))).thenAnswer(new Answer<PointF>() {
			@Override
			public PointF answer(InvocationOnMock invocation) {
				return copyPointF((PointF) invocation.getArgument(0));
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
		when(workspace.contains(toolToTest.toolPosition)).thenReturn(true);
		when(toolPaint.getColor()).thenReturn(Color.RED);

		assertFalse(toolToTest.toolInDrawMode);

		PointF point = new PointF(200, 200);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		assertTrue(toolToTest.toolInDrawMode);
		assertEquals(Color.RED, toolToTest.cursorToolSecondaryShapeColor);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		assertFalse(toolToTest.toolInDrawMode);
		assertEquals(Color.LTGRAY, toolToTest.cursorToolSecondaryShapeColor);

		when(toolPaint.getColor()).thenReturn(Color.GREEN);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		assertTrue(toolToTest.toolInDrawMode);
		assertEquals(Color.GREEN, toolToTest.cursorToolSecondaryShapeColor);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		assertFalse(toolToTest.toolInDrawMode);
		assertEquals(Color.LTGRAY, toolToTest.cursorToolSecondaryShapeColor);

		// test if color also changes if cursor already active
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);
		assertTrue(toolToTest.toolInDrawMode);

		when(toolPaint.getColor()).thenReturn(Color.CYAN);
		toolToTest.changePaintColor(Color.CYAN);

		assertEquals(Color.CYAN, toolToTest.cursorToolSecondaryShapeColor);
	}
}
