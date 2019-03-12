package org.catrobat.paintroid.tools.helper;

import android.graphics.Point;
import android.graphics.PointF;

public final class Conversion {
	private Conversion() {
		throw new RuntimeException("no");
	}

	public static Point toPoint(PointF point) {
		return new Point((int) point.x, (int) point.y);
	}
}
