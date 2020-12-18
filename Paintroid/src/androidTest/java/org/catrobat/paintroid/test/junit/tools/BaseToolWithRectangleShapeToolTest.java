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
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.util.DisplayMetrics;

import org.catrobat.paintroid.command.CommandManager;
import org.catrobat.paintroid.tools.ContextCallback;
import org.catrobat.paintroid.tools.ToolPaint;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.Workspace;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.options.ToolOptionsVisibilityController;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BaseToolWithRectangleShapeToolTest {
	private static final int RESIZE_MOVE_DISTANCE = 50;
	private int screenWidth;
	private int screenHeight;
	private float rectWidth;
	private float rectHeight;
	private float rotation;
	private float symbolDistance;

	private PointF toolPosition;

	@Mock
	private CommandManager commandManager;
	@Mock
	private ToolOptionsVisibilityController toolOptionsViewController;
	@Mock
	private ContextCallback contextCallback;
	@Mock
	private Workspace workspace;
	@Mock
	private ToolPaint toolPaint;
	@Mock
	private DisplayMetrics metrics;

	private BaseToolWithRectangleShape toolToTest;

	@Before
	public void setUp() {
		metrics.heightPixels = 1920;
		metrics.widthPixels = 1080;
		metrics.density = 1;
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		when(contextCallback.getOrientation()).thenReturn(ContextCallback.ScreenOrientation.PORTRAIT);
		when(contextCallback.getDisplayMetrics()).thenReturn(metrics);
		when(workspace.getScale()).thenReturn(1f);
		when(workspace.getWidth()).thenReturn(screenWidth);
		when(workspace.getPerspective()).thenReturn(new Perspective(screenWidth, screenHeight));
		when(workspace.getHeight()).thenReturn(screenHeight);
		when(workspace.contains(any(PointF.class))).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) {
				PointF point = invocation.getArgument(0);
				return point.x >= 0 && point.y >= 0 && point.x < screenWidth && point.y < screenHeight;
			}
		});

		toolToTest = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.BRUSH, toolPaint, workspace, commandManager);

		toolPosition = toolToTest.toolPosition;
		rectWidth = toolToTest.boxWidth;
		rectHeight = toolToTest.boxHeight;
		rotation = toolToTest.boxRotation;
		symbolDistance = toolToTest.rotationSymbolDistance;
	}

	@Test
	public void testResizeRectangleMinimumSizeBiggerThanMargin() {
		when(workspace.contains(any(PointF.class))).thenReturn(true);

		float rectWidth = toolToTest.boxWidth;
		float rectHeight = toolToTest.boxHeight;
		PointF rectPosition = toolToTest.toolPosition;

		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX + rectWidth + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2;
		float dragToY = dragFromY + rectHeight + RESIZE_MOVE_DISTANCE;

		toolToTest.handleDown(new PointF(dragFromX, dragFromY));
		toolToTest.handleMove(new PointF(dragToX, dragToY));
		toolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = toolToTest.boxWidth;
		float newHeight = toolToTest.boxHeight;
		float boxResizeMargin = BaseToolWithRectangleShape.DEFAULT_BOX_RESIZE_MARGIN;

		assertThat(newHeight, is(greaterThanOrEqualTo(boxResizeMargin)));
		assertThat(newWidth, is(greaterThanOrEqualTo(boxResizeMargin)));
	}

	@Test
	public void testMoveRectangle() {
		float rectWidth = toolToTest.boxWidth;
		float rectHeight = toolToTest.boxHeight;
		PointF rectPosition = toolToTest.toolPosition;

		float dragFromX = rectPosition.x;
		float dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y;
		float dragToY = dragFromY + RESIZE_MOVE_DISTANCE;

		toolToTest.handleDown(new PointF(dragFromX, dragFromY));
		toolToTest.handleMove(new PointF(dragToX, dragToY));
		toolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = toolToTest.boxWidth;
		float newHeight = toolToTest.boxHeight;
		PointF newPosition = toolToTest.toolPosition;

		assertEquals("width should be the same", rectWidth, newWidth, Float.MIN_VALUE);
		assertEquals("height should be the same", rectHeight, newHeight, Float.MIN_VALUE);
		assertTrue("position should have moved", (newPosition.x == dragToX) && (newPosition.y == dragToY));
	}

	@Test
	public void testRectangleSizeMaximumWhenZoomed() {

		when(workspace.getScale()).thenReturn(0.8f, 0.15f, 0.1f);

		toolToTest = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.SHAPE,
				toolPaint, workspace, commandManager);

		float width = toolToTest.boxWidth;
		float height = toolToTest.boxHeight;

		assertEquals("Width and Height should be the same with activating Rectangletool on low zoom out", width, height, Double.MIN_VALUE);

		toolToTest = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.SHAPE,
				toolPaint, workspace, commandManager);

		width = toolToTest.boxWidth;
		height = toolToTest.boxHeight;

		assertNotSame(
				"With zooming out a lot, height and width should not be the same anymore and adjust the ratio to the drawinSurface",
				width, height);

		toolToTest = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.SHAPE,
				toolPaint, workspace, commandManager);

		float newWidth = toolToTest.boxWidth;
		float newHeight = toolToTest.boxHeight;

		assertEquals(
				"After zooming out a little more (from already beeing zoomed out a lot), width should stay the same",
				newWidth, width, Double.MIN_VALUE);

		assertEquals(
				"After zooming out a little more (from already beeing zoomed out a lot), height should stay the same",
				newHeight, height, Double.MIN_VALUE);
	}

	@Test
	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel2() {
		when(workspace.getScale()).thenReturn(1f, 2f);
		BaseToolWithRectangleShape rectTool1 = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.BRUSH,
				toolPaint, workspace, commandManager);
		BaseToolWithRectangleShape rectTool2 = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.BRUSH,
				toolPaint, workspace, commandManager);

		assertTrue("rectangle should be smaller with scale 2",
				(rectTool1.boxWidth > rectTool2.boxWidth)
						&& (rectTool1.boxHeight > rectTool2.boxHeight));
	}

	@Test
	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel05() {
		when(workspace.getScale()).thenReturn(1f, 0.5f);

		BaseToolWithRectangleShape rectTool1 = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.BRUSH,
				toolPaint, workspace, commandManager);
		BaseToolWithRectangleShape rectTool05 = new BaseToolWithRectangleShapeImpl(contextCallback, toolOptionsViewController, ToolType.BRUSH,
				toolPaint, workspace, commandManager);
		assertThat(rectTool1.boxWidth, is(lessThan(rectTool05.boxWidth)));
		assertThat(rectTool1.boxHeight, is(lessThan(rectTool05.boxHeight)));
	}

	@Test
	public void testRotateRectangleRight() {

		toolToTest.rotationEnabled = true;
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF topLeftRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance / 2,
				toolPosition.y - rectHeight / 2 - symbolDistance / 2);

		// try rotate right
		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(new PointF(screenWidth / 2, topLeftRotationPoint.y));
		toolToTest.handleUp(new PointF(screenWidth / 2, topLeftRotationPoint.y));
		float newRotation = toolToTest.boxRotation;
		assertThat(newRotation, is(greaterThan(rotation)));
	}

	@Test
	public void testRotateRectangleLeft() {
		toolToTest.rotationEnabled = true;
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF topLeftRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance / 2,
				toolPosition.y - rectHeight / 2 - symbolDistance / 2);

		// try rotate left
		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(new PointF(topLeftRotationPoint.x, screenHeight / 2f));
		toolToTest.handleUp(new PointF(topLeftRotationPoint.x, screenHeight / 2f));
		float newRotation = toolToTest.boxRotation;
		assertThat(newRotation, is(lessThan(rotation)));
	}

	@Test
	public void testRotateRectangle() {

		toolToTest.rotationEnabled = true;
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF topLeftRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance / 2,
				toolPosition.y - rectHeight / 2 - symbolDistance / 2);
		PointF topRightRotationPoint = new PointF(toolPosition.x + toolPosition.x - topLeftRotationPoint.x,
				topLeftRotationPoint.y);
		PointF bottomRightRotationPoint = new PointF(topRightRotationPoint.x,
				toolPosition.y + rectHeight / 2 + symbolDistance / 2);
		PointF bottomLeftRotationPoint = new PointF(topLeftRotationPoint.x,
				bottomRightRotationPoint.y);

		PointF currentPosition = topLeftRotationPoint;
		PointF newPosition = new PointF(topRightRotationPoint.x, topRightRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		float newRotation = toolToTest.boxRotation;
		assertEquals("Rotation value should be 90 degree.", newRotation, 90, Float.MIN_VALUE);

		currentPosition = newPosition;
		newPosition = new PointF(bottomRightRotationPoint.x, bottomRightRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		newRotation = toolToTest.boxRotation;
		assertEquals("Rotation value should be 180 degree.", newRotation, 180, Float.MIN_VALUE);

		currentPosition = newPosition;
		newPosition = new PointF(bottomLeftRotationPoint.x, bottomLeftRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		newRotation = toolToTest.boxRotation;
		assertEquals("Rotation value should be -90 degree.", newRotation, -90, Float.MIN_VALUE);

		currentPosition = newPosition;
		newPosition = new PointF(topLeftRotationPoint.x, topLeftRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		newRotation = toolToTest.boxRotation;
		assertEquals("Rotation value should be 0 degree.", newRotation, 0, Float.MIN_VALUE);
	}

	@Test
	public void testRotateOnlyNearCorner() {

		toolToTest.rotationEnabled = true;
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF noRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance,
				toolPosition.y - rectHeight / 2 - symbolDistance);
		PointF topLeftRotationPoint = new PointF(noRotationPoint.x + 1, noRotationPoint.y + 1);
		PointF destinationPoint = new PointF(noRotationPoint.x + 10, noRotationPoint.y);

		toolToTest.handleDown(noRotationPoint);
		toolToTest.handleMove(destinationPoint);
		float newRotation = toolToTest.boxRotation;
		assertEquals("Rectangle should not rotate.", newRotation, 0, Float.MIN_VALUE);
		toolToTest.handleMove(noRotationPoint);
		toolToTest.handleUp(noRotationPoint);

		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(destinationPoint);
		toolToTest.handleUp(destinationPoint);
		newRotation = toolToTest.boxRotation;
		assertNotEquals("Rectangle should rotate.", newRotation, 0);
	}

	@Test
	public void testIsClickInsideBoxCalculatedCorrect() {
		PointF topLeftCorner = new PointF(toolPosition.x - rectWidth / 2 + 20,
				toolPosition.y - rectHeight / 2 + 20);

		PointF pointInRotatedRectangle = new PointF(toolPosition.x,
				toolPosition.y - rectHeight / 2);

		PointF topLeftRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance / 2,
				toolPosition.y - rectHeight / 2 - symbolDistance / 2);

		toolToTest.rotationEnabled = true;
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		assertTrue(toolToTest.boxContainsPoint(topLeftCorner));
		assertFalse(toolToTest.boxContainsPoint(pointInRotatedRectangle));

		//rotate right
		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(new PointF(screenWidth / 2, topLeftRotationPoint.y));
		toolToTest.handleUp(new PointF(screenWidth / 2, topLeftRotationPoint.y));

		assertFalse(toolToTest.boxContainsPoint(topLeftCorner));
		assertTrue(toolToTest.boxContainsPoint(pointInRotatedRectangle));
	}

	@Test
	public void testToolClicksOnTouchDownPosition() {
		float initialToolPositionX = toolToTest.toolPosition.x;
		float initialToolPositionY = toolToTest.toolPosition.y;

		toolToTest.handleDown(new PointF(initialToolPositionX, initialToolPositionY));
		toolToTest.handleMove(new PointF(initialToolPositionX + 9, initialToolPositionY + 9));
		toolToTest.handleUp(new PointF(initialToolPositionX + 9, initialToolPositionY + 9));

		assertEquals(toolToTest.toolPosition.x, initialToolPositionX, 0);
		assertEquals(toolToTest.toolPosition.y, initialToolPositionY, 0);
	}

	private class BaseToolWithRectangleShapeImpl extends BaseToolWithRectangleShape {
		private final ToolType toolType;

		BaseToolWithRectangleShapeImpl(ContextCallback contextCallback,
										ToolOptionsVisibilityController toolOptionsViewController, ToolType toolType, ToolPaint toolPaint, Workspace layerModelWrapper, CommandManager commandManager) {
			super(contextCallback, toolOptionsViewController, toolPaint, layerModelWrapper, commandManager);
			this.toolType = toolType;
		}

		@Override
		public void resetInternalState() {
		}

		@Override
		public void onClickOnButton() {
			drawingBitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}

		@Override
		public ToolType getToolType() {
			return toolType != null ? toolType : ToolType.BRUSH;
		}
	}
}
