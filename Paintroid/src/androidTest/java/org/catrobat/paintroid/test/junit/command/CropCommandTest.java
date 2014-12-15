/**
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

package org.catrobat.paintroid.test.junit.command;

import java.io.File;

import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.CropCommand;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CropCommandTest extends CommandTestSetup {

	private int mCropCoordinateXLeft;
	private int mCropCoordinateYTop;
	private int mCropCoordinateXRight;
	private int mCropCoordinateYBottom;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCropCoordinateXLeft = 1;
		mCropCoordinateYTop = 1;
		mCropCoordinateXRight = mBitmapUnderTest.getWidth() - 1;
		mCropCoordinateYBottom = mBitmapUnderTest.getHeight() - 1;
		mCommandUnderTest = new CropCommand(mCropCoordinateXLeft, mCropCoordinateYTop, mCropCoordinateXRight,
				mCropCoordinateYBottom);
		mCommandUnderTestNull = new CropCommand(1, 1, 2, 2);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null)
			fileToCroppedBitmap.delete();
		super.tearDown();
	}

	@Test
	public void testIfBitmapIsCropped() throws InterruptedException, SecurityException, IllegalArgumentException,
			NoSuchFieldException, IllegalAccessException {
		int widthOriginal = mBitmapUnderTest.getWidth();
		int heightOriginal = mBitmapUnderTest.getHeight();
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(100);

		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap == null) {
			fail("failed to store cropped bitmap");
		}

		Bitmap croppedBitmap = BitmapFactory.decodeFile(fileToCroppedBitmap.getAbsolutePath());
		fileToCroppedBitmap.delete();
		assertEquals("Cropping failed width not correct ", widthOriginal - mCropCoordinateXLeft
				- (widthOriginal - mCropCoordinateXRight), croppedBitmap.getWidth());
		assertEquals("Cropping failed height not correct ", heightOriginal - mCropCoordinateYTop
				- (widthOriginal - mCropCoordinateYBottom), croppedBitmap.getHeight());
		croppedBitmap.recycle();
		croppedBitmap = null;

	}

	@Test
	public void testIfBitmapIsNotCroppedWithInvalidBounds() throws InterruptedException, SecurityException,
			IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		mCommandUnderTest = new CropCommand(1, 1, 1, mBitmapUnderTest.getHeight());
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		File fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with invalid Y bottom bound");
		}
		mCommandUnderTest = new CropCommand(1, 1, mBitmapUnderTest.getWidth(), 1);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with invalid X right bound");
		}
		mCommandUnderTest = new CropCommand(-1, 1, 1, 1);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with invalid X left bound");
		}
		mCommandUnderTest = new CropCommand(1, -1, 1, 1);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with invalid Y top bound");
		}

		mCommandUnderTest = new CropCommand(1, 1, 0, 1);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with widthXRight < widthXLeft bound");
		}

		mCommandUnderTest = new CropCommand(0, 1, 1, 0);
		mCommandUnderTest.run(mCanvasUnderTest, mCanvasBitmapUnderTest);
		Thread.sleep(50);
		fileToCroppedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
				"mFileToStoredBitmap");
		if (fileToCroppedBitmap != null) {
			fileToCroppedBitmap.delete();
			fail("bitmap must not be created with widthYBottom < widthYTop bound");
		}

		mCommandUnderTest = new CropCommand(0, 0, mBitmapUnderTest.getWidth(), mBitmapUnderTest.getHeight());
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
