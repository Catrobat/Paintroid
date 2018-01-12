/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.command;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Point;

import org.catrobat.paintroid.command.implementation.BaseCommand;
import org.catrobat.paintroid.command.implementation.StampCommand;
import org.catrobat.paintroid.test.utils.PaintroidAsserts;
import org.catrobat.paintroid.tools.Layer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class StampCommandTest extends CommandTestSetup {

	private Bitmap stampBitmapUnderTest;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		stampBitmapUnderTest = canvasBitmapUnderTest.copy(Config.ARGB_8888, true);
		stampBitmapUnderTest.eraseColor(BITMAP_REPLACE_COLOR);
		commandUnderTest = new StampCommand(stampBitmapUnderTest, new Point(canvasBitmapUnderTest.getWidth() / 2,
				canvasBitmapUnderTest.getHeight() / 2), canvasBitmapUnderTest.getWidth(),
				canvasBitmapUnderTest.getHeight(), 0);
		commandUnderTestNull = new StampCommand(null, null, 0, 0, 0);
	}

	@Override
	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void testRun() {
		commandUnderTest.run(canvasUnderTest, new Layer(0, Bitmap.createBitmap(1, 1, Config.ARGB_8888)));
		PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest);

		assertNull("Stamp bitmap not recycled.", ((BaseCommand) commandUnderTest).bitmap);
		assertNotNull("Bitmap not stored", ((BaseCommand) commandUnderTest).fileToStoredBitmap);
		commandUnderTest.run(canvasUnderTest, new Layer(0, Bitmap.createBitmap(10, 10, Config.ARGB_8888)));
		PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest);
	}

	@Test
	public void testRunRotateStamp() {
		stampBitmapUnderTest.setPixel(0, 0, Color.GREEN);
		commandUnderTest = new StampCommand(stampBitmapUnderTest, new Point((int) pointUnderTest.x,
				(int) pointUnderTest.y), canvasBitmapUnderTest.getWidth(), canvasBitmapUnderTest.getHeight(), 180);
		commandUnderTest.run(canvasUnderTest, null);
		stampBitmapUnderTest.setPixel(0, 0, Color.CYAN);
		stampBitmapUnderTest.setPixel(stampBitmapUnderTest.getWidth() - 1, stampBitmapUnderTest.getHeight() - 1,
				Color.GREEN);
		PaintroidAsserts.assertBitmapEquals(stampBitmapUnderTest, canvasBitmapUnderTest);
		assertNull("Stamp bitmap not recycled.", ((BaseCommand) commandUnderTest).bitmap);
		assertNotNull("Bitmap not stored", ((BaseCommand) commandUnderTest).fileToStoredBitmap);
	}
}
