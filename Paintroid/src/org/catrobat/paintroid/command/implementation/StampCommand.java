/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
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

import org.catrobat.paintroid.FileIO;

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

	public StampCommand(Bitmap bitmap, Point position, float width,
			float height, float rotation) {
		super(new Paint(Paint.DITHER_FLAG));

		if (position != null) {
			mCoordinates = new Point(position.x, position.y);
		} else {
			mCoordinates = null;
		}
		if (bitmap != null) {
			mBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		}
		mBoxWidth = width;
		mBoxHeight = height;
		mBoxRotation = rotation;
		mBoxRect = new RectF(-mBoxWidth / 2f, -mBoxHeight / 2f, mBoxWidth / 2f,
				mBoxHeight / 2f);
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {
		setChanged();
		notifyObservers(BaseCommand.NOTIFY_STATES.COMMAND_STARTED);
		if (mBitmap == null && mFileToStoredBitmap != null) {
			mBitmap = FileIO.getBitmapFromFile(mFileToStoredBitmap);
		}
		if (mBitmap != null) {
			canvas.save();
			canvas.translate(mCoordinates.x, mCoordinates.y);
			canvas.rotate(mBoxRotation);
			canvas.drawBitmap(mBitmap, null, mBoxRect, mPaint);

			canvas.restore();

			if (mFileToStoredBitmap == null) {
				mBitmap = Bitmap.createBitmap(mBitmap);
				storeBitmap();
			} else {
				mBitmap.recycle();
				mBitmap = null;
			}
		}
		setChanged();
		notifyObservers(BaseCommand.NOTIFY_STATES.COMMAND_DONE);
	}
}
