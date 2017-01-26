/*
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
import org.catrobat.paintroid.ui.DrawingSurface;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

public class RotationToolIntegrationTest extends BaseIntegrationTestClass {

	public RotationToolIntegrationTest() throws Exception {
		super();
	}

	@Override
	@Before
	protected void setUp() {
		super.setUp();

	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testRotationOfOnePixelTurnLeft() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		Point topLeftPixel = new Point(0, 0);

		mCurrentDrawingSurfaceBitmap.setPixel(topLeftPixel.x, topLeftPixel.y, Color.BLUE);
		mCurrentDrawingSurfaceBitmap.setPixel(1, 1, Color.BLACK);

		selectTool(ToolType.ROTATE);
		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		Point expectedPixle = new Point(0, mCurrentDrawingSurfaceBitmap.getHeight() - 1);

		assertEquals("Rotation left didn't work (first time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

		// second rotation
		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		expectedPixle.x = mCurrentDrawingSurfaceBitmap.getWidth() - 1;
		expectedPixle.y = mCurrentDrawingSurfaceBitmap.getHeight() - 1;
		assertEquals("Rotation left didn't work (second time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

		// third rotation
		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		expectedPixle.x = mCurrentDrawingSurfaceBitmap.getWidth() - 1;
		expectedPixle.y = 0;
		assertEquals("Rotation left didn't work (third time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

		// fully rotated
		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		expectedPixle.x = 0;
		expectedPixle.y = 0;
		assertEquals("Rotation left didn't work (fourth time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

	}

	@Test
	public void testRotationOfOnePixelTurnRight() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		Point topLeftPixel = new Point(0, 0);
		mCurrentDrawingSurfaceBitmap.setPixel(topLeftPixel.x, topLeftPixel.y, Color.BLUE);

		selectTool(ToolType.ROTATE);

		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		Point expectedPixle = new Point(mCurrentDrawingSurfaceBitmap.getWidth() - 1, 0);

		assertEquals("Rotation right didn't work (first time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

		// second rotation
		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		expectedPixle.x = mCurrentDrawingSurfaceBitmap.getWidth() - 1;
		expectedPixle.y = mCurrentDrawingSurfaceBitmap.getHeight() - 1;
		assertEquals("Rotation right didn't work (second time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

		// third rotation
		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		expectedPixle.x = 0;
		expectedPixle.y = mCurrentDrawingSurfaceBitmap.getHeight() - 1;
		assertEquals("Rotation right didn't work (third time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

		// fully rotated
		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		expectedPixle.x = 0;
		expectedPixle.y = 0;
		assertEquals("Rotation right didn't work (fourth time)", Color.BLUE,
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y));

	}

	@Test
	public void testBitmapSizeAfterRotation() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		selectTool(ToolType.ROTATE);

		int bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		int bitmapWidthAfter = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightAfter = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("old bitmap width should be new bitmap height", bitmapWidthBefore == bitmapHeightAfter);
		assertTrue("old bitmap height should be new bitmap width", bitmapHeightBefore == bitmapWidthAfter);

		// turn left second time
		bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		bitmapWidthAfter = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightAfter = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width after rotation still the same", bitmapWidthBefore != bitmapWidthAfter);
		assertTrue("Bitmap Height after rotation still the same", bitmapHeightBefore != bitmapHeightAfter);

		// turn right
		bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		bitmapWidthAfter = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightAfter = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width after rotation still the same", bitmapWidthBefore != bitmapWidthAfter);
		assertTrue("Bitmap Height after rotation still the same", bitmapHeightBefore != bitmapHeightAfter);

		// second time right
		bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		bitmapWidthAfter = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightAfter = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width after rotation still the same", bitmapWidthBefore != bitmapWidthAfter);
		assertTrue("Bitmap Height after rotation still the same", bitmapHeightBefore != bitmapHeightAfter);

	}

	@Test
	public void testRotationWithDifferentColorsInEachEdge() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		selectTool(ToolType.ROTATE);

		int bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		mCurrentDrawingSurfaceBitmap.setPixel(0, 0, Color.RED);
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth() - 1, 0, Color.GRAY);
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth() - 1,
				mCurrentDrawingSurfaceBitmap.getHeight() - 1, Color.YELLOW);
		mCurrentDrawingSurfaceBitmap.setPixel(0, mCurrentDrawingSurfaceBitmap.getHeight() - 1, Color.GREEN);

		rotateLeft();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		int bitmapWidthAfter = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightAfter = mCurrentDrawingSurfaceBitmap.getHeight();

		assertEquals("Top Left Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(0, 0), Color.GRAY);
		assertEquals("Top Right Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(mCurrentDrawingSurfaceBitmap.getWidth() - 1, 0), Color.YELLOW);
		assertEquals("Bottom Right Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(
				mCurrentDrawingSurfaceBitmap.getWidth() - 1, mCurrentDrawingSurfaceBitmap.getHeight() - 1), Color.GREEN);
		assertEquals("Bottom Left Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(0, mCurrentDrawingSurfaceBitmap.getHeight() - 1), Color.RED);

		assertEquals("Wrong width after rotation", bitmapHeightBefore, bitmapWidthAfter);
		assertEquals("Wrong height after rotation", bitmapWidthBefore, bitmapHeightAfter);

		// rotate back

		rotateRight();

		mCurrentDrawingSurfaceBitmap = (Bitmap) PrivateAccess.getMemberValue(DrawingSurface.class,
				PaintroidApplication.drawingSurface, "mWorkingBitmap");

		assertEquals("Top Left Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(0, 0), Color.RED);
		assertEquals("Top Right Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(mCurrentDrawingSurfaceBitmap.getWidth() - 1, 0), Color.GRAY);
		assertEquals("Bottom Right Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(
				mCurrentDrawingSurfaceBitmap.getWidth() - 1, mCurrentDrawingSurfaceBitmap.getHeight() - 1),
				Color.YELLOW);
		assertEquals("Bottom Left Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(0, mCurrentDrawingSurfaceBitmap.getHeight() - 1), Color.GREEN);

	}

	@Test
	public void testIfBitmapIsCenteredAfterRotation() throws SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {

		Point topLeftPoint = new Point(1, 1);
		Point checkPoint = Utils.convertFromCanvasToScreen(topLeftPoint, PaintroidApplication.perspective);
		assertTrue("Checkpoint is not on the left side of the center", checkPoint.x < mScreenWidth / 2);

		float getAwayFromNavigationDrawer = 42;
		PointF startPointOne = new PointF(getAwayFromNavigationDrawer, mScreenHeight / 2.0f - 10);
		PointF startPointTwo = new PointF(getAwayFromNavigationDrawer, mScreenHeight / 2.0f + 10);
		PointF endPointOne = new PointF(mScreenWidth - 10, mScreenHeight - 20);
		PointF endPointTwo = new PointF(mScreenWidth - 10, mScreenHeight - 10);
		mSolo.swipe(startPointOne, startPointTwo, endPointOne, endPointTwo);

		checkPoint = Utils.convertFromCanvasToScreen(topLeftPoint, PaintroidApplication.perspective);
		assertTrue("Checkpoint is not on the right side of the center", checkPoint.x > mScreenWidth / 2);

		selectTool(ToolType.ROTATE);
		rotateLeft();

		checkPoint = Utils.convertFromCanvasToScreen(topLeftPoint, PaintroidApplication.perspective);
		assertTrue("Checkpoint is not on the left side of the center", checkPoint.x < mScreenWidth / 2);

	}


	private void rotateLeft() {
		assertTrue("Tool options should be visible", toolOptionsAreShown());
		mSolo.clickOnView(mSolo.getView(R.id.rotate_left_btn));
		mSolo.sleep(500);
	}

	private void rotateRight() {
		assertTrue("Tool options should be visible", toolOptionsAreShown());
		mSolo.clickOnView(mSolo.getView(R.id.rotate_right_btn));
		mSolo.sleep(500);
	}

}
