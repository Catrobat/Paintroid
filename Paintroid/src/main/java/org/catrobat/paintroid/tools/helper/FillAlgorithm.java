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

package org.catrobat.paintroid.tools.helper;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.annotation.VisibleForTesting;

import java.util.LinkedList;
import java.util.Queue;

public class FillAlgorithm {
	private static final boolean UP = true;
	private static final boolean DOWN = false;

	private Bitmap bitmap;
	@VisibleForTesting
	public int[][] pixels;
	@VisibleForTesting
	public Point clickedPixel;
	@VisibleForTesting
	public int targetColor;
	@VisibleForTesting
	public int replacementColor;
	@VisibleForTesting
	public int colorToleranceThresholdSquared;
	private boolean considerTolerance;
	private int width;
	private int height;
	@VisibleForTesting
	public Queue<Range> ranges;
	private boolean[][] filledPixels;

	public FillAlgorithm(Bitmap bitmap, Point clickedPixel, int targetColor, int replacementColor, float colorToleranceThreshold) {
		this.bitmap = bitmap;
		width = bitmap.getWidth();
		height = bitmap.getHeight();
		pixels = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < height; i++) {
			this.bitmap.getPixels(pixels[i], 0, width, 0, i, width, 1);
		}
		filledPixels = new boolean[bitmap.getHeight()][bitmap.getWidth()];
		this.clickedPixel = clickedPixel;
		this.targetColor = targetColor;
		this.replacementColor = replacementColor;
		ranges = new LinkedList<>();
		colorToleranceThresholdSquared = (int) (colorToleranceThreshold * colorToleranceThreshold);
		considerTolerance = colorToleranceThreshold > 0;
	}

	public void performFilling() {
		Range range = generateRangeAndReplaceColor(clickedPixel.y, clickedPixel.x, UP);
		ranges.add(range);
		ranges.add(new Range(range.line, range.start, range.end, DOWN));

		int row;
		while (!ranges.isEmpty()) {
			range = ranges.poll();

			if (range.direction == UP) {
				row = range.line - 1;
				if (row >= 0) {
					checkRangeAndGenerateNewRanges(range, row, UP);
				}
			} else {
				row = range.line + 1;
				if (row < height) {
					checkRangeAndGenerateNewRanges(range, row, DOWN);
				}
			}
		}
	}

	private Range generateRangeAndReplaceColor(int row, int col, boolean direction) {
		Range range = new Range();
		int i;
		int start;

		pixels[row][col] = targetColor;
		filledPixels[row][col] = true;

		for (i = col - 1; i >= 0; i--) {
			if (!filledPixels[row][i] && (pixels[row][i] == replacementColor
					|| (considerTolerance && isPixelWithinColorTolerance(pixels[row][i], replacementColor)))) {
				pixels[row][i] = targetColor;
				filledPixels[row][i] = true;
			} else {
				break;
			}
		}
		start = i + 1;

		for (i = col + 1; i < width; i++) {
			if (!filledPixels[row][i] && (pixels[row][i] == replacementColor
					|| (considerTolerance && isPixelWithinColorTolerance(pixels[row][i], replacementColor)))) {
				pixels[row][i] = targetColor;
				filledPixels[row][i] = true;
			} else {
				break;
			}
		}

		range.line = row;
		range.start = start;
		range.end = i - 1;
		range.direction = direction;

		bitmap.setPixels(pixels[row], start, width, start, row, i - start, 1);

		return range;
	}

	private void checkRangeAndGenerateNewRanges(Range range, int row, boolean directionUp) {
		Range newRange;
		for (int col = range.start; col <= range.end; col++) {
			if (!filledPixels[row][col] && (pixels[row][col] == replacementColor
					|| (considerTolerance && isPixelWithinColorTolerance(pixels[row][col], replacementColor)))) {
				newRange = generateRangeAndReplaceColor(row, col, directionUp);
				ranges.add(newRange);

				if (newRange.start <= range.start - 2) {
					ranges.add(new Range(row, newRange.start, range.start - 2, !directionUp));
				}
				if (newRange.end >= range.end + 2) {
					ranges.add(new Range(row, range.end + 2, newRange.end, !directionUp));
				}

				if (newRange.end >= range.end - 1) {
					break;
				} else {
					col = newRange.end + 1;
				}
			}
		}
	}

	private boolean isPixelWithinColorTolerance(int pixel, int referenceColor) {
		int redDiff = ((pixel >> 16) & 0xFF) - ((referenceColor >> 16) & 0xFF);
		int greenDiff = ((pixel >> 8) & 0xFF) - ((referenceColor >> 8) & 0xFF);
		int blueDiff = (pixel & 0xFF) - (referenceColor & 0xFF);
		int alphaDiff = (pixel >>> 24) - (referenceColor >>> 24);

		return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff + alphaDiff * alphaDiff
				<= colorToleranceThresholdSquared;
	}

	class Range {
		public int line;
		public int start;
		public int end;
		public boolean direction;

		Range(int line, int start, int end, boolean directionUp) {
			this.line = line;
			this.start = start;
			this.end = end;
			this.direction = directionUp;
		}

		Range() {
			this.line = 0;
			this.start = 0;
			this.end = 0;
			this.direction = false;
		}
	}
}
