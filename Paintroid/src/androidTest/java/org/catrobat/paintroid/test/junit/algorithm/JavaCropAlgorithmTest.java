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

package org.catrobat.paintroid.test.junit.algorithm;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import org.catrobat.paintroid.tools.helper.JavaCropAlgorithm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(JUnit4.class)
public class JavaCropAlgorithmTest {

	private JavaCropAlgorithm cropAlgorithm;
	private Bitmap bitmap;

	@Before
	public void setUp() {
		cropAlgorithm = new JavaCropAlgorithm();
		bitmap = Bitmap.createBitmap(3, 3, Bitmap.Config.ARGB_8888);
	}

	@Test
	public void testJavaCropAlgorithmWhenMiddlePixelFilled() {
		bitmap.setPixel(1, 1, Color.BLACK);
		Rect expectedBounds = new Rect(1, 1, 1, 1);

		Rect bounds = cropAlgorithm.crop(bitmap);

		assertEquals(expectedBounds, bounds);
	}

	@Test
	public void testJavaCropAlgorithmWhenTopRightPixelFilled() {
		bitmap.setPixel(2, 2, Color.BLACK);
		Rect expectedBounds = new Rect(2, 2, 2, 2);

		Rect bounds = cropAlgorithm.crop(bitmap);

		assertEquals(expectedBounds, bounds);
	}

	@Test
	public void testJavaCropAlgorithmWhenBitmapTransparentThenReturnNull() {
		Rect bounds = cropAlgorithm.crop(bitmap);

		assertNull(bounds);
	}

	@Test
	public void testJavaCropAlgorithmWhenBitmapFilled() {
		bitmap.eraseColor(Color.BLACK);
		Rect expectedBounds = new Rect(0, 0, 2, 2);

		Rect bounds = cropAlgorithm.crop(bitmap);

		assertEquals(expectedBounds, bounds);
	}
}
