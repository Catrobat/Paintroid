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
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;

public class FlipCommand implements Command {
	private FlipDirection flipDirection;

	public FlipCommand(FlipDirection flipDirection) {
		this.flipDirection = flipDirection;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		if (flipDirection == null) {
			return;
		}

		Matrix flipMatrix = new Matrix();

		switch (flipDirection) {
			case FLIP_HORIZONTAL:
				flipMatrix.setScale(1, -1);
				flipMatrix.postTranslate(0, layerModel.getHeight());
				break;
			case FLIP_VERTICAL:
				flipMatrix.setScale(-1, 1);
				flipMatrix.postTranslate(layerModel.getWidth(), 0);
				break;
		}

		Bitmap bitmap = layerModel.getCurrentLayer().getBitmap();
		Bitmap bitmapCopy = bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
		Canvas flipCanvas = new Canvas(bitmap);
		bitmap.eraseColor(Color.TRANSPARENT);

		flipCanvas.drawBitmap(bitmapCopy, flipMatrix, new Paint());
	}

	@Override
	public void freeResources() {
	}

	public enum FlipDirection {
		FLIP_HORIZONTAL, FLIP_VERTICAL
	}
}
