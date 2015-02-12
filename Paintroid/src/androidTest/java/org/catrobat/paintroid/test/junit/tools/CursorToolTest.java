/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.tools;

import java.util.List;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.CursorTool;
import org.catrobat.paintroid.ui.TopBar.ToolButtonIDs;
import org.junit.Ignore;
import org.junit.Test;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

public class CursorToolTest extends BaseToolTest {

	protected PrivateAccess mPrivateAccess = new PrivateAccess();

	public CursorToolTest() {
		super();
	}

	@Override
	public void setUp() throws Exception {
		mToolToTest = new CursorTool(this.getActivity(), ToolType.CURSOR);
		super.setUp();
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = mToolToTest.getToolType();

		assertEquals(ToolType.CURSOR, toolType);
	}

	@Ignore
	public void testShouldActivateCursorOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF point = new PointF(0, 0);

		boolean handleDownEventResult = this.mToolToTest.handleDown(point);
		boolean handleUpEventResult = this.mToolToTest.handleUp(point);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(1, mCommandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) mCommandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		boolean draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode");
		assertTrue(draw);
	}

	@Ignore
	public void testShouldNotActivateCursorOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF pointDown = new PointF(0, 0);
		PointF pointUp = new PointF(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y + MOVE_TOLERANCE + 1);

		// +/+
		boolean handleDownEventResult = this.mToolToTest.handleDown(pointDown);
		boolean handleUpEventResult = this.mToolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));
		boolean draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode");
		assertFalse(draw);

		// +/0
		pointUp.set(pointDown.x + MOVE_TOLERANCE + 1, pointDown.y);

		handleDownEventResult = this.mToolToTest.handleDown(pointDown);
		handleUpEventResult = this.mToolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));

		draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode");
		assertFalse(draw);

		// 0/+
		pointUp.set(pointDown.x, pointDown.y + MOVE_TOLERANCE + 1);
		handleDownEventResult = this.mToolToTest.handleDown(pointDown);
		handleUpEventResult = this.mToolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));
		draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode");
		assertFalse(draw);

		// -/-
		pointUp.set(pointDown.x - MOVE_TOLERANCE - 1, pointDown.y - MOVE_TOLERANCE - 1);
		handleDownEventResult = this.mToolToTest.handleDown(pointDown);
		handleUpEventResult = this.mToolToTest.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, mCommandManagerStub.getCallCount("commitCommand"));
		draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode");
		assertFalse(draw);
	}

	@Ignore
	public void testShouldMovePathOnUpEvent() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE, MOVE_TOLERANCE);
		PointF event3 = new PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE);
		PointF testCursorPosition = new PointF();
		PointF actualCursorPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, this.mToolToTest,
				"mToolPosition");
		assertNotNull(actualCursorPosition);
		testCursorPosition.set(actualCursorPosition);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(CursorTool.class, this.mToolToTest, "pathToDraw", pathStub);
		assertFalse(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode"));
		float vectorCX = event1.x;
		float vectorCY = event1.y;

		// e1
		boolean returnValue = mToolToTest.handleDown(event1);
		assertTrue(returnValue);
		assertFalse(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode"));
		returnValue = mToolToTest.handleUp(event1);
		assertTrue(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode"));
		assertTrue(returnValue);
		assertEquals(testCursorPosition.x, actualCursorPosition.x);
		assertEquals(testCursorPosition.y, actualCursorPosition.y);
		// e2
		returnValue = mToolToTest.handleMove(event2);
		vectorCX = event2.x - event1.x;
		vectorCY = event2.y - event1.y;
		testCursorPosition.set(testCursorPosition.x + vectorCX, testCursorPosition.y + vectorCY);
		assertEquals(testCursorPosition.x, actualCursorPosition.x);
		assertEquals(testCursorPosition.y, actualCursorPosition.y);
		assertTrue(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode"));
		assertTrue(returnValue);
		// e3
		returnValue = mToolToTest.handleUp(event3);
		assertTrue(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest, "toolInDrawMode"));
		assertTrue(returnValue);
		assertEquals(testCursorPosition.x, actualCursorPosition.x);
		assertEquals(testCursorPosition.y, actualCursorPosition.y);

		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		assertEquals(1, pathStub.getCallCount("lineTo"));
		List<Object> arguments = pathStub.getCall("lineTo", 0);
		assertEquals(testCursorPosition.x, arguments.get(0));
		assertEquals(testCursorPosition.y, arguments.get(1));
	}

	@Test
	public void testShouldReturnCorrectResourceForCurrentToolButton() {
		int resource = mToolToTest.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_TOOL);
		assertEquals("Cursor tool icon should be displayed", R.drawable.icon_menu_cursor, resource);
	}

	@Test
	public void testShouldCheckIfColorChangesIfToolIsActive() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		boolean checkIfInDrawMode = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.mToolToTest,
				"toolInDrawMode");
		assertFalse(checkIfInDrawMode);

		PointF point = new PointF(200, 200);
		mToolToTest.handleDown(point);
		mToolToTest.handleUp(point);

		checkIfInDrawMode = PrivateAccess.getMemberValueBoolean(CursorTool.class, mToolToTest, "toolInDrawMode");
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint = (Paint) (PrivateAccess.getMemberValue(BaseTool.class, mToolToTest, "mBitmapPaint"));
		int testmSecondaryShapeColor = (Integer) (PrivateAccess.getMemberValue(CursorTool.class, mToolToTest,
				"mSecondaryShapeColor"));

		assertEquals(testmBitmapPaint.getColor(), testmSecondaryShapeColor);

		mToolToTest.handleDown(point);
		mToolToTest.handleUp(point);

		checkIfInDrawMode = PrivateAccess.getMemberValueBoolean(CursorTool.class, mToolToTest, "toolInDrawMode");
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint = (Paint) (PrivateAccess.getMemberValue(BaseTool.class, mToolToTest, "mBitmapPaint"));
		testmSecondaryShapeColor = (Integer) (PrivateAccess.getMemberValue(CursorTool.class, mToolToTest,
				"mSecondaryShapeColor"));
		assertTrue(testmBitmapPaint.getColor() != testmSecondaryShapeColor);

		mToolToTest.changePaintColor(Color.GREEN);
		mToolToTest.handleDown(point);
		mToolToTest.handleUp(point);

		checkIfInDrawMode = PrivateAccess.getMemberValueBoolean(CursorTool.class, mToolToTest, "toolInDrawMode");
		assertTrue(checkIfInDrawMode);
		Paint testmBitmapPaint2 = (Paint) (PrivateAccess.getMemberValue(BaseTool.class, mToolToTest, "mBitmapPaint"));
		int testmSecondaryShapeColor2 = (Integer) (PrivateAccess.getMemberValue(CursorTool.class, mToolToTest,
				"mSecondaryShapeColor"));
		assertEquals(testmBitmapPaint2.getColor(), testmSecondaryShapeColor2);

		mToolToTest.handleDown(point);
		mToolToTest.handleUp(point);

		checkIfInDrawMode = PrivateAccess.getMemberValueBoolean(CursorTool.class, mToolToTest, "toolInDrawMode");
		assertFalse(checkIfInDrawMode);
		testmBitmapPaint2 = (Paint) (PrivateAccess.getMemberValue(BaseTool.class, mToolToTest, "mBitmapPaint"));
		testmSecondaryShapeColor2 = (Integer) (PrivateAccess.getMemberValue(CursorTool.class, mToolToTest,
				"mSecondaryShapeColor"));
		assertTrue(testmBitmapPaint2.getColor() != testmSecondaryShapeColor2);

		// test if color also changes if cursor already active
		mToolToTest.handleDown(point);
		mToolToTest.handleUp(point);
		checkIfInDrawMode = PrivateAccess.getMemberValueBoolean(CursorTool.class, mToolToTest, "toolInDrawMode");
		assertTrue(checkIfInDrawMode);

		mToolToTest.changePaintColor(Color.CYAN);

		Paint testmBitmapPaint3 = (Paint) (PrivateAccess.getMemberValue(BaseTool.class, mToolToTest, "mBitmapPaint"));
		int testmSecondaryShapeColor3 = (Integer) (PrivateAccess.getMemberValue(CursorTool.class, mToolToTest,
				"mSecondaryShapeColor"));
		assertEquals("If cursor already active and color gets changed, cursortool should change color immediately",
				testmBitmapPaint3.getColor(), testmSecondaryShapeColor3);

	}
}
