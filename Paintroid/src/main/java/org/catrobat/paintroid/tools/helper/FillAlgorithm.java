package org.catrobat.paintroid.tools.helper;

import android.graphics.Bitmap;
import android.graphics.Color;
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
	private int mWidth;
	private int mHeight;
	private Queue<Range> mRanges;

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

	public FillAlgorithm(Bitmap bitmap, Point clickedPixel, int targetColor, int replacementColor, int colorToleranceThreshold) {
		mBitmap = bitmap;
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		mPixels = new int[bitmap.getHeight()][bitmap.getWidth()];
		for (int i = 0; i < mHeight; i++) {
			mBitmap.getPixels(mPixels[i], 0, mWidth, 0, i, mWidth, 1);
		}
		mClickedPixel = clickedPixel;
		mTargetColor = targetColor;
		mReplacementColor = replacementColor;
		mRanges = new LinkedList<Range>();
		mColorToleranceThresholdSquared = colorToleranceThreshold*colorToleranceThreshold;
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

		for (i = col - 1; i >= 0; i--) {
			if (mPixels[row][i] == mReplacementColor || isPixelWithinColorTolerance(mPixels[row][i])) {
				mPixels[row][i] = mTargetColor;
			} else {
				break;
			}
		}
		i++;
		start = i;

		for (i = col + 1; i < mWidth; i++) {
			if (mPixels[row][i] == mReplacementColor || isPixelWithinColorTolerance(mPixels[row][i])) {
				mPixels[row][i] = mTargetColor;
			} else {
				break;
			}
		}
		i--;

		range.line = row;
		range.start = start;
		range.end = i;
		range.direction = direction;
		
		mBitmap.setPixels(mPixels[row], start, mWidth, start, row, i - start + 1, 1);

		return range;
	}

	private void checkRangeAndGenerateNewRanges(Range range, int row, boolean directionUp) {
		Range newRange;
		for (int col = range.start; col <= range.end; col++) {
			if (mPixels[row][col] == mReplacementColor || isPixelWithinColorTolerance(mPixels[row][col])) {
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

	private boolean isPixelWithinColorTolerance(int pixel) {
		return 	(Color.red(pixel) - Color.red(mReplacementColor))*(Color.red(pixel) - Color.red(mReplacementColor))
				  + (Color.green(pixel) - Color.green(mReplacementColor))*(Color.green(pixel) - Color.green(mReplacementColor))
				  + (Color.blue(pixel) - Color.blue(mReplacementColor))*(Color.blue(pixel) - Color.blue(mReplacementColor))
				  + (Color.alpha(pixel) - Color.alpha(mReplacementColor))*(Color.alpha(pixel) - Color.alpha(mReplacementColor)) 
				<= mColorToleranceThresholdSquared;
	}

}
