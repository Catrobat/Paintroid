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

package org.catrobat.paintroid.tools.helper;

import android.graphics.Bitmap;
import android.graphics.Point;

public class NativeFillAlgorithm implements FillAlgorithm {
	private Bitmap bitmap;
	private Point clickedPixel;
	private int targetColor;
	private int replacementColor;
	private float colorToleranceThreshold;

	static {
		System.loadLibrary("native-lib"); // NOPMD native fill command
	}

	public native void performFilling(int[] arr, int xStart, int yStart, int xSize, int ySize,
			int targetColor, int replacementColor, int colorToleranceThresholdSquared);

	@Override
	public void setParameters(Bitmap bitmap, Point clickedPixel, int targetColor, int replacementColor, float colorToleranceThreshold) {
		this.bitmap = bitmap;
		this.clickedPixel = clickedPixel;
		this.targetColor = targetColor;
		this.replacementColor = replacementColor;
		this.colorToleranceThreshold = colorToleranceThreshold;
	}

	@Override
	public void performFilling() {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int[] pixelArray = new int[width * height];

		bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height);
		performFilling(pixelArray, clickedPixel.x, clickedPixel.y, width, height, targetColor, replacementColor, (int) (colorToleranceThreshold * colorToleranceThreshold));
		bitmap.setPixels(pixelArray, 0, width, 0, 0, width, height);
	}
}
