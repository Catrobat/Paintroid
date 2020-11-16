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

package org.catrobat.paintroid.common;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

public class CommonFactory {
	public Canvas createCanvas() {
		return new Canvas();
	}

	public Bitmap createBitmap(int width, int height, Config config) {
		return Bitmap.createBitmap(width, height, config);
	}

	public Paint createPaint(Paint paint) {
		return new Paint(paint);
	}

	public PointF createPointF(PointF point) {
		return new PointF(point.x, point.y);
	}

	public Point createPoint(int x, int y) {
		return new Point(x, y);
	}

	public Path createPath(Path path) {
		return new Path(path);
	}

	public RectF createRectF(RectF rect) {
		return new RectF(rect);
	}
}
