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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResizeCommandTest extends CommandTestSetup {

	private static final int MAXIMUM_BITMAP_RESOLUTION_FACTOR = 4;

	private int resizeCoordinateXLeft;
	private int resizeCoordinateYTop;
	private int resizeCoordinateXRight;
	private int resizeCoordinateYBottom;
	private int maximumBitmapResolution;

	@Override
	@Before
	public void setUp() {
		super.setUp();
		resizeCoordinateXLeft = 0;
		resizeCoordinateYTop = 0;
		resizeCoordinateXRight = bitmapUnderTest.getWidth() - 1;
		resizeCoordinateYBottom = bitmapUnderTest.getHeight() - 1;
		maximumBitmapResolution = bitmapUnderTest.getWidth() * bitmapUnderTest.getHeight()
				* MAXIMUM_BITMAP_RESOLUTION_FACTOR;
		commandUnderTest = new ResizeCommand(resizeCoordinateXLeft, resizeCoordinateYTop,
				resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution);
		commandUnderTestNull = new ResizeCommand(1, 1, 2, 2, maximumBitmapResolution);
	}

	@Override
	@After
	public void tearDown() {
		File fileToResizedBitmap = ((BaseCommand) commandUnderTest).fileToStoredBitmap;
		if (fileToResizedBitmap != null) {
			assertTrue(fileToResizedBitmap.delete());
		}
		super.tearDown();
	}

	@Test
	public void testIfBitmapIsCropped() {
		int widthOriginal = bitmapUnderTest.getWidth();
		int heightOriginal = bitmapUnderTest.getHeight();
		resizeCoordinateXLeft = 1;
		resizeCoordinateYTop = 1;
		resizeCoordinateXRight = bitmapUnderTest.getWidth() - 2;
		resizeCoordinateYBottom = bitmapUnderTest.getHeight() - 2;
		commandUnderTest = new ResizeCommand(resizeCoordinateXLeft, resizeCoordinateYTop,
				resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution);

		commandUnderTest.run(canvasUnderTest, layerUnderTest);

		Bitmap croppedBitmap = layerUnderTest.getImage();
		assertEquals("Cropping failed, width not correct ", widthOriginal - resizeCoordinateXLeft
				- (widthOriginal - (resizeCoordinateXRight + 1)), croppedBitmap.getWidth());
		assertEquals("Cropping failed, height not correct ", heightOriginal - resizeCoordinateYTop
				- (widthOriginal - (resizeCoordinateYBottom + 1)), croppedBitmap.getHeight());
		croppedBitmap.recycle();
	}

	@Test
	public void testIfBitmapIsEnlarged() {
		int widthOriginal = bitmapUnderTest.getWidth();
		int heightOriginal = bitmapUnderTest.getHeight();
		resizeCoordinateXLeft = -1;
		resizeCoordinateYTop = -1;
		resizeCoordinateXRight = bitmapUnderTest.getWidth();
		resizeCoordinateYBottom = bitmapUnderTest.getHeight();
		commandUnderTest = new ResizeCommand(resizeCoordinateXLeft, resizeCoordinateYTop,
				resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution);

		commandUnderTest.run(canvasUnderTest, layerUnderTest);

		Bitmap enlargedBitmap = layerUnderTest.getImage();
		assertEquals("Enlarging failed, width not correct ", widthOriginal - resizeCoordinateXLeft
				- (widthOriginal - (resizeCoordinateXRight + 1)), enlargedBitmap.getWidth());
		assertEquals("Enlarging failed, height not correct ", heightOriginal - resizeCoordinateYTop
				- (widthOriginal - (resizeCoordinateYBottom + 1)), enlargedBitmap.getHeight());
		enlargedBitmap.recycle();
	}

	@Test
	public void testIfBitmapIsShifted() {
		int widthOriginal = bitmapUnderTest.getWidth();
		int heightOriginal = bitmapUnderTest.getHeight();
		resizeCoordinateXLeft = bitmapUnderTest.getWidth() / 2 - 1;
		resizeCoordinateYTop = bitmapUnderTest.getHeight() / 2 - 1;
		resizeCoordinateXRight = resizeCoordinateXLeft + bitmapUnderTest.getWidth() - 1;
		resizeCoordinateYBottom = resizeCoordinateYTop + bitmapUnderTest.getHeight() - 1;
		commandUnderTest = new ResizeCommand(resizeCoordinateXLeft, resizeCoordinateYTop,
				resizeCoordinateXRight, resizeCoordinateYBottom, maximumBitmapResolution);

		commandUnderTest.run(canvasUnderTest, layerUnderTest);

		Bitmap enlargedBitmap = layerUnderTest.getImage();
		assertEquals("Enlarging failed, width not correct ", widthOriginal, enlargedBitmap.getWidth());
		assertEquals("Enlarging failed, height not correct ", heightOriginal, enlargedBitmap.getHeight());
		enlargedBitmap.recycle();
	}

	@Test
	public void testIfMaximumResolutionIsRespected() {
		int widthOriginal = bitmapUnderTest.getWidth();
		int heightOriginal = bitmapUnderTest.getHeight();
		commandUnderTest = new ResizeCommand(0, 0, widthOriginal * 2, heightOriginal * 2, maximumBitmapResolution);

		commandUnderTest.run(canvasUnderTest, layerUnderTest);

		assertEquals("Width should not have changed", widthOriginal, layerUnderTest.getImage().getWidth());
		assertEquals("Height should not have changed", heightOriginal, layerUnderTest.getImage().getHeight());
	}

	@Test
	public void testIfBitmapIsNotResizedWithInvalidBounds() {
		Bitmap originalBitmap = layerUnderTest.getImage();
		commandUnderTest = new ResizeCommand(bitmapUnderTest.getWidth(), 0, bitmapUnderTest.getWidth(),
				0, maximumBitmapResolution);

		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change if X left is larger than bitmap scope", originalBitmap.sameAs(layerUnderTest.getImage()));

		commandUnderTest = new ResizeCommand(-1, 0, -1, 0, maximumBitmapResolution);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change if X right is smaller than bitmap scope", originalBitmap.sameAs(layerUnderTest.getImage()));

		commandUnderTest = new ResizeCommand(0, bitmapUnderTest.getHeight(), 0,
				bitmapUnderTest.getHeight(), maximumBitmapResolution);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change if Y top is larger than bitmap scope", originalBitmap.sameAs(layerUnderTest.getImage()));

		commandUnderTest = new ResizeCommand(0, -1, 0, -1, maximumBitmapResolution);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change if Y bottom is smaller than bitmap scope", originalBitmap.sameAs(layerUnderTest.getImage()));

		commandUnderTest = new ResizeCommand(1, 0, 0, 0, maximumBitmapResolution);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change with widthXRight < widthXLeft bound", originalBitmap.sameAs(layerUnderTest.getImage()));

		commandUnderTest = new ResizeCommand(0, 1, 0, 0, maximumBitmapResolution);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change with widthYBottom < widthYTop bound", originalBitmap.sameAs(layerUnderTest.getImage()));

		commandUnderTest = new ResizeCommand(0, 0, bitmapUnderTest.getWidth() - 1,
				bitmapUnderTest.getHeight() - 1, maximumBitmapResolution);
		commandUnderTest.run(canvasUnderTest, layerUnderTest);
		assertTrue("bitmap must not change because bounds are the same as original bitmap", originalBitmap.sameAs(layerUnderTest.getImage()));
	}
}
