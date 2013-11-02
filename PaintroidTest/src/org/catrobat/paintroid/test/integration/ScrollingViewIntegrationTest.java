package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Color;
import android.graphics.PointF;

public class ScrollingViewIntegrationTest extends BaseIntegrationTestClass {

	private final static int SLEEP_TIME = 1000;
	private final static int DRAG_STEPS = 500;
	private final static int CLICK_TIME = 2000;

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

		PointF middle = new PointF(xMiddle, yMiddle);
		PointF rightMiddle = new PointF(xRight, yMiddle);
		PointF leftMiddle = new PointF(xLeft, yMiddle);
		PointF topMiddle = new PointF(xMiddle, yTop);
		PointF bottomMiddle = new PointF(xMiddle, yBottom);
		PointF topLeft = new PointF(xLeft, yTop);
		PointF bottomRight = new PointF(xRight, yBottom);
		PointF bottomLeft = new PointF(xLeft, yBottom);
		PointF topRight = new PointF(xRight, yTop);

		longpressOnPointAndCheckIfCanvasPointHasNotChanged(middle);

		longpressOnPointAndCheckIfCanvasPointHasChanged(rightMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(leftMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomRight, true);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topLeft, true);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomLeft, true);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topRight, true);

		dragAndCheckIfCanvasHasMoved(middle, rightMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, leftMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
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

		dragAndCheckIfCanvasHasMoved(middle, rightMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, leftMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, bottomLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(middle, topLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);
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

	public void testScrollingViewCursorTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
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

		selectTool(ToolType.CURSOR);

		longpressOnPointAndCheckIfCanvasPointHasNotChanged(rightMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(leftMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomMiddle);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomRight);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topLeft);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomLeft);
		mSolo.sleep(SLEEP_TIME);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topRight);

		// inactive
		dragAndCheckIfCanvasHasMoved(bottomMiddle, topMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, bottomMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, leftMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, rightMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);

		dragAndCheckIfCanvasHasMoved(bottomLeft, topRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, bottomLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomRight, topLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, bottomRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle, true);

		mSolo.clickOnScreen(middle.x, middle.y);

		// active
		dragAndCheckIfCanvasHasMoved(bottomMiddle, topMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topMiddle, bottomMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, leftMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(leftMiddle, rightMiddle, false);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);
		mSolo.sleep(SLEEP_TIME);

		dragAndCheckIfCanvasHasMoved(bottomLeft, topRight, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topRight, bottomLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(bottomRight, topLeft, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);
		mSolo.sleep(SLEEP_TIME);
		dragAndCheckIfCanvasHasMoved(topLeft, bottomRight, true);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChanged(PointF clickPoint, boolean bothDirections) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(clickPoint);

		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);
		mSolo.clickLongOnScreen(clickPoint.x, clickPoint.y, CLICK_TIME);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		int startPointColor = PaintroidApplication.drawingSurface.getPixel(startPointCanvas);
		int endPointColor = PaintroidApplication.drawingSurface.getPixel(endPointCanvas);
		assertEquals("start", Color.BLACK, startPointColor);
		assertEquals("end", Color.BLACK, endPointColor);
		if (bothDirections) {
			assertTrue("scrolling did not work in x", startPointCanvas.x != endPointCanvas.x);
			assertTrue("scrolling did not work in y", startPointCanvas.y != endPointCanvas.y);
		} else {
			assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x)
					|| (startPointCanvas.y != endPointCanvas.y));
		}
	}

	public void longpressOnPointAndCheckIfCanvasPointHasNotChanged(PointF clickPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(clickPoint);

		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);
		mSolo.clickLongOnScreen(clickPoint.x, clickPoint.y, CLICK_TIME);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		assertEquals("view should not scroll", startPointCanvas.x, endPointCanvas.x);
		assertEquals("view should not scroll", startPointCanvas.y, endPointCanvas.y);
	}

	public void dragAndCheckIfCanvasHasMoved(PointF fromPoint, PointF toPoint, boolean bothDirections) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, DRAG_STEPS);

		PointF endPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		if (bothDirections) {
			assertTrue("scrolling did not work in x", startPointCanvas.x != endPointCanvas.x);
			assertTrue("scrolling did not work in y", startPointCanvas.y != endPointCanvas.y);
		} else {
			assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x)
					|| (startPointCanvas.y != endPointCanvas.y));
		}
	}

	public void dragAndCheckIfCanvasHasNotMoved(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, DRAG_STEPS);

		PointF endPointSurface = Utils.convertFromScreenToSurface(fromPoint);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		assertEquals("view should not scroll but did it in x direction", startPointCanvas.x, endPointCanvas.x);
		assertEquals("view should not scroll but did it in y direction", startPointCanvas.y, endPointCanvas.y);
	}
}
