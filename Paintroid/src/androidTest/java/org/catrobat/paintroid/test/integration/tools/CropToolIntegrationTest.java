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
import org.catrobat.paintroid.tools.implementation.CropTool;
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.test.FlakyTest;

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	private final int CROPPING_SLEEP_BETWEEN_FINISH_CHECK = 500;
	private final int MAXIMUM_CROPPING_TIMEOUT_COUNTS = 300;
	private final float BITMAP_DOWNSCALE_FACTOR = 0.5f;
	private int mLineLength;
	private final int STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE = 800;// !try multiple times with emulators and
																		// different hardware before decreasing the
																		// value!

	public CropToolIntegrationTest() throws Exception {
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
		final int cropToastSleepingTime = 100;
		for (int cropToastTimeoutCounter = 0; cropToastSleepingTime * cropToastTimeoutCounter < TIMEOUT; cropToastTimeoutCounter++) {
			if (mSolo.waitForText(mSolo.getString(R.string.crop_algorithm_finish_text), 1, 10)
					|| mSolo.waitForText(mSolo.getString(R.string.crop_nothing_to_corp), 1, 10))
				mSolo.sleep(cropToastSleepingTime);
			else
				break;
		}
		super.tearDown();
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() throws SecurityException, IllegalArgumentException, InterruptedException,
			NoSuchFieldException, IllegalAccessException {
		scaleDownTestBitmap();
		selectTool(ToolType.CROP);

		assertEquals("Zoom factor is wrong", 0.95f, PaintroidApplication.perspective.getScale());

		failWhenCroppingTimedOut();

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("nothing to crop text missing",
				mSolo.waitForText(mSolo.getString(R.string.crop_nothing_to_corp), 1, TIMEOUT, true));

	}

	@Test
	public void testIfOnePixelIsFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());

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
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
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

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
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
		mSolo.clickOnView(mMenuBottomParameter1, true);
		hasCroppingTimedOut();
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
		mSolo.clickOnView(mMenuBottomParameter1, true);
		hasCroppingTimedOut();
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
		mSolo.clickOnView(mMenuBottomParameter1, true);
		hasCroppingTimedOut();
		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Width did not change:", drawingSurfaceOriginalWidth - 1,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());

	}

	@Test
	public void testSmallBitmapCropping() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth() / 2,
				mCurrentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getPixel(new PointF(0, 0)));
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
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
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
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
		// mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
		// PaintroidApplication.drawingSurface, "mWorkingBitmap");

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
		mSolo.clickOnView(mMenuBottomParameter2, true);
		assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
		mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
		assertEquals("Left border should be 0", 0.0f,
				PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.currentTool, "mCropBoundWidthXLeft"));
		assertEquals("Right border should be equal bitmap.width",
				((float) PaintroidApplication.drawingSurface.getBitmapWidth()),
				PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.currentTool, "mCropBoundWidthXRight"));
		assertEquals("Top border should be 0", 0.0f,
				PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.currentTool, "mCropBoundHeightYTop"));
		assertEquals("Bottom border should be equal bitmap.height",
				((float) PaintroidApplication.drawingSurface.getBitmapHeight()), PrivateAccess.getMemberValue(
						CropTool.class, PaintroidApplication.currentTool, "mCropBoundHeightYBottom"));
	}

	@FlakyTest(tolerance = 4)
	public void testMoveLeftCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedLeftBorder = 0; movedLeftBorder < 4; movedLeftBorder++) {
			float leftCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXLeft");
			float rightCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXRight");
			float imageWidthAfterCropWithoutMovingBorders = rightCroppingBorder - leftCroppingBorder;
			float imageWidthAfterCropWithMovedBorder = imageWidthAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxWidth", (float) Math.floor(imageWidthAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.x = toolPosition.x + (float) Math.floor(imageWidthAfterCropWithMovedBorder / 2f);
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
			mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
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
			float topCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYTop");
			float bottomCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYBottom");
			float imageHeightAfterCropWithoutMovingBorders = bottomCroppingBorder - topCroppingBorder;
			float imageHeightAfterCropWithMovedBorder = imageHeightAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxHeight", (float) Math.floor(imageHeightAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.y = toolPosition.y + imageHeightAfterCropWithMovedBorder / 2f;
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
			mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
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
			float leftCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXLeft");
			float rightCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXRight");
			float imageWidthAfterCropWithoutMovingBorders = rightCroppingBorder - leftCroppingBorder;
			float imageWidthAfterCropWithMovedBorder = imageWidthAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxWidth", (float) Math.floor(imageWidthAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.x = toolPosition.x - (float) Math.floor(imageWidthAfterCropWithMovedBorder / 2f);
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
			mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
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
			float topCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYTop");
			float bottomCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYBottom");
			float imageHeightAfterCropWithoutMovingBorders = bottomCroppingBorder - topCroppingBorder;
			float imageHeightAfterCropWithMovedBorder = imageHeightAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxHeight", (float) Math.floor(imageHeightAfterCropWithMovedBorder));

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.y = toolPosition.y - (float) Math.floor(imageHeightAfterCropWithMovedBorder / 2f);
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", mSolo.waitForDialogToClose());
			mSolo.sleep(STABLE_TIME_FOR_THREADS_AND_BITMAPS_UPDATE);
			assertEquals("Run " + movedTopBorder + ": Cropped image height is wrong",
					Math.floor(imageHeightAfterCropWithMovedBorder),
					(double) PaintroidApplication.drawingSurface.getBitmapHeight());
		}
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
		selectTool(ToolType.CROP);
		failWhenCroppingTimedOut();
	}

	private void drawPlus() {

		int lineWidth = 10;
		int mHorizontalLineStartX = (mCurrentDrawingSurfaceBitmap.getWidth() / 4);
		int horizontalLineStartY = (mCurrentDrawingSurfaceBitmap.getHeight() / 2);
		int verticalLineStartX = (mCurrentDrawingSurfaceBitmap.getWidth() / 2);
		int mVertivalLineStartY = (mCurrentDrawingSurfaceBitmap.getHeight() / 2 - mLineLength / 2);

		int[] pixelsColorArray = new int[lineWidth * mLineLength];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		mCurrentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, mLineLength, mHorizontalLineStartX,
				horizontalLineStartY, mLineLength, lineWidth);

		mCurrentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, mVertivalLineStartY,
				lineWidth, mLineLength);
	}

	private int hasCroppingTimedOut() {
		int croppingTimeoutCounter = 0;
		Integer[][] croppingBounds = new Integer[4][2];
		try {
			croppingBounds[0][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mIntermediateCropBoundWidthXLeft");
			croppingBounds[1][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mIntermediateCropBoundWidthXRight");
			croppingBounds[2][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mIntermediateCropBoundHeightYTop");
			croppingBounds[3][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mIntermediateCropBoundHeightYBottom");

			for (; croppingTimeoutCounter < MAXIMUM_CROPPING_TIMEOUT_COUNTS; croppingTimeoutCounter++) {
				Thread.yield();
				croppingBounds[0][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.currentTool, "mIntermediateCropBoundWidthXLeft");
				croppingBounds[1][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.currentTool, "mIntermediateCropBoundWidthXRight");
				croppingBounds[2][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.currentTool, "mIntermediateCropBoundHeightYTop");
				croppingBounds[3][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.currentTool, "mIntermediateCropBoundHeightYBottom");

				if (croppingBounds[0][0].equals(croppingBounds[0][1])
						&& croppingBounds[1][0].equals(croppingBounds[1][1])
						&& croppingBounds[2][0].equals(croppingBounds[2][1])
						&& croppingBounds[3][0].equals(croppingBounds[3][1])
						&& (Boolean) (PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.currentTool,
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
