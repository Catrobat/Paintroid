/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http:></http:>//developer.catrobat.org/credits>)
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
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package org.catrobat.paintroid.test.utils

import org.catrobat.paintroid.test.utils.PaintroidAsserts
import android.graphics.RectF
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import org.junit.Assert

object PaintroidAsserts {
    fun assertPaintEquals(expectedPaint: Paint, actualPaint: Paint) {
        assertPaintEquals(null, expectedPaint, actualPaint)
    }

    @JvmStatic
	fun assertPaintEquals(message: String?, expectedPaint: Paint, actualPaint: Paint) {
        Assert.assertEquals(message, expectedPaint.color.toLong(), actualPaint.color.toLong())
        Assert.assertEquals(message, expectedPaint.strokeCap, actualPaint.strokeCap)
        Assert.assertEquals(message, expectedPaint.strokeWidth, actualPaint.strokeWidth, Float.MIN_VALUE)
    }

    fun assertPathEquals(expectedPath: Path, actualPath: Path) {
        assertPathEquals(null, expectedPath, actualPath)
    }

    @JvmStatic
	fun assertPathEquals(message: String?, expectedPath: Path, actualPath: Path) {
        expectedPath.close()
        actualPath.close()
        val expectedPathBounds = RectF()
        val actualPathBounds = RectF()
        expectedPath.computeBounds(expectedPathBounds, true)
        actualPath.computeBounds(actualPathBounds, true)
        Assert.assertEquals(message, expectedPathBounds.bottom, actualPathBounds.bottom, Float.MIN_VALUE)
        Assert.assertEquals(message, expectedPathBounds.top, actualPathBounds.top, Float.MIN_VALUE)
        Assert.assertEquals(message, expectedPathBounds.left, actualPathBounds.left, Float.MIN_VALUE)
        Assert.assertEquals(message, expectedPathBounds.right, actualPathBounds.right, Float.MIN_VALUE)
    }

    fun assertBitmapEquals(expectedBitmap: Bitmap, actualBitmap: Bitmap?) {
        assertBitmapEquals(null, expectedBitmap, actualBitmap)
    }

    @JvmStatic
	fun assertBitmapEquals(message: String?, expectedBitmap: Bitmap, actualBitmap: Bitmap?) {
        Assert.assertTrue(message, expectedBitmap.sameAs(actualBitmap))
    }
}