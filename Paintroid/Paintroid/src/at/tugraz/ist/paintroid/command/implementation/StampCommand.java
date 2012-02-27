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

package at.tugraz.ist.paintroid.command.implementation;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;

public class StampCommand extends BaseCommand {
	protected final Point mCoordinates;
	protected final float mBoxWidth;
	protected final float mBoxHeight;
	protected final float mBoxRotation;
	protected final RectF mBoxRect;
	protected final Bitmap mBitmap;
	protected File mStoredBitmap;

	public StampCommand(Bitmap bitmap, Point position, float width, float height, float rotation) {
		super(new Paint(Paint.DITHER_FLAG));

		mCoordinates = new Point(position.x, position.y);
		mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		mBoxWidth = width;
		mBoxHeight = height;
		mBoxRotation = rotation;
		mBoxRect = new RectF(-mBoxWidth / 2f, -mBoxHeight / 2f, mBoxWidth / 2f, mBoxHeight / 2f);
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		if (mBitmap.isRecycled()) {
			// TODO store and load bitmap from storage
			// mStoredBitmap = new File();
		}

		canvas.save();
		canvas.translate(mCoordinates.x, mCoordinates.y);
		canvas.rotate(mBoxRotation);
		canvas.drawBitmap(mBitmap, null, mBoxRect, mPaint);
		canvas.restore();

		mBitmap.recycle();
	}
}
