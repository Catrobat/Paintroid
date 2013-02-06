package org.catrobat.paintroid.tools.helper.floodfill;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;

public class QueueLinearFloodFiller {

	private static FloodFillRangeQueue mRanges;
	private static int mBitmapWidth;
	private static int mBitmapHeight;
	private static boolean[] mPixelsChecked;
	private static Bitmap mBitmap;
	private static int mTargetColor;
	private static int mReplacementColor;
	private static double mSelectionThreshold;

	public static void floodFill(Bitmap bitmap, Point clickedPoint,
			int targetColor, int replacementColor, double selectionThreshold) {
		mBitmap = bitmap;
		mReplacementColor = replacementColor;
		mTargetColor = targetColor;
		mSelectionThreshold = selectionThreshold;

		mBitmapWidth = bitmap.getWidth();
		mBitmapHeight = bitmap.getHeight();
		mPixelsChecked = new boolean[mBitmapHeight * mBitmapWidth];

		// TODO: check if pixel access via array is faster
		// int[] pixelArray = new int[bitmapHeight * bitmapWidth];
		// bitmap.getPixels(pixelArray, 0, bitmapWidth, 0, 0, bitmapWidth,
		// bitmapHeight);

		// Init flood-fill range queue
		mRanges = new FloodFillRangeQueue(
				((mBitmapWidth + mBitmapHeight) / 2) * 5); // bitmapWidth
															// *
															// bitmapHeight);

		// First call of flood-fill
		linearFill(clickedPoint.x, clickedPoint.y);

		// Call flood-fill routine while flood-fill ranges still exist on the
		// queue
		while (mRanges.getCount() > 0) {
			// Get next range of the queue
			FloodFillRange range = mRanges.removeAndReturnFirstElement();

			int upY = range.y - 1;
			int downY = range.y + 1;

			for (int i = range.startX; i <= range.endX; i++) {
				// Start Fill Upwards,
				// if we're not above the top of the bitmap and the pixel
				// above this one is within the color tolerance
				if (checkPoint(i, upY)) {
					linearFill(i, upY);
				}

				// Start Fill Downwards,
				// if we're not below the bottom of the bitmap and
				// the pixel below this one is
				// within the color tolerance
				if (checkPoint(i, downY)) {
					linearFill(i, downY);
				}
			}
		}

	}

	private static boolean checkPoint(int x, int y) {
		int i = y * mBitmapHeight + x;
		if ((x > 0) && (x <= mBitmapWidth) && (y > 0) && (y <= mBitmapHeight)
				&& (!mPixelsChecked[i]) && isPixelWithinColorTolerance(x, y)) {
			return true;
		}
		return false;
	}

	private static boolean isPixelWithinColorTolerance(int x, int y) {
		int pixelColor = mBitmap.getPixel(x, y);

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

	// Finds the furtermost left and right boundaries of the fill area on a
	// given y coordinate and fills them on the way
	// Adds the resulting horizontal range to the ranges-queue to be processed
	// in the main loop
	private static void linearFill(int x, int y) {

		// find left edge of color area
		int leftMostX = x;

		// find right edge of color area
		int rightMostX = x;

		FloodFillRange range = new FloodFillRange(leftMostX, rightMostX, y);
		mRanges.addToEndOfQueue(range);
	}
}
