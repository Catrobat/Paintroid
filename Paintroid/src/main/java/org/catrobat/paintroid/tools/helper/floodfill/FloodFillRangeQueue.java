/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2013 The Catrobat Team
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
