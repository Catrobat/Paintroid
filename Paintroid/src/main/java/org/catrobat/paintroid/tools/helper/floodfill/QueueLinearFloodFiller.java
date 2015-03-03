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

package org.catrobat.paintroid.tools.helper.floodfill;

import android.graphics.Color;
import android.graphics.Point;

/*
 * This Java-Implementation is based on the following two approaches:
 * http://www.codeproject.com/Articles/16405/Queue-Linear-Flood-Fill-A-Fast-Flood-Fill-Algorith?fid=359235&fr=31#xx0xx
 * https://github.com/nrkn/NrknLib/blob/master/Geometry/FloodFiller/QueueLinearFloodFiller.cs
 */
public class QueueLinearFloodFiller {

	private static FloodFillRangeQueue mRanges;
	private static int mBitmapWidth;
	private static int mBitmapHeight;
	private static boolean[] mPixelsChecked;
	private static int[] mPixels;
	private static int mTargetColor;
	private static int mReplacementColor;
	private static double mSelectionThreshold;

	public static void floodFill(int[] pixels, int width, int height,
			Point clickedPoint, int targetColor, int replacementColor,
			double selectionThreshold) {
		mPixels = pixels;
		mReplacementColor = replacementColor;
		mTargetColor = targetColor;
		mSelectionThreshold = selectionThreshold;

		mBitmapWidth = width;
		mBitmapHeight = height;
		mPixelsChecked = new boolean[mBitmapHeight * mBitmapWidth];

		mRanges = new FloodFillRangeQueue(mBitmapWidth + mBitmapHeight);

		linearFill(clickedPoint.x, clickedPoint.y);

		// Call flood-fill routine while flood-fill ranges still exist in queue
		while (mRanges.getCount() > 0) {
			// Get next range of the queue
			FloodFillRange range = mRanges.removeAndReturnFirstElement();

			int upY = range.y - 1;
			int downY = range.y + 1;

			for (int i = range.startX; i <= range.endX; i++) {
				// Start Fill Upwards,
				if (checkPoint(i, upY)) {
					linearFill(i, upY);
				}

				// Start Fill Downwards,
				if (checkPoint(i, downY)) {
					linearFill(i, downY);
				}
			}
		}
	}

	/*
	 * Find the furthermost left and right boundaries of the fill area on a
	 * given y coordinate and fills them on the way. Adds the resulting
	 * horizontal range to the ranges-queue to be processed in the main loop.
	 */
	private static void linearFill(int x, int y) {
		// find left edge of color area
		int leftMostX = x;
		int pixelIndex = y * mBitmapWidth + x;
		while (true) {
			mPixels[(mBitmapWidth * y) + leftMostX] = mReplacementColor;
			mPixelsChecked[pixelIndex] = true;
			leftMostX--;
			pixelIndex--;
			if (!checkPoint(leftMostX, y)) {
				break;
			}
		}
		leftMostX++;

		// find right edge of color area
		int rightMostX = x;
		pixelIndex = y * mBitmapWidth + x;
		while (true) {
			mPixels[(mBitmapWidth * y) + rightMostX] = mReplacementColor;
			mPixelsChecked[pixelIndex] = true;
			rightMostX++;
			pixelIndex++;
			if (!checkPoint(rightMostX, y)) {
				break;
			}
		}
		rightMostX--;

		FloodFillRange range = new FloodFillRange(leftMostX, rightMostX, y);
		mRanges.addToEndOfQueue(range);
	}

	private static boolean checkPoint(int x, int y) {
		int pixelIndex = y * mBitmapWidth + x;
		if ((x >= 0) && (x < mBitmapWidth) && (y >= 0) && (y < mBitmapHeight)
				&& (!mPixelsChecked[pixelIndex])
				&& isPixelWithinColorTolerance(x, y)) {
			return true;
		}
		return false;
	}

	private static boolean isPixelWithinColorTolerance(int x, int y) {
		int pixelColor = mPixels[(mBitmapWidth * y) + x];

		int targetRed = Color.red(mTargetColor);
		int pixelRed = Color.red(pixelColor);
		int targetBlue = Color.blue(mTargetColor);
		int pixelBlue = Color.blue(pixelColor);
		int targetGreen = Color.green(mTargetColor);
		int pixelGreen = Color.green(pixelColor);
		int targetAlpha = Color.alpha(mTargetColor);
		int pixelAlpha = Color.alpha(pixelColor);

		double diff = Math.sqrt(Math.pow((pixelRed - targetRed), 2)
				+ Math.pow((pixelGreen - targetGreen), 2)
				+ Math.pow((pixelBlue - targetBlue), 2)
				+ Math.pow((pixelAlpha - targetAlpha), 2));

		return diff < mSelectionThreshold;
	}
}
