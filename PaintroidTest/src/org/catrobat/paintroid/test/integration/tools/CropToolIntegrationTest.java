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

package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.catrobat.paintroid.test.utils.PrivateAccess;
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

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	private final int CROPPING_SLEEP_BETWEEN_FINISH_CHECK = 500;
	private final int MAXIMUM_CROPPING_TIMEOUT_COUNTS = 300;
	private int mLineLength;

	public CropToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		mLineLength = (mCurrentDrawingSurfaceBitmap.getWidth() / 2);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		mSolo.sleep(2000);
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
		assertTrue("Crop command has not finished", hasCropCommandFinished());
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
		assertTrue("Crop command has not finished", hasCropCommandFinished());

		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testIfMultiplePixelAreFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		mCurrentDrawingSurfaceBitmap.setPixel(1, 1, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight - 1, Color.BLUE);

		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", hasCropCommandFinished());
		assertEquals("Wrong width after cropping ", originalWidth - 1,
				PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 1,
				PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();

		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth / 2, 0, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(0, originalHeight / 2, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight / 2, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(originalWidth / 2, originalHeight - 1, Color.BLUE);

		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", hasCropCommandFinished());
		assertEquals("Wrong width after cropping ", originalWidth, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight,
				PaintroidApplication.drawingSurface.getBitmapHeight());
	}

	@Test
	public void testIfClickOnCanvasDoesNothing() {
		mCurrentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		int drawingSurfaceOriginalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int drawingSurfaceOriginalHeight = mCurrentDrawingSurfaceBitmap.getHeight();
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			mCurrentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		standardAutoCrop();

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		assertEquals("Width changed:", drawingSurfaceOriginalWidth,
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
		assertTrue("Crop command has not finished", hasCropCommandFinished());
		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.drawingSurface.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.drawingSurface.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testCenterBitmapAfterCrop() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException, InterruptedException {

		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		Point topleftCanvasPoint = new Point(0, 0);
		Point bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth() - 1,
				mCurrentDrawingSurfaceBitmap.getHeight() - 1);
		Point originalTopleftScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				topleftCanvasPoint, PaintroidApplication.perspective);
		Point originalBottomrightScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				bottomrightCanvasPoint, PaintroidApplication.perspective);

		assertEquals("Canvas and screen bottomright coordinates are not the same ", bottomrightCanvasPoint,
				originalBottomrightScreenPoint);

		drawPlus();
		standardAutoCrop();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", hasCropCommandFinished());
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

	}

	@Test
	public void testCenterBitmapAfterCropDrawingOnTopRight() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {

		int originalWidth = mCurrentDrawingSurfaceBitmap.getWidth();
		int originalHeight = mCurrentDrawingSurfaceBitmap.getHeight();

		Point topleftCanvasPoint = new Point(0, 0);
		Point bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth(),
				mCurrentDrawingSurfaceBitmap.getHeight());
		Point originalTopleftScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				topleftCanvasPoint, PaintroidApplication.perspective);
		Point originalBottomrightScreenPoint = org.catrobat.paintroid.test.utils.Utils.convertFromCanvasToScreen(
				bottomrightCanvasPoint, PaintroidApplication.perspective);

		int lineWidth = 10;
		int verticalLineStartX = (mCurrentDrawingSurfaceBitmap.getWidth() - lineWidth);
		int mVertivalLineStartY = 10;

		int[] pixelsColorArray = new int[lineWidth * mLineLength];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		mCurrentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, mVertivalLineStartY,
				lineWidth, mLineLength);

		standardAutoCrop();
		mSolo.clickOnView(mMenuBottomParameter2);
		assertTrue("Crop command has not finished", hasCropCommandFinished());
		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");
		topleftCanvasPoint = new Point(0, 0);
		bottomrightCanvasPoint = new Point(mCurrentDrawingSurfaceBitmap.getWidth() - 1,
				mCurrentDrawingSurfaceBitmap.getHeight() - 1);

		Point centerOfScreen = new Point(originalBottomrightScreenPoint.x / 2, originalBottomrightScreenPoint.y / 2);

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

	}

	public void testIfBordersAreAlignedCorrectAfterCrop() throws SecurityException, IllegalArgumentException,
			InterruptedException, NoSuchFieldException, IllegalAccessException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		mSolo.clickOnView(mMenuBottomParameter2, true);
		assertTrue("Crop command has not finished", hasCropCommandFinished());
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

	public void testMoveLeftCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedLeftBorder = 0; movedLeftBorder < 4; movedLeftBorder++) {
			mSolo.sleep(100);
			float leftCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXLeft");
			float rightCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXRight");
			float imageWidthAfterCropWithoutMovingBorders = rightCroppingBorder - leftCroppingBorder;
			float imageWidthAfterCropWithMovedBorder = imageWidthAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxWidth", imageWidthAfterCropWithMovedBorder);

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.x = toolPosition.x + imageWidthAfterCropWithMovedBorder / 2f;
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", hasCropCommandFinished());
			assertEquals("Run " + movedLeftBorder + ": Cropped image width is wrong",
					(int) imageWidthAfterCropWithMovedBorder, PaintroidApplication.drawingSurface.getBitmapWidth());
		}
	}

	public void testMoveTopCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedTopBorder = 0; movedTopBorder < 4; movedTopBorder++) {
			mSolo.sleep(100);
			float topCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYTop");
			float bottomCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYBottom");
			float imageHeightAfterCropWithoutMovingBorders = bottomCroppingBorder - topCroppingBorder;
			float imageHeightAfterCropWithMovedBorder = imageHeightAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxHeight", imageHeightAfterCropWithMovedBorder);

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.y = toolPosition.y + imageHeightAfterCropWithMovedBorder / 2f;
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", hasCropCommandFinished());
			assertEquals("Run " + movedTopBorder + ": Cropped image height is wrong",
					(int) imageHeightAfterCropWithMovedBorder, PaintroidApplication.drawingSurface.getBitmapHeight());
		}
	}

	public void testMoveRightCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		drawPlus();
		standardAutoCrop();
		for (int movedRightBorder = 0; movedRightBorder < 4; movedRightBorder++) {
			mSolo.sleep(100);
			float leftCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXLeft");
			float rightCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundWidthXRight");
			float imageWidthAfterCropWithoutMovingBorders = rightCroppingBorder - leftCroppingBorder;
			float imageWidthAfterCropWithMovedBorder = imageWidthAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxWidth", imageWidthAfterCropWithMovedBorder);

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.x = toolPosition.x - imageWidthAfterCropWithMovedBorder / 2f;
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", hasCropCommandFinished());
			assertEquals("Run " + movedRightBorder + ": Cropped image width is wrong",
					(int) imageWidthAfterCropWithMovedBorder, PaintroidApplication.drawingSurface.getBitmapWidth());
		}
	}

	public void testMoveBottomCroppingBorderAndDoCrop() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException, InterruptedException {
		scaleDownTestBitmap();
		drawPlus();
		standardAutoCrop();
		for (int movedTopBorder = 0; movedTopBorder < 4; movedTopBorder++) {
			mSolo.sleep(100);
			float topCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYTop");
			float bottomCroppingBorder = (Float) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.currentTool, "mCropBoundHeightYBottom");
			float imageHeightAfterCropWithoutMovingBorders = bottomCroppingBorder - topCroppingBorder;
			float imageHeightAfterCropWithMovedBorder = imageHeightAfterCropWithoutMovingBorders / 2f;

			PrivateAccess.setMemberValue(BaseToolWithRectangleShape.class, PaintroidApplication.currentTool,
					"mBoxHeight", imageHeightAfterCropWithMovedBorder);

			PointF toolPosition = (PointF) PrivateAccess.getMemberValue(BaseToolWithShape.class,
					PaintroidApplication.currentTool, "mToolPosition");
			toolPosition.y = toolPosition.y - imageHeightAfterCropWithMovedBorder / 2f;
			mSolo.clickOnView(mMenuBottomParameter2, true);
			assertTrue("Crop command has not finished", hasCropCommandFinished());
			assertEquals("Run " + movedTopBorder + ": Cropped image height is wrong",
					(int) imageHeightAfterCropWithMovedBorder, PaintroidApplication.drawingSurface.getBitmapHeight());
		}
	}

	private void scaleDownTestBitmap() {
		Bitmap shrinkedTestBitmap = Bitmap.createBitmap(200, 200, Config.ARGB_8888);
		PaintroidApplication.drawingSurface.setBitmap(shrinkedTestBitmap);
		mCurrentDrawingSurfaceBitmap = shrinkedTestBitmap;
		mLineLength = (mCurrentDrawingSurfaceBitmap.getWidth() / 2);
	}

	private boolean hasCropCommandFinished() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		mSolo.sleep(500);
		int currentWaitForCropCommand = 0;
		boolean cropRunFinished = (Boolean) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.currentTool, "mCropRunFinished");
		for (; currentWaitForCropCommand < 50 && cropRunFinished == false; currentWaitForCropCommand++) {
			cropRunFinished = (Boolean) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.currentTool,
					"mCropRunFinished");
			mSolo.sleep(50);
		}
		return cropRunFinished;
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
