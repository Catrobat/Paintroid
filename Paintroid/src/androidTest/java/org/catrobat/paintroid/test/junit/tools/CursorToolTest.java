/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.test.annotation.UiThreadTest;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CursorToolTest extends BaseToolTest {

	public CursorToolTest() {
		super();
	}

	@UiThreadTest
	@Override
	@Before
	public void setUp() throws Exception {
		toolToTest = new CursorTool(this.getActivity(), ToolType.CURSOR);
		super.setUp();
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

		boolean handleDownEventResult = this.toolToTest.handleDown(point);
		boolean handleUpEventResult = this.toolToTest.handleUp(point);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(1, commandManagerStub.getCallCount("commitCommandToLayer"));
		Command command = (Command) commandManagerStub.getCall("commitCommandToLayer", 0).get(1);
		assertTrue(command instanceof PointCommand);
		boolean draw = ((CursorTool) toolToTest).toolInDrawMode;
		assertTrue(draw);
	}

	@UiThreadTest
	@Test
	public void testShouldNotActivateCursorOnTabEvent() {
		PointF pointDown = new PointF(0, 0);
		PointF pointUp = new PointF(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y + MOVE_TOLERANCE + 1);

		// +/+
		boolean handleDownEventResult = this.toolToTest.handleDown(pointDown);
		boolean handleUpEventResult = this.toolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));
		boolean draw = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(draw);

		// +/0
		pointUp.set(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y);

		handleDownEventResult = this.toolToTest.handleDown(pointDown);
		handleUpEventResult = this.toolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));

		draw = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(draw);

		// 0/+
		pointUp.set(pointDown.x, pointDown.y + MOVE_TOLERANCE + 1);
		handleDownEventResult = this.toolToTest.handleDown(pointDown);
		handleUpEventResult = this.toolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));
		draw = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(draw);

		// -/-
		pointUp.set(pointDown.x - MOVE_TOLERANCE - 1, pointDown.y - MOVE_TOLERANCE - 1);
		handleDownEventResult = this.toolToTest.handleDown(pointDown);
		handleUpEventResult = this.toolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));
		draw = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(draw);
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnUpEvent() {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE, MOVE_TOLERANCE);
		PointF event3 = new PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE);
		PointF testCursorPosition = new PointF();
		PointF actualCursorPosition = ((CursorTool) toolToTest).toolPosition;
		assertNotNull(actualCursorPosition);
		testCursorPosition.set(actualCursorPosition);
		PathStub pathStub = new PathStub();
		((CursorTool) toolToTest).pathToDraw = pathStub;
		assertFalse(((CursorTool) toolToTest).toolInDrawMode);

		// e1
		boolean returnValue = toolToTest.handleDown(event1);
		assertTrue(returnValue);
		assertFalse(((CursorTool) toolToTest).toolInDrawMode);
		returnValue = toolToTest.handleUp(event1);
		assertTrue(((CursorTool) toolToTest).toolInDrawMode);
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
		assertTrue(((CursorTool) toolToTest).toolInDrawMode);
		assertTrue(returnValue);
		// e3
		returnValue = toolToTest.handleUp(event3);
		assertTrue(((CursorTool) toolToTest).toolInDrawMode);
		assertTrue(returnValue);
		assertEquals(testCursorPosition.x, actualCursorPosition.x, Double.MIN_VALUE);
		assertEquals(testCursorPosition.y, actualCursorPosition.y, Double.MIN_VALUE);

		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		assertEquals(1, pathStub.getCallCount("lineTo"));
		List<Object> arguments = pathStub.getCall("lineTo", 0);
		assertEquals(testCursorPosition.x, arguments.get(0));
		assertEquals(testCursorPosition.y, arguments.get(1));
	}

	@UiThreadTest
	@Test
	public void testShouldCheckIfColorChangesIfToolIsActive() {

		boolean checkIfInDrawMode = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(checkIfInDrawMode);

		PointF point = new PointF(200, 200);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = ((CursorTool) toolToTest).toolInDrawMode;
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint = CursorTool.BITMAP_PAINT;
		int testmSecondaryShapeColor = ((CursorTool) toolToTest).cursorToolSecondaryShapeColor;

		assertEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint = CursorTool.BITMAP_PAINT;
		testmSecondaryShapeColor = ((CursorTool) toolToTest).cursorToolSecondaryShapeColor;
		assertNotEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		toolToTest.changePaintColor(Color.GREEN);
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = ((CursorTool) toolToTest).toolInDrawMode;
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint2 = CursorTool.BITMAP_PAINT;
		int testmSecondaryShapeColor2 = ((CursorTool) toolToTest).cursorToolSecondaryShapeColor;
		assertEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		toolToTest.handleDown(point);
		toolToTest.handleUp(point);

		checkIfInDrawMode = ((CursorTool) toolToTest).toolInDrawMode;
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint2 = CursorTool.BITMAP_PAINT;
		testmSecondaryShapeColor2 = ((CursorTool) toolToTest).cursorToolSecondaryShapeColor;
		assertNotEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		// test if color also changes if cursor already active
		toolToTest.handleDown(point);
		toolToTest.handleUp(point);
		checkIfInDrawMode = ((CursorTool) toolToTest).toolInDrawMode;
		assertTrue(checkIfInDrawMode);

		toolToTest.changePaintColor(Color.CYAN);

		Paint testmBitmapPaint3 = CursorTool.BITMAP_PAINT;
		int testmSecondaryShapeColor3 = ((CursorTool) toolToTest).cursorToolSecondaryShapeColor;
		assertEquals("If cursor already active and color gets changed, cursortool should change color immediately",
				testmBitmapPaint3.getColor(), testmSecondaryShapeColor3);
	}
}
