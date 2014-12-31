package org.catrobat.paintroid.test.integration;

import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;

import android.graphics.Color;
import android.graphics.PointF;

public class ScrollingViewIntegrationTest extends BaseIntegrationTestClass {

	private final static int SLEEP_TIME = 100;
	private final static int DRAG_STEPS = 200;
	private final static int CLICK_TIME = 100;
    private final static int OFFSET = 100;
    private final static int SCALE = 2;

    private PointF middle;
    private PointF rightMiddle;
    private PointF leftMiddle;
    private PointF topMiddle;
    private PointF bottomMiddle;
    private PointF topLeft;
    private PointF bottomRight;
    private PointF bottomLeft;
    private PointF topRight;
    private PointF topRightOffset;
    private PointF rightMiddleOffset;
    private PointF leftMiddleOffset;
    private PointF topMiddleOffset;
    private PointF bottomMiddleOffset;
    private PointF topLeftOffset;
    private PointF bottomRightOffset;
    private PointF bottomLeftOffset;


    public ScrollingViewIntegrationTest() throws Exception {
		super();
	}

    @Override
    public void setUp() {
        super.setUp();

        float surfaceWidth;
        float surfaceHeight;
        float xRight;
        float xLeft;
        float xMiddle;
        float yMiddle;
        float yTop;
        float yBottom;

        try {
            surfaceWidth = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
                    "mSurfaceWidth");
            surfaceHeight = (Float) PrivateAccess.getMemberValue(Perspective.class, PaintroidApplication.perspective,
                    "mSurfaceHeight");
            xRight = surfaceWidth - 1;
            xLeft = 1;
            xMiddle = surfaceWidth / 2;

            yMiddle = (surfaceHeight / 2 + Utils.getActionbarHeight() + Utils.getStatusbarHeight());
            yTop = (Utils.getActionbarHeight() + Utils.getStatusbarHeight());
            yBottom = surfaceHeight + yTop - 1;

            middle = new PointF(xMiddle, yMiddle);
            rightMiddle = new PointF(xRight, yMiddle);
            leftMiddle = new PointF(xLeft, yMiddle);
            topMiddle = new PointF(xMiddle, yTop);
            bottomMiddle = new PointF(xMiddle, yBottom);
            topLeft = new PointF(xLeft, yTop);
            bottomRight = new PointF(xRight, yBottom);
            bottomLeft = new PointF(xLeft, yBottom);
            topRight = new PointF(xRight, yTop);
            topRightOffset = new PointF(xRight - OFFSET, yTop + OFFSET);
            rightMiddleOffset = new PointF(xRight - OFFSET, yMiddle);
            leftMiddleOffset = new PointF(xLeft + OFFSET, yMiddle);
            topMiddleOffset = new PointF(xMiddle, yTop + OFFSET);
            bottomMiddleOffset = new PointF(xMiddle, yBottom - OFFSET);
            topLeftOffset = new PointF(xLeft + OFFSET, yTop + OFFSET);
            bottomRightOffset = new PointF(xRight - OFFSET, yBottom - OFFSET);
            bottomLeftOffset = new PointF(xLeft + OFFSET, yBottom - OFFSET);
        } catch (Exception e) {
            e.printStackTrace();
            fail("setup failed" + e.toString());
        }
    }

	public void testScrollingViewDrawTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PaintroidApplication.perspective.setScale(SCALE);

		longpressOnPointAndCheckIfCanvasPointHasNotChanged(middle);

		longpressOnPointAndCheckIfCanvasPointHasChanged(rightMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(leftMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomMiddle, false);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomRight, true);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topLeft, true);
		longpressOnPointAndCheckIfCanvasPointHasChanged(bottomLeft, true);
		longpressOnPointAndCheckIfCanvasPointHasChanged(topRight, true);

        dragAndCheckIfCanvasHasMoved(rightMiddleOffset, rightMiddle, false);
        dragAndCheckIfCanvasHasMoved(leftMiddleOffset, leftMiddle, false);
        dragAndCheckIfCanvasHasMoved(topMiddleOffset, topMiddle, false);
        dragAndCheckIfCanvasHasMoved(bottomMiddleOffset, bottomMiddle, false);
        dragAndCheckIfCanvasHasMoved(topRightOffset, topRight, true);
        dragAndCheckIfCanvasHasMoved(bottomRightOffset, bottomRight, true);
        dragAndCheckIfCanvasHasMoved(bottomLeftOffset, bottomLeft, true);
        dragAndCheckIfCanvasHasMoved(topLeftOffset, topLeft, true);
	}

	public void testScrollingViewRectTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PaintroidApplication.perspective.setScale(SCALE);

		selectTool(ToolType.RECT);

		dragAndCheckIfCanvasHasMoved(middle, rightMiddle, false);
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
		dragAndCheckIfCanvasHasMoved(topLeft, middle, true);

		dragAndCheckIfCanvasHasNotMoved(topLeft, topRight);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, topRight);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, bottomLeft);
		dragAndCheckIfCanvasHasNotMoved(topLeft, bottomLeft);

	}

	public void testScrollingViewCursorTool() throws SecurityException, IllegalArgumentException, NoSuchFieldException,
			IllegalAccessException {

		PaintroidApplication.perspective.setScale(SCALE);

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
		dragAndCheckIfCanvasHasMoved(bottomMiddle, topMiddle, false);
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
		mSolo.sleep(SLEEP_TIME);

		// active
		dragAndCheckIfCanvasHasMoved(bottomMiddle, topMiddle, false);
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
