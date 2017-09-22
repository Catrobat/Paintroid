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

import java.io.File;

import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.ResizeCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ResizeCommandTest extends CommandTestSetup {

	private int mResizeCoordinateXLeft;
	private int mResizeCoordinateYTop;
	private int mResizeCoordinateXRight;
	private int mResizeCoordinateYBottom;
	private int mMaximumBitmapResolution;
	private int mMaximumBitmapResolutionFactor = 4;

	@Override
	@Before
	protected void setUp() throws Exception {
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
	protected void tearDown() throws Exception {
		File fileToResizedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToResizedBitmap != null)
			fileToResizedBitmap.delete();
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
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(100);

		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap == null) {
			fail("failed to store cropped bitmap");
		}

		Bitmap croppedBitmap = BitmapFactory.decodeFile(fileToCroppedBitmap.getAbsolutePath());
		fileToCroppedBitmap.delete();
		assertEquals("Cropping failed, width not correct ", widthOriginal - mResizeCoordinateXLeft
				- (widthOriginal - (mResizeCoordinateXRight + 1)), croppedBitmap.getWidth());
		assertEquals("Cropping failed, height not correct ", heightOriginal - mResizeCoordinateYTop
				- (widthOriginal - (mResizeCoordinateYBottom + 1)), croppedBitmap.getHeight());
		croppedBitmap.recycle();
		croppedBitmap = null;

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
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(100);

		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap == null) {
			fail("failed to store cropped bitmap");
		}

		Bitmap enlargedBitmap = BitmapFactory.decodeFile(fileToCroppedBitmap.getAbsolutePath());
		fileToCroppedBitmap.delete();
		assertEquals("Enlarging failed, width not correct ", widthOriginal - mResizeCoordinateXLeft
				- (widthOriginal - (mResizeCoordinateXRight + 1)), enlargedBitmap.getWidth());
		assertEquals("Enlarging failed, height not correct ", heightOriginal - mResizeCoordinateYTop
				- (widthOriginal - (mResizeCoordinateYBottom + 1)), enlargedBitmap.getHeight());
		enlargedBitmap.recycle();
		enlargedBitmap = null;

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
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(100);

		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap == null) {
			fail("failed to store cropped bitmap");
		}

		Bitmap enlargedBitmap = BitmapFactory.decodeFile(fileToCroppedBitmap.getAbsolutePath());
		fileToCroppedBitmap.delete();
		assertEquals("Enlarging failed, width not correct ", widthOriginal, enlargedBitmap.getWidth());
		assertEquals("Enlarging failed, height not correct ", heightOriginal, enlargedBitmap.getHeight());
		enlargedBitmap.recycle();
		enlargedBitmap = null;

	}

	@Test
	public void testIfMaximumResolutionIsRespected() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mCommandUnderTest = new ResizeCommand(0, 0, widthOriginal * 2, heightOriginal * 2, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created if bitmap resolution would get too high");
		}

	}

	@Test
	public void testIfBitmapIsNotResizedWithInvalidBounds() throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		mCommandUnderTest = new ResizeCommand(mBitmapUnderTest.getWidth(), 0, mBitmapUnderTest.getWidth(),
				0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created if X left is larger than bitmap scope");
		}

		mCommandUnderTest = new ResizeCommand(-1, 0, -1, 0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created if X right is smaller than bitmap scope");
		}

		mCommandUnderTest = new ResizeCommand(0, mBitmapUnderTest.getHeight(), 0,
				mBitmapUnderTest.getHeight(), mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created if Y top is larger than bitmap scope");
		}

		mCommandUnderTest = new ResizeCommand(0, -1, 0, -1, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created if Y bottom is smaller than bitmap scope");
		}

		mCommandUnderTest = new ResizeCommand(1, 0, 0, 0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with widthXRight < widthXLeft bound");
		}

		mCommandUnderTest = new ResizeCommand(0, 1, 0, 0, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with widthYBottom < widthYTop bound");
		}

		mCommandUnderTest = new ResizeCommand(0, 0, mBitmapUnderTest.getWidth() - 1,
				mBitmapUnderTest.getHeight() - 1, mMaximumBitmapResolution);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created because bounds are the same as original bitmap");
		}

	}
}
