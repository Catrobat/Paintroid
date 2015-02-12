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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.junit.Before;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.PointF;

public class BaseToolWithRectangleShapeToolTest extends BaseToolTest {

	private static final String TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String TOOL_MEMBER_RESPECT_BOUNDS = "mRespectImageBounds";
	private static final String TOOL_MEMBER_BOX_RESIZE_MARGIN = "DEFAULT_BOX_RESIZE_MARGIN";
	private static final String TOOL_MEMBER_ROTATION = "mBoxRotation";
	private static final String TOOL_MEMBER_ROTATION_ENABLED = "mRotationEnabled";
	private static final int RESIZE_MOVE_DISTANCE = 50;
	private static final int X_OFFSET = 5;
	private static final int Y_OFFSET = 40;

	private float mScreenWidth = 1;
	private float mScreenHeight = 1;

	public BaseToolWithRectangleShapeToolTest() {
		super();
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		mToolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.NONE);
		super.setUp();
		mScreenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();
	}

	public void testResizeRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
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

	public void testResizeRectangleMinimumSizeBiggerThanMargin() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);

		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX + rectWidth + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2;
		float dragToY = dragFromY + rectHeight + RESIZE_MOVE_DISTANCE;

		mToolToTest.handleDown(new PointF(dragFromX, dragFromY));
		mToolToTest.handleMove(new PointF(dragToX, dragToY));
		mToolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		float boxResizeMargin = (Integer) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_BOX_RESIZE_MARGIN);

		assertTrue("new width should be bigger or equal to the resize margin", newWidth >= boxResizeMargin);
		assertTrue("new height should be bigger or equal to the resize margin", newHeight >= boxResizeMargin);

	}

	public void testMoveRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);

		float dragFromX = rectPosition.x;
		float dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y;
		float dragToY = dragFromY + RESIZE_MOVE_DISTANCE;

		mToolToTest.handleDown(new PointF(dragFromX, dragFromY));
		mToolToTest.handleMove(new PointF(dragToX, dragToY));
		mToolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);

		assertTrue("width should be the same", rectWidth == newWidth);
		assertTrue("height should be the same", rectHeight == newHeight);
		assertTrue("position should have moved", (newPosition.x == dragToX) && (newPosition.y == dragToY));
	}

	public void testMoveRectangleRespectBorders() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, mToolToTest, TOOL_MEMBER_RESPECT_BOUNDS, true);
		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);

		float dragFromX = rectPosition.x;
		float dragToX = rectPosition.x - mScreenWidth;
		float dragFromY = rectPosition.y;
		float dragToY = rectPosition.y - mScreenHeight;

		mToolToTest.handleDown(new PointF(dragFromX, dragFromY));
		mToolToTest.handleMove(new PointF(dragToX, dragToY));
		mToolToTest.handleUp(new PointF(dragToX, dragToY));

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);

		assertEquals("old width should be same as new width", rectWidth, newWidth);
		assertEquals("old height should be same as new height", rectHeight, newHeight);
		assertEquals("rectangle should be top left: x ", rectWidth / 2, newPosition.x);
		assertEquals("rectangle should be top left: y", rectHeight / 2, newPosition.y);

	}

	public void testRectangleSizeMaximumWhenZoomed() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float scale = 0.8f;
		PaintroidApplication.perspective.setScale(scale);

		mToolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.NONE);

		float width = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float height = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);

		assertEquals("Width and Height should be the same with activating Rectangletool on low zoom out", width, height);

		scale = 0.15f;
		PaintroidApplication.perspective.setScale(scale);

		mToolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.NONE);

		width = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest, TOOL_MEMBER_WIDTH);
		height = (Float) PrivateAccess
				.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest, TOOL_MEMBER_HEIGHT);

		assertNotSame(
				"With zooming out a lot, height and width should not be the same anymore and adjust the ratio to the drawinSurface",
				width, height);

		scale = 0.1f;
		PaintroidApplication.perspective.setScale(scale);

		mToolToTest = new BaseToolWithRectangleShapeImpl(getActivity(), ToolType.NONE);

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);

		assertEquals(
				"After zooming out a little more (from already beeing zoomed out a lot), width should stay the same",
				newWidth, width);

		assertEquals(
				"After zooming out a little more (from already beeing zoomed out a lot), height should stay the same",
				newHeight, height);
	}

	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel2() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		float scale = 1f;
		PaintroidApplication.perspective.setScale(scale);
		BaseToolWithRectangleShape rectTool1 = new BaseToolWithRectangleShapeImpl(getActivity(), null);
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
			e.printStackTrace();
		}

		BaseToolWithRectangleShape rectTool2 = new BaseToolWithRectangleShapeImpl(getActivity(), null);
		Float rectWidthZoom2 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool2,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom2 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool2,
				TOOL_MEMBER_HEIGHT);
		assertTrue("rectangle should be smaller with scale 2",
				(rectWidthZoom1.floatValue() > rectWidthZoom2.floatValue())
						&& (rectHeightZoom1.floatValue() > rectHeightZoom2.floatValue()));
	}

	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel05() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		float scale = 1f;
		PaintroidApplication.perspective.setScale(scale);

		BaseToolWithRectangleShape rectTool1 = new BaseToolWithRectangleShapeImpl(getActivity(), null);
		Float rectWidthZoom1 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool1,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom1 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool1,
				TOOL_MEMBER_HEIGHT);
		scale = 0.5f;
		PaintroidApplication.perspective.setScale(scale);
		BaseToolWithRectangleShape rectTool05 = new BaseToolWithRectangleShapeImpl(getActivity(), null);
		Float rectWidthZoom05 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool05,
				TOOL_MEMBER_WIDTH);
		Float rectHeightZoom05 = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, rectTool05,
				TOOL_MEMBER_HEIGHT);
		assertTrue("rectangle should be bigger with scale 0.5",
				(rectWidthZoom1.floatValue() < rectWidthZoom05.floatValue())
						&& (rectHeightZoom1.floatValue() < rectHeightZoom05.floatValue()));

	}

	public void testRotateRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {

		PointF position = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, mToolToTest, TOOL_MEMBER_ROTATION_ENABLED, true);
		mToolToTest.handleDown(position);
		mToolToTest.handleUp(position);
		float rotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_ROTATION);
		// try rotate right
		mToolToTest.handleDown(new PointF(X_OFFSET, Y_OFFSET));
		mToolToTest.handleMove(new PointF(mScreenWidth / 2, Y_OFFSET + 30));
		mToolToTest.handleUp(new PointF(mScreenWidth / 2, Y_OFFSET + 30));
		float newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be bigger after rotating.", rotation < newRotation);

		// try rotate left
		rotation = newRotation;
		mToolToTest.handleDown(new PointF(mScreenWidth / 2, Y_OFFSET));
		mToolToTest.handleMove(new PointF(X_OFFSET, Y_OFFSET + 30));
		mToolToTest.handleUp(new PointF(X_OFFSET, Y_OFFSET + 30));
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// try rotate even more left (start from bottom of screen)
		rotation = newRotation;
		mToolToTest.handleDown(new PointF(mScreenWidth - X_OFFSET, mScreenWidth / 2));
		mToolToTest.handleMove(new PointF(mScreenWidth - X_OFFSET, Y_OFFSET));
		mToolToTest.handleUp(new PointF(mScreenWidth - X_OFFSET, Y_OFFSET));
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// and now a lot to the right
		rotation = newRotation;
		mToolToTest.handleDown(new PointF(X_OFFSET, mScreenHeight / 2));
		mToolToTest.handleMove(new PointF(X_OFFSET * 2, Y_OFFSET));
		mToolToTest.handleUp(new PointF(X_OFFSET * 2, Y_OFFSET));
		mToolToTest.handleDown(new PointF(mScreenWidth / 2, Y_OFFSET));
		mToolToTest.handleMove(new PointF(mScreenWidth - X_OFFSET * 2, Y_OFFSET * 2));
		mToolToTest.handleUp(new PointF(mScreenWidth - X_OFFSET * 2, Y_OFFSET * 2));
		newRotation = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation < newRotation);
	}

	private void doResize(float dragFromX, float dragToX, float dragFromY, float dragToY, boolean resizeWidth,
			boolean resizeHeight, boolean resizeBigger, boolean isCorner) throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {

		float rectWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
				TOOL_MEMBER_POSITION);

		PointF pointDown = new PointF(dragFromX, dragFromY);
		PointF pointMoveTo = new PointF(dragToX, dragToY);

		mToolToTest.handleDown(pointDown);
		mToolToTest.handleMove(pointMoveTo);
		mToolToTest.handleUp(pointMoveTo);

		float newWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mToolToTest,
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
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, mToolToTest, TOOL_MEMBER_WIDTH, rectWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, mToolToTest, TOOL_MEMBER_HEIGHT, rectHeight);
	}

	public void testRatioOfBoxAfterSetImage() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		float bitmapWidth = 300;
		float bitmapHeight = 200;
		float bitmapRatio = bitmapWidth / bitmapHeight;
		Bitmap bitmap = Bitmap.createBitmap((int) bitmapWidth, (int) bitmapHeight, Config.ARGB_8888);
		((BaseToolWithRectangleShape) mToolToTest).setBitmap(bitmap);

		float boxWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_WIDTH);
		float boxHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class, mToolToTest,
				TOOL_MEMBER_HEIGHT);
		float boxRatio = boxWidth / boxHeight;

		// correct floating point errors
		bitmapRatio *= 1000.0f;
		boxRatio *= 1000.0f;

		assertEquals("bitmap ratio should be box Ratio", Math.round(bitmapRatio), Math.round(boxRatio));

	}

	private class BaseToolWithRectangleShapeImpl extends BaseToolWithRectangleShape {

		public BaseToolWithRectangleShapeImpl(Context context, ToolType toolType) {
			super(context, toolType);
		}

		@Override
		public void resetInternalState() {
		}

		@Override
		protected void onClickInBox() {
			mDrawingBitmap = Bitmap.createBitmap(1, 1, Config.ALPHA_8);
		}

		@Override
		protected void drawToolSpecifics(Canvas canvas) {
		}

	}
}
