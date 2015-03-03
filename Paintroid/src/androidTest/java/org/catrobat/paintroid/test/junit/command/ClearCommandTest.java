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

import org.catrobat.paintroid.command.implementation.ClearCommand;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

public class ClearCommandTest extends CommandTestSetup {

	protected ClearCommand mClearCommandUnderTestTransparent;
	protected ClearCommand mClearCommandUnderTestColored;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		mClearCommandUnderTestTransparent = new ClearCommand();
		mClearCommandUnderTestColored = new ClearCommand(BITMAP_REPLACE_COLOR);
		mCommandUnderTestNull = new ClearCommand(0);
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		super.tearDown();
		mClearCommandUnderTestTransparent = null;
		mClearCommandUnderTestColored = null;
	}

	@Test
	public void testRunFillBitmapWithDecidedColor() {
		Bitmap bitmapToCompare = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		bitmapToCompare.eraseColor(BITMAP_REPLACE_COLOR);
		Bitmap bitmapUnderTest = bitmapToCompare.copy(Config.ARGB_8888, true);
		bitmapUnderTest.eraseColor(BITMAP_REPLACE_COLOR - 1);
		mClearCommandUnderTestColored.run(null, bitmapUnderTest);
		PaintroidAsserts.assertBitmapEquals(bitmapToCompare, bitmapUnderTest);

		bitmapToCompare.recycle();
		bitmapUnderTest.recycle();

		bitmapToCompare = null;
		bitmapUnderTest = null;
	}

	public void testRunFillBitmapWithPreselectedColor() {
		Bitmap bitmapToCompare = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		bitmapToCompare.eraseColor(Color.TRANSPARENT);
		Bitmap bitmapUnderTest = Bitmap.createBitmap(10, 10, Config.ARGB_8888);
		bitmapUnderTest.eraseColor(BITMAP_REPLACE_COLOR);
		mClearCommandUnderTestTransparent.run(null, bitmapUnderTest);
		PaintroidAsserts.assertBitmapEquals(bitmapToCompare, bitmapUnderTest);

		bitmapToCompare.recycle();
		bitmapUnderTest.recycle();

		bitmapToCompare = null;
		bitmapUnderTest = null;
	}

	@Test
	public void testClearCommand() {
		try {
			assertEquals(Color.TRANSPARENT,
					PrivateAccess.getMemberValue(ClearCommand.class, mClearCommandUnderTestTransparent, "mColor"));
			assertEquals(BITMAP_REPLACE_COLOR,
					PrivateAccess.getMemberValue(ClearCommand.class, mClearCommandUnderTestColored, "mColor"));
		} catch (Exception e) {
			fail("Failed with exception:" + e.toString());
		}
	}
}
