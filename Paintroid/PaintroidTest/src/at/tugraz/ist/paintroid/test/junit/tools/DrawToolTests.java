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

import static at.tugraz.ist.paintroid.test.utils.PaintroidAsserts.assertPaintEquals;
import static at.tugraz.ist.paintroid.test.utils.PaintroidAsserts.assertPathEquals;

import java.util.List;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import at.tugraz.ist.paintroid.MainActivity;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.command.Command;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.command.implementation.PathCommand;
import at.tugraz.ist.paintroid.command.implementation.PointCommand;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog;
import at.tugraz.ist.paintroid.dialog.BrushPickerDialog.OnBrushChangedListener;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog;
import at.tugraz.ist.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import at.tugraz.ist.paintroid.test.junit.stubs.BrushPickerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.ColorPickerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.CommandManagerStub;
import at.tugraz.ist.paintroid.test.junit.stubs.PathStub;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.BaseTool;
import at.tugraz.ist.paintroid.tools.implementation.DrawTool;
import at.tugraz.ist.paintroid.ui.button.ToolbarButton.ToolButtonIDs;

public class DrawToolTests extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Tool tool;
	protected CommandManagerStub commandHandlerStub;
	protected Paint paint;
	protected ColorPickerStub colorPickerStub;
	protected BrushPickerStub brushPickerStub;

	public DrawToolTests() {
		super("at.tugraz.ist.paintroid", MainActivity.class);
	}

	@Override
	public void setUp() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		System.gc();
		this.paint = new Paint();
		this.paint.setColor(Color.BLACK);
		this.paint.setStrokeCap(Cap.ROUND);
		this.paint.setStrokeWidth(15);
		this.commandHandlerStub = new CommandManagerStub();
		this.tool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		this.tool.setDrawPaint(this.paint);
		this.colorPickerStub = new ColorPickerStub(this.getActivity(), null);
		PrivateAccess.setMemberValue(BaseTool.class, this.tool, "colorPicker", this.colorPickerStub);
		this.brushPickerStub = new BrushPickerStub(this.getActivity(), null, paint);
		PrivateAccess.setMemberValue(BaseTool.class, this.tool, "brushPicker", this.brushPickerStub);
		PaintroidApplication.COMMAND_MANAGER = this.commandHandlerStub;
	}

	public void testShouldReturnCorrectToolType() {
		ToolType toolType = tool.getToolType();

		assertEquals(ToolType.BRUSH, toolType);
	}

	public void testShouldReturnPaint() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		tool.setDrawPaint(this.paint);
		Paint drawPaint = (Paint) PrivateAccess.getMemberValue(BaseTool.class, tool, "bitmapPaint");
		assertEquals(this.paint.getColor(), drawPaint.getColor());
		assertEquals(this.paint.getStrokeWidth(), drawPaint.getStrokeWidth());
		assertEquals(this.paint.getStrokeCap(), drawPaint.getStrokeCap());
		assertEquals(this.paint.getShader(), drawPaint.getShader());
	}

	// down event
	public void testShouldMovePathOnDownEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		boolean returnValue = tool.handleDown(event);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		List<Object> arguments = pathStub.getCall("moveTo", 0);
		assertEquals(event.x, arguments.get(0));
		assertEquals(event.y, arguments.get(1));
	}

	public void testShouldNotAddCommandOnDownEvent() {
		PointF event = new PointF(0, 0);

		boolean returnValue = tool.handleDown(event);

		assertTrue(returnValue);
		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}

	public void testShouldNotStartPathIfNoCoordinateOnDownEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		boolean returnValue = tool.handleDown(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("reset"));
		assertEquals(0, pathStub.getCallCount("moveTo"));
	}

	// move event
	public void testShouldMovePathOnMoveEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(5, 6);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event1);
		boolean returnValue = tool.handleMove(event2);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		List<Object> arguments = pathStub.getCall("quadTo", 0);
		final float cx = (event1.x + event2.x) / 2;
		final float cy = (event1.y + event2.y) / 2;
		assertEquals(event1.x, arguments.get(0));
		assertEquals(event1.y, arguments.get(1));
		assertEquals(cx, arguments.get(2));
		assertEquals(cy, arguments.get(3));
	}

	public void testShouldNotAddCommandOnMoveEvent() {
		PointF event = new PointF(0, 0);

		tool.handleDown(event);
		boolean returnValue = tool.handleMove(event);

		assertTrue(returnValue);
		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}

	public void testShouldNotMovePathIfNoCoordinateOnMoveEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event);
		boolean returnValue = tool.handleMove(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("quadTo"));
	}

	// up event
	public void testShouldMovePathOnUpEvent() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE, PaintroidApplication.MOVE_TOLLERANCE);
		PointF event3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE * 2, -PaintroidApplication.MOVE_TOLLERANCE);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event1);
		tool.handleMove(event2);
		boolean returnValue = tool.handleUp(event3);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		assertEquals(1, pathStub.getCallCount("lineTo"));
		List<Object> arguments = pathStub.getCall("lineTo", 0);
		assertEquals(event3.x, arguments.get(0));
		assertEquals(event3.y, arguments.get(1));
	}

	public void testShouldNotMovePathIfNoCoordinateOnUpEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event);
		tool.handleMove(event);
		boolean returnValue = tool.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("lineTo"));
	}

	public void testShouldAddCommandOnUpEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF event = new PointF(0, 0);
		PointF event1 = new PointF(PaintroidApplication.MOVE_TOLLERANCE + 0.1f, 0);
		PointF event2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE + 2, PaintroidApplication.MOVE_TOLLERANCE + 2);
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.handleDown(event);
		tool.handleMove(event1);
		boolean returnValue = tool.handleUp(event2);

		assertTrue(returnValue);
		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PathCommand);
		Path path = (Path) PrivateAccess.getMemberValue(PathCommand.class, command, "mPath");
		assertPathEquals(pathStub, path);
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(this.paint, paint);
	}

	public void testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);

		tool.handleDown(event);
		tool.handleMove(event);
		boolean returnValue = tool.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, commandHandlerStub.getCallCount("commitCommand"));
	}

	// tab event
	public void testShouldAddCommandOnTabEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF tab = new PointF(0, 0);

		boolean returnValue1 = tool.handleDown(tab);
		boolean returnValue2 = tool.handleUp(tab);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		PointF point = (PointF) PrivateAccess.getMemberValue(PointCommand.class, command, "mPoint");
		assertTrue(tab.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(this.paint, paint);
	}

	public void testShouldAddCommandOnTabWithinTolleranceEvent() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(PaintroidApplication.MOVE_TOLLERANCE - 0.1f, 0);
		PointF tab3 = new PointF(PaintroidApplication.MOVE_TOLLERANCE - 0.1f,
				PaintroidApplication.MOVE_TOLLERANCE - 0.1f);

		boolean returnValue1 = tool.handleDown(tab1);
		boolean returnValue2 = tool.handleMove(tab2);
		boolean returnValue3 = tool.handleUp(tab3);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertTrue(returnValue3);
		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PointCommand);
		PointF point = (PointF) PrivateAccess.getMemberValue(PointCommand.class, command, "mPoint");
		assertTrue(tab1.equals(point.x, point.y));
		Paint paint = (Paint) PrivateAccess.getMemberValue(BaseCommand.class, command, "mPaint");
		assertPaintEquals(this.paint, paint);
	}

	public void testShouldAddPathCommandOnMultipleMovesWithinTolleranceEvent() throws SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(0, PaintroidApplication.MOVE_TOLLERANCE - 0.1f);
		PointF tab3 = new PointF(0, 0);
		PointF tab4 = new PointF(0, -PaintroidApplication.MOVE_TOLLERANCE + 0.1f);
		PointF tab5 = new PointF(0, 0);

		tool.handleDown(tab1);
		tool.handleMove(tab2);
		tool.handleMove(tab3);
		tool.handleMove(tab4);
		tool.handleUp(tab5);

		assertEquals(1, commandHandlerStub.getCallCount("commitCommand"));
		Command command = (Command) commandHandlerStub.getCall("commitCommand", 0).get(0);
		assertTrue(command instanceof PathCommand);
	}

	public void testShouldRewindPathOnAppliedToBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		PathStub pathStub = new PathStub();
		PrivateAccess.setMemberValue(DrawTool.class, tool, "pathToDraw", pathStub);

		tool.resetInternalState();

		assertEquals(1, pathStub.getCallCount("rewind"));
	}

	public void testShouldReturnPaintsColorForButton1() {
		int color = tool.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);

		assertEquals(paint.getColor(), color);
	}

	public void testShouldReturnBlackForButton2() {
		int color = tool.getAttributeButtonColor(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);

		assertEquals(Color.BLACK, color);
	}

	public void testShouldReturnCorrectResourceForButton2() {
		int resource = tool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);

		assertEquals(R.drawable.circle_1_32, resource);
	}

	public void testShouldReturnCorrectResourceForButton1IfColorIsTransparent() {
		tool.changePaintColor(Color.TRANSPARENT);

		int resource = tool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);

		assertEquals(R.drawable.transparent_64, resource);
	}

	public void testShouldReturnNoResourceForButton1IfColorIsNotTransparent() {
		tool.changePaintColor(Color.RED);

		int resource = tool.getAttributeButtonResource(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);

		assertEquals(0, resource);
	}

	public void testShouldStartColorPickerOnAttributeButton1Click() {
		tool.attributeButtonClick(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_2);

		assertEquals(1, colorPickerStub.getCallCount("setInitialColor"));
		assertEquals(this.paint.getColor(), colorPickerStub.getCall("setInitialColor", 0).get(0));
		assertEquals(1, colorPickerStub.getCallCount("show"));
	}

	public void testShouldChangePaintFromColorPicker() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		tool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		tool.setDrawPaint(this.paint);
		ColorPickerDialog colorPicker = (ColorPickerDialog) PrivateAccess.getMemberValue(BaseTool.class, this.tool,
				"colorPicker");
		OnColorPickedListener colorPickerListener = (OnColorPickedListener) PrivateAccess.getMemberValue(
				ColorPickerDialog.class, colorPicker, "mOnColorPickedListener");

		colorPickerListener.colorChanged(Color.RED);

		assertEquals(Color.RED, tool.getDrawPaint().getColor());

	}

	public void testShouldStartBrushPickerOnAttributeButton1Click() {
		tool.attributeButtonClick(ToolButtonIDs.BUTTON_ID_PARAMETER_BOTTOM_1);

		assertEquals(1, brushPickerStub.getCallCount("show"));
	}

	public void testShouldChangePaintFromBrushPicker() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		tool = new DrawTool(this.getActivity(), Tool.ToolType.BRUSH);
		tool.setDrawPaint(this.paint);
		BrushPickerDialog brushPicker = (BrushPickerDialog) PrivateAccess.getMemberValue(BaseTool.class, this.tool,
				"brushPicker");
		OnBrushChangedListener brushPickerListener = (OnBrushChangedListener) PrivateAccess.getMemberValue(
				BrushPickerDialog.class, brushPicker, "mBrushChangedListener");

		brushPickerListener.setCap(Cap.ROUND);
		brushPickerListener.setStroke(15);

		assertEquals(Cap.ROUND, tool.getDrawPaint().getStrokeCap());
		assertEquals(15f, tool.getDrawPaint().getStrokeWidth());
	}
}
