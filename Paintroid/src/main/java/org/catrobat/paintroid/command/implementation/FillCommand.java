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
import android.graphics.Paint;
import android.graphics.Point;

import org.catrobat.paintroid.tools.Layer;
import org.catrobat.paintroid.tools.helper.FillAlgorithm;

public class FillCommand extends BaseCommand {
	private static final boolean USE_CPP = true;

	static {
		System.loadLibrary("native-lib"); // NOPMD native fill command
	}

	private float colorTolerance;
	private Point clickedPixel;

	public FillCommand(Point clickedPixel, Paint currentPaint, float colorTolerance) {
		super(currentPaint);
		this.clickedPixel = clickedPixel;
		this.colorTolerance = colorTolerance;
	}

	public native void performFilling(int[] arr, int xStart, int yStart, int xSize, int ySize,
			int targetColor, int replacementColor, int colorToleranceThresholdSquared);

	@Override
	public void run(Canvas canvas, Layer layer) {
		Bitmap bitmap = layer.getImage();

		notifyStatus(NotifyStates.COMMAND_STARTED);
		if (clickedPixel == null) {
			setChanged();
			notifyStatus(NotifyStates.COMMAND_FAILED);
			return;
		}

		Bitmap emptyImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
		if (bitmap.sameAs(emptyImage)) {
			canvas.drawColor(paint.getColor());
		} else {
			int replacementColor = bitmap.getPixel(clickedPixel.x, clickedPixel.y);
			int targetColor = paint.getColor();

			if (USE_CPP) {
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				int[] pixelArray = new int[width * height];

				bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);
				performFilling(pixelArray, clickedPixel.x, clickedPixel.y, width, height,
						targetColor, replacementColor, (int) (colorTolerance * colorTolerance));
				bitmap.setPixels(pixelArray, 0, width, 0, 0, width, height);
			} else {
				FillAlgorithm fillAlgorithm = new FillAlgorithm(bitmap, clickedPixel, targetColor, replacementColor, colorTolerance);
				fillAlgorithm.performFilling();
			}
		}

		notifyStatus(NotifyStates.COMMAND_DONE);
	}
}
