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

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;

import org.catrobat.paintroid.tools.Layer;

public class GeometricFillCommand extends BaseCommand {
	protected final Point coordinates;
	protected final float boxWidth;
	protected final float boxHeight;
	protected final float boxRotation;
	protected final RectF boxRect;

	protected Paint geometricFillPaint;

	public GeometricFillCommand(Bitmap bitmap, Point position, float width, float height,
			float rotation, Paint paint) {
		super(new Paint(Paint.DITHER_FLAG));

		coordinates = position != null ? new Point(position.x, position.y) : null;
		if (bitmap != null) {
			this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		}
		boxWidth = width;
		boxHeight = height;
		boxRotation = rotation;
		boxRect = new RectF(-boxWidth / 2f, -boxHeight / 2f, boxWidth / 2f,
				boxHeight / 2f);
		geometricFillPaint = new Paint(paint);
	}

	@Override
	public void run(Canvas canvas, Layer layer) {

		notifyStatus(NotifyStates.COMMAND_STARTED);

		if (bitmap == null) {
			setChanged();
			notifyStatus(NotifyStates.COMMAND_FAILED);
			return;
		}

		canvas.save();
		canvas.translate(coordinates.x, coordinates.y);
		canvas.rotate(boxRotation);

		Paint testPaint = new Paint();
		testPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		testPaint.setAntiAlias(true);

		canvas.drawBitmap(bitmap, null, boxRect, geometricFillPaint);

		canvas.restore();

		notifyStatus(NotifyStates.COMMAND_DONE);
	}
}
