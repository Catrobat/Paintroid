/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.junit.tools;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.tools.algorithm.JavaCropAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class CropAlgorithmTest {
	private JavaCropAlgorithm cropAlgorithm;

	@Before
	public void setUp() {
		cropAlgorithm = new JavaCropAlgorithm();
	}

	@Test
	public void testOnePixelSetMiddle() {
		Bitmap bitmap = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888);
		bitmap.setPixel(1, 1, Color.BLACK);
		Rect expectedBounds = new Rect(1, 1, 1, 1);
		Rect bounds = cropAlgorithm.crop(bitmap);
		assertEquals(expectedBounds, bounds);
	}

	@Test
	public void testOnePixelSetTopRight() {
		Bitmap bitmap = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888);
		bitmap.setPixel(2, 2, Color.BLACK);
		Rect expectedBounds = new Rect(2, 2, 2, 2);
		Rect bounds = cropAlgorithm.crop(bitmap);
		assertEquals(expectedBounds, bounds);
	}

	@Test
	public void testEmptyBitmapReturnsNull() {
		Bitmap bitmap = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888);
		Rect bounds = cropAlgorithm.crop(bitmap);
		assertNull(bounds);
	}

	@Test
	public void testFilledBitmapReturnsFullRectangle() {
		Bitmap bitmap = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888);
		bitmap.eraseColor(Color.BLACK);
		Rect expectedBounds = new Rect(0, 0, 2, 2);
		Rect bounds = cropAlgorithm.crop(bitmap);
		assertEquals(expectedBounds, bounds);
	}
}
