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
import org.catrobat.paintroid.command.implementation.PathCommand;
import org.catrobat.paintroid.command.implementation.PointCommand;
import org.catrobat.paintroid.listener.BrushPickerView;
import org.catrobat.paintroid.test.junit.stubs.PathStub;
import org.catrobat.paintroid.tools.Tool.StateChange;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.catrobat.paintroid.tools.implementation.DrawTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.List;

import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPaintEquals;
import static org.catrobat.paintroid.test.utils.PaintroidAsserts.assertPathEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(AndroidJUnit4.class)
public class DrawToolTests {
	private static final float MOVE_TOLERANCE = BaseTool.MOVE_TOLERANCE;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public MockitoRule mockito = MockitoJUnit.rule();

	@Mock
	private CommandManager commandManager;

	private DrawTool toolToTest;
	private Paint paint;

	@UiThreadTest
	@Before
	public void setUp() {
		toolToTest = new DrawTool(activityTestRule.getActivity(), ToolType.BRUSH);
		paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(BaseTool.STROKE_25);
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

		assertEquals(ToolType.BRUSH, toolType);
	}

	@UiThreadTest
	@Test
	public void testShouldReturnPaint() {
		toolToTest.setDrawPaint(paint);
		Paint drawPaint = BaseTool.BITMAP_PAINT;
		assertEquals(paint.getColor(), drawPaint.getColor());
		assertEquals(paint.getStrokeWidth(), drawPaint.getStrokeWidth(), Double.MIN_VALUE);
		assertEquals(paint.getStrokeCap(), drawPaint.getStrokeCap());
		assertEquals(paint.getShader(), drawPaint.getShader());
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnDownEvent() {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		boolean returnValue = toolToTest.handleDown(event);

		assertTrue(returnValue);
		Path stub = pathStub.getStub();
		verify(stub).moveTo(event.x, event.y);
	}

	@UiThreadTest
	@Test
	public void testShouldNotAddCommandOnDownEvent() {
		PointF event = new PointF(0, 0);

		boolean returnValue = toolToTest.handleDown(event);

		assertTrue(returnValue);
		verify(commandManager, never()).addCommand(any(Command.class));
	}

	@UiThreadTest
	@Test
	public void testShouldNotStartPathIfNoCoordinateOnDownEvent() {
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		boolean returnValue = toolToTest.handleDown(null);

		assertFalse(returnValue);
		Path stub = pathStub.getStub();
		verify(stub, never()).reset();
		verify(stub, never()).moveTo(anyFloat(), anyFloat());
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnMoveEvent() {
		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(5, 6);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		toolToTest.handleDown(event1);
		boolean returnValue = toolToTest.handleMove(event2);

		assertTrue(returnValue);
		Path stub = pathStub.getStub();
		verify(stub).moveTo(anyFloat(), anyFloat());
		verify(stub).quadTo(event1.x, event1.y, event2.x, event2.y);
	}

	@UiThreadTest
	@Test
	public void testShouldNotAddCommandOnMoveEvent() {
		PointF event = new PointF(0, 0);

		toolToTest.handleDown(event);
		boolean returnValue = toolToTest.handleMove(event);

		assertTrue(returnValue);
		verify(commandManager, never()).addCommand(any(Command.class));
	}

	@UiThreadTest
	@Test
	public void testShouldNotMovePathIfNoCoordinateOnMoveEvent() {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		toolToTest.handleDown(event);
		boolean returnValue = toolToTest.handleMove(null);

		assertFalse(returnValue);
		verify(pathStub.getStub(), never()).quadTo(anyFloat(), anyFloat(), anyFloat(), anyFloat());
	}

	@UiThreadTest
	@Test
	public void testShouldMovePathOnUpEvent() {

		PointF event1 = new PointF(0, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE, MOVE_TOLERANCE);
		PointF event3 = new PointF(MOVE_TOLERANCE * 2, -MOVE_TOLERANCE);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		toolToTest.handleDown(event1);
		toolToTest.handleMove(event2);
		boolean returnValue = toolToTest.handleUp(event3);

		assertTrue(returnValue);
		Path stub = pathStub.getStub();
		verify(stub).moveTo(anyFloat(), anyFloat());
		verify(stub).quadTo(anyFloat(), anyFloat(), anyFloat(), anyFloat());
		verify(stub).lineTo(event3.x, event3.y);
	}

	@UiThreadTest
	@Test
	public void testShouldNotMovePathIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		toolToTest.handleDown(event);
		toolToTest.handleMove(event);
		boolean returnValue = toolToTest.handleUp(null);

		assertFalse(returnValue);
		verify(pathStub.getStub(), never()).lineTo(anyFloat(), anyFloat());
	}

	@UiThreadTest
	@Test
	public void testShouldAddCommandOnUpEvent() {
		PointF event = new PointF(0, 0);
		PointF event1 = new PointF(MOVE_TOLERANCE + 0.1f, 0);
		PointF event2 = new PointF(MOVE_TOLERANCE + 2, MOVE_TOLERANCE + 2);
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		toolToTest.handleDown(event);
		toolToTest.handleMove(event1);
		boolean returnValue = toolToTest.handleUp(event2);

		assertTrue(returnValue);
		ArgumentCaptor<PathCommand> argument = ArgumentCaptor.forClass(PathCommand.class);
		verify(commandManager).addCommand(argument.capture());
		PathCommand command = argument.getValue();
		assertPathEquals(pathStub, command.path);
		assertPaintEquals(this.paint, command.paint);
	}

	@UiThreadTest
	@Test
	public void testShouldNotAddCommandIfNoCoordinateOnUpEvent() {
		PointF event = new PointF(0, 0);

		toolToTest.handleDown(event);
		toolToTest.handleMove(event);
		boolean returnValue = toolToTest.handleUp(null);

		assertFalse(returnValue);
		verify(commandManager, never()).addCommand(any(Command.class));
	}

	@UiThreadTest
	@Test
	public void testShouldAddCommandOnTabEvent() {
		PointF tab = new PointF(5, 5);

		boolean returnValue1 = toolToTest.handleDown(tab);
		boolean returnValue2 = toolToTest.handleUp(tab);

		assertTrue(returnValue1);
		assertTrue(returnValue2);
		ArgumentCaptor<PointCommand> argument = ArgumentCaptor.forClass(PointCommand.class);
		verify(commandManager).addCommand(argument.capture());
		PointCommand command = argument.getValue();
		assertEquals(tab, command.point);
		assertPaintEquals(this.paint, command.paint);
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
		ArgumentCaptor<PointCommand> argument = ArgumentCaptor.forClass(PointCommand.class);
		verify(commandManager).addCommand(argument.capture());
		PointCommand command = argument.getValue();
		assertEquals(tab1, command.point);
		assertPaintEquals(this.paint, command.paint);
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

		verify(commandManager).addCommand(isA(PathCommand.class));
	}

	@UiThreadTest
	@Test
	public void testShouldRewindPathOnAppliedToBitmap() {
		PathStub pathStub = new PathStub();
		toolToTest.pathToDraw = pathStub;

		toolToTest.resetInternalState(StateChange.RESET_INTERNAL_STATE);

		verify(pathStub.getStub()).rewind();
	}

	@UiThreadTest
	@Test
	public void testShouldReturnBlackForForTopParameterButton() {
		int color = getAttributeButtonColor();
		assertEquals(Color.BLACK, color);
	}

	@UiThreadTest
	@Test
	public void testShouldChangePaintFromBrushPicker() {
		toolToTest.setupToolOptions();
		toolToTest.setDrawPaint(this.paint);
		BrushPickerView brushPicker = toolToTest.brushPickerView;
		List<BrushPickerView.OnBrushChangedListener> brushPickerListener = brushPicker.brushChangedListener;

		for (BrushPickerView.OnBrushChangedListener onBrushChangedListener : brushPickerListener) {
			onBrushChangedListener.setCap(Paint.Cap.ROUND);
			onBrushChangedListener.setStroke(15);
			assertEquals(Paint.Cap.ROUND, toolToTest.getDrawPaint().getStrokeCap());
			assertEquals(15f, toolToTest.getDrawPaint().getStrokeWidth(), Double.MIN_VALUE);
		}
	}

	private int getAttributeButtonColor() {
		return BaseTool.BITMAP_PAINT.getColor();
	}
}
