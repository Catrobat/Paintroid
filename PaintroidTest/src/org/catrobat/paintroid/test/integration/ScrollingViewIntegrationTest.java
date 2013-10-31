package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Color;
import android.graphics.PointF;

public class ScrollingViewIntegrationTest extends BaseIntegrationTestClass {

	private final static int SLEEP_TIME = 500;

	public ScrollingViewIntegrationTest() throws Exception {
		super();
	}

	public void testScrollingViewDrawTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PaintroidApplication.perspective.setScale(5);
		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceHeight");
		float xRight = surfaceWidth - 1;
		float xLeft = 1;
		float xMiddle = surfaceWidth / 2;

		float yMiddle = (surfaceHeight / 2 + Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yTop = (Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yBottom = surfaceHeight + yTop - 1;

		PointF rightMiddle = new PointF(xRight, yMiddle);
		PointF leftMiddle = new PointF(xLeft, yMiddle);
		PointF topMiddle = new PointF(xMiddle, yTop);
		PointF bottomMiddle = new PointF(xMiddle, yBottom);
		PointF topLeft = new PointF(xLeft, yTop);
		PointF bottomRight = new PointF(xRight, yBottom);
		PointF bottomLeft = new PointF(xLeft, yBottom);
		PointF topRight = new PointF(xRight, yTop);

		longpressOnPointAndCheckIfCanvasPointHasChanged(rightMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(leftMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomRight);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topLeft);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomLeft);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topRight);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChanged(PointF clickPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(clickPoint);

		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);
		mSolo.clickLongOnScreen(clickPoint.x, clickPoint.y, 2000);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		int startPointColor = PaintroidApplication.drawingSurface.getPixel(startPointCanvas);
		int endPointColor = PaintroidApplication.drawingSurface.getPixel(endPointCanvas);
		assertEquals("start", Color.BLACK, startPointColor);
		assertEquals("end", Color.BLACK, endPointColor);
		assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x)
				|| (startPointCanvas.y != endPointCanvas.y));
	}

	public void testScrollingViewRectTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {
		PaintroidApplication.perspective.setScale(5);
		float surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceWidth");
		float surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
				"mSurfaceHeight");
		float xRight = surfaceWidth - 1;
		float xLeft = 1;
		float xMiddle = surfaceWidth / 2;

		float yMiddle = (surfaceHeight / 2 + Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yTop = (Utils.getActionbarHeight() + Utils.getStatusbarHeight());
		float yBottom = surfaceHeight + yTop - 1;

		PointF middle = new PointF(xMiddle, yMiddle);
		PointF rightMiddle = new PointF(xRight, yMiddle);
		PointF leftMiddle = new PointF(xLeft, yMiddle);
		PointF topMiddle = new PointF(xMiddle, yTop);
		PointF bottomMiddle = new PointF(xMiddle, yBottom);
		PointF topLeft = new PointF(xLeft, yTop);
		PointF bottomRight = new PointF(xRight, yBottom);
		PointF bottomLeft = new PointF(xLeft, yBottom);
		PointF topRight = new PointF(xRight, yTop);

		selectTool(ToolType.RECT);

		dragAndCheckIfCanvasHasMoved(middle, rightMiddle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, leftMiddle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topMiddle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomMiddle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topRight);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomRight);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomLeft);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topLeft);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, middle);
		mSolo.sleep(SLEEP_TIME);

		dragAndCheckIfCanvasHasNotMoved(topLeft, topRight);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, topRight);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, bottomLeft);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasNotMoved(topLeft, bottomLeft);
		mSolo.sleep(SLEEP_TIME);

	}

	public void dragAndCheckIfCanvasHasMoved(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, 100);

		PointF endPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x)
				|| (startPointCanvas.y != endPointCanvas.y));
	}

	public void dragAndCheckIfCanvasHasNotMoved(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, 100);

		PointF endPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		assertTrue("scrolling did not work", (startPointCanvas.x == endPointCanvas.x)
				|| (startPointCanvas.y == endPointCanvas.y));
	}
}
