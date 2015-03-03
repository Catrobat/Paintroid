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

import java.nio.ByteBuffer;
import java.util.Arrays;

import junit.framework.Assert;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class PaintroidAsserts extends Assert {
	protected PaintroidAsserts() {

	}

	public static void assertPaintEquals(Paint expectedPaint, Paint actualPaint) {
		assertEquals(expectedPaint.getColor(), actualPaint.getColor());
		assertEquals(expectedPaint.getStrokeCap(), actualPaint.getStrokeCap());
		assertEquals(expectedPaint.getStrokeWidth(), actualPaint.getStrokeWidth());
	}

	public static void assertPathEquals(Path expectedPath, Path actualPath) {
		expectedPath.close();
		actualPath.close();
		RectF expectedPathBounds = new RectF();
		RectF actualPathBounds = new RectF();
		expectedPath.computeBounds(expectedPathBounds, true);
		actualPath.computeBounds(actualPathBounds, true);
		assertEquals(expectedPathBounds.bottom, actualPathBounds.bottom);
		assertEquals(expectedPathBounds.top, actualPathBounds.top);
		assertEquals(expectedPathBounds.left, actualPathBounds.left);
		assertEquals(expectedPathBounds.right, actualPathBounds.right);
	}

	public static void assertBitmapEquals(Bitmap expectedBitmap, Bitmap actualBitmap) {
		ByteBuffer expectedBitmapBuffer = ByteBuffer
				.allocate(expectedBitmap.getHeight() * expectedBitmap.getRowBytes());
		expectedBitmap.copyPixelsToBuffer(expectedBitmapBuffer);

		ByteBuffer actualBitmapBuffer = ByteBuffer.allocate(actualBitmap.getHeight() * actualBitmap.getRowBytes());
		actualBitmap.copyPixelsToBuffer(actualBitmapBuffer);

		assertTrue(Arrays.equals(expectedBitmapBuffer.array(), actualBitmapBuffer.array()));
		actualBitmapBuffer = null;
		expectedBitmapBuffer = null;
	}
}
