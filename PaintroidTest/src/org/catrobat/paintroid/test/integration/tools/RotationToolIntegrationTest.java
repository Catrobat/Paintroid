/*
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

package org.catrobat.paintroid.test.integration.tools;

import org.catrobat.paintroid.test.integration.BaseIntegrationTestClass;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Color;
import android.graphics.Point;

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
	public void testRotationOfOnePixelTurnLeft() {
		// set top left pixel
		Point topLeftPixel = new Point(0, 0);
		mCurrentDrawingSurfaceBitmap.setPixel(topLeftPixel.x, topLeftPixel.y, Color.BLUE);

		// click rotation 90° left (check if para1 is the correct button)
		mSolo.clickOnView(mMenuBottomParameter1);

		// expected Pixel after rotation
		Point expectedPixle = new Point(0, mCurrentDrawingSurfaceBitmap.getWidth()); // or hight if it update is correct

		assertEquals("Rotation left didn't work (first time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

		// second rotation
		mSolo.clickOnView(mMenuBottomParameter1);

		expectedPixle.x = mCurrentDrawingSurfaceBitmap.getWidth();
		expectedPixle.y = mCurrentDrawingSurfaceBitmap.getHeight();
		assertEquals("Rotation left didn't work (second time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

		// third rotation
		mSolo.clickOnView(mMenuBottomParameter1);

		expectedPixle.x = mCurrentDrawingSurfaceBitmap.getHeight(); // or width if update is correct
		expectedPixle.y = 0;
		assertEquals("Rotation left didn't work (third time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

		// fully rotated
		mSolo.clickOnView(mMenuBottomParameter1);

		expectedPixle.x = 0;
		expectedPixle.y = 0;
		assertEquals("Rotation left didn't work (fourth time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

	}

	@Test
	public void testRotationOfOnePixelTurnRight() {
		// set top left pixel
		Point topLeftPixel = new Point(0, 0);
		mCurrentDrawingSurfaceBitmap.setPixel(topLeftPixel.x, topLeftPixel.y, Color.BLUE);

		// click rotation 90° right (check if para2 is the correct button)
		mSolo.clickOnView(mMenuBottomParameter2);

		// expected Pixel after rotation
		Point expectedPixle = new Point(mCurrentDrawingSurfaceBitmap.getWidth(), 0); // or hight??

		assertEquals("Rotation right didn't work (first time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

		// second rotation
		mSolo.clickOnView(mMenuBottomParameter2);

		expectedPixle.x = mCurrentDrawingSurfaceBitmap.getWidth();
		expectedPixle.y = mCurrentDrawingSurfaceBitmap.getHeight();
		assertEquals("Rotation right didn't work (second time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

		// third rotation
		mSolo.clickOnView(mMenuBottomParameter2);

		expectedPixle.x = 0;
		expectedPixle.y = mCurrentDrawingSurfaceBitmap.getHeight(); // or width??
		assertEquals("Rotation right didn't work (third time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

		// fully rotated
		mSolo.clickOnView(mMenuBottomParameter1);

		expectedPixle.x = 0;
		expectedPixle.y = 0;
		assertEquals("Rotation right didn't work (fourth time)",
				mCurrentDrawingSurfaceBitmap.getPixel(expectedPixle.x, expectedPixle.y), Color.BLUE);

	}

	@Test
	public void testBitmapSizeAferRotation() {
		int bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		mSolo.clickOnView(mMenuBottomParameter1);

		int bitmapWidthAfer = mCurrentDrawingSurfaceBitmap.getWidth(); // if it works or do an update
		int bitmapHeightAfer = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width afer rotation still the same", bitmapWidthBefore != bitmapWidthAfer);
		assertTrue("Bitmap Height afer rotation still the same", bitmapHeightBefore != bitmapHeightAfer);

		// second time
		bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		mSolo.clickOnView(mMenuBottomParameter1);

		bitmapWidthAfer = mCurrentDrawingSurfaceBitmap.getWidth(); // if it works or do an update
		bitmapHeightAfer = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width afer rotation still the same", bitmapWidthBefore != bitmapWidthAfer);
		assertTrue("Bitmap Height afer rotation still the same", bitmapHeightBefore != bitmapHeightAfer);

		// turn right
		bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		mSolo.clickOnView(mMenuBottomParameter2);// right direction

		bitmapWidthAfer = mCurrentDrawingSurfaceBitmap.getWidth(); // if it works or do an update
		bitmapHeightAfer = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width afer rotation still the same", bitmapWidthBefore != bitmapWidthAfer);
		assertTrue("Bitmap Height afer rotation still the same", bitmapHeightBefore != bitmapHeightAfer);

		// second time right
		bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		mSolo.clickOnView(mMenuBottomParameter2);// right direction

		bitmapWidthAfer = mCurrentDrawingSurfaceBitmap.getWidth(); // if it works or do an update
		bitmapHeightAfer = mCurrentDrawingSurfaceBitmap.getHeight();

		assertTrue("Bitmap Width afer rotation still the same", bitmapWidthBefore != bitmapWidthAfer);
		assertTrue("Bitmap Height afer rotation still the same", bitmapHeightBefore != bitmapHeightAfer);

	}

	@Test
	public void testDifferentColorsInEachEdge() {

		int bitmapWidthBefore = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightBefore = mCurrentDrawingSurfaceBitmap.getHeight();

		mCurrentDrawingSurfaceBitmap.setPixel(0, 0, Color.RED); // topLeft - red
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth(), 0, Color.GRAY); // topRight -
																										// Gray
		mCurrentDrawingSurfaceBitmap.setPixel(mCurrentDrawingSurfaceBitmap.getWidth(),
				mCurrentDrawingSurfaceBitmap.getHeight(), Color.YELLOW); // bottomRight - Yellow
		mCurrentDrawingSurfaceBitmap.setPixel(0, mCurrentDrawingSurfaceBitmap.getHeight(), Color.GREEN); // bottomLeft -
																											// Green

		mSolo.clickOnView(mMenuBottomParameter1);

		int bitmapWidthAfer = mCurrentDrawingSurfaceBitmap.getWidth();
		int bitmapHeightAfer = mCurrentDrawingSurfaceBitmap.getHeight();

		assertEquals("Top Left Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(0, 0), Color.GRAY);
		assertEquals("Top Right Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(mCurrentDrawingSurfaceBitmap.getWidth(), 0), Color.YELLOW);
		assertEquals("Bottom Right Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(
				mCurrentDrawingSurfaceBitmap.getWidth(), mCurrentDrawingSurfaceBitmap.getHeight()), Color.GREEN);
		assertEquals("Bottom Left Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(0, mCurrentDrawingSurfaceBitmap.getHeight()), Color.RED);

		assertEquals("Wrong width after rotation", bitmapHeightBefore, bitmapWidthAfer);
		assertEquals("Wrong height after rotation", bitmapWidthBefore, bitmapHeightAfer);

		// rotate back

		mSolo.clickOnView(mMenuBottomParameter2);

		assertEquals("Top Left Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(0, 0), Color.RED);
		assertEquals("Top Right Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(mCurrentDrawingSurfaceBitmap.getWidth(), 0), Color.GRAY);
		assertEquals("Bottom Right Pixel has wrong color", mCurrentDrawingSurfaceBitmap.getPixel(
				mCurrentDrawingSurfaceBitmap.getWidth(), mCurrentDrawingSurfaceBitmap.getHeight()), Color.YELLOW);
		assertEquals("Bottom Left Pixel has wrong color",
				mCurrentDrawingSurfaceBitmap.getPixel(0, mCurrentDrawingSurfaceBitmap.getHeight()), Color.GREEN);

	}

}
