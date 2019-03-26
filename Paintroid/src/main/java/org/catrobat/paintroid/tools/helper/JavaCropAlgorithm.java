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
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;

public class JavaCropAlgorithm implements CropAlgorithm {
	private boolean containsOpaquePixel(int[][] pixels, int fromX, int fromY, int toX, int toY) {
		for (int y = fromY; y <= toY; y++) {
			for (int x = fromX; x <= toX; x++) {
				if (pixels[y][x] != Color.TRANSPARENT) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Rect crop(Bitmap bitmap) {
		if (bitmap == null) {
			Log.e("cropAlgorithmSnail", "bitmap is null!");
			return null;
		}

		int[][] pixels = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < bitmap.getHeight(); i++) {
			bitmap.getPixels(pixels[i], 0, bitmap.getWidth(), 0, i, bitmap.getWidth(), 1);
		}

		Rect bounds = new Rect(0, 0, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
		int x;
		int y;
		for (y = bounds.top; y <= bounds.bottom; y++) {
			bounds.top = y;
			if (containsOpaquePixel(pixels, bounds.left, y, bounds.right, y)) {
				break;
			}
		}
		if (y > bounds.bottom) {
			Log.i("cropAlgorithmSnail", "nothing to crop");
			return null;
		}

		for (x = bounds.left; x <= bounds.right; x++) {
			bounds.left = x;
			if (containsOpaquePixel(pixels, x, bounds.top, x, bounds.bottom)) {
				break;
			}
		}

		for (y = bounds.bottom; y >= bounds.top; y--) {
			bounds.bottom = y;
			if (containsOpaquePixel(pixels, bounds.left, y, bounds.right, y)) {
				break;
			}
		}

		for (x = bounds.right; x >= bounds.left; x--) {
			bounds.right = x;
			if (containsOpaquePixel(pixels, x, bounds.top, x, bounds.bottom)) {
				break;
			}
		}

		return bounds;
	}
}
