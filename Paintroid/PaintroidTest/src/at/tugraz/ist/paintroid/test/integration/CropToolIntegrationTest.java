/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.integration;

import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import at.tugraz.ist.paintroid.PaintroidApplication;
import at.tugraz.ist.paintroid.R;
import at.tugraz.ist.paintroid.test.utils.PrivateAccess;
import at.tugraz.ist.paintroid.tools.Tool.ToolType;
import at.tugraz.ist.paintroid.tools.implementation.CropTool;
import at.tugraz.ist.paintroid.ui.implementation.DrawingSurfaceImplementation;

public class CropToolIntegrationTest extends BaseIntegrationTestClass {

	private final int CROPPING_TIMOUT = 5000;
	private final int STEP_COUNTER = 2;
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

	public CropToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();
		// currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		try {
			currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
					PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
		} catch (Exception whatever) {
			// TODO Auto-generated catch block
			whatever.printStackTrace();
			fail(whatever.toString());
		}

		mLineLength = (currentDrawingSurfaceBitmap.getWidth() / 2);
		mHorizontalLineStartX = (currentDrawingSurfaceBitmap.getWidth() / 4);
		mVerticalLineStartY = (currentDrawingSurfaceBitmap.getHeight() / 2 - mLineLength / 2);
		mStatusbarHeight = Utils.getStatusbarHeigt(getActivity());
	}

	@Test
	public void testWhenNoPixelIsOnBitmap() {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		selectTool(ToolType.CROP);

		assertEquals("Zoom factor is wrong", 0.95f, PaintroidApplication.CURRENT_PERSPECTIVE.getScale());

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

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
		// Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth - 2,
				PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight - 2,
				PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(0, 0)));
		// =======
		// mSolo.clickOnView(mToolBarButtonTwo);
		// mSolo.sleep(1000);
		// currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		// assertEquals("Wrong width after cropping ", originalWidth - 2, currentDrawingSurfaceBitmap.getWidth());
		// assertEquals("Wrong height after cropping ", originalHeight - 2, currentDrawingSurfaceBitmap.getHeight());
		// assertEquals("Wrong color of cropped bitmap", Color.BLUE, currentDrawingSurfaceBitmap.getPixel(0, 0));
		// >>>>>>> refs/remotes/origin/master
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
		// assertEquals(currentDrawingSurfaceBitmap, PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
		// PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap"));

		// Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", originalWidth,
				PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", originalHeight,
				PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
	}

	@Test
	public void testIfClickOnCanvasDoesNothing() {
		// try {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		// <<<<<<< HEAD
		// Bitmap currentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(
		// DrawingSurfaceImplementation.class, PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap");
		// currentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		// int drawingSurfaceOriginalWidth = currentDrawingSurfaceBitmap.getWidth();
		// int drawingSurfaceOriginalHeight = currentDrawingSurfaceBitmap.getHeight();
		// for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
		// currentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		// }
		//
		// selectTool(ToolType.CROP);
		//
		// int croppingTimeoutCounter = hasCroppingTimedOut();
		// if (croppingTimeoutCounter >= 0) {
		// fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		// }
		//
		// mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		// // Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		// assertEquals("Width changed:", drawingSurfaceOriginalWidth,
		// PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		// assertEquals("Height changed:", drawingSurfaceOriginalHeight,
		// PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
		// } catch (Exception whatever) {
		// fail(whatever.toString());
		// =======
		currentDrawingSurfaceBitmap.eraseColor(Color.BLACK);
		int drawingSurfaceOriginalWidth = currentDrawingSurfaceBitmap.getWidth();
		int drawingSurfaceOriginalHeight = currentDrawingSurfaceBitmap.getHeight();
		for (int indexWidth = 0; indexWidth < drawingSurfaceOriginalWidth; indexWidth++) {
			currentDrawingSurfaceBitmap.setPixel(indexWidth, 0, Color.TRANSPARENT);
		}

		standardAutoCrop();

		mSolo.clickOnScreen(mScreenWidth / 2, mScreenHeight / 2);
		// currentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Width changed:", drawingSurfaceOriginalWidth,
				PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Height changed:", drawingSurfaceOriginalHeight,
				PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
	}

	@Test
	public void testSmallBitmapCropping() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		// <<<<<<< HEAD
		// ((Bitmap) PrivateAccess.getMemberValue(DrawingSurfaceImplementation.class,
		// PaintroidApplication.DRAWING_SURFACE, "mWorkingBitmap")).setPixel(
		// PaintroidApplication.DRAWING_SURFACE.getBitmapWidth() / 2,
		// PaintroidApplication.DRAWING_SURFACE.getBitmapHeight() / 2, Color.BLUE);
		// =======
		currentDrawingSurfaceBitmap.setPixel(currentDrawingSurfaceBitmap.getWidth() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, Color.BLUE);
		standardAutoCrop();

		mSolo.clickOnView(mMenuBottomParameter2);
		mSolo.sleep(2000);
		// Bitmap newCurrentDrawingSurfaceBitmap = PaintroidApplication.DRAWING_SURFACE.getBitmap();
		assertEquals("Wrong width after cropping ", 1, PaintroidApplication.DRAWING_SURFACE.getBitmapWidth());
		assertEquals("Wrong height after cropping ", 1, PaintroidApplication.DRAWING_SURFACE.getBitmapHeight());
		assertEquals("Wrong color of cropped bitmap", Color.BLUE,
				PaintroidApplication.DRAWING_SURFACE.getBitmapColor(new PointF(0, 0)));
	}

	@Test
	public void testManualCroppingTakeMiddleOfBorder() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

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

		mSolo.drag(currentDrawingSurfaceBitmap.getWidth() / 2, currentDrawingSurfaceBitmap.getWidth() / 2,
				mVerticalLineStartY + mLineLength + mStatusbarHeight, dragBottomBoundToY, STEP_COUNTER);
		mSolo.drag(currentDrawingSurfaceBitmap.getWidth() / 2, currentDrawingSurfaceBitmap.getWidth() / 2,
				mVerticalLineStartY + mStatusbarHeight, dragTopBoundToY, STEP_COUNTER);
		mSolo.drag(mHorizontalLineStartX, dragLeftBoundToX, currentDrawingSurfaceBitmap.getHeight() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, STEP_COUNTER);
		mSolo.drag(mHorizontalLineStartX + mLineLength, dragRightBoundToX, currentDrawingSurfaceBitmap.getHeight() / 2,
				currentDrawingSurfaceBitmap.getHeight() / 2, STEP_COUNTER);

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

		mSolo.drag(mHorizontalLineStartX, dragTopLeftToX, mVerticalLineStartY + mStatusbarHeight, dragTopLeftToY,
				STEP_COUNTER);
		mSolo.drag(mHorizontalLineStartX + mLineLength, dragBottomRightToX, mVerticalLineStartY + mLineLength
				+ mStatusbarHeight, dragBottomRightToY, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound did not move correct", (int) mCropBoundHeightYTop < mVerticalLineStartY);
		assertTrue("Left bound did not move correct", (int) mCropBoundWidthXLeft < mHorizontalLineStartX);
		assertTrue("Bottom bound did not move correct", (int) mCropBoundHeightYBottom > mVerticalLineStartY
				+ mLineLength);
		assertTrue("Right bound did not move correct", (int) mCropBoundWidthXRight > mHorizontalLineStartX
				+ mLineLength);

		mSolo.clickOnView(mMenuBottomParameter1);

		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		int dragBottomLeftToX = SHORT_DISTANCE;
		int dragBottomLeftToY = currentDrawingSurfaceBitmap.getHeight() - SHORT_DISTANCE;
		int dragTopRightToX = currentDrawingSurfaceBitmap.getWidth() - SHORT_DISTANCE;
		int dragTopRightToY = SHORT_DISTANCE + mStatusbarHeight;

		mSolo.drag(mHorizontalLineStartX, dragBottomLeftToX, mVerticalLineStartY + mLineLength + mStatusbarHeight,
				dragBottomLeftToY, STEP_COUNTER);
		mSolo.drag(mHorizontalLineStartX + mLineLength, dragTopRightToX, mVerticalLineStartY + mStatusbarHeight,
				dragTopRightToY, STEP_COUNTER);

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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		drawPlus();

		standardAutoCrop();

		hasCroppingTimedOut();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		int dragTopLeftToX = -SHORT_DISTANCE;
		int dragTopLeftToY = -SHORT_DISTANCE;
		int dragBottomRightToX = currentDrawingSurfaceBitmap.getWidth() + SHORT_DISTANCE;
		int dragBottomRightToY = currentDrawingSurfaceBitmap.getHeight() + SHORT_DISTANCE;

		mSolo.drag(mHorizontalLineStartX, dragTopLeftToX, mVerticalLineStartY + mStatusbarHeight, dragTopLeftToY,
				STEP_COUNTER);
		mSolo.drag(mHorizontalLineStartX + mLineLength, dragBottomRightToX, mVerticalLineStartY + mLineLength
				+ mStatusbarHeight, dragBottomRightToY, STEP_COUNTER);

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
		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		int horizontalMiddle = currentDrawingSurfaceBitmap.getWidth() / 2;
		int verticalMiddle = currentDrawingSurfaceBitmap.getHeight() / 2;

		drawPlus();

		standardAutoCrop();
		try {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu), true);
		} catch (Exception menuItemnotFound) {
			mSolo.clickOnMenuItem(mSolo.getString(R.string.menu_hide_menu_condensed), true);
		}
		// ((SherlockActivity) PaintroidApplication.APPLICATION_CONTEXT).getSupportActionBar().hide();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		mSolo.drag(horizontalMiddle, horizontalMiddle - SHORT_DISTANCE, verticalMiddle,
				verticalMiddle - SHORT_DISTANCE, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound not correct after first drag", (int) mCropBoundHeightYTop < mVerticalLineStartY);
		assertTrue("Left bound not correct after first drag", (int) mCropBoundWidthXLeft < mHorizontalLineStartX);
		assertTrue("Bottom bound not correct after first drag",
				(int) mCropBoundHeightYBottom < (mVerticalLineStartY + mLineLength));
		assertTrue("Right bound not correct after first drag",
				(int) mCropBoundWidthXRight < (mHorizontalLineStartX + mLineLength));

		mSolo.drag(horizontalMiddle - SHORT_DISTANCE, horizontalMiddle + SHORT_DISTANCE, verticalMiddle
				- SHORT_DISTANCE, verticalMiddle + SHORT_DISTANCE, STEP_COUNTER);

		getCurrentBorders();

		assertTrue("Top bound not correct after second drag", (int) mCropBoundHeightYTop > mVerticalLineStartY);
		assertTrue("Left bound not correct after second drag", (int) mCropBoundWidthXLeft > mHorizontalLineStartX);
		assertTrue("Bottom bound not correct after second drag",
				(int) mCropBoundHeightYBottom > (mVerticalLineStartY + mLineLength));
		assertTrue("Right bound not correct after second drag",
				(int) mCropBoundWidthXRight > (mHorizontalLineStartX + mLineLength));

		mSolo.drag(horizontalMiddle + SHORT_DISTANCE, 0, verticalMiddle + SHORT_DISTANCE, mStatusbarHeight,
				STEP_COUNTER);

		getCurrentBorders();

		assertEquals("Top should equal after third drag", (int) mCropBoundHeightYTop, 0);
		assertEquals("Left should be smaler after third drag", (int) mCropBoundWidthXLeft, 0);
		assertTrue("Bottom bound not correct after third drag",
				(int) mCropBoundHeightYBottom < (mVerticalLineStartY + mLineLength));
		assertTrue("Right bound not correct after third drag",
				(int) mCropBoundWidthXRight < (mHorizontalLineStartX + mLineLength));

		int centerX = (int) (mCropBoundWidthXRight - mCropBoundWidthXLeft) / 2;
		int centerY = (int) (mCropBoundHeightYBottom - mCropBoundHeightYTop) / 2;

		Point canvasCenterPoint = new Point(centerX, centerY);
		Point screenCenterPoint = at.tugraz.ist.paintroid.test.utils.Utils.convertFromCanvasToScreen(canvasCenterPoint,
				PaintroidApplication.CURRENT_PERSPECTIVE);

		mSolo.drag(screenCenterPoint.x, currentDrawingSurfaceBitmap.getWidth(), screenCenterPoint.y,
				currentDrawingSurfaceBitmap.getHeight(), STEP_COUNTER);

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

		assertTrue("Waiting for DrawingSurface", mSolo.waitForView(DrawingSurfaceImplementation.class, 1, TIMEOUT));

		drawPlus();

		standardAutoCrop();

		getCurrentBorders();

		assertEquals("Bottom Bound not correct", (int) mCropBoundHeightYBottom, (mVerticalLineStartY + mLineLength - 1));
		assertEquals("Top Bound not correct", (int) mCropBoundHeightYTop, mVerticalLineStartY);
		assertEquals("Left Bound not correct", (int) mCropBoundWidthXLeft, mHorizontalLineStartX);
		assertEquals("Right Bound not correct", (int) mCropBoundWidthXRight, mHorizontalLineStartX + mLineLength - 1);

		mSolo.drag(mHorizontalLineStartX + mLineLength, 0, mVerticalLineStartY + mLineLength + mStatusbarHeight, 0,
				STEP_COUNTER);

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
		int croppingTimeoutCounter = hasCroppingTimedOut();
		if (croppingTimeoutCounter >= 0) {
			fail("Cropping algorithm took too long " + croppingTimeoutCounter * TIMEOUT + "ms");
		}
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
		int maximumCroppingTimeoutCounts = 30;
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

			for (; croppingTimeoutCounter < maximumCroppingTimeoutCounts; croppingTimeoutCounter++) {
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

					mSolo.sleep(CROPPING_TIMOUT);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(e.toString(), false);
		}
		if (croppingTimeoutCounter >= maximumCroppingTimeoutCounts) {
			return croppingTimeoutCounter;
		}
		return -1;
	}
}
