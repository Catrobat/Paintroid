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

package org.catrobat.paintroid.test.junit.command;

import android.graphics.Bitmap;

import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResizeCommandTest extends CommandTestSetup {

	private int mResizeCoordinateXLeft;
	private int mResizeCoordinateYTop;
	private int mResizeCoordinateXRight;
	private int mResizeCoordinateYBottom;
	private int mMaximumBitmapResolution;
	private static final int mMaximumBitmapResolutionFactor = 4;

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		mResizeCoordinateXLeft = 0;
		mResizeCoordinateYTop = 0;
		mResizeCoordinateXRight = mBitmapUnderTest.getWidth() - 1;
		mResizeCoordinateYBottom = mBitmapUnderTest.getHeight() - 1;
		mMaximumBitmapResolution = mBitmapUnderTest.getWidth() * mBitmapUnderTest.getHeight()
				* mMaximumBitmapResolutionFactor;
		mCommandUnderTest = new ResizeCommand(mResizeCoordinateXLeft, mResizeCoordinateYTop,
				mResizeCoordinateXRight, mResizeCoordinateYBottom, mMaximumBitmapResolution);
		mCommandUnderTestNull = new ResizeCommand(1, 1, 2, 2, mMaximumBitmapResolution);
	}

	@Override
	@After
	public void tearDown() throws Exception {
		File fileToResizedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToResizedBitmap != null) {
			assertTrue(fileToResizedBitmap.delete());
		}
		super.tearDown();
	}

	@Test
	public void testIfBitmapIsCropped() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mResizeCoordinateXLeft = 1;
		mResizeCoordinateYTop = 1;
		mResizeCoordinateXRight = mBitmapUnderTest.getWidth() - 2;
		mResizeCoordinateYBottom = mBitmapUnderTest.getHeight() - 2;
		mCommandUnderTest = new ResizeCommand(mResizeCoordinateXLeft, mResizeCoordinateYTop,
				mResizeCoordinateXRight, mResizeCoordinateYBottom, mMaximumBitmapResolution);

		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);

		Bitmap croppedBitmap = mLayerUnderTest.getImage();
		assertEquals("Cropping failed, width not correct ", widthOriginal - mResizeCoordinateXLeft
				- (widthOriginal - (mResizeCoordinateXRight + 1)), croppedBitmap.getWidth());
		assertEquals("Cropping failed, height not correct ", heightOriginal - mResizeCoordinateYTop
				- (widthOriginal - (mResizeCoordinateYBottom + 1)), croppedBitmap.getHeight());
		croppedBitmap.recycle();

	}

	@Test
	public void testIfBitmapIsEnlarged() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mResizeCoordinateXLeft = -1;
		mResizeCoordinateYTop = -1;
		mResizeCoordinateXRight = mBitmapUnderTest.getWidth();
		mResizeCoordinateYBottom = mBitmapUnderTest.getHeight();
		mCommandUnderTest = new ResizeCommand(mResizeCoordinateXLeft, mResizeCoordinateYTop,
				mResizeCoordinateXRight, mResizeCoordinateYBottom, mMaximumBitmapResolution);

		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);

		Bitmap enlargedBitmap = mLayerUnderTest.getImage();
		assertEquals("Enlarging failed, width not correct ", widthOriginal - mResizeCoordinateXLeft
				- (widthOriginal - (mResizeCoordinateXRight + 1)), enlargedBitmap.getWidth());
		assertEquals("Enlarging failed, height not correct ", heightOriginal - mResizeCoordinateYTop
				- (widthOriginal - (mResizeCoordinateYBottom + 1)), enlargedBitmap.getHeight());
		enlargedBitmap.recycle();

	}

	@Test
	public void testIfBitmapIsShifted() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mResizeCoordinateXLeft = mBitmapUnderTest.getWidth() / 2 - 1;
		mResizeCoordinateYTop = mBitmapUnderTest.getHeight() / 2 - 1;
		mResizeCoordinateXRight = mResizeCoordinateXLeft + mBitmapUnderTest.getWidth() - 1;
		mResizeCoordinateYBottom = mResizeCoordinateYTop + mBitmapUnderTest.getHeight() - 1;
		mCommandUnderTest = new ResizeCommand(mResizeCoordinateXLeft, mResizeCoordinateYTop,
				mResizeCoordinateXRight, mResizeCoordinateYBottom, mMaximumBitmapResolution);

		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);

		Bitmap enlargedBitmap = mLayerUnderTest.getImage();
		assertEquals("Enlarging failed, width not correct ", widthOriginal, enlargedBitmap.getWidth());
		assertEquals("Enlarging failed, height not correct ", heightOriginal, enlargedBitmap.getHeight());
		enlargedBitmap.recycle();

	}

	@Test
	public void testIfMaximumResolutionIsRespected() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mCommandUnderTest = new ResizeCommand(0, 0, widthOriginal * 2, heightOriginal * 2, mMaximumBitmapResolution);

		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);

		assertEquals("Width should not have changed", widthOriginal, mLayerUnderTest.getImage().getWidth());
		assertEquals("Height should not have changed", heightOriginal, mLayerUnderTest.getImage().getHeight());
	}

	@Test
	public void testIfBitmapIsNotResizedWithInvalidBounds() throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		Bitmap originalBitmap = mLayerUnderTest.getImage();
		mCommandUnderTest = new ResizeCommand(mBitmapUnderTest.getWidth(), 0, mBitmapUnderTest.getWidth(),
				0, mMaximumBitmapResolution);

		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change if X left is larger than bitmap scope", originalBitmap.sameAs(mLayerUnderTest.getImage()));

		mCommandUnderTest = new ResizeCommand(-1, 0, -1, 0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change if X right is smaller than bitmap scope", originalBitmap.sameAs(mLayerUnderTest.getImage()));

		mCommandUnderTest = new ResizeCommand(0, mBitmapUnderTest.getHeight(), 0,
				mBitmapUnderTest.getHeight(), mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change if Y top is larger than bitmap scope", originalBitmap.sameAs(mLayerUnderTest.getImage()));

		mCommandUnderTest = new ResizeCommand(0, -1, 0, -1, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change if Y bottom is smaller than bitmap scope", originalBitmap.sameAs(mLayerUnderTest.getImage()));

		mCommandUnderTest = new ResizeCommand(1, 0, 0, 0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change with widthXRight < widthXLeft bound", originalBitmap.sameAs(mLayerUnderTest.getImage()));

		mCommandUnderTest = new ResizeCommand(0, 1, 0, 0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change with widthYBottom < widthYTop bound", originalBitmap.sameAs(mLayerUnderTest.getImage()));

		mCommandUnderTest = new ResizeCommand(0, 0, mBitmapUnderTest.getWidth() - 1,
				mBitmapUnderTest.getHeight() - 1, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mLayerUnderTest);
		assertTrue("bitmap must not change because bounds are the same as original bitmap", originalBitmap.sameAs(mLayerUnderTest.getImage()));
	}
}
