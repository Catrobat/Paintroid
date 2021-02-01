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

package org.catrobat.paintroid.test.utils;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class PaintroidAsserts {

	public static void assertPaintEquals(Paint expectedPaint, Paint actualPaint) {
		assertPaintEquals(null, expectedPaint, actualPaint);
	}

	public static void assertPaintEquals(String message, Paint expectedPaint, Paint actualPaint) {
		assertEquals(message, expectedPaint.getColor(), actualPaint.getColor());
		assertEquals(message, expectedPaint.getStrokeCap(), actualPaint.getStrokeCap());
		assertEquals(message, expectedPaint.getStrokeWidth(), actualPaint.getStrokeWidth(), Float.MIN_VALUE);
	}

	public static void assertPathEquals(Path expectedPath, Path actualPath) {
		assertPathEquals(null, expectedPath, actualPath);
	}

	public static void assertPathEquals(String message, Path expectedPath, Path actualPath) {
		expectedPath.close();
		actualPath.close();
		RectF expectedPathBounds = new RectF();
		RectF actualPathBounds = new RectF();
		expectedPath.computeBounds(expectedPathBounds, true);
		actualPath.computeBounds(actualPathBounds, true);
		assertEquals(message, expectedPathBounds.bottom, actualPathBounds.bottom, Float.MIN_VALUE);
		assertEquals(message, expectedPathBounds.top, actualPathBounds.top, Float.MIN_VALUE);
		assertEquals(message, expectedPathBounds.left, actualPathBounds.left, Float.MIN_VALUE);
		assertEquals(message, expectedPathBounds.right, actualPathBounds.right, Float.MIN_VALUE);
	}

	public static void assertBitmapEquals(Bitmap expectedBitmap, Bitmap actualBitmap) {
		assertBitmapEquals(null, expectedBitmap, actualBitmap);
	}

	public static void assertBitmapEquals(String message, Bitmap expectedBitmap, Bitmap actualBitmap) {
		assertTrue(message, expectedBitmap.sameAs(actualBitmap));
	}
}
