/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.PointF;
import android.support.test.annotation.UiThreadTest;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BaseToolWithRectangleShapeToolTest extends BaseToolTest {

	private static final String TOOL_MEMBER_WIDTH = "boxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "boxHeight";
	private static final String TOOL_MEMBER_POSITION = "toolPosition";
	private static final String TOOL_MEMBER_RESPECT_BOUNDS = "respectImageBounds";
	private static final String TOOL_MEMBER_BOX_RESIZE_MARGIN = "DEFAULT_BOX_RESIZE_MARGIN";
	private static final String TOOL_MEMBER_ROTATION = "boxRotation";
	private static final String TOOL_MEMBER_ROTATION_ENABLED = "rotationEnabled";
	private static final String TOOL_MEMBER_ROTATION_SYMBOL_DISTANCE = "rotationSymbolDistance";
	private static final int RESIZE_MOVE_DISTANCE = 50;

	private float screenWidth = 1;
	private float screenHeight = 1;
	private PointF toolPosition;
	private float rectWidth;
	private float rectHeight;
	private float rotation;
	private float symbolDistance;

	public BaseToolWithRectangleShapeToolTest() {
		super();
	}

	@UiThreadTest
	@Override
	@Before
	public void setUp() throws Exception {
		toolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.SHAPE);
		super.setUp();

		screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
		toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);
		rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		rotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		symbolDistance = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION_SYMBOL_DISTANCE);
	}

	@UiThreadTest
	@Test
	public void testResizeRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		// resize bigger top left only on Y-coordinate
		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX;
		float dragFromY = rectPosition.y - rectHeight / 2;
		float dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true, true);

		// resize smaller top left only on Y-coordinate
		dragToX = dragFromX;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false, true);

		// resize bigger top left only on X-coordinate
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true, true);

		// resize smaller top left only on X-coordinate
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false, true);

		// resize bigger top center
		dragFromX = rectPosition.x;
		dragToX = dragFromX;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, true, false);

		// resize smaller top center;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, false, false);

		// resize bigger top right
		dragFromX = rectPosition.x + rectWidth / 2;
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE / 2;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true, true);

		// resize smaller top right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE / 2;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false, true);

		// resize bigger center right
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, true, false);

		// resize smaller center right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, false, false);

		// resize bigger bottom right
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + rectHeight / 2;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true, true);

		// resize smaller bottom right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false, true);

		// resize bigger bottom center
		dragFromX = rectPosition.x;
		dragToX = dragFromX;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, true, false);

		// resize smaller bottom center
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, false, false);

		// resize bigger bottom left only on Y-coordinate
		dragFromX = rectPosition.x - rectWidth / 2;
		dragFromY = rectPosition.y + rectHeight / 2;
		dragToX = dragFromX;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true, true);

		// resize smaller bottom left only on Y-coordinate
		dragToX = dragFromX;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false, true);

		// resize bigger bottom left only on X-coordinate
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true, true);

		// resize smaller bottom left only on X-coordinate
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false, true);

		// resize bigger center left
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, true, false);

		// resize smaller center left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, false, false);
	}

	@UiThreadTest
	@Test
	public void testResizeRectangleMinimumSizeBiggerThanMargin() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX + rectWidth + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2;
		float dragToY = dragFromY + rectHeight + RESIZE_MOVE_DISTANCE;

		toolToTest.handleDown(new PointF(dragFromX, dragFromY));
		toolToTest.handleMove(new PointF(dragToX, dragToY));
		toolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		float boxResizeMargin = (Integer) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_BOX_RESIZE_MARGIN);

		assertTrue("new width should be bigger or equal to the resize margin", newWidth >= boxResizeMargin);
		assertTrue("new height should be bigger or equal to the resize margin", newHeight >= boxResizeMargin);
	}

	@UiThreadTest
	@Test
	public void testMoveRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		float dragFromX = rectPosition.x;
		float dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y;
		float dragToY = dragFromY + RESIZE_MOVE_DISTANCE;

		toolToTest.handleDown(new PointF(dragFromX, dragFromY));
		toolToTest.handleMove(new PointF(dragToX, dragToY));
		toolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		assertTrue("width should be the same", rectWidth == newWidth);
		assertTrue("height should be the same", rectHeight == newHeight);
		assertTrue("position should have moved", (newPosition.x == dragToX) && (newPosition.y == dragToY));
	}

	@UiThreadTest
	@Test
	public void testMoveRectangleRespectBorders() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_RESPECT_BOUNDS, true);
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		float dragFromX = rectPosition.x;
		float dragToX = rectPosition.x - screenWidth;
		float dragFromY = rectPosition.y;
		float dragToY = rectPosition.y - screenHeight;

		toolToTest.handleDown(new PointF(dragFromX, dragFromY));
		toolToTest.handleMove(new PointF(dragToX, dragToY));
		toolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		assertEquals("old width should be same as new width", rectWidth, newWidth, Double.MIN_VALUE);
		assertEquals("old height should be same as new height", rectHeight, newHeight, Double.MIN_VALUE);
		assertEquals("rectangle should be top left: x ", rectWidth / 2, newPosition.x, Double.MIN_VALUE);
		assertEquals("rectangle should be top left: y", rectHeight / 2, newPosition.y, Double.MIN_VALUE);
	}

	@UiThreadTest
	@Test
	public void testRectangleSizeMaximumWhenZoomed() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float scale = 0.8f;
		PaintroidApplication.perspective.setScale(scale);

		toolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.SHAPE);

		float width = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float height = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);

		assertEquals("Width and Height should be the same with activating Rectangletool on low zoom out", width, height, Double.MIN_VALUE);

		scale = 0.15f;
		PaintroidApplication.perspective.setScale(scale);

		toolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.SHAPE);

		width = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_WIDTH);
		height = (Float) PrivateAccess
				.getMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_HEIGHT);

		assertNotSame(
				"With zooming out a lot, height and width should not be the same anymore and adjust the ratio to the drawinSurface",
				width, height);

		scale = 0.1f;
		PaintroidApplication.perspective.setScale(scale);

		toolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.SHAPE);

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);

		assertEquals(
				"After zooming out a little more (from already beeing zoomed out a lot), width should stay the same",
				newWidth, width, Double.MIN_VALUE);

		assertEquals(
				"After zooming out a little more (from already beeing zoomed out a lot), height should stay the same",
				newHeight, height, Double.MIN_VALUE);
	}

	@UiThreadTest
	@Test
	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel2() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		float scale = 1f;
		PaintroidApplication.perspective.setScale(scale);
		BaseToolWithRectangleShape rectTool1 = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.BRUSH);
		Float rectWidthZoom1 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool1,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom1 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool1,
				TOOL_MEMBER_HEIGHT);
		scale = 2f;
		PaintroidApplication.perspective.setScale(scale);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			fail(e.getMessage());
		}

		BaseToolWithRectangleShape rectTool2 = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.BRUSH);
		Float rectWidthZoom2 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool2,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom2 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool2,
				TOOL_MEMBER_HEIGHT);
		assertTrue("rectangle should be smaller with scale 2",
				(rectWidthZoom1 > rectWidthZoom2)
						&& (rectHeightZoom1 > rectHeightZoom2));
	}

	@UiThreadTest
	@Test
	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel05() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		float scale = 1f;
		PaintroidApplication.perspective.setScale(scale);

		BaseToolWithRectangleShape rectTool1 = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.BRUSH);
		Float rectWidthZoom1 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool1,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom1 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool1,
				TOOL_MEMBER_HEIGHT);
		scale = 0.5f;
		PaintroidApplication.perspective.setScale(scale);
		BaseToolWithRectangleShape rectTool05 = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.BRUSH);
		Float rectWidthZoom05 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool05,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom05 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool05,
				TOOL_MEMBER_HEIGHT);
		assertTrue("rectangle should be bigger with scale 0.5",
				(rectWidthZoom1 < rectWidthZoom05)
						&& (rectHeightZoom1 < rectHeightZoom05));
	}

	@UiThreadTest
	@Test
	public void testRotateRectangleRight() throws NoSuchFieldException, IllegalAccessException {

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_ROTATION_ENABLED, true);
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF topLeftRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance / 2,
				toolPosition.y - rectHeight / 2 - symbolDistance / 2);

		// try rotate right
		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(new PointF(screenWidth / 2, topLeftRotationPoint.y));
		toolToTest.handleUp(new PointF(screenWidth / 2, topLeftRotationPoint.y));
		float newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be bigger after rotating.", rotation < newRotation);
	}

	@UiThreadTest
	@Test
	public void testRotateRectangleLeft() throws NoSuchFieldException, IllegalAccessException {

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_ROTATION_ENABLED, true);
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF topLeftRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance / 2,
				toolPosition.y - rectHeight / 2 - symbolDistance / 2);

		// try rotate left
		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(new PointF(topLeftRotationPoint.x, screenHeight / 2));
		toolToTest.handleUp(new PointF(topLeftRotationPoint.x, screenHeight / 2));
		float newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);
	}

	@UiThreadTest
	@Test
	public void testRotateRectangle() throws NoSuchFieldException, IllegalAccessException {

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_ROTATION_ENABLED, true);
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
		float newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be 90 degree.", newRotation == 90);

		currentPosition = newPosition;
		newPosition = new PointF(bottomRightRotationPoint.x, bottomRightRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be 180 degree.", newRotation == 180);

		currentPosition = newPosition;
		newPosition = new PointF(bottomLeftRotationPoint.x, bottomLeftRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be -90 degree.", newRotation == -90);

		currentPosition = newPosition;
		newPosition = new PointF(topLeftRotationPoint.x, topLeftRotationPoint.y);
		toolToTest.handleDown(currentPosition);
		toolToTest.handleMove(newPosition);
		toolToTest.handleUp(newPosition);
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be 0 degree.", newRotation == 0);
	}

	@UiThreadTest
	@Test
	public void testRotateOnlyNearCorner() throws NoSuchFieldException, IllegalAccessException {

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_ROTATION_ENABLED, true);
		toolToTest.handleDown(toolPosition);
		toolToTest.handleUp(toolPosition);

		PointF noRotationPoint = new PointF(toolPosition.x - rectWidth / 2 - symbolDistance,
				toolPosition.y - rectHeight / 2 - symbolDistance);
		PointF topLeftRotationPoint = new PointF(noRotationPoint.x + 1, noRotationPoint.y + 1);
		PointF destinationPoint = new PointF(noRotationPoint.x + 10, noRotationPoint.y);

		toolToTest.handleDown(noRotationPoint);
		toolToTest.handleMove(destinationPoint);
		float newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rectangle should not rotate.", newRotation == 0);
		toolToTest.handleMove(noRotationPoint);
		toolToTest.handleUp(noRotationPoint);

		toolToTest.handleDown(topLeftRotationPoint);
		toolToTest.handleMove(destinationPoint);
		toolToTest.handleUp(destinationPoint);
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_ROTATION);
		assertFalse("Rectangle should rotate.", newRotation == 0);
	}

	private void doResize(float dragFromX, float dragToX, float dragFromY, float dragToY, boolean resizeWidth,
			boolean resizeHeight, boolean resizeBigger, boolean isCorner) throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		PointF pointDown = new PointF(dragFromX, dragFromY);
		PointF pointMoveTo = new PointF(dragToX, dragToY);

		toolToTest.handleDown(pointDown);
		toolToTest.handleMove(pointMoveTo);
		toolToTest.handleUp(pointMoveTo);

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, toolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, toolToTest,
				TOOL_MEMBER_POSITION);

		if (resizeBigger) {
			if (resizeWidth) {
				assertTrue("new width should be bigger", newWidth > rectWidth);
			} else {
				assertTrue("height should not have changed", newWidth == rectWidth);
			}
			if (resizeHeight) {
				assertTrue("new width should be bigger", newHeight > rectHeight);
			} else {
				assertTrue("height should not have changed", newHeight == rectHeight);
			}
			if (isCorner) {
				assertTrue("resizing should be in aspect ratio", newHeight == newWidth);
			}
		} else {
			if (resizeWidth) {
				assertTrue("new height should be smaller", newWidth < rectWidth);
			} else {
				assertTrue("height should not have changed", newWidth == rectWidth);
			}
			if (resizeHeight) {
				assertTrue("new width should be smaller", newHeight < rectHeight);
			} else {
				assertTrue("height should not have changed", newHeight == rectHeight);
			}
			if (isCorner) {
				assertTrue("resizing should be in aspect ratio", newHeight == newWidth);
			}
		}

		assertTrue("position should be the same", (newPosition.x == rectPosition.x)
				&& (newPosition.y == rectPosition.y));
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_WIDTH, rectWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, toolToTest, TOOL_MEMBER_HEIGHT, rectHeight);
	}

	private class BaseToolWithRectangleShapeImpl extends BaseToolWithRectangleShape {

		BaseToolWithRectangleShapeImpl(Context context, ToolType toolType) {
			super(context, toolType);
		}

		@Override
		public void resetInternalState() {
		}

		@Override
		protected void onClickInBox() {
			drawingBitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}
	}
}
