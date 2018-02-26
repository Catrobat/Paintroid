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
import android.graphics.Path;
import android.graphics.PointF;
import android.support.test.annotation.UiThreadTest;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog.OnColorPickedListener;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPaintEquals;
import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPathEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DrawToolTests extends BaseToolTest {

	public DrawToolTests() {
		super();
	}

	@UiThreadTest
	@Override
	@Before
	public void setUp() throws Exception {
		toolToTest = new DrawTool(this.getActivity(), ToolType.BRUSH);
		super.setUp();
	}

	@UiThreadTest
	@Override
	@After
	public void tearDown() throws Exception {
		toolToTest = null;
		paint = null;
		commandManagerStub = null;
		getActivity().finish();
		super.tearDown();
	}

	@UiThreadTest
	@Test
	public void testShouldReturnCorrectToolType() {
		ToolType toolType = toolToTest.getToolType();

		assertEquals(ToolType.BRUSH, toolType);
	}

	@UiThreadTest
	@Test
	public void testShouldReturnPaint() {
		toolToTest.setDrawPaint(this.paint);
		Paint drawPaint = BaseTool.BITMAP_PAINT;
		assertEquals(this.paint.getColor(), drawPaint.getColor());
		assertEquals(this.paint.getStrokeWidth(), drawPaint.getStrokeWidth(), Double.MIN_VALUE);
		assertEquals(this.paint.getStrokeCap(), drawPaint.getStrokeCap());
		assertEquals(this.paint.getShader(), drawPaint.getShader());
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnDownEvent() {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		boolean returnValue = toolToTest.handleDown(event);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		List<Object> arguments = pathStub.getCall("moveTo", 0);
		assertEquals(event.x, arguments.get(0));
		assertEquals(event.y, arguments.get(1));
	}

	@UiThreadTest
	@Test
	public void testShouldNotAddCommandOnDownEvent() {
		PointF event = new PointF(0, 0);

		boolean returnValue = toolToTest.handleDown(event);

		assertTrue(returnValue);
		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));
	}

	@UiThreadTest
	@Test
	public void testShouldNotStartPathIfNoCoordinateOnDownEvent() {
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		boolean returnValue = toolToTest.handleDown(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("reset"));
		assertEquals(0, pathStub.getCallCount("moveTo"));
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnMoveEvent() {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(5, 6);
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		toolToTest.handleDown(event1);
		boolean returnValue = toolToTest.handleMove(event2);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		List<Object> arguments = pathStub.getCall("quadTo", 0);
		assertEquals(event1.x, arguments.get(0));
		assertEquals(event1.y, arguments.get(1));
		assertEquals(event2.x, arguments.get(2));
		assertEquals(event2.y, arguments.get(3));
	}

	@UiThreadTest
	@Test
	public void testShouldNotAddCommandOnMoveEvent() {
		PointF event = new PointF(0, 0);

		toolToTest.handleDown(event);
		boolean returnValue = toolToTest.handleMove(event);

		assertTrue(returnValue);
		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));
	}

	@UiThreadTest
	@Test
	public void testShouldNotMovePathIfNoCoordinateOnMoveEvent() {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		toolToTest.handleDown(event);
		boolean returnValue = toolToTest.handleMove(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("quadTo"));
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnUpEvent() {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE, MOVE_TOLERANCE);
		PointF event3 = new PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE);
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		toolToTest.handleDown(event1);
		toolToTest.handleMove(event2);
		boolean returnValue = toolToTest.handleUp(event3);

		assertTrue(returnValue);
		assertEquals(1, pathStub.getCallCount("moveTo"));
		assertEquals(1, pathStub.getCallCount("quadTo"));
		assertEquals(1, pathStub.getCallCount("lineTo"));
		List<Object> arguments = pathStub.getCall("lineTo", 0);
		assertEquals(event3.x, arguments.get(0));
		assertEquals(event3.y, arguments.get(1));
	}

	@UiThreadTest
	@Test
	public void testShouldNotMovePathIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		toolToTest.handleDown(event);
		toolToTest.handleMove(event);
		boolean returnValue = toolToTest.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, pathStub.getCallCount("lineTo"));
	}

	@UiThreadTest
	@Test
	public void testShouldAddCommandOnUpEvent() {
		PointF event = new PointF(0, 0);
		PointF event1 = new PointF(MOVE_TOLERANCE + 0.1f, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE + 2, MOVE_TOLERANCE + 2);
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		toolToTest.handleDown(event);
		toolToTest.handleMove(event1);
		boolean returnValue = toolToTest.handleUp(event2);

		assertTrue(returnValue);
		assertEquals(1, commandManagerStub.getCallCount("commitCommandToLayer"));
		Command command = (Command) commandManagerStub.getCall("commitCommandToLayer", 0).get(1);
		assertTrue(command instanceof PathCommand);
		Path path = ((PathCommand) command).path;
		assertPathEquals(pathStub, path);
		Paint paint = ((BaseCommand) command).paint;
		assertPaintEquals(this.paint, paint);
	}

	@UiThreadTest
	@Test
	public void testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);

		toolToTest.handleDown(event);
		toolToTest.handleMove(event);
		boolean returnValue = toolToTest.handleUp(null);

		assertFalse(returnValue);
		assertEquals(0, commandManagerStub.getCallCount("commitCommandToLayer"));
	}

	@UiThreadTest
	@Test
	public void testShouldAddCommandOnTabEvent() {
		PointF tab = new PointF(5, 5);

		boolean returnValue1 = toolToTest.handleDown(tab);
		boolean returnValue2 = toolToTest.handleUp(tab);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertEquals(1, commandManagerStub.getCallCount("commitCommandToLayer"));
		Command command = (Command) commandManagerStub.getCall("commitCommandToLayer", 0).get(1);
		assertTrue(command instanceof PointCommand);
		PointF point = ((PointCommand) command).point;
		assertEquals(tab, point);
		Paint paint = ((BaseCommand) command).paint;
		assertPaintEquals(this.paint, paint);
	}

	@UiThreadTest
	@Test
	public void testShouldAddCommandOnTabWithinTolleranceEvent() {
		PointF tab1 = new PointF(0, 0);
		PointF tab2 = new PointF(MOVE_TOLERANCE - 0.1f, 0);
		PointF tab3 = new PointF(MOVE_TOLERANCE - 0.1f, MOVE_TOLERANCE - 0.1f);

		boolean returnValue1 = toolToTest.handleDown(tab1);
		boolean returnValue2 = toolToTest.handleMove(tab2);
		boolean returnValue3 = toolToTest.handleUp(tab3);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		assertTrue(returnValue3);
		assertEquals(1, commandManagerStub.getCallCount("commitCommandToLayer"));
		Command command = (Command) commandManagerStub.getCall("commitCommandToLayer", 0).get(1);
		assertTrue(command instanceof PointCommand);
		PointF point = ((PointCommand) command).point;
		assertEquals(tab1, point);
		Paint paint = ((BaseCommand) command).paint;
		assertPaintEquals(this.paint, paint);
	}

	@UiThreadTest
	@Test
	public void testShouldAddPathCommandOnMultipleMovesWithinTolleranceEvent() {
		PointF tab1 = new PointF(7, 7);
		PointF tab2 = new PointF(7, MOVE_TOLERANCE - 0.1f);
		PointF tab3 = new PointF(7, 7);
		PointF tab4 = new PointF(7, -MOVE_TOLERANCE + 0.1f);
		PointF tab5 = new PointF(7, 7);

		toolToTest.handleDown(tab1);
		toolToTest.handleMove(tab2);
		toolToTest.handleMove(tab3);
		toolToTest.handleMove(tab4);
		toolToTest.handleUp(tab5);

		assertEquals(1, commandManagerStub.getCallCount("commitCommandToLayer"));
		Command command = (Command) commandManagerStub.getCall("commitCommandToLayer", 0).get(1);
		assertTrue(command instanceof PathCommand);
	}

	@UiThreadTest
	@Test
	public void testShouldRewindPathOnAppliedToBitmap() {
		PathStub pathStub = new PathStub();
		((DrawTool) toolToTest).pathToDraw = pathStub;

		toolToTest.resetInternalState(StateChange.RESET_INTERNAL_STATE);

		assertEquals(1, pathStub.getCallCount("rewind"));
	}

	@UiThreadTest
	@Test
	public void testShouldReturnBlackForForTopParameterButton() {
		int color = getAttributeButtonColor();
		assertEquals(Color.BLACK, color);
	}

	@UiThreadTest
	@Test
	public void testShouldChangePaintFromColorPicker() {
		//toolToTest = new DrawTool(getActivity(), ToolType.BRUSH);
		toolToTest.setDrawPaint(paint);
		ColorPickerDialog colorPicker = ColorPickerDialog.getInstance();
		ArrayList<OnColorPickedListener> colorPickerListener = colorPicker.onColorPickedListener;

		for (OnColorPickedListener onColorPickedListener : colorPickerListener) {
			onColorPickedListener.colorChanged(Color.RED);
			// check if colorpicker listener is a tool, can also be color Button
			if (onColorPickedListener instanceof Tool) {
				assertEquals(Color.RED, toolToTest.getDrawPaint().getColor());
			}
		}
	}

	@UiThreadTest
	@Test
	public void testShouldChangePaintFromBrushPicker() throws NoSuchFieldException, IllegalAccessException {
		toolToTest.setupToolOptions();
		toolToTest.setDrawPaint(this.paint);
		BrushPickerView brushPicker = ((DrawTool) toolToTest).brushPickerView;
		ArrayList<BrushPickerView.OnBrushChangedListener> brushPickerListener = brushPicker.brushChangedListener;

		for (BrushPickerView.OnBrushChangedListener onBrushChangedListener : brushPickerListener) {
			onBrushChangedListener.setCap(Paint.Cap.ROUND);
			onBrushChangedListener.setStroke(15);
			assertEquals(Paint.Cap.ROUND, toolToTest.getDrawPaint().getStrokeCap());
			assertEquals(15f, toolToTest.getDrawPaint().getStrokeWidth(), Double.MIN_VALUE);
		}
	}
}
