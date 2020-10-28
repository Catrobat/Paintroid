/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso;

import android.content.res.TypedArray;
import android.graphics.PointF;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchLongAt;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;

@RunWith(AndroidJUnit4.class)
public class ScrollingViewIntegrationTest {
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	private int drawerEdgeSize;
	private Perspective perspective;
	private MainActivity mainActivity;

	@Before
	public void setUp() {
		MainActivity activity = launchActivityRule.getActivity();
		float displayDensity = activity.getResources().getDisplayMetrics().density;
		perspective = activity.perspective;
		drawerEdgeSize = (int) (20 * displayDensity + 0.5f);
		mainActivity = launchActivityRule.getActivity();
	}

	@Test
	public void testScrollingViewDrawTool() {

		final int perspectiveScale = 5;
		perspective.setScale(perspectiveScale);

		float surfaceWidth = perspective.surfaceWidth;
		float surfaceHeight = perspective.surfaceHeight;

		float xRight = surfaceWidth - 1 - drawerEdgeSize;
		float xLeft = 1 + drawerEdgeSize;
		float xMiddle = surfaceWidth / 2;

		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

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

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		longpressOnPointAndCheckIfCanvasPointHasNotChanged(middle);

		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(rightMiddle);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(leftMiddle);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(topMiddle);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(bottomMiddle);

		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(bottomRight);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(topLeft);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(bottomLeft);
		longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(topRight);
	}

	@Test
	public void testScrollingViewCursorTool() {
		final int perspectiveScale = 5;
		perspective.setScale(perspectiveScale);

		float surfaceWidth = perspective.surfaceWidth;
		float surfaceHeight = perspective.surfaceHeight;

		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		float xRight = surfaceWidth - 100;
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

		onToolBarView()
				.performSelectTool(ToolType.CURSOR);

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

		onView(isRoot()).perform(touchCenterMiddle());
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChangedInXAndY(PointF clickPoint) {
		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		PointF startPointSurface = new PointF(perspective.surfaceCenterX, perspective.surfaceCenterY + actionBarHeight + statusBarHeight);

		PointF startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(touchLongAt(clickPoint.x, clickPoint.y));

		PointF endPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		float delta = 0.5f;
		assertNotEquals("view should scroll in x", startPointCanvas.x, endPointCanvas.x, delta);
		assertNotEquals("view should scroll in y", startPointCanvas.y, endPointCanvas.y, delta);
	}

	public void longpressOnPointAndCheckIfCanvasPointHasChangedInXOrY(PointF clickPoint) {
		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		PointF startPointSurface = new PointF(perspective.surfaceCenterX, perspective.surfaceCenterY + actionBarHeight + statusBarHeight);

		PointF startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(touchLongAt(clickPoint.x, clickPoint.y));

		PointF endPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		assertTrue("scrolling did not work", (startPointCanvas.x != endPointCanvas.x) || (startPointCanvas.y != endPointCanvas.y));
	}

	public void longpressOnPointAndCheckIfCanvasPointHasNotChanged(PointF clickPoint) {
		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		PointF startPointSurface = new PointF(perspective.surfaceCenterX, perspective.surfaceCenterY + actionBarHeight + statusBarHeight);

		PointF startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(touchLongAt(clickPoint.x, clickPoint.y));

		PointF endPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		float delta = 0.5f;
		assertEquals("view should not scroll in x", startPointCanvas.x, endPointCanvas.x, delta);
		assertEquals("view should not scroll in y", startPointCanvas.y, endPointCanvas.y, delta);
	}

	public void dragAndCheckIfCanvasHasMovedInXAndY(PointF fromPoint, PointF toPoint) {
		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		PointF startPointSurface = new PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight);
		PointF startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint));

		PointF endPointSurface = new PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight);
		PointF endPointCanvas = perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		assertNotEquals("scrolling did not work in x", startPointCanvas.x, endPointCanvas.x);
		assertNotEquals("scrolling did not work in y", startPointCanvas.y, endPointCanvas.y);
	}

	public void dragAndCheckIfCanvasHasMovedInXOrY(PointF fromPoint, PointF toPoint) {
		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		PointF startPointSurface = new PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight);
		PointF startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint));

		PointF endPointSurface = new PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight);
		PointF endPointCanvas = perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		String message = "startX(" + startPointCanvas.x + ") != endX(" + endPointCanvas.x
				+ ") || startY(" + startPointCanvas.y + ") != endY(" + endPointCanvas.y + ")";
		assertTrue(message, (startPointCanvas.x != endPointCanvas.x) || (startPointCanvas.y != endPointCanvas.y));
	}

	public void dragAndCheckIfCanvasHasNotMoved(PointF fromPoint, PointF toPoint) {
		int statusBarHeight = 0;
		int resourceId = mainActivity.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			statusBarHeight = mainActivity.getResources().getDimensionPixelSize(resourceId);
		}

		int actionBarHeight;
		final TypedArray styledAttributes = mainActivity.getTheme().obtainStyledAttributes(
				new int[] {android.R.attr.actionBarSize}
		);
		actionBarHeight = (int) styledAttributes.getDimension(0, 0);

		PointF startPointSurface = new PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight);
		PointF startPointCanvas = perspective.getCanvasPointFromSurfacePoint(startPointSurface);

		onView(isRoot()).perform(UiInteractions.swipe(fromPoint, toPoint));

		PointF endPointSurface = new PointF(fromPoint.x, fromPoint.y + actionBarHeight + statusBarHeight);
		PointF endPointCanvas = perspective.getCanvasPointFromSurfacePoint(endPointSurface);

		float delta = 0.5f;
		assertEquals("view should not scroll but did it in x direction", startPointCanvas.x, endPointCanvas.x, delta);
		assertEquals("view should not scroll but did it in y direction", startPointCanvas.y, endPointCanvas.y, delta);
	}
}
