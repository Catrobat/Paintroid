package org.catrobat.paintroid.tools.common;

import android.graphics.Point;

public class PointScrollBehavior implements ScrollBehavior {
	private final int scrollTolerance;

	public PointScrollBehavior(int scrollTolerance) {
		this.scrollTolerance = scrollTolerance;
	}

	@Override
	public Point getScrollDirection(float pointX, float pointY, int viewWidth, int viewHeight) {
		int deltaX = 0;
		int deltaY = 0;

		if (pointX < scrollTolerance) {
			deltaX = 1;
		}
		if (pointX > viewWidth - scrollTolerance) {
			deltaX = -1;
		}

		if (pointY < scrollTolerance) {
			deltaY = 1;
		}

		if (pointY > viewHeight - scrollTolerance) {
			deltaY = -1;
		}

		return new Point(deltaX, deltaY);
	}
}
