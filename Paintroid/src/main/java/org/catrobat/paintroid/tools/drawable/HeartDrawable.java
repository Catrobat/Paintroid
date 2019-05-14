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

package org.catrobat.paintroid.tools.drawable;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class HeartDrawable implements ShapeDrawable {
	private Path path = new Path();
	private Paint paint = new Paint();

	@Override
	public void draw(Canvas canvas, RectF shapeRect, Paint drawPaint) {
		paint.set(drawPaint);

		float midWidth = shapeRect.width() / 2;
		float height = shapeRect.height();
		float width = shapeRect.width();

		path.reset();
		path.moveTo(midWidth, height);
		path.cubicTo(-0.2f * width, 4.5f * height / 8,
				0.8f * width / 8, -1.5f * height / 8,
				midWidth, 1.5f * height / 8);
		path.cubicTo(7.2f * width / 8, -1.5f * height / 8,
				1.2f * width, 4.5f * height / 8,
				midWidth, height);
		path.close();

		path.offset(shapeRect.left, shapeRect.top);
		canvas.drawPath(path, paint);
	}
}
