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

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.contract.LayerContracts;

public class StampCommand extends BaseCommand {
	private final Point coordinates;
	private final float boxRotation;
	private final RectF boxRect;

	public StampCommand(Bitmap bitmap, Point position, float width, float height, float rotation) {
		super(new Paint(Paint.DITHER_FLAG));

		if (position != null) {
			coordinates = new Point(position.x, position.y);
		} else {
			coordinates = null;
		}
		if (bitmap != null) {
			this.bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		}

		boxRotation = rotation;
		boxRect = new RectF(-width / 2f, -height / 2f, width / 2f,
				height / 2f);
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {

		if (fileToStoredBitmap != null) {
			bitmap = FileIO.getBitmapFromFile(fileToStoredBitmap);
		}

		if (bitmap == null) {
			return;
		}

		canvas.save();
		canvas.translate(coordinates.x, coordinates.y);
		canvas.rotate(boxRotation);
		canvas.drawBitmap(bitmap, null, boxRect, paint);

		canvas.restore();

		if (fileToStoredBitmap == null) {
			storeBitmap(PaintroidApplication.cacheDir);
		} else {
			bitmap.recycle();
			bitmap = null;
		}
	}
}
