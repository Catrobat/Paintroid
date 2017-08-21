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
import org.catrobat.paintroid.test.junit.stubs.BaseCommandStub;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;

import static org.junit.Assert.*;

public class BaseCommandTest {

	private BaseCommandStub mBaseCommand;
	private Bitmap mBitmap;

	@Before
	public void setUp() throws Exception {
		mBaseCommand = new BaseCommandStub();
		mBitmap = Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
		PrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mBitmap", mBitmap);
	}

	@After
	public void tearDown() throws Exception {
		mBitmap.recycle();
		mBitmap = null;
	}

	@Test
	public void testBaseCommand() {
		try {
			new BaseCommandStub(null);
			new BaseCommandStub(new Paint());
		} catch (Exception e) {
			fail("EXCEPTION: failed with uninitialised Objects" + e.toString());
		}
	}

	@Test
	public void testFreeResources() {
		File cacheDir = PaintroidApplication.applicationContext.getCacheDir();
		File storedBitmap = new File(cacheDir.getAbsolutePath(), "test");
		try {
			assertFalse(storedBitmap.exists());

			PrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mFileToStoredBitmap", storedBitmap);
			mBaseCommand.freeResources();
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));

			File restoredBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand,
					"mFileToStoredBitmap");

			assertFalse("bitmap not deleted", restoredBitmap.exists());
			if (restoredBitmap.exists())
				assertTrue(restoredBitmap.delete());
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

		try {
			assertTrue(storedBitmap.createNewFile());
			assertTrue(storedBitmap.exists());
			mBaseCommand.freeResources();
			assertFalse(storedBitmap.exists());
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));
		} catch (Exception e) {
			fail("EXCEPTION: " + e.toString());
		}

	}

	@Test
	public void testStoreBitmap() {
		File storedBitmap = null;
		try {
			PrivateAccess.setMemberValue(BaseCommand.class, mBaseCommand, "mFileToStoredBitmap", null);

			Bitmap bitmapCopy = mBitmap.copy(mBitmap.getConfig(), mBitmap.isMutable());
			mBaseCommand.storeBitmapStub();
			assertNull(PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mBitmap"));

			storedBitmap = (File) PrivateAccess.getMemberValue(BaseCommand.class, mBaseCommand, "mFileToStoredBitmap");
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
