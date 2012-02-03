/*
 *   This file is part of Paintroid, a software part of the Catroid project.
 *   Copyright (C) 2010  Catroid development team
 *   <http://code.google.com/p/catroid/wiki/Credits>
 *
 *   Paintroid is free software: you can redistribute it and/or modify it
 *   under the terms of the GNU Affero General Public License as published
 *   by the Free Software Foundation, either version 3 of the License, or
 *   at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.test.utils;

import junit.framework.Assert;
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
}
