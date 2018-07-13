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
import android.graphics.Matrix;

import org.catrobat.paintroid.command.Command;
import org.catrobat.paintroid.contract.LayerContracts;

import java.util.Iterator;

public class RotateCommand implements Command {
	private static final float ANGLE = 90;
	private RotateDirection rotateDirection;

	public RotateCommand(RotateDirection rotateDirection) {
		this.rotateDirection = rotateDirection;
	}

	@Override
	public void run(Canvas canvas, LayerContracts.Model layerModel) {
		if (rotateDirection == null) {
			return;
		}

		Matrix rotateMatrix = new Matrix();
		switch (rotateDirection) {
			case ROTATE_RIGHT:
				rotateMatrix.postRotate(ANGLE);
				break;
			case ROTATE_LEFT:
				rotateMatrix.postRotate(-ANGLE);
				break;
		}

		Iterator<LayerContracts.Layer> iterator = layerModel.listIterator(0);
		while (iterator.hasNext()) {
			LayerContracts.Layer currentLayer = iterator.next();
			Bitmap rotatedBitmap = Bitmap.createBitmap(currentLayer.getBitmap(), 0, 0,
					layerModel.getWidth(), layerModel.getHeight(), rotateMatrix, true);
			currentLayer.setBitmap(rotatedBitmap);
		}

		int width = layerModel.getWidth();
		layerModel.setWidth(layerModel.getHeight());
		layerModel.setHeight(width);
	}

	@Override
	public void freeResources() {
	}

	public enum RotateDirection {
		ROTATE_LEFT, ROTATE_RIGHT
	}
}
