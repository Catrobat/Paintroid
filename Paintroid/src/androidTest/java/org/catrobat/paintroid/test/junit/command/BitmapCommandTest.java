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

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.BitmapCommand;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class BitmapCommandTest extends CommandTestSetup {
	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mCommandUnderTest = new BitmapCommand(mBitmapUnderTest);
		mCommandUnderTestNull = new BitmapCommand(null);
		mCanvasBitmapUnderTest.eraseColor(BITMAP_BASE_COLOR - 10);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testRunInsertNewBitmap() {
		Bitmap hasToBeTransparentBitmap = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		hasToBeTransparentBitmap.eraseColor(Color.DKGRAY);
		Bitmap bitmapToCompare = mBitmapUnderTest.copy(Config.ARGB_8888, false);
		try {

			assertNull("There should not be a file for a bitmap at the beginning.",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));

			mCommandUnderTest.run(mCanvasUnderTest, hasToBeTransparentBitmap);

			assertNull("Bitmap is not cleaned up.",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
			PaintroidAsserts.assertBitmapEquals(PaintroidApplication.drawingSurface.getBitmapCopy(), bitmapToCompare);
			File fileToStoredBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest,
					"mFileToStoredBitmap");
			assertNotNull("Bitmap is not stored to filesystem.", fileToStoredBitmap);
			assertTrue("There is nothing in the bitmap file.", fileToStoredBitmap.length() > 0);

			fileToStoredBitmap.delete();

		} catch (Exception e) {
			fail("Failed to replace new bitmap:" + e.toString());
		} finally {
			if (hasToBeTransparentBitmap != null) {
				hasToBeTransparentBitmap.recycle();
				hasToBeTransparentBitmap = null;
			}

			if (bitmapToCompare != null) {
				bitmapToCompare.recycle();
				bitmapToCompare = null;
			}
		}
	}

	@Test
	public void testRunReplaceBitmapFromFileSystem() {
		Bitmap bitmapToCompare = mBitmapUnderTest.copy(Config.ARGB_8888, false);
		try {
			assertNull(
					"There should not be a file in the system (hint: check if too many tests crashed and no files were deleted)",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));

			mCommandUnderTest.run(mCanvasUnderTest, null);
			assertNotNull("No file - no restore forme file system - no test.",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));

			mCanvasBitmapUnderTest.eraseColor(Color.TRANSPARENT);
			mCommandUnderTest.run(mCanvasUnderTest, null);// this should load an existing bitmap from file-system

			PaintroidAsserts.assertBitmapEquals(bitmapToCompare, PaintroidApplication.drawingSurface.getBitmapCopy());

		} catch (Exception e) {
			fail("Failed to restore bitmap from file system" + e.toString());
		} finally {
			if (bitmapToCompare != null) {
				bitmapToCompare.recycle();
				bitmapToCompare = null;
			}
		}
	}

	@Test
	public void testBitmapCommand() {
		try {
			PaintroidAsserts.assertBitmapEquals(mBitmapUnderTest,
					(Bitmap) PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}
}
