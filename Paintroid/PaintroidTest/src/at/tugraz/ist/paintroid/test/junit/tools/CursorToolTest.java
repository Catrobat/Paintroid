/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.junit.tools;

import java.util.List;

import org.junit.Ignore;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.PointCommand;
import at.tugraz.ist.paintroid.test.junit.stubs.BrushPickerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.ColorPickerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.CommandManagerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.PathStub;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.BaseTool;
import at.tugraz.ist.paintroid.tools.implementation.BaseToolWithShape;
import at.tugraz.ist.paintroid.tools.implementation.CursorTool;

public class CursorToolTest extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Tool tool;
	protected CommandManagerStub commandManagerStub;
	protected Paint paint;
	protected ColorPickerStub colorPickerStub;
	protected BrushPickerStub brushPickerStub;
	protected PrivateAccess mPrivateAccess = new PrivateAccess();

	public CursorToolTest() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		this.commandManagerStub = new CommandManagerStub();
		this.tool = new CursorTool(this.getActivity(), Tool.ToolType.CURSOR);
		this.paint = new Paint();
		this.paint.setColor(Color.BLACK);
		this.paint.setStrokeCap(Cap.ROUND);
		this.paint.setStrokeWidth(tool.stroke15);
		this.tool.setDrawPaint(this.paint);
		this.colorPickerStub = new ColorPickerStub(this.getActivity(), null);
		PrivateAccess.setMemberValue(BaseTool.class, this.tool, "mColorPickerDialog", this.colorPickerStub);
		this.brushPickerStub = new BrushPickerStub(this.getActivity(), null, paint);
		PrivateAccess.setMemberValue(BaseTool.class, this.tool, "mBrushPickerDialog", this.brushPickerStub);
		PaintroidApplication.COMMAND_MANAGER = this.commandManagerStub;
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = tool.getToolType();

		assertEquals(ToolType.CURSOR, toolType);
	}

	@Ignore
	public void testShouldActivateCursorOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF point = new PointF(0, 0);

		boolean handleDownEventResult = this.tool.handleDown(point);
		boolean handleUpEventResult = this.tool.handleUp(point);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(1, commandManagerStub.getCallCount("commitCommand"));
		Command command = (Command) commandManagerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		boolean draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode");
		assertTrue(draw);
	}

	@Ignore
	public void testShouldNotActivateCursorOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF pointDown = new PointF(0, 0);
		PointF pointUp = new PointF(pointDown.x + PaintroidApplication.MOVE_TOLLERANCE + 1, pointDown.y
				+ PaintroidApplication.MOVE_TOLLERANCE + 1);

		// +/+
		boolean handleDownEventResult = this.tool.handleDown(pointDown);
		boolean handleUpEventResult = this.tool.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommand"));
		boolean draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode");
		assertFalse(draw);

		// +/0
		pointUp.set(pointDown.x + PaintroidApplication.MOVE_TOLLERANCE + 1, pointDown.y);

		handleDownEventResult = this.tool.handleDown(pointDown);
		handleUpEventResult = this.tool.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommand"));

		draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode");
		assertFalse(draw);

		// 0/+
		pointUp.set(pointDown.x, pointDown.y + PaintroidApplication.MOVE_TOLLERANCE + 1);
		handleDownEventResult = this.tool.handleDown(pointDown);
		handleUpEventResult = this.tool.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommand"));
		draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode");
		assertFalse(draw);

		// -/-
		pointUp.set(pointDown.x - PaintroidApplication.MOVE_TOLLERANCE - 1, pointDown.y
				- PaintroidApplication.MOVE_TOLLERANCE - 1);
		handleDownEventResult = this.tool.handleDown(pointDown);
		handleUpEventResult = this.tool.handleUp(pointUp);

		assertTrue(handleDownEventResult);
		assertTrue(handleUpEventResult);

		assertEquals(0, commandManagerStub.getCallCount("commitCommand"));
		draw = PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode");
		assertFalse(draw);
	}

	@Ignore
	public void testShouldMovePathOnUpEvent() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE, PaintroidApplication.MOVE_TOLLERANCE);
		PointF event3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE * 2, -PaintroidApplication.MOVE_TOLLERANCE);
		PointF testCursorPosition = new PointF();
		PointF actualCursorPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, this.tool,
				"mToolPosition");
		assertNotNull(actualCursorPosition);
		testCursorPosition.set(actualCursorPosition);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(CursorTool.class, this.tool, "pathToDraw", pathStub);
		assertFalse(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode"));
		float vectorCX = event1.x;
		float vectorCY = event1.y;

		// e1
		boolean returnValue = tool.handleDown(event1);
		assertTrue(returnValue);
		assertFalse(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode"));
		returnValue = tool.handleUp(event1);
		assertTrue(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode"));
		assertTrue(returnValue);
		assertEquals(testCursorPosition.x, actualCursorPosition.x);
		assertEquals(testCursorPosition.y, actualCursorPosition.y);
		// e2
		returnValue = tool.handleMove(event2);
		vectorCX = event2.x - event1.x;
		vectorCY = event2.y - event1.y;
		testCursorPosition.set(testCursorPosition.x + vectorCX, testCursorPosition.y + vectorCY);
		assertEquals(testCursorPosition.x, actualCursorPosition.x);
		assertEquals(testCursorPosition.y, actualCursorPosition.y);
		assertTrue(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode"));
		assertTrue(returnValue);
		// e3
		returnValue = tool.handleUp(event3);
		assertTrue(PrivateAccess.getMemberValueBoolean(CursorTool.class, this.tool, "toolInDrawMode"));
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
}
