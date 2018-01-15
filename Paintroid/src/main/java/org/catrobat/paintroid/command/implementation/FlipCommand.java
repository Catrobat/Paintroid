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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import org.catrobat.paintroid.tools.Layer;

public class FlipCommand extends BaseCommand {
	private static final String TAG = FlipCommand.class.getSimpleName();

	private FlipDirection flipDirection;

	public FlipCommand(FlipDirection flipDirection) {
		this.flipDirection = flipDirection;
	}

	@Override
	public void run(Canvas canvas, Layer layer) {
		Bitmap bitmap = layer.getImage();

		notifyStatus(NotifyStates.COMMAND_STARTED);
		if (flipDirection == null) {

			notifyStatus(NotifyStates.COMMAND_FAILED);
			return;
		}

		Matrix flipMatrix = new Matrix();

		switch (flipDirection) {
			case FLIP_HORIZONTAL:
				flipMatrix.setScale(1, -1);
				flipMatrix.postTranslate(0, bitmap.getHeight());
				Log.i(TAG, "flip horizontal");
				break;
			case FLIP_VERTICAL:
				flipMatrix.setScale(-1, 1);
				flipMatrix.postTranslate(bitmap.getWidth(), 0);
				Log.i(TAG, "flip vertical");
				break;
			default:

				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
		}

		Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
		Canvas flipCanvas = new Canvas(bitmap);
		bitmap.eraseColor(Color.TRANSPARENT);

		flipCanvas.drawBitmap(bitmapCopy, flipMatrix, new Paint());

		notifyStatus(NotifyStates.COMMAND_DONE);
	}

	public enum FlipDirection {
		FLIP_HORIZONTAL, FLIP_VERTICAL
	}
}
