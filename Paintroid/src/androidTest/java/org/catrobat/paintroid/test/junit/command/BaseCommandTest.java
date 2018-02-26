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
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.support.test.InstrumentationRegistry;

import org.catrobat.paintroid.test.junit.stubs.BaseCommandStub;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BaseCommandTest {

	private BaseCommandStub baseCommand;
	private Bitmap bitmap;

	@Before
	public void setUp() {
		baseCommand = new BaseCommandStub();
		bitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		baseCommand.bitmap = bitmap;
	}

	@After
	public void tearDown() {
		bitmap.recycle();
		bitmap = null;
	}

	@Test
	public void testBaseCommand() {
		new BaseCommandStub(null);
		new BaseCommandStub(new Paint());
	}

	@Test
	public void testFreeResources() {
		File cacheDir = InstrumentationRegistry.getTargetContext().getCacheDir();
		File storedBitmap = new File(cacheDir.getAbsolutePath(), "test");
		try {
			assertFalse(storedBitmap.exists());

			baseCommand.fileToStoredBitmap = storedBitmap;
			baseCommand.freeResources();
			assertNull(baseCommand.bitmap);

			File restoredBitmap = baseCommand.fileToStoredBitmap;
			assertFalse("bitmap not deleted", restoredBitmap.exists());
			if (restoredBitmap.exists()) {
				assertTrue(restoredBitmap.delete());
			}
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

		try {
			assertTrue(storedBitmap.createNewFile());
			assertTrue(storedBitmap.exists());
			baseCommand.freeResources();
			assertFalse(storedBitmap.exists());
			assertNull(baseCommand.bitmap);
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}
	}

	@Test
	public void testStoreBitmap() {
		File storedBitmap = null;
		try {
			baseCommand.fileToStoredBitmap = null;
			Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
			baseCommand.storeBitmapStub();
			assertNull(baseCommand.bitmap);

			storedBitmap = baseCommand.fileToStoredBitmap;
			assertNotNull(storedBitmap);
			assertNotNull(storedBitmap.getAbsolutePath());
			Bitmap restoredBitmap = BitmapFactory.decodeFile(storedBitmap.getAbsolutePath());
			PaintroidAsserts.assertBitmapEquals("Loaded file doesn't match saved file.", restoredBitmap, bitmapCopy);
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		} finally {
			assertNotNull("Failed to delete the stored bitmap(0)", storedBitmap);
			assertTrue("Failed to delete the stored bitmap(1)", storedBitmap.delete());
		}
	}
}
