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

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

public class CropCommand extends BaseCommand {

	private final int mCropCoordinateXLeft;
	private final int mCropCoordinateYTop;
	private final int mCropCoordinateXRight;
	private final int mCropCoordinateYBottom;

	public CropCommand(int cropCoordinaetXLeft, int cropCoordinateYTop,
			int cropCoordinateXRight, int cropCoordinateYBottom) {
		mCropCoordinateXLeft = cropCoordinaetXLeft;
		mCropCoordinateYTop = cropCoordinateYTop;
		mCropCoordinateXRight = cropCoordinateXRight;
		mCropCoordinateYBottom = cropCoordinateYBottom;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {

		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mFileToStoredBitmap != null) {
			PaintroidApplication.drawingSurface.setBitmap(FileIO
					.getBitmapFromFile(mFileToStoredBitmap));

			notifyStatus(NOTIFY_STATES.COMMAND_DONE);
			return;
		}
		try {

			if (mCropCoordinateXRight < mCropCoordinateXLeft) {
				Log.e(PaintroidApplication.TAG,
						"coordinate X left is larger than coordinate X right");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateXRight < 0 || mCropCoordinateXLeft < 0
					|| mCropCoordinateXRight > bitmap.getWidth()
					|| mCropCoordinateXLeft > bitmap.getWidth()) {
				Log.e(PaintroidApplication.TAG,
						"coordinate X is out of bitmap scope");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateYBottom < mCropCoordinateYTop) {
				Log.e(PaintroidApplication.TAG,
						"coordinate Y bottom is smaller than coordinate Y top");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateYBottom < 0 || mCropCoordinateYTop < 0
					|| mCropCoordinateYBottom > bitmap.getHeight()
					|| mCropCoordinateYTop > bitmap.getHeight()) {
				Log.e(PaintroidApplication.TAG,
						"coordinate Y is out of bitmap scope");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mCropCoordinateXLeft <= 0
					&& mCropCoordinateXRight == bitmap.getWidth()
					&& mCropCoordinateYBottom == bitmap.getHeight()
					&& mCropCoordinateYTop <= 0) {
				Log.e(PaintroidApplication.TAG, " no need to crop ");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}

			Bitmap croppedBitmap = Bitmap.createBitmap(bitmap,
					mCropCoordinateXLeft, mCropCoordinateYTop,
					mCropCoordinateXRight - mCropCoordinateXLeft,
					mCropCoordinateYBottom - mCropCoordinateYTop);
			PaintroidApplication.drawingSurface.setBitmap(croppedBitmap);

			if (mFileToStoredBitmap == null) {
				mBitmap = croppedBitmap.copy(Config.ARGB_8888, true);
				storeBitmap();
			}

		} catch (Exception e) {
			Log.e(PaintroidApplication.TAG,
					"failed to crop bitmap:" + e.getMessage());

			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
		}

		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
