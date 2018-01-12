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
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.tools.Layer;

public class RotateCommand extends BaseCommand {
	private static final String TAG = RotateCommand.class.getSimpleName();

	private static final float ANGLE = 90;
	private RotateDirection rotateDirection;

	public RotateCommand(RotateDirection rotateDirection) {
		this.rotateDirection = rotateDirection;
	}

	@Override
	public void run(Canvas canvas, Layer layer) {
		Bitmap bitmap = layer.getImage();

		setChanged();
		notifyStatus(NotifyStates.COMMAND_STARTED);
		if (rotateDirection == null) {
			setChanged();
			notifyStatus(NotifyStates.COMMAND_FAILED);
			return;
		}

		Matrix rotateMatrix = new Matrix();

		switch (rotateDirection) {
			case ROTATE_RIGHT:
				rotateMatrix.postRotate(ANGLE);
				Log.i(TAG, "rotate right");
				break;

			case ROTATE_LEFT:
				rotateMatrix.postRotate(-ANGLE);
				Log.i(TAG, "rotate left");
				break;

			default:
				setChanged();
				notifyStatus(NotifyStates.COMMAND_FAILED);
				return;
		}

		rotateMatrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);

		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
		Canvas rotateCanvas = new Canvas(rotatedBitmap);

		rotateCanvas.drawBitmap(bitmap, rotateMatrix, new Paint());

		layer.setImage(rotatedBitmap);

		setChanged();

		PaintroidApplication.perspective.resetScaleAndTranslation();
		notifyStatus(NotifyStates.COMMAND_DONE);
	}

	public RotateDirection getRotateDirection() {
		return rotateDirection;
	}

	public enum RotateDirection {
		ROTATE_LEFT, ROTATE_RIGHT
	}
}
