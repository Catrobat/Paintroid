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
		longpressOnPointAndCheckIfCanvasPointHasChanged(leftMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomRight, true);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topLeft, true);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomLeft, true);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topRight, true);

		/*dragAndCheckIfCanvasHasMoved(middle, rightMiddle, false);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, leftMiddle, false);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, topMiddle, false);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, bottomMiddle, false);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, topRight, true);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		dragAndCheckIfCanvasHasMoved(middle, bottomRight, true);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle, true);
		dragAndCheckIfCanvasHasMoved(middle, bottomLeft, true);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		dragAndCheckIfCanvasHasMoved(middle, topLeft, true);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);*/
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
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, leftMiddle, false);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, topMiddle, false);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(middle, bottomMiddle, false);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		/*dragAndCheckIfCanvasHasMoved(middle, topRight, true);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		dragAndCheckIfCanvasHasMoved(middle, bottomRight, true);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle, true);
		dragAndCheckIfCanvasHasMoved(middle, bottomLeft, true);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		dragAndCheckIfCanvasHasMoved(middle, topLeft, true);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);*/

		/*dragAndCheckIfCanvasHasNotMoved(topLeft, topRight);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, topRight);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, bottomLeft);
		dragAndCheckIfCanvasHasNotMoved(topLeft, bottomLeft);*/

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
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(leftMiddle);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topMiddle);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomMiddle);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomRight);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topLeft);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(bottomLeft);
		longpressOnPointAndCheckIfCanvasPointHasNotChanged(topRight);

		// inactive
		/*dragAndCheckIfCanvasHasMoved(bottomMiddle, topMiddle, false);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(topMiddle, bottomMiddle, false);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(rightMiddle, leftMiddle, false);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(leftMiddle, rightMiddle, false);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);

		dragAndCheckIfCanvasHasMoved(bottomLeft, topRight, true);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		dragAndCheckIfCanvasHasMoved(topRight, bottomLeft, true);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		dragAndCheckIfCanvasHasMoved(bottomRight, topLeft, true);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);
		dragAndCheckIfCanvasHasMoved(topLeft, bottomRight, true);
		dragAndCheckIfCanvasHasMoved(bottomRight, middle, true);

		mSolo.clickOnScreen(middle.x, middle.y);
		mSolo.sleep(SLEEP_TIME);*/

		// active
		/*dragAndCheckIfCanvasHasMoved(bottomMiddle, topMiddle, false);
		dragAndCheckIfCanvasHasMoved(topMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(topMiddle, bottomMiddle, false);
		dragAndCheckIfCanvasHasMoved(bottomMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(rightMiddle, leftMiddle, false);
		dragAndCheckIfCanvasHasMoved(leftMiddle, middle, false);
		dragAndCheckIfCanvasHasMoved(leftMiddle, rightMiddle, false);
		dragAndCheckIfCanvasHasMoved(rightMiddle, middle, false);

		dragAndCheckIfCanvasHasMoved(bottomLeft, topRight, true);
		dragAndCheckIfCanvasHasMoved(topRight, middle, true);
		dragAndCheckIfCanvasHasMoved(topRight, bottomLeft, true);
		dragAndCheckIfCanvasHasMoved(bottomLeft, middle, true);
		dragAndCheckIfCanvasHasMoved(bottomRight, topLeft, true);
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);
		dragAndCheckIfCanvasHasMoved(topLeft, bottomRight, true);*/
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChanged(PointF clickPoint, boolean bothDirections) {
		PointF startPointSurface = Utils.getSurfacePointFromScreenPoint(clickPoint);

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
		mSolo.sleep(SLEEP_TIME);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasNotChanged(PointF clickPoint) {
		PointF startPointSurface = Utils.getSurfacePointFromScreenPoint(clickPoint);

		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);
		mSolo.clickLongOnScreen(clickPoint.x, clickPoint.y, CLICK_TIME);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		assertEquals("view should not scroll", startPointCanvas.x, endPointCanvas.x);
		assertEquals("view should not scroll", startPointCanvas.y, endPointCanvas.y);
		mSolo.sleep(SLEEP_TIME);
	}

	public void dragAndCheckIfCanvasHasMoved(PointF fromPoint, PointF toPoint, boolean bothDirections) {
		PointF startPointSurface = Utils.getSurfacePointFromScreenPoint(fromPoint);
		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, DRAG_STEPS);

		PointF endPointSurface = Utils.getSurfacePointFromScreenPoint(fromPoint);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		if (bothDirections) {
			assertTrue("scrolling did not work in x", startPointCanvas.x != endPointCanvas.x);
			assertTrue("scrolling did not work in y", startPointCanvas.y != endPointCanvas.y);
		} else {
			assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x)
					|| (startPointCanvas.y != endPointCanvas.y));
		}
		mSolo.sleep(SLEEP_TIME);
	}

	public void dragAndCheckIfCanvasHasNotMoved(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = Utils.getSurfacePointFromScreenPoint(fromPoint);
		PointF startPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		mSolo.drag(fromPoint.x, toPoint.x, fromPoint.y, toPoint.y, DRAG_STEPS);

		PointF endPointSurface = Utils.getSurfacePointFromScreenPoint(fromPoint);
		PointF endPointCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		assertEquals("view should not scroll but did it in x direction", startPointCanvas.x, endPointCanvas.x);
		assertEquals("view should not scroll but did it in y direction", startPointCanvas.y, endPointCanvas.y);
		mSolo.sleep(SLEEP_TIME);
	}
}
