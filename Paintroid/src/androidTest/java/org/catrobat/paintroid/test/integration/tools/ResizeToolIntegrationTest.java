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

package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseToolWithRectangleShape;
import org.catrobat.paintroid.tools.implementation.BaseToolWithShape;
import org.catrobat.paintroid.tools.implementation.ResizeTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.test.FlakyTest;
import android.view.Display;

import java.util.PropertyPermission;

public class ResizeToolIntegrationTest extends BaseIntegrationTestClass {

	private static final String TOOL_MEMBER_WIDTH = "mBoxWidth";
	private static final String TOOL_MEMBER_HEIGHT = "mBoxHeight";
	private static final String TOOL_MEMBER_POSITION = "mToolPosition";
	private static final String TOOL_MEMBER_RESIZE_BOUND_LEFT = "mResizeBoundWidthXLeft";
	private static final String TOOL_MEMBER_RESIZE_BOUND_RIGHT = "mResizeBoundWidthXRight";
	private static final String TOOL_MEMBER_RESIZE_BOUND_TOP = "mResizeBoundHeightYTop";
	private static final String TOOL_MEMBER_RESIZE_BOUND_BOTTOM = "mResizeBoundHeightYBottom";

	private final int CROPPING_SLEEP_BETWEEN_FINISH_CHECK = 500;
	private final int MAXIMUM_CROPPING_TIMEOUT_COUNTS = 300;
	private final float BITMAP_DOWNSCALE_FACTOR = 0.5f;
	private final int MAXIMUM_BITMAP_SIZE_FACTOR = 4;
	private int mLineLength;
	private final int STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE = 800;	// !try multiple times with emulators and
																		// different hardware before decreasing the
																		// value!
	public enum BitmapSide {
		LEFT, TOP, RIGHT, BOTTOM
	}

	public ResizeToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		mLineLength = (mCurrentDrawingSurfaceBitmap.getWidth() / 2);
		PaintroidApplication.perspective.setScale(1.0f);

	}

	@Override
	@After
	protected void tearDown() throws Exception {
		// eat all toasts
		final int resizeToastSleepingTime = 100;
		for (int resizeToastTimeoutCounter = 0; resizeToastSleepingTime * resizeToastTimeoutCounter < TIMEOUT; resizeToastTimeoutCounter++) {
			if (mSolo.waitForText(mSolo.getString(R.string.resize_to_resize_tap_text), 1, 10)
					|| mSolo.waitForText(mSolo.getString(R.string.resize_nothing_to_resize), 1, 10))
				mSolo.sleep(resizeToastSleepingTime);
			else
				break;
		}
		super.tearDown();
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() throws SecurityException, IllegalArgumentException, InterruptedException,
			NoSuchFieldException, IllegalAccessException {
		scaleDownTestBitmap();
		standardAutoCrop();
		assertEquals("Zoom factor is wrong", 0.95f, PaintroidApplication.perspective.getScale());

		assertEquals("Left crop border should be 0", 0.0f,
				PrivateAccess.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT));
		assertEquals("Right crop border should be equal bitmap width-1",
				((float) PaintroidApplication.drawingSurface.getBitmapWidth() - 1),
				PrivateAccess.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT));
		assertEquals("Top crop border should be 0", 0.0f,
				PrivateAccess.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP));
		assertEquals("Bottom crop border should be equal bitmap height-1",
				((float) PaintroidApplication.drawingSurface.getBitmapHeight() - 1), PrivateAccess.getMemberValue(
						ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM));

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("nothing to crop text missing",
				mSolo.waitForText(mSolo.getString(R.string.resize_nothing_to_resize), 1, TIMEOUT, true));

	}

	@Test
	public void testDoCropOnEmptyBitmap() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		standardAutoCrop();
		clickOnBottomParameterTwo();

		assertEquals("Wrong width after cropping ", originalWidth,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());
	}

	@Test
	public void testDisplayResizeInformation() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();

		selectTool(ToolType.RESIZE);
		assertTrue("to resize tap text missing",
				mSolo.waitForText(mSolo.getString(R.string.resize_to_resize_tap_text), 1, TIMEOUT, true));
		assertTrue("Resize command has not finished", mSolo.waitForDialogToClose());

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("nothing to resize text missing",
				mSolo.waitForText(mSolo.getString(R.string.resize_nothing_to_resize), 1, TIMEOUT, true));
	}

	@Test
	public void testMoveCroppingBordersOnEmptyBitmapAndDoCrop() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		standardAutoCrop();

		int resizeWidth = originalWidth / 2;
		int resizeHeight = originalHeight / 2;
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_WIDTH, originalWidth - resizeWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_HEIGHT, originalHeight - resizeHeight);

		clickOnBottomParameterTwo();

		assertEquals("Wrong width after cropping ", originalWidth - resizeWidth,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight - resizeHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());
	}

	@Test
	public void testIfOnePixelIsFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		clickOnBottomParameterTwo();

		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getPixel(new PointF(0, 0)));
	}

	@FlakyTest(tolerance = 3)
	public void testIfMultiplePixelAreFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		mCurrentDrawingSurfaceBitmap.setPixel(1, 1, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight - 1, Color.BLUE);
		assertEquals("Wrong color on bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getPixel(new PointF(1, 1)));
		assertEquals("Wrong color color on bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getPixel(new PointF(originalWidth - 1, originalHeight - 1)));

		standardAutoCrop();
		mSolo.sleep(200);
		clickOnBottomParameterTwo();
		assertEquals("Wrong width after cropping ", originalWidth - 1,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 1,
				PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getPixel(new PointF(0, 0)));
	}

	@FlakyTest(tolerance = 3)
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth / 2, 0, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(0, originalHeight / 2, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight / 2, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth / 2, originalHeight - 1, Color.BLUE);
		mSolo.sleep(200);

		standardAutoCrop();

		clickOnBottomParameterTwo();
		assertEquals("Wrong width after cropping ", originalWidth, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());
	}

	@FlakyTest(tolerance = 4)
	public void testIfClickOnCanvasCrops() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		scaleDownTestBitmap();
		mCurrentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		int drawingSurfaceOriginalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int drawingSurfaceOriginalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		mSolo.sleep(500);
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			mCurrentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		mSolo.sleep(500);
		standardAutoCrop();

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Width changed:", drawingSurfaceOriginalWidth,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Height did not change:", drawingSurfaceOriginalHeight - 1,
				PaintroidApplication.drawingSurface.getBitmapHeight());

		drawingSurfaceOriginalWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		drawingSurfaceOriginalHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			mCurrentDrawingSurfaceBitmap.setPixel(indexWidth, drawingSurfaceOriginalHeight - 1, Color.TRANSPARENT);
		}
		mSolo.sleep(500);
		clickOnBottomParameterOne();
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Width changed:", drawingSurfaceOriginalWidth,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Height did not change:", drawingSurfaceOriginalHeight - 1,
				PaintroidApplication.drawingSurface.getBitmapHeight());

		drawingSurfaceOriginalWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		drawingSurfaceOriginalHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");
		for (int indexHeight = 0; indexHeight < drawingSurfaceOriginalHeight; indexHeight++) {
			mCurrentDrawingSurfaceBitmap.setPixel(0, indexHeight, Color.TRANSPARENT);
		}
		mSolo.sleep(500);
		clickOnBottomParameterOne();
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Width did not change:", drawingSurfaceOriginalWidth - 1,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());

		drawingSurfaceOriginalWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
		drawingSurfaceOriginalHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");
		for (int indexHeight = 0; indexHeight < drawingSurfaceOriginalHeight; indexHeight++) {
			mCurrentDrawingSurfaceBitmap.setPixel(drawingSurfaceOriginalWidth - 1, indexHeight, Color.TRANSPARENT);
		}
		mSolo.sleep(500);
		clickOnBottomParameterOne();
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Width did not change:", drawingSurfaceOriginalWidth - 1,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());

	}

	@Test
	public void testSmallBitmapResizing() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		scaleDownTestBitmap();
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		clickOnBottomParameterTwo();
		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getPixel(new PointF(0, 0)));

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_WIDTH, originalWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_HEIGHT, originalHeight);
		clickOnBottomParameterTwo();
		assertEquals("Wrong width after enlarging ", originalWidth,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after enlarging ", originalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());

	}

	@Test
	public void testCenterBitmapAfterCropAndUndo() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		Point topleftCanvasPoint = new Point(0, 0);
		Point bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth() - 1,
				mCurrentDrawingSurfaceBitmap.getHeight() - 1);
		Point originalTopleftScreenPoint = Utils.convertFromCanvasToScreen(topleftCanvasPoint,
				PaintroidApplication.perspective);
		Point originalBottomrightScreenPoint = Utils.convertFromCanvasToScreen(bottomrightCanvasPoint,
				PaintroidApplication.perspective);

		assertEquals("Canvas and screen bottomright coordinates are not the same ", bottomrightCanvasPoint,
				originalBottomrightScreenPoint);

		drawPlus();
		standardAutoCrop();
		clickOnBottomParameterTwo();
		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");
		Point centerOfScreen = new Point(originalBottomrightScreenPoint.x / 2, originalBottomrightScreenPoint.y / 2);
		topleftCanvasPoint = new Point(0, 0);
		bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth() - 1,
				mCurrentDrawingSurfaceBitmap.getHeight() - 1);

		Point topleftScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				topleftCanvasPoint, PaintroidApplication.perspective);

		Point bottomrightScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				bottomrightCanvasPoint, PaintroidApplication.perspective);

		assertTrue("Wrong width after cropping", originalWidth > mCurrentDrawingSurfaceBitmap.getWidth());
		assertTrue("Wrong height after cropping", originalHeight > mCurrentDrawingSurfaceBitmap.getHeight());

		assertTrue("Wrong left screen coordinate", (topleftScreenPoint.x > originalTopleftScreenPoint.x)
				&& (topleftScreenPoint.x < centerOfScreen.x));
		assertTrue("Wrong top screen coordinate", (topleftScreenPoint.y > originalTopleftScreenPoint.y)
				&& (topleftScreenPoint.y < centerOfScreen.y));
		assertTrue("Wrong right screen coordinate", (bottomrightScreenPoint.x < originalBottomrightScreenPoint.x)
				&& (bottomrightScreenPoint.x > centerOfScreen.x));
		assertTrue("Wrong bottom screen coordinate", (bottomrightScreenPoint.y < originalBottomrightScreenPoint.y)
				&& (bottomrightScreenPoint.y > centerOfScreen.y));

		mSolo.clickOnView(mButtonTopUndo);

		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);

		PaintroidApplication.perspective.setScale(1.0f);

		bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth() - 1,
				mCurrentDrawingSurfaceBitmap.getHeight() - 1);
		originalBottomrightScreenPoint = Utils.convertFromCanvasToScreen(bottomrightCanvasPoint,
				PaintroidApplication.perspective);

		assertEquals("Canvas and screen bottomright coordinates are not the same after undo", bottomrightCanvasPoint,
				originalBottomrightScreenPoint);
	}

	@Test
	public void testCenterBitmapAfterCropDrawingOnTopRight() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		Point topleftCanvasPoint = new Point(0, 0);
		Point bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth(),
				mCurrentDrawingSurfaceBitmap.getHeight());
		Point originalTopleftScreenPoint = Utils.convertFromCanvasToScreen(topleftCanvasPoint,
				PaintroidApplication.perspective);
		Point originalBottomrightScreenPoint = Utils.convertFromCanvasToScreen(bottomrightCanvasPoint,
				PaintroidApplication.perspective);

		int lineWidth = 10;
		int verticalLineStartX = (mCurrentDrawingSurfaceBitmap.getWidth() - lineWidth);
		int mVertivalLineStartY = 10;

		int[] pixelsColorArray = new int[lineWidth * mLineLength];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		mCurrentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, mVertivalLineStartY,
				lineWidth, mLineLength);
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		standardAutoCrop();
		clickOnBottomParameterTwo();

		topleftCanvasPoint = new Point(0, 0);
		bottomrightCanvasPoint = new Point(PaintroidApplication.drawingSurface.getBitmapWidth() - 1,
				PaintroidApplication.drawingSurface.getBitmapHeight() - 1);

		Point centerOfScreen = new Point(originalBottomrightScreenPoint.x / 2, originalBottomrightScreenPoint.y / 2);

		Point topleftScreenPoint = Utils
				.convertFromCanvasToScreen(topleftCanvasPoint, PaintroidApplication.perspective);

		Point bottomrightScreenPoint = Utils.convertFromCanvasToScreen(bottomrightCanvasPoint,
				PaintroidApplication.perspective);

		assertTrue("Wrong width after cropping", originalWidth > PaintroidApplication.drawingSurface.getBitmapWidth());
		assertTrue("Wrong height after cropping",
				originalHeight > PaintroidApplication.drawingSurface.getBitmapHeight());

		assertTrue("Wrong left screen coordinate", (topleftScreenPoint.x > originalTopleftScreenPoint.x)
				&& (topleftScreenPoint.x < centerOfScreen.x));
		assertTrue("Wrong top screen coordinate", (topleftScreenPoint.y > originalTopleftScreenPoint.y)
				&& (topleftScreenPoint.y < centerOfScreen.y));
		assertTrue("Wrong right screen coordinate", (bottomrightScreenPoint.x < originalBottomrightScreenPoint.x)
				&& (bottomrightScreenPoint.x > centerOfScreen.x));
		assertTrue("Wrong bottom screen coordinate", (bottomrightScreenPoint.y < originalBottomrightScreenPoint.y)
				&& (bottomrightScreenPoint.y > centerOfScreen.y));

	}

	public void testIfBordersAreAlignedCorrectAfterCrop() throws SecurityException, IllegalArgumentException,
			InterruptedException, NoSuchFieldException, IllegalAccessException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		clickOnBottomParameterTwo();
		assertEquals("Left border should be 0", 0.0f,
				PrivateAccess.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT));
		assertEquals("Right border should be bitmap width",
				((float) PaintroidApplication.drawingSurface.getBitmapWidth() - 1), PrivateAccess.getMemberValue(
						ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT));
		assertEquals("Top border should be 0", 0.0f,
				PrivateAccess.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP));
		assertEquals("Bottom border should be bitmap height",
				((float) PaintroidApplication.drawingSurface.getBitmapHeight() - 1), PrivateAccess.getMemberValue(
						ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM));
	}

	@FlakyTest(tolerance = 4)
	public void testMoveLeftCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedLeftBorder = 0; movedLeftBorder < 4; movedLeftBorder++) {
			float leftCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT);
			float rightCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT);
			float imageWidthAfterCropWithoutMovingBorders = rightCroppingBorder - leftCroppingBorder;
			float imageWidthAfterCropWithMovedBorder = imageWidthAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					TOOL_MEMBER_WIDTH, (float) Math.floor(imageWidthAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_POSITION);
			toolPosition.x = toolPosition.x + (float) Math.floor(imageWidthAfterCropWithMovedBorder / 2f);
			clickOnBottomParameterTwo();
			assertEquals("Run " + movedLeftBorder + ": Cropped image width is wrong",
					Math.floor(imageWidthAfterCropWithMovedBorder),
					(double) PaintroidApplication.drawingSurface.getBitmapWidth());
		}
	}

	@FlakyTest(tolerance = 4)
	public void testMoveTopCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedTopBorder = 0; movedTopBorder < 4; movedTopBorder++) {
			float topCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP);
			float bottomCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM);
			float imageHeightAfterCropWithoutMovingBorders = bottomCroppingBorder - topCroppingBorder;
			float imageHeightAfterCropWithMovedBorder = imageHeightAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					TOOL_MEMBER_HEIGHT, (float) Math.floor(imageHeightAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_POSITION);
			toolPosition.y = toolPosition.y + imageHeightAfterCropWithMovedBorder / 2f;
			clickOnBottomParameterTwo();
			assertEquals("Run " + movedTopBorder + ": Cropped image height is wrong",
					Math.floor(imageHeightAfterCropWithMovedBorder),
					(double) PaintroidApplication.drawingSurface.getBitmapHeight());
		}
	}

	@FlakyTest(tolerance = 4)
	public void testMoveRightCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedRightBorder = 0; movedRightBorder < 4; movedRightBorder++) {
			float leftCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT);
			float rightCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT);
			float imageWidthAfterCropWithoutMovingBorders = rightCroppingBorder - leftCroppingBorder;
			float imageWidthAfterCropWithMovedBorder = imageWidthAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					TOOL_MEMBER_WIDTH, (float) Math.floor(imageWidthAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_POSITION);
			toolPosition.x = toolPosition.x - (float) Math.floor(imageWidthAfterCropWithMovedBorder / 2f);
			clickOnBottomParameterTwo();
			assertEquals("Run " + movedRightBorder + ": Cropped image width is wrong",
					Math.floor(imageWidthAfterCropWithMovedBorder),
					(double) PaintroidApplication.drawingSurface.getBitmapWidth());
		}
	}

	@FlakyTest(tolerance = 4)
	public void testMoveBottomCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedTopBorder = 0; movedTopBorder < 4; movedTopBorder++) {
			float topCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP);
			float bottomCroppingBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM);
			float imageHeightAfterCropWithoutMovingBorders = bottomCroppingBorder - topCroppingBorder;
			float imageHeightAfterCropWithMovedBorder = imageHeightAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					TOOL_MEMBER_HEIGHT, (float) Math.floor(imageHeightAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_POSITION);
			toolPosition.y = toolPosition.y - (float) Math.floor(imageHeightAfterCropWithMovedBorder / 2f);
			clickOnBottomParameterTwo();
			assertEquals("Run " + movedTopBorder + ": Cropped image height is wrong",
					Math.floor(imageHeightAfterCropWithMovedBorder),
					(double) PaintroidApplication.drawingSurface.getBitmapHeight());
		}
	}

	public void testCropFromEverySideOnFilledBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		PaintroidApplication.perspective.resetScaleAndTranslation();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		selectTool(ToolType.FILL);
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Wrong pixel color after fill", colorToFill, colorAfterFill);

		standardAutoCrop();

		int bitmapWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		float leftResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT);
		float rightResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT);
		float topResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP);
		float bottomResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM);
		assertEquals("left cropping border should be zero", 0.0f, leftResizeBorder);
		assertEquals("right cropping border should be bitmap width",
				PaintroidApplication.drawingSurface.getBitmapWidth() - 1.0f, rightResizeBorder);
		assertEquals("top cropping border should be zero", 0.0f, topResizeBorder);
		assertEquals("bottom cropping border should be bitmap height",
				PaintroidApplication.drawingSurface.getBitmapHeight() - 1.0f, bottomResizeBorder);

		int pixelsLeftHeight[];
		int pixelsRightHeight[];
		int pixelsTopWidth[];
		int pixelsBottomWidth[];

		for (BitmapSide side : BitmapSide.values()) {
			int cropSizeWidth = 0;
			int cropSizeHeight = 0;
			Bitmap bitmapCopy = PaintroidApplication.drawingSurface.getBitmapCopy();
			switch (side) {
				case LEFT:
					cropSizeWidth = originalWidth / 8;
					pixelsLeftHeight = new int[cropSizeWidth * bitmapHeight];
					bitmapCopy.setPixels(pixelsLeftHeight, 0, cropSizeWidth,
							0, 0, cropSizeWidth, bitmapHeight);
					break;
				case TOP:
					cropSizeHeight = originalHeight / 8;
					pixelsTopWidth = new int[cropSizeHeight * bitmapWidth];
					bitmapCopy.setPixels(pixelsTopWidth, 0, bitmapWidth,
							0, 0, bitmapWidth, cropSizeHeight);
					break;
				case RIGHT:
					cropSizeWidth = originalWidth / 8;
					pixelsRightHeight = new int[cropSizeWidth * bitmapHeight];
					bitmapCopy.setPixels(pixelsRightHeight, 0, cropSizeWidth,
							bitmapWidth - cropSizeWidth, 0, cropSizeWidth, bitmapHeight);
					break;
				case BOTTOM:
					cropSizeHeight = originalHeight / 8;
					pixelsBottomWidth = new int[cropSizeHeight * bitmapWidth];
					bitmapCopy.setPixels(pixelsBottomWidth, 0, bitmapWidth,
							0, bitmapHeight - cropSizeHeight, bitmapWidth, cropSizeHeight);
					break;
			}
			PaintroidApplication.drawingSurface.setBitmap(bitmapCopy);

			clickOnBottomParameterOne();

			clickOnBottomParameterTwo();

			assertEquals("Wrong bitmap width after cropping",bitmapWidth - cropSizeWidth,
					PaintroidApplication.drawingSurface.getBitmapWidth());
			assertEquals("Wrong bitmap height after cropping", bitmapHeight - cropSizeHeight,
					PaintroidApplication.drawingSurface.getBitmapHeight());

			bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
			bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
			pixelsLeftHeight = new int[bitmapHeight];
			pixelsRightHeight = new int[bitmapHeight];
			pixelsTopWidth = new int[bitmapWidth];
			pixelsBottomWidth = new int[bitmapWidth];
			PaintroidApplication.drawingSurface.getPixels(pixelsLeftHeight, 0, 1, 0, 0, 1, bitmapHeight);
			PaintroidApplication.drawingSurface.getPixels(pixelsRightHeight, 0, 1, bitmapWidth - 1, 0, 1, bitmapHeight);
			PaintroidApplication.drawingSurface.getPixels(pixelsTopWidth, 0, bitmapWidth, 0, 0, bitmapWidth, 1);
			PaintroidApplication.drawingSurface.getPixels(pixelsBottomWidth, 0, bitmapWidth, 0, bitmapHeight - 1, bitmapWidth, 1);
			for (int i = 0; i < pixelsLeftHeight.length; i++) {
				assertEquals("Wrong pixel color left", colorAfterFill, pixelsLeftHeight[i]);
				assertEquals("Wrong pixel color right", colorAfterFill, pixelsRightHeight[i]);
			}
			for (int i = 0; i < pixelsTopWidth.length; i++) {
				assertEquals("Wrong pixel color top", colorAfterFill, pixelsTopWidth[i]);
				assertEquals("Wrong pixel color bottom", colorAfterFill, pixelsBottomWidth[i]);
			}
		}

	}

	public void testResizeBordersMatchBitmapBordersAfterCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		selectTool(ToolType.RESIZE);

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_WIDTH, mCurrentDrawingSurfaceBitmap.getWidth() - (mCurrentDrawingSurfaceBitmap.getWidth() / 8));
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_HEIGHT, mCurrentDrawingSurfaceBitmap.getHeight() - (mCurrentDrawingSurfaceBitmap.getHeight() / 8));

		clickOnBottomParameterTwo();

		float leftResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT);
		float rightResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT);
		float topResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP);
		float bottomResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM);
		assertEquals("left cropping border should be zero", 0.0f, leftResizeBorder);
		assertEquals("right cropping border should be bitmap width",
				PaintroidApplication.drawingSurface.getBitmapWidth() - 1.0f, rightResizeBorder);
		assertEquals("top cropping border should be zero", 0.0f, topResizeBorder);
		assertEquals("bottom cropping border should be bitmap height",
				PaintroidApplication.drawingSurface.getBitmapHeight() - 1.0f, bottomResizeBorder);
	}

	@FlakyTest(tolerance = 3)
	public void testNoMaximumBorderRatio() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		selectTool(ToolType.RESIZE);

		float maxBorderRatio = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				PaintroidApplication.currentTool, "MAXIMUM_BORDER_RATIO");
		float boundingBoxWidth = mCurrentDrawingSurfaceBitmap.getWidth() / (maxBorderRatio * 2);
		float boundingBoxHeight = mCurrentDrawingSurfaceBitmap.getHeight() / (maxBorderRatio * 2);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_WIDTH, boundingBoxWidth);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_HEIGHT, boundingBoxHeight);
		clickOnBottomParameterTwo();

		float newBoundingBoxWidth = boundingBoxWidth * maxBorderRatio + 1;
		float newBoundingBoxHeight = boundingBoxHeight * maxBorderRatio + 1;

		Point dragFrom = Utils.convertFromCanvasToScreen(
				new Point((int) boundingBoxWidth, 0),
				PaintroidApplication.perspective);
		Point dragTo = Utils.convertFromCanvasToScreen(
				new Point((int) newBoundingBoxWidth, 0),
				PaintroidApplication.perspective);
		mSolo.drag(dragFrom.x, dragTo.x, dragFrom.y, dragTo.y, 20);
		mSolo.sleep(SHORT_TIMEOUT);

		PaintroidApplication.perspective.resetScaleAndTranslation();

		dragFrom = Utils.convertFromCanvasToScreen(
				new Point((int) (newBoundingBoxWidth / 2), (int) (boundingBoxHeight / 2)),
				PaintroidApplication.perspective);
		dragTo = Utils.convertFromCanvasToScreen(
				new Point((int) (newBoundingBoxWidth / 2), (int) (newBoundingBoxHeight - boundingBoxHeight / 2)),
				PaintroidApplication.perspective);
		mSolo.drag(dragFrom.x, dragTo.x, dragFrom.y, dragTo.y, 20);
		mSolo.sleep(SHORT_TIMEOUT);

		float mBoxWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_WIDTH);
		float mBoxHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_HEIGHT);
		float maxWidth = PaintroidApplication.drawingSurface.getBitmapWidth() * maxBorderRatio;
		float maxHeight = PaintroidApplication.drawingSurface.getBitmapHeight() * maxBorderRatio;
		assertTrue("Wrong bounding box width", maxWidth < mBoxWidth);
		assertTrue("Wrong bounding box height", maxHeight < mBoxHeight);

	}

	public void testPreventTooLargeBitmaps() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		int displayWidth = displaySize.x;
		int displayHeight = displaySize.y;

		int maxBitmapSize = displayWidth * displayHeight * MAXIMUM_BITMAP_SIZE_FACTOR;
		int widthToGetMaxBitmapSize = displayWidth * (MAXIMUM_BITMAP_SIZE_FACTOR / 2);
		int heightToGetMaxBitmapSize = displayHeight * (MAXIMUM_BITMAP_SIZE_FACTOR / 2);

		selectTool(ToolType.RESIZE);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_WIDTH, widthToGetMaxBitmapSize);
		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_HEIGHT, heightToGetMaxBitmapSize);
		clickOnBottomParameterTwo();

		assertEquals("Wrong bitmap width after resizing", widthToGetMaxBitmapSize,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong bitmap height after resizing", heightToGetMaxBitmapSize,
				PaintroidApplication.drawingSurface.getBitmapHeight());
		int bitmapSize = PaintroidApplication.drawingSurface.getBitmapWidth() *
				PaintroidApplication.drawingSurface.getBitmapHeight();
		assertEquals("Bitmap size should be max bitmap size", maxBitmapSize, bitmapSize);

		PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_WIDTH, widthToGetMaxBitmapSize + 1);

		clickOnBottomParameterTwo();
		bitmapSize = PaintroidApplication.drawingSurface.getBitmapWidth() *
				PaintroidApplication.drawingSurface.getBitmapHeight();
		assertFalse("Bitmap should not got larger", bitmapSize > maxBitmapSize);
	}

	public void testMaxImageResolutionTextIsShown() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point displaySize = new Point();
		display.getSize(displaySize);
		int displayWidth = displaySize.x;
		int displayHeight = displaySize.y;

		int maxBitmapSize = displayWidth * displayHeight * MAXIMUM_BITMAP_SIZE_FACTOR;
		int widthToGetMaxBitmapSize = displayWidth * (MAXIMUM_BITMAP_SIZE_FACTOR / 2);

		selectTool(ToolType.RESIZE);
		float zoomFactor = PaintroidApplication.perspective
				.getScaleForCenterBitmap() * 0.25f;
		PaintroidApplication.perspective.setScale(zoomFactor);

		Point dragFrom = Utils.convertFromCanvasToScreen(
				new Point(mCurrentDrawingSurfaceBitmap.getWidth(), mCurrentDrawingSurfaceBitmap.getHeight()),
				PaintroidApplication.perspective);
		Point dragTo = Utils.convertFromCanvasToScreen(
				new Point((int) widthToGetMaxBitmapSize + 10, mCurrentDrawingSurfaceBitmap.getHeight()),
				PaintroidApplication.perspective);
		mSolo.drag(dragFrom.x, dragTo.x, dragFrom.y, dragTo.y, 50);

		assertTrue("Maximum resize resolution text missing", mSolo.waitForText(
				mSolo.getString(R.string.resize_max_image_resolution_reached)));

		clickOnBottomParameterTwo();
		int bitmapSize = PaintroidApplication.drawingSurface.getBitmapWidth() *
				PaintroidApplication.drawingSurface.getBitmapHeight();
		assertFalse("Bitmap should not get larger than max bitmap size", bitmapSize > maxBitmapSize);
	}

	public void testEnlargeEverySideAndCheckEnlargedColor() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		PaintroidApplication.perspective.resetScaleAndTranslation();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		selectTool(ToolType.FILL);
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Wrong pixel color after fill", colorToFill, colorAfterFill);

		selectTool(ToolType.RESIZE);
		int enlargeColor = 0;

		for (BitmapSide side : BitmapSide.values()) {
			float boxWidth = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_WIDTH);
			float boxHeight = (Float) PrivateAccess.getMemberValue(BaseToolWithRectangleShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_HEIGHT);
			PointF boxPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, TOOL_MEMBER_POSITION);
			int pixels[] = {0};
			int bitmapWidth;
			int bitmapHeight;
			switch (side) {
				case LEFT:
					PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_WIDTH, boxWidth + 1);
					PrivateAccess.setMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_POSITION, new PointF(boxPosition.x - 0.5f, boxPosition.y));
					clickOnBottomParameterTwo();
					bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
					pixels = new int[bitmapHeight];
					PaintroidApplication.drawingSurface.getPixels(pixels, 0, 1, 0, 0, 1, bitmapHeight);
					break;
				case TOP:
					PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_HEIGHT, boxHeight + 1);
					PrivateAccess.setMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_POSITION, new PointF(boxPosition.x, boxPosition.y - 0.5f));
					clickOnBottomParameterTwo();
					bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
					pixels = new int[bitmapWidth];
					PaintroidApplication.drawingSurface.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth, 1);
					break;
				case RIGHT:
					PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_WIDTH, boxWidth + 1);
					PrivateAccess.setMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_POSITION, new PointF(boxPosition.x + 0.5f, boxPosition.y));
					clickOnBottomParameterTwo();
					bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
					bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
					pixels = new int[bitmapHeight];
					PaintroidApplication.drawingSurface.getPixels(pixels, 0, 1, bitmapWidth - 1, 0, 1, bitmapHeight);
					break;
				case BOTTOM:
					PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_HEIGHT, boxHeight + 1);
					PrivateAccess.setMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool,
							TOOL_MEMBER_POSITION, new PointF(boxPosition.x, boxPosition.y + 0.5f));
					clickOnBottomParameterTwo();
					bitmapWidth = PaintroidApplication.drawingSurface.getBitmapWidth();
					bitmapHeight = PaintroidApplication.drawingSurface.getBitmapHeight();
					pixels = new int[bitmapWidth];
					PaintroidApplication.drawingSurface.getPixels(pixels, 0, bitmapWidth, 0, bitmapHeight - 1, bitmapWidth, 1);
					break;
			}

			for (int i = 0; i < pixels.length; i++) {
				assertEquals("Wrong pixel color", enlargeColor, pixels[i]);
			}
		}
		assertEquals("Wrong bitmap width", originalWidth + BitmapSide.values().length / 2,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong bitmap height", originalHeight + BitmapSide.values().length / 2,
				PaintroidApplication.drawingSurface.getBitmapHeight());

	}

	public void testResizeWithPartialOverlapping() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		PaintroidApplication.perspective.resetScaleAndTranslation();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		selectTool(ToolType.FILL);
		int colorToFill = PaintroidApplication.currentTool.getDrawPaint().getColor();
		PointF screenPoint = new PointF(mScreenWidth / 2, mScreenHeight / 2);
		PointF canvasPoint = Utils.getCanvasPointFromScreenPoint(screenPoint);
		mSolo.clickOnScreen(screenPoint.x, screenPoint.y);
		mSolo.sleep(SHORT_SLEEP);
		mSolo.waitForDialogToClose(SHORT_TIMEOUT);
		int colorAfterFill = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Wrong pixel color after fill", colorToFill, colorAfterFill);

		selectTool(ToolType.RESIZE);

		PrivateAccess.setMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_POSITION, new PointF(originalWidth, originalHeight));
		clickOnBottomParameterTwo();
		assertEquals("Bitmap width should not change", originalWidth,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Bitmap height should not change", originalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());

		clickOnBottomParameterOne();
		assertEquals("Left resizing border should be zero", 0.0f, PrivateAccess.getMemberValue(
				ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT));
		assertEquals("Top resizing border should be zero", 0.0f, PrivateAccess.getMemberValue(
				ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_TOP));
		assertEquals("Right resizing border should equal overlapping width", originalWidth / 2.0f - 1, PrivateAccess
				.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_RIGHT));
		assertEquals("Bottom resizing border should equal overlapping height", originalHeight / 2.0f - 1, PrivateAccess
				.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_BOTTOM));

	}

	public void testResizeBoxCompletelyOutsideBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		selectTool(ToolType.RESIZE);

		PointF outsideBitmapToolPosition = new PointF(
				mCurrentDrawingSurfaceBitmap.getWidth() + mCurrentDrawingSurfaceBitmap.getWidth() / 2.0f,
				 mCurrentDrawingSurfaceBitmap.getHeight() / 2.0f);
		PrivateAccess.setMemberValue(BaseToolWithShape.class, PaintroidApplication.currentTool,
				TOOL_MEMBER_POSITION, outsideBitmapToolPosition);
		mSolo.sleep(SHORT_SLEEP);
		float leftResizeBorder = (Float) PrivateAccess.getMemberValue(ResizeTool.class,
				PaintroidApplication.currentTool, TOOL_MEMBER_RESIZE_BOUND_LEFT);
		mSolo.clickOnView(mMenuBottomParameter2, true);
		assertTrue("Nothing to resize text missing", mSolo.waitForText(mSolo.getString(
		R.string.resize_nothing_to_resize)));

	}


	private void scaleDownTestBitmap() {
		mCurrentDrawingSurfaceBitmap = Bitmap.createBitmap(
				(int) (mCurrentDrawingSurfaceBitmap.getWidth() * BITMAP_DOWNSCALE_FACTOR),
				(int) (mCurrentDrawingSurfaceBitmap.getHeight() * BITMAP_DOWNSCALE_FACTOR), Config.ARGB_8888);
		PaintroidApplication.drawingSurface.setBitmap(mCurrentDrawingSurfaceBitmap);
		mSolo.sleep(200);
		mLineLength = (mCurrentDrawingSurfaceBitmap.getWidth() / 2);
	}

	private void standardAutoCrop() {
		selectTool(ToolType.RESIZE);
		mSolo.clickOnView(mMenuBottomParameter1);
		failWhenCroppingTimedOut();
	}

	private void clickOnBottomParameterOne() {
		mSolo.clickOnView(mMenuBottomParameter1, true);
		hasCroppingTimedOut();
	}

	private void clickOnBottomParameterTwo() {
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Resize command has not finished", mSolo.waitForDialogToClose());
	}

	private void drawPlus() {

		int lineWidth = 10;
		int mHorizontalLineStartX = (mCurrentDrawingSurfaceBitmap.getWidth() / 4);
		int horizontalLineStartY = (mCurrentDrawingSurfaceBitmap.getHeight() / 2);
		int verticalLineStartX = (mCurrentDrawingSurfaceBitmap.getWidth() / 2);
		int mVerticalLineStartY = (mCurrentDrawingSurfaceBitmap.getHeight() / 2 - mLineLength / 2);

		int[] pixelsColorArray = new int[lineWidth * mLineLength];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		mCurrentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, mLineLength, mHorizontalLineStartX,
				horizontalLineStartY, mLineLength, lineWidth);

		mCurrentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, mVerticalLineStartY,
				lineWidth, mLineLength);
	}

	private int hasCroppingTimedOut() {
		int croppingTimeoutCounter = 0;
		Integer[][] croppingBounds = new Integer[4][2];
		try {
			croppingBounds[0][0] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, "mIntermediateResizeBoundWidthXLeft");
			croppingBounds[1][0] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, "mIntermediateResizeBoundWidthXRight");
			croppingBounds[2][0] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, "mIntermediateResizeBoundHeightYTop");
			croppingBounds[3][0] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
					PaintroidApplication.currentTool, "mIntermediateResizeBoundHeightYBottom");

			for (; croppingTimeoutCounter < MAXIMUM_CROPPING_TIMEOUT_COUNTS; croppingTimeoutCounter++) {
				Thread.yield();
				croppingBounds[0][1] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
						PaintroidApplication.currentTool, "mIntermediateResizeBoundWidthXLeft");
				croppingBounds[1][1] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
						PaintroidApplication.currentTool, "mIntermediateResizeBoundWidthXRight");
				croppingBounds[2][1] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
						PaintroidApplication.currentTool, "mIntermediateResizeBoundHeightYTop");
				croppingBounds[3][1] = (Integer) PrivateAccess.getMemberValue(ResizeTool.class,
						PaintroidApplication.currentTool, "mIntermediateResizeBoundHeightYBottom");

				if (croppingBounds[0][0].equals(croppingBounds[0][1])
						&& croppingBounds[1][0].equals(croppingBounds[1][1])
						&& croppingBounds[2][0].equals(croppingBounds[2][1])
						&& croppingBounds[3][0].equals(croppingBounds[3][1])
						&& (Boolean) (PrivateAccess.getMemberValue(ResizeTool.class, PaintroidApplication.currentTool,
						"mCropRunFinished")) == true) {
					break;
				} else {
					croppingBounds[0][0] = croppingBounds[0][1];
					croppingBounds[1][0] = croppingBounds[1][1];
					croppingBounds[2][0] = croppingBounds[2][1];
					croppingBounds[3][0] = croppingBounds[3][1];

					mSolo.sleep(CROPPING_SLEEP_BETWEEN_FINISH_CHECK);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
		}
		if (croppingTimeoutCounter >= MAXIMUM_CROPPING_TIMEOUT_COUNTS) {
			return croppingTimeoutCounter;
		}
		return -1;
	}

	private void failWhenCroppingTimedOut() {
		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}
	}
}
