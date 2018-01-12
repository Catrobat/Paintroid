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
import android.graphics.Canvas;
import android.util.Log;

import org.catrobat.paintroid.tools.Layer;

public class ResizeCommand extends BaseCommand {
	private static final String TAG = ResizeCommand.class.getSimpleName();

	private final int resizeCoordinateXLeft;
	private final int resizeCoordinateYTop;
	private final int resizeCoordinateXRight;
	private final int resizeCoordinateYBottom;
	private final int maximumBitmapResolution;

	public ResizeCommand(int resizeCoordinateXLeft, int resizeCoordinateYTop,
			int resizeCoordinateXRight, int resizeCoordinateYBottom,
			int maximumBitmapResolution) {
		this.resizeCoordinateXLeft = resizeCoordinateXLeft;
		this.resizeCoordinateYTop = resizeCoordinateYTop;
		this.resizeCoordinateXRight = resizeCoordinateXRight;
		this.resizeCoordinateYBottom = resizeCoordinateYBottom;
		this.maximumBitmapResolution = maximumBitmapResolution;
	}

	@Override
	public void run(Canvas canvas, Layer layer) {
		notifyStatus(NotifyStates.COMMAND_STARTED);

		try {
			Bitmap bitmap = layer.getImage();

			if (resizeCoordinateXRight < resizeCoordinateXLeft) {
				Log.e(TAG, "coordinate X right must be larger than coordinate X left");

				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
			}
			if (resizeCoordinateYBottom < resizeCoordinateYTop) {
				Log.e(TAG, "coordinate Y bottom must be larger than coordinate Y top");

				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
			}
			if (resizeCoordinateXLeft >= bitmap.getWidth() || resizeCoordinateXRight < 0
					|| resizeCoordinateYTop >= bitmap.getHeight() || resizeCoordinateYBottom < 0) {
				Log.e(TAG, "resize coordinates are out of bitmap scope");

				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
			}
			if (resizeCoordinateXLeft == 0
					&& resizeCoordinateXRight == bitmap.getWidth() - 1
					&& resizeCoordinateYBottom == bitmap.getHeight() - 1
					&& resizeCoordinateYTop == 0) {
				Log.e(TAG, " no need to resize ");

				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
			}
			if ((resizeCoordinateXRight + 1 - resizeCoordinateXLeft)
					* (resizeCoordinateYBottom + 1 - resizeCoordinateYTop) > maximumBitmapResolution) {
				Log.e(TAG, " image resolution not supported ");

				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
			}

			Bitmap resizedBitmap = Bitmap.createBitmap(
					resizeCoordinateXRight + 1 - resizeCoordinateXLeft,
					resizeCoordinateYBottom + 1 - resizeCoordinateYTop,
					bitmap.getConfig());

			int copyFromXLeft = Math.max(0, resizeCoordinateXLeft);
			int copyFromXRight = Math.min(bitmap.getWidth() - 1, resizeCoordinateXRight);
			int copyFromYTop = Math.max(0, resizeCoordinateYTop);
			int copyFromYBottom = Math.min(bitmap.getHeight() - 1, resizeCoordinateYBottom);
			int copyFromWidth = copyFromXRight - copyFromXLeft + 1;
			int copyFromHeight = copyFromYBottom - copyFromYTop + 1;
			int copyToXLeft = Math.abs(Math.min(0, resizeCoordinateXLeft));
			int copyToXRight = Math.min(bitmap.getWidth() - 1, resizeCoordinateXRight) - resizeCoordinateXLeft;
			int copyToYTop = Math.abs(Math.min(0, resizeCoordinateYTop));
			int copyToYBottom = Math.min(bitmap.getHeight() - 1, resizeCoordinateYBottom) - resizeCoordinateYTop;
			int copyToWidth = copyToXRight - copyToXLeft + 1;
			int copyToHeight = copyToYBottom - copyToYTop + 1;
			int[] pixelsToCopy = new int[(copyFromXRight - copyFromXLeft + 1) * (copyFromYBottom - copyFromYTop + 1)];

			bitmap.getPixels(pixelsToCopy, 0, copyFromWidth, copyFromXLeft, copyFromYTop, copyFromWidth, copyFromHeight);
			resizedBitmap.setPixels(pixelsToCopy, 0, copyToWidth, copyToXLeft, copyToYTop, copyToWidth, copyToHeight);

			layer.setImage(resizedBitmap);

			setChanged();
		} catch (Exception e) {
			Log.e(TAG, "failed to resize bitmap:" + e.getMessage());

			notifyStatus(NotifyStates.COMMAND_FAILED);
		}

		notifyStatus(NotifyStates.COMMAND_DONE);
	}

	public int getResizeCoordinateXLeft() {
		return resizeCoordinateXLeft;
	}

	public int getResizeCoordinateYTop() {
		return resizeCoordinateYTop;
	}

	public int getResizeCoordinateXRight() {
		return resizeCoordinateXRight;
	}

	public int getResizeCoordinateYBottom() {
		return resizeCoordinateYBottom;
	}

	public int getMaximumBitmapResolution() {
		return maximumBitmapResolution;
	}
}
