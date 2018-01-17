package org.catrobat.paintroid.test.espresso;

import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getActionbarHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromSurfacePoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getStatusbarHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfaceHeight;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfacePointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getSurfaceWidth;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchLongAt;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class ScrollingViewIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		selectTool(ToolType.BRUSH);
	}

	@Test
	public void testScrollingViewDrawTool() throws NoSuchFieldException, IllegalAccessException {

		final int perspectiveScale = 5;
		PaintroidApplication.perspective.setScale(perspectiveScale);

		float surfaceWidth = getSurfaceWidth();
		float surfaceHeight = getSurfaceHeight();

		float xRight = surfaceWidth - 1;
		float xLeft = 1;
		float xMiddle = surfaceWidth / 2;

		final float actionBarHeight = getActionbarHeight();
		final float statusBarHeight = getStatusbarHeight();

		float yMiddle = (surfaceHeight / 2 + actionBarHeight + statusBarHeight);
		float yTop = (actionBarHeight + statusBarHeight);
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

		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(rightMiddle);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(leftMiddle);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(topMiddle);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(bottomMiddle);

		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(bottomRight);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(topLeft);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(bottomLeft);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(topRight);

//		dragAndCheckIfCanvasHasMovedInXOrY(middle, rightMiddle);
//		dragAndCheckIfCanvasHasMovedInXOrY(rightMiddle, middle);
//		dragAndCheckIfCanvasHasMovedInXOrY(middle, leftMiddle);
//		dragAndCheckIfCanvasHasMovedInXOrY(leftMiddle, middle);
//		dragAndCheckIfCanvasHasMovedInXOrY(middle, topMiddle);
//		dragAndCheckIfCanvasHasMovedInXOrY(topMiddle, middle);
//		dragAndCheckIfCanvasHasMovedInXOrY(middle, bottomMiddle);
//		dragAndCheckIfCanvasHasMovedInXOrY(bottomMiddle, middle);

//		dragAndCheckIfCanvasHasMovedInXAndY(middle, topRight);
//		dragAndCheckIfCanvasHasMovedInXAndY(topRight, middle);
//		dragAndCheckIfCanvasHasMovedInXAndY(middle, bottomRight);
//		dragAndCheckIfCanvasHasMovedInXAndY(bottomRight, middle);
//		dragAndCheckIfCanvasHasMovedInXAndY(middle, bottomLeft);
//		dragAndCheckIfCanvasHasMovedInXAndY(bottomLeft, middle);
//		dragAndCheckIfCanvasHasMovedInXAndY(middle, topLeft);
//		dragAndCheckIfCanvasHasMovedInXAndY(topLeft, middle);
	}

	@Ignore
	@Test
	public void testScrollingViewRectTool() throws NoSuchFieldException, IllegalAccessException {

		final int perspectiveScale = 5;
		PaintroidApplication.perspective.setScale(perspectiveScale);

		float surfaceWidth = getSurfaceWidth();
		float surfaceHeight = getSurfaceHeight();

		float distanceToLayerView = 60;
		float distanceToNavigationDrawer = 60;
		float xRight = surfaceWidth - distanceToLayerView;
		float xLeft = distanceToNavigationDrawer;
		float xMiddle = surfaceWidth / 2;

		final float actionBarHeight = getActionbarHeight();
		final float statusBarHeight = getStatusbarHeight();

		float yMiddle = (surfaceHeight / 2 + actionBarHeight + statusBarHeight);
		float yTop = (actionBarHeight + statusBarHeight);
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

		selectTool(ToolType.SHAPE);
		clickSelectedToolButton();

		dragAndCheckIfCanvasHasMovedInXOrY(middle, rightMiddle);
		dragAndCheckIfCanvasHasMovedInXOrY(rightMiddle, middle);
		dragAndCheckIfCanvasHasMovedInXOrY(middle, leftMiddle);
		dragAndCheckIfCanvasHasMovedInXOrY(leftMiddle, middle);
		dragAndCheckIfCanvasHasMovedInXOrY(middle, topMiddle);
		dragAndCheckIfCanvasHasMovedInXOrY(topMiddle, middle);
		dragAndCheckIfCanvasHasMovedInXOrY(middle, bottomMiddle);
		dragAndCheckIfCanvasHasMovedInXOrY(bottomMiddle, middle);

		dragAndCheckIfCanvasHasMovedInXAndY(middle, topRight);
		dragAndCheckIfCanvasHasMovedInXAndY(topRight, middle);
		dragAndCheckIfCanvasHasMovedInXAndY(middle, bottomRight);
		dragAndCheckIfCanvasHasMovedInXAndY(bottomRight, middle);
		dragAndCheckIfCanvasHasMovedInXAndY(middle, bottomLeft);
		dragAndCheckIfCanvasHasMovedInXAndY(bottomLeft, middle);
		dragAndCheckIfCanvasHasMovedInXAndY(middle, topLeft);
		dragAndCheckIfCanvasHasMovedInXAndY(topLeft, middle);

		/*dragAndCheckIfCanvasHasNotMoved(topLeft, topRight);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, topRight);
		dragAndCheckIfCanvasHasNotMoved(bottomRight, bottomLeft);
		dragAndCheckIfCanvasHasNotMoved(topLeft, bottomLeft);*/
	}

	@Test
	public void testScrollingViewCursorTool() throws NoSuchFieldException, IllegalAccessException {
		final int perspectiveScale = 5;
		PaintroidApplication.perspective.setScale(perspectiveScale);

		float surfaceWidth = getSurfaceWidth();
		float surfaceHeight = getSurfaceHeight();

		final float actionBarHeight = getActionbarHeight();
		final float statusBarHeight = getStatusbarHeight();

		float xRight = surfaceWidth - 1;
		float xLeft = 1;
		float xMiddle = surfaceWidth / 2;

		float yMiddle = (surfaceHeight / 2 + actionBarHeight + statusBarHeight);
		float yTop = (actionBarHeight + statusBarHeight);
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

		dragAndCheckIfCanvasHasMovedInXOrY(bottomMiddle, topMiddle);
		dragAndCheckIfCanvasHasMovedInXOrY(topMiddle, middle);
		dragAndCheckIfCanvasHasMovedInXOrY(topMiddle, bottomMiddle);
		dragAndCheckIfCanvasHasMovedInXOrY(bottomMiddle, middle);
//		dragAndCheckIfCanvasHasMovedInXOrY(rightMiddle, leftMiddle);
//		dragAndCheckIfCanvasHasMovedInXOrY(leftMiddle, middle);
//		dragAndCheckIfCanvasHasMovedInXOrY(leftMiddle, rightMiddle);
//		dragAndCheckIfCanvasHasMovedInXOrY(rightMiddle, middle);

//		dragAndCheckIfCanvasHasMovedInXAndY(bottomLeft, topRight);
//		dragAndCheckIfCanvasHasMovedInXAndY(topRight, middle);
//		dragAndCheckIfCanvasHasMovedInXAndY(topRight, bottomLeft);
//		dragAndCheckIfCanvasHasMovedInXAndY(bottomLeft, middle);
//		dragAndCheckIfCanvasHasMovedInXAndY(bottomRight, topLeft);
//		dragAndCheckIfCanvasHasMovedInXAndY(topLeft, middle);
//		dragAndCheckIfCanvasHasMovedInXAndY(topLeft, bottomRight);
//		dragAndCheckIfCanvasHasMovedInXAndY(bottomRight, middle);

		onView(isRoot()).perform(touchCenterMiddle());
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(PointF clickPoint) {
		PointF startPointSurface = getSurfacePointFromScreenPoint(clickPoint);

		PointF startPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(touchLongAt(clickPoint.x, clickPoint.y));

		PointF endPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		float delta = 0.5f;
		assertNotEquals("view should scroll in x", startPointCanvas.x, endPointCanvas.x, delta);
		assertNotEquals("view should scroll in y", startPointCanvas.y, endPointCanvas.y, delta);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(PointF clickPoint) {
		PointF startPointSurface = getSurfacePointFromScreenPoint(clickPoint);

		PointF startPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(touchLongAt(clickPoint.x, clickPoint.y));

		PointF endPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x) || (startPointCanvas.y != endPointCanvas.y));
	}

	public void longpressOnPointAndCheckIfCanvasPointHasNotChanged(PointF clickPoint) {
		PointF startPointSurface = getSurfacePointFromScreenPoint(clickPoint);

		PointF startPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(touchLongAt(clickPoint.x, clickPoint.y));

		PointF endPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		float delta = 0.5f;
		assertEquals("view should not scroll in x", startPointCanvas.x, endPointCanvas.x, delta);
		assertEquals("view should not scroll in y", startPointCanvas.y, endPointCanvas.y, delta);
	}

	public void dragAndCheckIfCanvasHasMovedInXAndY(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = getSurfacePointFromScreenPoint(fromPoint);
		PointF startPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint));

		PointF endPointSurface = getSurfacePointFromScreenPoint(fromPoint);
		PointF endPointCanvas = getCanvasPointFromSurfacePoint(endPointSurface);

		assertNotEquals("scrolling did not work in x", startPointCanvas.x, endPointCanvas.x);
		assertNotEquals("scrolling did not work in y", startPointCanvas.y, endPointCanvas.y);
	}

	public void dragAndCheckIfCanvasHasMovedInXOrY(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = getSurfacePointFromScreenPoint(fromPoint);
		PointF startPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint));

		PointF endPointSurface = getSurfacePointFromScreenPoint(fromPoint);
		PointF endPointCanvas = getCanvasPointFromSurfacePoint(endPointSurface);

		assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x) || (startPointCanvas.y != endPointCanvas.y));
	}

	public void dragAndCheckIfCanvasHasNotMoved(PointF fromPoint, PointF toPoint) {
		PointF startPointSurface = getSurfacePointFromScreenPoint(fromPoint);
		PointF startPointCanvas = getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint));

		PointF endPointSurface = getSurfacePointFromScreenPoint(fromPoint);
		PointF endPointCanvas = getCanvasPointFromSurfacePoint(endPointSurface);

		float delta = 0.5f;
		assertEquals("view should not scroll but did it in x direction", startPointCanvas.x, endPointCanvas.x, delta);
		assertEquals("view should not scroll but did it in y direction", startPointCanvas.y, endPointCanvas.y, delta);
	}
}
