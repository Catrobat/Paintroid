package at.tugraz.ist.paintroid.test.utils;

import junit.framework.Assert;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class PaintroidAsserts extends Assert {
	protected PaintroidAsserts() {

	}

	public static void assertPaintEquals(Paint expectedPaint, Paint actualPaint) {
		assertEquals(expectedPaint.getColor(), actualPaint.getColor());
		assertEquals(expectedPaint.getStrokeCap(), actualPaint.getStrokeCap());
		assertEquals(expectedPaint.getStrokeWidth(), actualPaint.getStrokeWidth());
	}

	public static void assertPathEquals(Path expectedPath, Path actualPath) {
		expectedPath.close();
		actualPath.close();
		RectF expectedPathBounds = new RectF();
		RectF actualPathBounds = new RectF();
		expectedPath.computeBounds(expectedPathBounds, true);
		actualPath.computeBounds(actualPathBounds, true);
		assertEquals(expectedPathBounds.bottom, actualPathBounds.bottom);
		assertEquals(expectedPathBounds.top, actualPathBounds.top);
		assertEquals(expectedPathBounds.left, actualPathBounds.left);
		assertEquals(expectedPathBounds.right, actualPathBounds.right);
	}
}
