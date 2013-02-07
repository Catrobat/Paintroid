package org.catrobat.paintroid.tools.helper.floodfill;


public class FloodFillRangeQueue {
	private FloodFillRange[] array;
	private int head;
	private int count;

	// Returns the number of items currently in the queue
	public int getCount() {
		return count;
	}

	public FloodFillRangeQueue(int initialSize) {
		array = new FloodFillRange[initialSize];
		head = 0;
		count = 0;
	}

	public FloodFillRange getFirst() {
		return array[head];
	}

	public void addToEndOfQueue(FloodFillRange range) {
		if ((count + head) == array.length) {
			FloodFillRange[] newArray = new FloodFillRange[2 * array.length];
			System.arraycopy(array, head, newArray, 0, count);
			array = newArray;
			head = 0;
		}
		array[head + (count++)] = range;
	}

	public FloodFillRange removeAndReturnFirstElement() {
		FloodFillRange range = null;
		if (count > 0) {
			range = array[head];
			array[head] = null;
			head++;// advance head position
			count--;// update size to exclude dequeued item
		}
		return range;
	}
}
