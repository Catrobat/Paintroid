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

import java.util.LinkedList;
import java.util.Queue;


public class FillAlgorithm {
	private static final boolean UP = true;
	private static final boolean DOWN = false;

	private Bitmap mBitmap;
	private int[][] mPixels;
	private Point mClickedPixel;
	private int mTargetColor;
	private int mReplacementColor;
	private int mColorToleranceThresholdSquared;
	private boolean mConsiderTolerance;
	private int mWidth;
	private int mHeight;
	private Queue<Range> mRanges;
	private boolean[][] mFilledPixels;

	class Range {
		public int line;
		public int start;
		public int end;
		public boolean direction;

		public Range(int line, int start, int end, boolean directionUp) {
			this.line = line;
			this.start = start;
			this.end = end;
			this.direction = directionUp;
		}

		public Range() {
			this.line = 0;
			this.start = 0;
			this.end = 0;
			this.direction = false;
		}
	}

	public FillAlgorithm(Bitmap bitmap, Point clickedPixel, int targetColor, int replacementColor, float colorToleranceThreshold) {
		mBitmap = bitmap;
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		mPixels = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < mHeight; i++) {
			mBitmap.getPixels(mPixels[i], 0, mWidth, 0, i, mWidth, 1);
		}
		mFilledPixels = new boolean[bitmap.getHeight()][bitmap.getWidth()];
		mClickedPixel = clickedPixel;
		mTargetColor = targetColor;
		mReplacementColor = replacementColor;
		mRanges = new LinkedList<Range>();
		mColorToleranceThresholdSquared = (int)(colorToleranceThreshold*colorToleranceThreshold);
		mConsiderTolerance = colorToleranceThreshold > 0;
	}

	public void performFilling()
	{
		Range range = generateRangeAndReplaceColor(mClickedPixel.y, mClickedPixel.x, UP);
		mRanges.add(range);
		mRanges.add(new Range(range.line, range.start, range.end, DOWN));

		int row;
		while (!mRanges.isEmpty()) {
			range = mRanges.poll();

			if (range.direction == UP) {
				row = range.line - 1;
				if (row >= 0) {
					checkRangeAndGenerateNewRanges(range, row, UP);
				}
			} else {
				row = range.line + 1;
				if (row < mHeight) {
					checkRangeAndGenerateNewRanges(range, row, DOWN);
				}
			}
		}
	}

	private Range generateRangeAndReplaceColor(int row, int col, boolean direction) {
		Range range = new Range();
		int i;
		int start;

		mPixels[row][col] = mTargetColor;
		mFilledPixels[row][col] = true;

		for (i = col - 1; i >= 0; i--) {
			if (!mFilledPixels[row][i] && (mPixels[row][i] == mReplacementColor ||
					(mConsiderTolerance && isPixelWithinColorTolerance(mPixels[row][i], mReplacementColor)))) {
				mPixels[row][i] = mTargetColor;
				mFilledPixels[row][i] = true;
			} else {
				break;
			}
		}
		start = i+1;

		for (i = col + 1; i < mWidth; i++) {
			if (!mFilledPixels[row][i] && (mPixels[row][i] == mReplacementColor ||
					(mConsiderTolerance && isPixelWithinColorTolerance(mPixels[row][i], mReplacementColor)))) {
				mPixels[row][i] = mTargetColor;
				mFilledPixels[row][i] = true;
			} else {
				break;
			}
		}

		range.line = row;
		range.start = start;
		range.end = i-1;
		range.direction = direction;
		
		mBitmap.setPixels(mPixels[row], start, mWidth, start, row, i - start, 1);

		return range;
	}

	private void checkRangeAndGenerateNewRanges(Range range, int row, boolean directionUp) {
		Range newRange;
		for (int col = range.start; col <= range.end; col++) {
			if (!mFilledPixels[row][col] && (mPixels[row][col] == mReplacementColor ||
					(mConsiderTolerance && isPixelWithinColorTolerance(mPixels[row][col], mReplacementColor)))) {
				newRange = generateRangeAndReplaceColor(row, col, directionUp);
				mRanges.add(newRange);

				if (newRange.start <= range.start - 2) {
					mRanges.add(new Range(row, newRange.start, range.start - 2, !directionUp));
				}
				if (newRange.end >= range.end + 2) {
					mRanges.add(new Range(row, range.end + 2, newRange.end, !directionUp));
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

		return redDiff*redDiff + greenDiff*greenDiff + blueDiff*blueDiff + alphaDiff*alphaDiff
				<= mColorToleranceThresholdSquared;
	}

}
