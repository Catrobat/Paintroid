/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.junit.stubs.BrushPickerStub;
import org.catrobat.paintroid.test.junit.stubs.ColorPickerStub;
import org.catrobat.paintroid.test.junit.stubs.CommandManagerStub;
import org.catrobat.paintroid.test.junit.stubs.DrawingSurfaceStub;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.StampTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Paint;
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;

public class StampToolTest extends ActivityInstrumentationTestCase2<MainActivity> {

	protected Tool mTool;
	protected CommandManagerStub mCommandHandlerStub;
	protected Paint mPaint;
	protected ColorPickerStub mColorPickerStub;
	protected BrushPickerStub mBrushPickerStub;
	private DrawingSurfaceStub mDrawingSurfaceStub;
	private static final String STAMP_TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String STAMP_TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String STAMP_TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String STAMP_TOOL_MEMBER_BOX_RESIZE_MARGIN = "DEFAULT_BOX_RESIZE_MARGIN";
	private static final String STAMP_TOOL_MEMBER_ROTATION = "mBoxRotation";
	private static final String STAMP_TOOL_MEMBER_DRAWING_SURFACE = "mDrawingSurface";

	private static final int BITMAP_X_Y_LENGTH = 500;
	private static final int RESIZE_MOVE_DISTANCE = 50;
	private static final int DRAG_STEPS = 10;
	private static final int X_OFFSET = 5;
	private static final int Y_OFFSET = 40;

	private float mScreenWidth = 1;
	private float mScreenHeight = 1;

	public StampToolTest() {
		super(MainActivity.class);
	}

	@Override
	@Before
	protected void setUp() throws Exception {
		Utils.doWorkaroundSleepForDrawingSurfaceThreadProblem();
		super.setUp();
		// mPaint = new Paint();
		// mPaint.setColor(Color.BLACK);
		// mPaint.setStrokeCap(Cap.ROUND);
		// mPaint.setStrokeWidth(15);
		mCommandHandlerStub = new CommandManagerStub();
		mDrawingSurfaceStub = new DrawingSurfaceStub();
		mDrawingSurfaceStub.setBitmap(Bitmap.createBitmap(BITMAP_X_Y_LENGTH, BITMAP_X_Y_LENGTH, Config.ALPHA_8));
		mTool = new StampTool(getActivity(), Tool.ToolType.STAMP);
		mScreenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
		mScreenHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

		// mTool.setDrawPaint(mPaint);
		// mColorPickerStub = new ColorPickerStub(getActivity(), null);
		// PrivateAccess.setMemberValue(BaseTool.class, mTool, "mColorPickerDialog", mColorPickerStub);
		// mBrushPickerStub = new BrushPickerStub(getActivity(), null, mPaint);
		// PrivateAccess.setMemberValue(BaseTool.class, mTool, "mBrushPickerDialog", mBrushPickerStub);
		PaintroidApplication.COMMAND_MANAGER = mCommandHandlerStub;
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testResizeStampToolBox() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mTool,
				STAMP_TOOL_MEMBER_POSITION);

		int statusbarHeight = 0;// Utils.getStatusbarHeigt(getActivity());

		// resize bigger top left
		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2 + statusbarHeight;
		float dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller top left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger top center
		dragFromX = rectPosition.x;
		dragToX = dragFromX;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, true);

		// resize smaller top center;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, false);

		// resize bigger top right
		dragFromX = rectPosition.x + rectWidth / 2;
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller top right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger center right
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + statusbarHeight;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, true);

		// resize smaller center right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, false);

		// resize bigger bottom right
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + rectHeight / 2 + statusbarHeight;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller bottom right
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger bottom center
		dragFromX = rectPosition.x;
		dragToX = dragFromX;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, true);

		// resize smaller bottom center
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, false, true, false);

		// resize bigger bottom left
		dragFromX = rectPosition.x - rectWidth / 2;
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, true);

		// resize smaller bottom left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		dragToY = dragFromY - RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, true, false);

		// resize bigger center left
		dragToX = dragFromX - RESIZE_MOVE_DISTANCE;
		dragFromY = rectPosition.y + statusbarHeight;
		dragToY = dragFromY;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, true);

		// resize smaller center left
		dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		doResize(dragFromX, dragToX, dragFromY, dragToY, true, false, false);
	}

	public void testResizeRectangleMinimumSizeBiggerThanMargin() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		StampTool stampTool = (StampTool) mTool;
		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		int statusbarHeight = org.catrobat.paintroid.test.Utils.getStatusbarHeigt(getActivity());

		float dragFromX = rectPosition.x - rectWidth / 2;
		float dragToX = dragFromX + rectWidth + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y - rectHeight / 2 + statusbarHeight;
		float dragToY = dragFromY + rectHeight + RESIZE_MOVE_DISTANCE;

		mTool.handleDown(new PointF(dragFromX, dragFromY));
		mTool.handleMove(new PointF(dragToX, dragToY));
		mTool.handleUp(new PointF(dragToX, dragToY));
		// mSolo.drag(dragFromX, dragToX, dragFromY, dragToY, DRAG_STEPS);

		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		float boxResizeMargin = (Integer) PrivateAccess.getMemberValue(StampTool.class, stampTool,
				STAMP_TOOL_MEMBER_BOX_RESIZE_MARGIN);

		assertTrue("new width should be bigger or equal to the resize margin", newWidth >= boxResizeMargin);
		assertTrue("new height should be bigger or equal to the resize margin", newHeight >= boxResizeMargin);

	}

	public void testMoveStampRectangle() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		StampTool stampTool = (StampTool) mTool;
		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		int statusbarHeight = org.catrobat.paintroid.test.Utils.getStatusbarHeigt(getActivity());
		float dragFromX = rectPosition.x;
		float dragToX = dragFromX + RESIZE_MOVE_DISTANCE;
		float dragFromY = rectPosition.y + statusbarHeight;
		float dragToY = dragFromY + RESIZE_MOVE_DISTANCE;

		mTool.handleDown(new PointF(dragFromX, dragFromY));
		mTool.handleMove(new PointF(dragToX, dragToY));
		mTool.handleUp(new PointF(dragToX, dragToY));
		// mSolo.drag(dragFromX, dragToX, dragFromY, dragToY, DRAG_STEPS);

		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		assertTrue("width should be the same", rectWidth == newWidth);
		assertTrue("height should be the same", rectHeight == newHeight);
		assertTrue("position should have moved", (newPosition.x == dragToX)
				&& (newPosition.y == dragToY - statusbarHeight));
	}

	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel2() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float scale = 1f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);
		StampTool stampToolZoom1 = new StampTool(getActivity(), ToolType.STAMP);
		Float rectWidthZoom1 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom1,
				STAMP_TOOL_MEMBER_WIDTH);
		Float rectHeightZoom1 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom1,
				STAMP_TOOL_MEMBER_HEIGHT);
		scale = 2f;

		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StampTool stampToolZoom2 = new StampTool(getActivity(), ToolType.STAMP);
		Float rectWidthZoom2 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom2,
				STAMP_TOOL_MEMBER_WIDTH);
		Float rectHeightZoom2 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom2,
				STAMP_TOOL_MEMBER_HEIGHT);

		assertTrue("rectangle should be smaller with scale 2",
				(rectWidthZoom1.floatValue() > rectWidthZoom2.floatValue())
						&& (rectHeightZoom1.floatValue() > rectHeightZoom2.floatValue()));
	}

	public void testRectangleSizeChangeWhenZoomedLevel1ToLevel05() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float scale = 1f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);

		StampTool stampToolZoom1 = new StampTool(getActivity(), ToolType.STAMP);
		Float rectWidthZoom1 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom1,
				STAMP_TOOL_MEMBER_WIDTH);
		Float rectHeightZoom1 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom1,
				STAMP_TOOL_MEMBER_HEIGHT);

		scale = 0.5f;
		PaintroidApplication.CURRENT_PERSPECTIVE.setScale(scale);

		StampTool stampToolZoom05 = new StampTool(getActivity(), ToolType.STAMP);
		Float rectWidthZoom05 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom05,
				STAMP_TOOL_MEMBER_WIDTH);
		Float rectHeightZoom05 = (Float) PrivateAccess.getMemberValue(StampTool.class, stampToolZoom05,
				STAMP_TOOL_MEMBER_HEIGHT);

		assertTrue("rectangle should be bigger with scale 0.5",
				(rectWidthZoom1.floatValue() < rectWidthZoom05.floatValue())
						&& (rectHeightZoom1.floatValue() < rectHeightZoom05.floatValue()));

	}

	public void testStampToolRotation() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		StampTool stampTool = (StampTool) mTool;
		PointF position = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, stampTool,
				STAMP_TOOL_MEMBER_POSITION);

		float rotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);

		mTool.handleDown(position);
		mTool.handleUp(position);
		// try rotate right
		mTool.handleDown(new PointF(X_OFFSET, Y_OFFSET));
		mTool.handleMove(new PointF(mScreenWidth / 2, Y_OFFSET + 30));
		mTool.handleUp(new PointF(mScreenWidth / 2, Y_OFFSET + 30));
		float newRotation = (Float) PrivateAccess
				.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be bigger after rotating.", rotation < newRotation);

		// try rotate left
		rotation = newRotation;
		mTool.handleDown(new PointF(mScreenWidth / 2, Y_OFFSET));
		mTool.handleMove(new PointF(X_OFFSET, Y_OFFSET + 30));
		mTool.handleUp(new PointF(X_OFFSET, Y_OFFSET + 30));
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// try rotate even more left (start from bottom of screen)
		rotation = newRotation;
		mTool.handleDown(new PointF(mScreenWidth - X_OFFSET, mScreenWidth / 2));
		mTool.handleMove(new PointF(mScreenWidth - X_OFFSET, Y_OFFSET));
		mTool.handleUp(new PointF(mScreenWidth - X_OFFSET, Y_OFFSET));
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation > newRotation);

		// and now a lot to the right
		rotation = newRotation;
		mTool.handleDown(new PointF(X_OFFSET, mScreenHeight / 2));
		mTool.handleMove(new PointF(X_OFFSET * 2, Y_OFFSET));
		mTool.handleUp(new PointF(X_OFFSET * 2, Y_OFFSET));

		mTool.handleDown(new PointF(mScreenWidth / 2, Y_OFFSET));
		mTool.handleMove(new PointF(mScreenWidth - X_OFFSET * 2, Y_OFFSET * 2));
		mTool.handleUp(new PointF(mScreenWidth - X_OFFSET * 2, Y_OFFSET * 2));
		newRotation = (Float) PrivateAccess.getMemberValue(StampTool.class, stampTool, STAMP_TOOL_MEMBER_ROTATION);
		assertTrue("Rotation value should be smaller after rotating.", rotation < newRotation);
	}

	private void doResize(float dragFromX, float dragToX, float dragFromY, float dragToY, boolean resizeWidth,
			boolean resizeHeight, boolean resizeBigger) throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		float rectWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_WIDTH);
		float rectHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF rectPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mTool,
				STAMP_TOOL_MEMBER_POSITION);

		PointF pointDown = new PointF(dragFromX, dragFromY);
		PointF pointMoveTo = new PointF(dragToX, dragToY);
		mTool.handleDown(pointDown);
		mTool.handleMove(pointMoveTo);
		mTool.handleUp(pointMoveTo);
		float newWidth = (Float) PrivateAccess.getMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_WIDTH);
		float newHeight = (Float) PrivateAccess.getMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_HEIGHT);
		PointF newPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class, mTool,
				STAMP_TOOL_MEMBER_POSITION);

		if (resizeBigger) {
			if (resizeWidth) {
				assertTrue("new width should be bigger", newWidth > rectWidth);
			} else {
				assertTrue("height should not have changed", newWidth == rectWidth);
			}
			if (resizeHeight) {
				assertTrue("new width should be bigger", newHeight > rectHeight);
			} else {
				assertTrue("height should not have chaned", newHeight == rectHeight);
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
				assertTrue("height should not have chaned", newHeight == rectHeight);
			}
		}

		assertTrue("position should be the same", (newPosition.x == rectPosition.x)
				&& (newPosition.y == rectPosition.y));

		PrivateAccess.setMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_WIDTH, rectWidth);
		PrivateAccess.setMemberValue(StampTool.class, mTool, STAMP_TOOL_MEMBER_HEIGHT, rectHeight);
	}
}
