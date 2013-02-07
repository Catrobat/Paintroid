package org.catrobat.paintroid.tools.helper.floodfill;

public class FloodFillRangeQueue {
	private FloodFillRange[] mArray;
	private int mHead;
	private int mCount;

	// Returns the number of items currently in the queue
	public int getCount() {
		return mCount;
	}

	public FloodFillRangeQueue(int initialSize) {
		mArray = new FloodFillRange[initialSize];
		mHead = 0;
		mCount = 0;
	}

	public FloodFillRange getFirst() {
		if (mHead < mArray.length) {
			return mArray[mHead];
		}
		return null;
	}

	public void addToEndOfQueue(FloodFillRange range) {
		if ((mCount + mHead) == mArray.length) {
			FloodFillRange[] newArray = new FloodFillRange[2 * mArray.length];
			System.arraycopy(mArray, mHead, newArray, 0, mCount);
			mArray = newArray;
			mHead = 0;
		}
		mArray[mHead + (mCount++)] = range;
	}

	public FloodFillRange removeAndReturnFirstElement() {
		FloodFillRange range = null;

		if (mCount > 0) {
			range = mArray[mHead];
			mArray[mHead] = null;
			mHead++; // advance head position
			mCount--; // update size to exclude dequeued item
		}

		return range;
	}
}
