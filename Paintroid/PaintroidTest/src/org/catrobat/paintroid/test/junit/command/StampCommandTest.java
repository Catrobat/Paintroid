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
package org.catrobat.paintroid.test.junit.command;

import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Point;
import at.tugraz.ist.paintroid.command.implementation.BaseCommand;
import at.tugraz.ist.paintroid.command.implementation.StampCommand;

public class StampCommandTest extends CommandTestSetup {

	protected Bitmap mStampBitmapUnderTest;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mStampBitmapUnderTest = mCanvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		mStampBitmapUnderTest.eraseColor(BITMAP_REPLACE_COLOR);
		mCommandUnderTest = new StampCommand(mStampBitmapUnderTest, new Point(mCanvasBitmapUnderTest.getWidth() / 2,
				mCanvasBitmapUnderTest.getHeight() / 2), mCanvasBitmapUnderTest.getWidth(),
				mCanvasBitmapUnderTest.getHeight(), 0);
		mCommandUnderTestNull = new StampCommand(null, null, 0, 0, 0);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testRun() {
		mCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(mStampBitmapUnderTest, mCanvasBitmapUnderTest);

		try {
			assertNull("Stamp bitmap not recycled.",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
			assertNotNull("Bitmap not stored",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));
		} catch (Exception e) {
			fail("Failed with exception " + e.toString());
		}
		mCommandUnderTest.run(mCanvasUnderTest, null);
		PaintroidAsserts.assertBitmapEquals(mStampBitmapUnderTest, mCanvasBitmapUnderTest);
	}

	@Test
	public void testRunRotateStamp() {
		mStampBitmapUnderTest.setPixel(0, 0, Color.GREEN);
		mCommandUnderTest = new StampCommand(mStampBitmapUnderTest, new Point((int) mPointUnderTest.x,
				(int) mPointUnderTest.y), mCanvasBitmapUnderTest.getWidth(), mCanvasBitmapUnderTest.getHeight(), 180);
		mCommandUnderTest.run(mCanvasUnderTest, null);
		mStampBitmapUnderTest.setPixel(0, 0, Color.CYAN);
		mStampBitmapUnderTest.setPixel(mStampBitmapUnderTest.getWidth() - 1, mStampBitmapUnderTest.getHeight() - 1,
				Color.GREEN);
		PaintroidAsserts.assertBitmapEquals(mStampBitmapUnderTest, mCanvasBitmapUnderTest);
		try {
			assertNull("Stamp bitmap not recycled.",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mBitmap"));
			assertNotNull("Bitmap not stored",
					PrivateAccess.getMemberValue(BaseCommand.class, mCommandUnderTest, "mFileToStoredBitmap"));
		} catch (Exception e) {
			fail("Failed with exception " + e.toString());
		}
	}
}
