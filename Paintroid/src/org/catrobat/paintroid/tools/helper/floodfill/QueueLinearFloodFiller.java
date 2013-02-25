package org.catrobat.paintroid.tools.helper.floodfill;

import org.catrobat.paintroid.PaintroidApplication;

import android.graphics.Color;
import android.graphics.Point;
import android.util.Log;

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
		Log.i(PaintroidApplication.TAG,
				"Index: " + Integer.toString(pixelIndex));
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

		double diff = Math.sqrt(Math.pow((pixelRed - targetRed), 2)
				+ Math.pow((pixelGreen - targetGreen), 2)
				+ Math.pow((pixelBlue - targetBlue), 2));

		return diff < mSelectionThreshold;
	}
}
