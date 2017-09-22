/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.command.implementation;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.util.Log;

import org.catrobat.paintroid.FileIO;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.dialog.LayersDialog;
import org.catrobat.paintroid.listener.LayerListener;

public class ResizeCommand extends BaseCommand {

	private final int mResizeCoordinateXLeft;
	private final int mResizeCoordinateYTop;
	private final int mResizeCoordinateXRight;
	private final int mResizeCoordinateYBottom;
	private final int mMaximumBitmapResolution;

	public ResizeCommand(int resizeCoordinateXLeft, int resizeCoordinateYTop,
						 int resizeCoordinateXRight, int resizeCoordinateYBottom,
						 int maximumBitmapResolution) {
		mResizeCoordinateXLeft = resizeCoordinateXLeft;
		mResizeCoordinateYTop = resizeCoordinateYTop;
		mResizeCoordinateXRight = resizeCoordinateXRight;
		mResizeCoordinateYBottom = resizeCoordinateYBottom;
		mMaximumBitmapResolution = maximumBitmapResolution;
	}

	@Override
	public void run(Canvas canvas, Bitmap bitmap) {

		notifyStatus(NOTIFY_STATES.COMMAND_STARTED);
		if (mFileToStoredBitmap != null) {
			PaintroidApplication.drawingSurface.setBitmap(FileIO.getBitmapFromFile(mFileToStoredBitmap));

			notifyStatus(NOTIFY_STATES.COMMAND_DONE);
			return;
		}
		try {

			if (mResizeCoordinateXRight < mResizeCoordinateXLeft) {
				Log.e(PaintroidApplication.TAG,
						"coordinate X right must be larger than coordinate X left");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mResizeCoordinateYBottom < mResizeCoordinateYTop) {
				Log.e(PaintroidApplication.TAG,
						"coordinate Y bottom must be larger than coordinate Y top");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mResizeCoordinateXLeft >= bitmap.getWidth() || mResizeCoordinateXRight < 0 ||
					mResizeCoordinateYTop >= bitmap.getHeight() || mResizeCoordinateYBottom < 0) {
				Log.e(PaintroidApplication.TAG,
						"resize coordinates are out of bitmap scope");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if (mResizeCoordinateXLeft == 0
					&& mResizeCoordinateXRight == bitmap.getWidth() - 1
					&& mResizeCoordinateYBottom == bitmap.getHeight() - 1
					&& mResizeCoordinateYTop == 0) {
				Log.e(PaintroidApplication.TAG, " no need to resize ");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}
			if ((mResizeCoordinateXRight + 1 - mResizeCoordinateXLeft)
					* (mResizeCoordinateYBottom + 1 - mResizeCoordinateYTop) > mMaximumBitmapResolution) {
				Log.e(PaintroidApplication.TAG, " image resolution not supported ");

				notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
				return;
			}

			Bitmap resizedBitmap = Bitmap.createBitmap(
					mResizeCoordinateXRight + 1 - mResizeCoordinateXLeft,
					mResizeCoordinateYBottom + 1 - mResizeCoordinateYTop,
					bitmap.getConfig());

			int copyFromXLeft = Math.max(0, mResizeCoordinateXLeft);
			int copyFromXRight = Math.min(bitmap.getWidth() - 1, mResizeCoordinateXRight);
			int copyFromYTop = Math.max(0, mResizeCoordinateYTop);
			int copyFromYBottom = Math.min(bitmap.getHeight() - 1, mResizeCoordinateYBottom);
			int copyFromWidth = copyFromXRight - copyFromXLeft + 1;
			int copyFromHeight = copyFromYBottom - copyFromYTop + 1;
			int copyToXLeft = Math.abs(Math.min(0, mResizeCoordinateXLeft));
			int copyToXRight = Math.min(bitmap.getWidth() - 1, mResizeCoordinateXRight) - mResizeCoordinateXLeft;
			int copyToYTop = Math.abs(Math.min(0, mResizeCoordinateYTop));
			int copyToYBottom = Math.min(bitmap.getHeight() - 1, mResizeCoordinateYBottom) - mResizeCoordinateYTop;
			int copyToWidth = copyToXRight - copyToXLeft + 1;
			int copyToHeight = copyToYBottom - copyToYTop + 1;
			int[] pixelsToCopy = new int[(copyFromXRight - copyFromXLeft + 1) * (copyFromYBottom - copyFromYTop + 1)];

			bitmap.getPixels(pixelsToCopy, 0, copyFromWidth, copyFromXLeft, copyFromYTop,
					copyFromWidth, copyFromHeight);

			resizedBitmap.setPixels(pixelsToCopy, 0, copyToWidth, copyToXLeft, copyToYTop,
					copyToWidth, copyToHeight);

			PaintroidApplication.drawingSurface.setBitmap(resizedBitmap);
			LayerListener.getInstance().getCurrentLayer().setImage(resizedBitmap);
			LayerListener.getInstance().refreshView();

			setChanged();

		} catch (Exception e) {
			Log.e(PaintroidApplication.TAG,
					"failed to resize bitmap:" + e.getMessage());

			notifyStatus(NOTIFY_STATES.COMMAND_FAILED);
		}

		notifyStatus(NOTIFY_STATES.COMMAND_DONE);
	}
}
