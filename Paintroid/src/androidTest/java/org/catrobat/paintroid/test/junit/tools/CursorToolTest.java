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

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class CursorToolTest {
	private static final float MOVE_TOLERANCE = BaseTool.MOVE_TOLERANCE;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private CommandManager commandManager;

	private CursorTool toolToTest;

	@UiThreadTest
	@Before
	public void setUp() {
		toolToTest = new CursorTool(activityTestRule.getActivity(), ToolType.CURSOR);
		PaintroidApplication.commandManager = commandManager;
	}

	@UiThreadTest
	@After
	public void tearDown() {
		PaintroidApplication.drawingSurface.setBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8));
		BaseTool.reset();
	}

	@UiThreadTest
	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = toolToTest.getToolType();

		assertEquals(ToolType.CURSOR, toolType);
	}

	@UiThreadTest
	@Test
	public void testShouldActivateCursorOnTabEvent() {
		PointF point = new PointF(5, 5);

		assertTrue(toolToTest.handleDown(point));
		assertTrue(toolToTest.handleUp(point));

		verify(commandManager).addCommand(isA(PointCommand.class));
		assertTrue(toolToTest.toolInDrawMode);
	}

	@UiThreadTest
	@Test
	public void testShouldNotActivateCursorOnTabEvent() {
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

	@UiThreadTest
	@Test
	public void testShouldMovePathOnUpEvent() {
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

	@UiThreadTest
	@Test
	public void testShouldCheckIfColorChangesIfToolIsActive() {

		boolean checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertFalse(checkIfInDrawMode);

		PointF point = new PointF(200, 200);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint = CursorTool.BITMAP_PAINT;
		int testmSecondaryShapeColor = toolToTest.cursorToolSecondaryShapeColor;

		assertEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint = CursorTool.BITMAP_PAINT;
		testmSecondaryShapeColor = toolToTest.cursorToolSecondaryShapeColor;
		assertNotEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		toolToTest.changePaintColor(Color.GREEN);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint2 = CursorTool.BITMAP_PAINT;
		int testmSecondaryShapeColor2 = toolToTest.cursorToolSecondaryShapeColor;
		assertEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint2 = CursorTool.BITMAP_PAINT;
		testmSecondaryShapeColor2 = toolToTest.cursorToolSecondaryShapeColor;
		assertNotEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		// test if color also changes if cursor already active
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);
		checkIfInDrawMode = toolToTest.toolInDrawMode;
		assertTrue(checkIfInDrawMode);

		toolToTest.changePaintColor(Color.CYAN);

		Paint testmBitmapPaint3 = CursorTool.BITMAP_PAINT;
		int testmSecondaryShapeColor3 = toolToTest.cursorToolSecondaryShapeColor;
		assertEquals("If cursor already active and color gets changed, cursortool should change color immediately",
				testmBitmapPaint3.getColor(), testmSecondaryShapeColor3);
	}
}
