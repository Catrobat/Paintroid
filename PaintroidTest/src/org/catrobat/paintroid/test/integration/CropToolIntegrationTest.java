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

package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.tools.Tool.ToolType;
import org.catrobat.paintroid.tools.implementation.CropTool;
import org.catrobat.paintroid.ui.implementation.DrawingSurfaceImplementation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	private final int CROPPING_SLEEP_BETWEEN_FINISH_CHECK = 500;
	private final int MAXIMUM_CROPPING_TIMEOUT_COUNTS = 300;
	private final int STEP_COUNTER = 5;
	private final int LONG_DISTANCE = 100;
	private final int SHORT_DISTANCE = 50;
	private int mLineLength;
	private int mHorizontalLineStartX;
	private int mVerticalLineStartY;
	private int mStatusbarHeight;
	private Bitmap currentDrawingSurfaceBitmap;
	private float mCropBoundWidthXLeft;
	private float mCropBoundWidthXRight;
	private float mCropBoundHeightYTop;
	private float mCropBoundHeightYBottom;
	private double mMarginWidth = 0;
	private double mMarginHeight = 0;
	private double mStartZoomFactor = 1.0;

	public CropToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		try {
			currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
			mStartZoomFactor = (Float) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
					"START_ZOOM_FACTOR");
			// mInverseZoomFactorForOneMargin = (float) (1.0 + (1.0 - (Float) ) / 2.0);
			mMarginWidth = currentDrawingSurfaceBitmap.getWidth() * (1.0 - mStartZoomFactor);
			mMarginHeight = currentDrawingSurfaceBitmap.getHeight() * (1.0 - mStartZoomFactor);
		} catch (Exception whatever) {
			whatever.printStackTrace();
			fail(whatever.toString());
		}

		mLineLength = (currentDrawingSurfaceBitmap.getWidth() / 2);
		mHorizontalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 4);
		mVerticalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2 - mLineLength / 2);
		mStatusbarHeight = Utils.getStatusbarHeigt(getActivity());
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		Thread.sleep(1000);
		super.tearDown();
		Thread.sleep(500);
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		selectTool(ToolType.CROP);

		assertEquals("Zoom factor is wrong", 0.95f, PaintroidApplication.CURRENT_PERSPECTIVE.getScale());

		failWhenCroppingTimedOut();

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(2000);
		assertTrue("nothing to crop text missing",
				mSolo.waitForText(mSolo.getString(R.string.crop_nothing_to_corp), 1, TIMEOUT, true));

	}

	@Test
	public void testIfOnePixelIsFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		currentDrawingSurfaceBitmap.setPixel(currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(2000);

		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testIfMultiplePixelAreFound() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
				PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
		int originalWidth = currentDrawingSurfaceBitmap.getWidth();
		int originalHeight = currentDrawingSurfaceBitmap.getHeight();
		currentDrawingSurfaceBitmap.setPixel(1, 1, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth - 2, originalHeight - 2, Color.BLUE);

		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(2000);
		assertEquals("Wrong width after cropping ", originalWidth - 2,
				PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 2,
				PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testIfDrawingSurfaceBoundsAreFoundAndNotCropped() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
				PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");

		int originalWidth = currentDrawingSurfaceBitmap.getWidth();
		int originalHeight = currentDrawingSurfaceBitmap.getHeight();
		currentDrawingSurfaceBitmap.setPixel(originalWidth / 2, 0, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(0, originalHeight / 2, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth - 1, originalHeight / 2, Color.BLUE);
		currentDrawingSurfaceBitmap.setPixel(originalWidth / 2, originalHeight - 1, Color.BLUE);

		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(2000);
		assertEquals("Wrong width after cropping ", originalWidth,
				PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight,
				PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
	}

	@Test
	public void testIfClickOnCanvasDoesNothing() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		currentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		int drawingSurfaceOriginalWidth = currentDrawingSurfaceBitmap.getWidth();
		int drawingSurfaceOriginalHeight = currentDrawingSurfaceBitmap.getHeight();
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			currentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		standardAutoCrop();

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		assertEquals("Width changed:", drawingSurfaceOriginalWidth,
				PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight,
				PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
	}

	@Test
	public void testSmallBitmapCropping() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		currentDrawingSurfaceBitmap.setPixel(currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(2000);
		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testManualCroppingTakeMiddleOfBorder() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		// assertTrue("This test crashes the whole paintroid jenkins environment!", false);// FIXME

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		drawPlus();

		standardAutoCrop();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		int dragBottomBoundToY = currentDrawingSurfaceBitmap.getHeight() - LONG_DISTANCE;
		int dragTopBoundToY = LONG_DISTANCE;
		int dragLeftBoundToX = LONG_DISTANCE;
		int dragRightBoundToX = currentDrawingSurfaceBitmap.getWidth() - LONG_DISTANCE;

		doSupportDragOnDrawingSurface((currentDrawingSurfaceBitmap.getWidth() / 2.0) / mStartZoomFactor,
				(currentDrawingSurfaceBitmap.getWidth() / 2.0) / mStartZoomFactor, (mVerticalLineStartY + mLineLength)
						/ mStartZoomFactor, dragBottomBoundToY / mStartZoomFactor, STEP_COUNTER);
		doSupportDragOnDrawingSurface((currentDrawingSurfaceBitmap.getWidth() / 2) / mStartZoomFactor,
				(currentDrawingSurfaceBitmap.getWidth() / 2) / mStartZoomFactor,
				(mVerticalLineStartY + mStatusbarHeight) / mStartZoomFactor - mMarginHeight, dragTopBoundToY
						/ mStartZoomFactor, STEP_COUNTER);
		doSupportDragOnDrawingSurface(mHorizontalLineStartX / mStartZoomFactor, dragLeftBoundToX / mStartZoomFactor,
				(currentDrawingSurfaceBitmap.getHeight() / 2) / mStartZoomFactor,
				(currentDrawingSurfaceBitmap.getHeight() / 2) / mStartZoomFactor, STEP_COUNTER);
		doSupportDragOnDrawingSurface((mHorizontalLineStartX + mLineLength) / mStartZoomFactor - mMarginWidth,
				dragRightBoundToX / mStartZoomFactor, (currentDrawingSurfaceBitmap.getHeight() / 2) / mStartZoomFactor,
				(currentDrawingSurfaceBitmap.getHeight() / 2) / mStartZoomFactor, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Bottom Bound do not move correct", (int) mCropBoundHeightYBottom > (mVerticalLineStartY
				+ mLineLength - 1));
		assertTrue("Top Bound do not move correct", (int) mCropBoundHeightYTop < mVerticalLineStartY);
		assertTrue("Left Bound do not move correct", (int) mCropBoundWidthXLeft < mHorizontalLineStartX);
		assertTrue("Right Bound do not move correct", (int) mCropBoundWidthXRight > mHorizontalLineStartX + mLineLength
				- 1);

	}

	@Test
	public void testManualCroppingTakeEdges() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		// assertTrue("This test may crash the whole paintroid jenkins environment!", false);// FIXME

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		drawPlus();

		standardAutoCrop();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		int dragTopLeftToX = SHORT_DISTANCE;
		int dragTopLeftToY = SHORT_DISTANCE;
		int dragBottomRightToX = currentDrawingSurfaceBitmap.getWidth() - SHORT_DISTANCE;
		int dragBottomRightToY = currentDrawingSurfaceBitmap.getHeight() - SHORT_DISTANCE;

		doSupportDragOnDrawingSurface(mHorizontalLineStartX / mStartZoomFactor, dragTopLeftToX / mStartZoomFactor,
				mVerticalLineStartY / mStartZoomFactor, dragTopLeftToY / mStartZoomFactor, STEP_COUNTER);
		doSupportDragOnDrawingSurface((mHorizontalLineStartX + mLineLength) / mStartZoomFactor - mMarginWidth,
				dragBottomRightToX / mStartZoomFactor - mMarginWidth, (mVerticalLineStartY + mLineLength)
						/ mStartZoomFactor - mMarginHeight, dragBottomRightToY / mStartZoomFactor - mMarginHeight,
				STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound did not move correct", (int) mCropBoundHeightYTop < mVerticalLineStartY);
		assertTrue("Left bound did not move correct", (int) mCropBoundWidthXLeft < mHorizontalLineStartX);
		assertTrue("Bottom bound did not move correct", (int) mCropBoundHeightYBottom > mVerticalLineStartY
				+ mLineLength);
		assertTrue("Right bound did not move correct", (int) mCropBoundWidthXRight > mHorizontalLineStartX
				+ mLineLength);

		mSolo.clickOnView(mMenuBottomParameter1);

		failWhenCroppingTimedOut();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		int dragBottomLeftToX = SHORT_DISTANCE;
		int dragBottomLeftToY = currentDrawingSurfaceBitmap.getHeight() - SHORT_DISTANCE;
		int dragTopRightToX = currentDrawingSurfaceBitmap.getWidth() - SHORT_DISTANCE;
		int dragTopRightToY = SHORT_DISTANCE + mStatusbarHeight;

		doSupportDragOnDrawingSurface(mHorizontalLineStartX / mStartZoomFactor, dragBottomLeftToX / mStartZoomFactor,
				(mVerticalLineStartY + mLineLength) / mStartZoomFactor - mMarginHeight, dragBottomLeftToY
						/ mStartZoomFactor - mMarginHeight, STEP_COUNTER);
		doSupportDragOnDrawingSurface((mHorizontalLineStartX + mLineLength) / mStartZoomFactor, dragTopRightToX
				/ mStartZoomFactor, mVerticalLineStartY / mStartZoomFactor, dragTopRightToY / mStartZoomFactor,
				STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound did not move correct", (int) mCropBoundHeightYTop < mVerticalLineStartY);
		assertTrue("Left bound did not move correct", (int) mCropBoundWidthXLeft < mHorizontalLineStartX);
		assertTrue("Bottom bound did not move correct", (int) mCropBoundHeightYBottom > mVerticalLineStartY
				+ mLineLength);
		assertTrue("Right bound did not move correct", (int) mCropBoundWidthXRight > mHorizontalLineStartX
				+ mLineLength);

	}

	@Test
	public void testManualCroppingDropOutsideFromBitmap() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		// assertTrue("This test may crash the whole paintroid jenkins environment!", false);// FIXME

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		drawPlus();

		standardAutoCrop();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		int dragTopLeftToX = -SHORT_DISTANCE;
		int dragTopLeftToY = -SHORT_DISTANCE;
		int dragBottomRightToX = currentDrawingSurfaceBitmap.getWidth() + SHORT_DISTANCE;
		int dragBottomRightToY = currentDrawingSurfaceBitmap.getHeight() + SHORT_DISTANCE;

		doSupportDragOnDrawingSurface(mHorizontalLineStartX / mStartZoomFactor, dragTopLeftToX / mStartZoomFactor,
				mVerticalLineStartY / mStartZoomFactor, dragTopLeftToY / mStartZoomFactor, STEP_COUNTER);
		doSupportDragOnDrawingSurface((mHorizontalLineStartX + mLineLength) / mStartZoomFactor - mMarginWidth,
				dragBottomRightToX / mStartZoomFactor - mMarginWidth, (mVerticalLineStartY + mLineLength)
						/ mStartZoomFactor - mMarginHeight, dragBottomRightToY / mStartZoomFactor - mMarginHeight,
				STEP_COUNTER);

		getCurrentBorders();

		assertFalse("Top equals after drag", (int) mCropBoundHeightYTop == mVerticalLineStartY);
		assertFalse("Left equals after drag", (int) mCropBoundWidthXLeft == mHorizontalLineStartX);
		assertFalse("Bottom equals after drag", (int) mCropBoundHeightYBottom == mVerticalLineStartY + mLineLength);
		assertFalse("Right equals after drag", (int) mCropBoundWidthXRight == mHorizontalLineStartX + mLineLength);

		assertEquals("Top bound is not at bitmap top ", (int) mCropBoundHeightYTop, 0);
		assertEquals("Bottom bound is not at bitmap bottom ", (int) mCropBoundHeightYBottom,
				currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Left bound is not at bitmap left ", (int) mCropBoundWidthXLeft, 0);
		assertEquals("Right bound is not at bitmap right ", (int) mCropBoundWidthXRight,
				currentDrawingSurfaceBitmap.getWidth());

	}

	@Test
	public void testMoveBox() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		// assertTrue("This test may crash the whole paintroid jenkins environment!", false);// FIXME
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int horizontalMiddle = currentDrawingSurfaceBitmap.getWidth() / 2;
		int verticalMiddle = currentDrawingSurfaceBitmap.getHeight() / 2;

		drawPlus();

		standardAutoCrop();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		doSupportDragOnDrawingSurface(horizontalMiddle / mStartZoomFactor, horizontalMiddle / mStartZoomFactor
				- SHORT_DISTANCE, verticalMiddle / mStartZoomFactor,
				verticalMiddle / mStartZoomFactor - SHORT_DISTANCE, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound not correct after first drag", (int) mCropBoundHeightYTop < mVerticalLineStartY);
		assertTrue("Left bound not correct after first drag", (int) mCropBoundWidthXLeft < mHorizontalLineStartX);
		assertTrue("Bottom bound not correct after first drag",
				(int) mCropBoundHeightYBottom < (mVerticalLineStartY + mLineLength));
		assertTrue("Right bound not correct after first drag",
				(int) mCropBoundWidthXRight < (mHorizontalLineStartX + mLineLength));

		doSupportDragOnDrawingSurface(horizontalMiddle / mStartZoomFactor - SHORT_DISTANCE, horizontalMiddle
				/ mStartZoomFactor + SHORT_DISTANCE, verticalMiddle / mStartZoomFactor - SHORT_DISTANCE, verticalMiddle
				/ mStartZoomFactor + SHORT_DISTANCE, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound not correct after second drag", (int) mCropBoundHeightYTop > mVerticalLineStartY);
		assertTrue("Left bound not correct after second drag", (int) mCropBoundWidthXLeft > mHorizontalLineStartX);
		assertTrue("Bottom bound not correct after second drag",
				(int) mCropBoundHeightYBottom > (mVerticalLineStartY + mLineLength));
		assertTrue("Right bound not correct after second drag",
				(int) mCropBoundWidthXRight > (mHorizontalLineStartX + mLineLength));

		doSupportDragOnDrawingSurface(horizontalMiddle / mStartZoomFactor + SHORT_DISTANCE, 0, verticalMiddle
				/ mStartZoomFactor + SHORT_DISTANCE, 0, STEP_COUNTER);

		getCurrentBorders();

		assertEquals("Top should equal after third drag", (int) mCropBoundHeightYTop, 0);
		assertEquals("Left should be smaler after third drag", (int) mCropBoundWidthXLeft, 0);
		assertTrue("Bottom bound not correct after third drag",
				(int) mCropBoundHeightYBottom < (mVerticalLineStartY + mLineLength));
		assertTrue("Right bound not correct after third drag",
				(int) mCropBoundWidthXRight < (mHorizontalLineStartX + mLineLength));

		mSolo.clickOnView(mMenuBottomParameter1);
		failWhenCroppingTimedOut();

		doSupportDragOnDrawingSurface(horizontalMiddle / mStartZoomFactor, currentDrawingSurfaceBitmap.getWidth()
				/ mStartZoomFactor, verticalMiddle / mStartZoomFactor, currentDrawingSurfaceBitmap.getHeight()
				/ mStartZoomFactor, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound not correct after fourth drag", (int) mCropBoundHeightYTop > mVerticalLineStartY);
		assertTrue("Left bound not correct after fourth drag", (int) mCropBoundWidthXLeft > mHorizontalLineStartX);
		assertEquals("Bottom should equals height after fourth drag", (int) mCropBoundHeightYBottom,
				currentDrawingSurfaceBitmap.getHeight());
		assertEquals("Right should equals width after fourth drag", (int) mCropBoundWidthXRight,
				currentDrawingSurfaceBitmap.getWidth());
	}

	@Test
	public void testMinimumBox() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		// assertTrue("This test may crash the whole paintroid jenkins environment!", false);// FIXME
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		drawPlus();

		standardAutoCrop();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		doSupportDragOnDrawingSurface((mHorizontalLineStartX + mLineLength) / mStartZoomFactor - mMarginWidth, 0,
				(mVerticalLineStartY + mLineLength) / mStartZoomFactor, 0, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound is bigger than bottom bound", (int) mCropBoundHeightYTop < mCropBoundHeightYBottom);
		assertTrue("Left bound is bigger than right bound", (int) mCropBoundWidthXLeft < mCropBoundWidthXRight);
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);

	}

	private void getCurrentBorders() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		mCropBoundWidthXLeft = (Float) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCropBoundWidthXLeft");
		mCropBoundWidthXRight = (Float) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCropBoundWidthXRight");
		mCropBoundHeightYTop = (Float) PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
				"mCropBoundHeightYTop");
		mCropBoundHeightYBottom = (Float) PrivateAccess.getMemberValue(CropTool.class,
				PaintroidApplication.CURRENT_TOOL, "mCropBoundHeightYBottom");
	}

	private void standardAutoCrop() {
		selectTool(ToolType.CROP);
		failWhenCroppingTimedOut();
	}

	private void drawPlus() {

		int lineWidth = 10;
		int mHorizontalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 4);
		int horizontalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2);
		int verticalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 2);
		int mVertivalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2 - mLineLength / 2);

		int[] pixelsColorArray = new int[lineWidth * mLineLength];
		for (int indexColorArray = 0; indexColorArray < pixelsColorArray.length; indexColorArray++) {
			pixelsColorArray[indexColorArray] = Color.BLACK;
		}

		currentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, mLineLength, mHorizontalLineStartX,
				horizontalLineStartY, mLineLength, lineWidth);

		currentDrawingSurfaceBitmap.setPixels(pixelsColorArray, 0, lineWidth, verticalLineStartX, mVertivalLineStartY,
				lineWidth, mLineLength);
	}

	private int hasCroppingTimedOut() {
		int croppingTimeoutCounter = 0;
		Integer[][] croppingBounds = new Integer[4][2];
		try {
			croppingBounds[0][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXLeft");
			croppingBounds[1][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXRight");
			croppingBounds[2][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYTop");
			croppingBounds[3][0] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
					PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYBottom");

			for (; croppingTimeoutCounter < MAXIMUM_CROPPING_TIMEOUT_COUNTS; croppingTimeoutCounter++) {
				Thread.yield();
				croppingBounds[0][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXLeft");
				croppingBounds[1][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundWidthXRight");
				croppingBounds[2][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYTop");
				croppingBounds[3][1] = (Integer) PrivateAccess.getMemberValue(CropTool.class,
						PaintroidApplication.CURRENT_TOOL, "mIntermediateCropBoundHeightYBottom");

				if (croppingBounds[0][0].equals(croppingBounds[0][1])
						&& croppingBounds[1][0].equals(croppingBounds[1][1])
						&& croppingBounds[2][0].equals(croppingBounds[2][1])
						&& croppingBounds[3][0].equals(croppingBounds[3][1])
						&& (Boolean) (PrivateAccess.getMemberValue(CropTool.class, PaintroidApplication.CURRENT_TOOL,
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

	private void doSupportDragOnDrawingSurface(double fromXStart, double toXEnd, double fromYStart, double toYEnd,
			int steps) {
		PointF startPoint = new PointF((float) fromXStart, (float) fromYStart);
		PointF endPoint = new PointF((float) toXEnd, (float) toYEnd);
		PointF direction = new PointF(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
		PaintroidApplication.CURRENT_TOOL.handleDown(startPoint);
		for (; steps > 0; steps--) {
			PointF movePosition = new PointF(startPoint.x + direction.x / steps, startPoint.y + direction.y / steps);
			PaintroidApplication.CURRENT_TOOL.handleMove(movePosition);
			mSolo.sleep(5);
		}
		PaintroidApplication.CURRENT_TOOL.handleUp(endPoint);
	}
}
