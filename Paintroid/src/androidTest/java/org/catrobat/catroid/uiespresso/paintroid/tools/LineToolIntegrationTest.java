/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.uiespresso.paintroid.tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.paintroid.MainActivity;
import org.catrobat.catroid.paintroid.PaintroidApplication;
import org.catrobat.catroid.paintroid.R;
import org.catrobat.catroid.uiespresso.paintroid.util.ActivityHelper;
import org.catrobat.catroid.common.paintroid.SystemAnimationsRule;
import org.catrobat.catroid.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.getCurrentToolPaint;
import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.getSurfacePointFromScreenPoint;
import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.resetColorPicker;
import static org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils.selectTool;
import static org.catrobat.catroid.uiespresso.paintroid.util.UiInteractions.swipe;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LineToolIntegrationTest {

	private static final int HALF_LINE_LENGTH = 25;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;

	private int displayWidth;
	private int displayHeight;

	private PointF pointOnScreenMiddle;

	@Before
	public void setUp() {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		displayWidth = activityHelper.getDisplayWidth();
		displayHeight = activityHelper.getDisplayHeight();

		pointOnScreenMiddle = new PointF(displayWidth / 2, displayHeight / 2);

		selectTool(ToolType.LINE);
		resetColorPicker();
	}

	@After
	public void tearDown() {
		activityHelper = null;
		displayWidth = 0;
		displayHeight = 0;
	}

	@Test
	public void testVerticalLineColor() {
		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);

		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, currentColor);

		onView(isRoot()).perform(swipe(pointOnScreenMiddle.x, pointOnScreenMiddle.y - HALF_LINE_LENGTH, pointOnScreenMiddle.x, pointOnScreenMiddle.y + HALF_LINE_LENGTH));

		currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, currentColor);
	}

	@Test
	public void testHorizontalLineColor() {
		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);

		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, currentColor);

		onView(isRoot()).perform(swipe(pointOnScreenMiddle.x - HALF_LINE_LENGTH, pointOnScreenMiddle.y, pointOnScreenMiddle.x + HALF_LINE_LENGTH, pointOnScreenMiddle.y));

		currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, currentColor);
	}

	@Test
	public void testDiagonalLineColor() {
		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);

		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, currentColor);

		onView(isRoot()).perform(swipe(pointOnScreenMiddle.x - HALF_LINE_LENGTH, pointOnScreenMiddle.y + HALF_LINE_LENGTH, pointOnScreenMiddle.x + HALF_LINE_LENGTH, pointOnScreenMiddle.y - HALF_LINE_LENGTH));

		currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);
		assertEquals("Color after drawing line has to be black", Color.BLACK, currentColor);
	}

	@Test
	public void testChangeLineToolForm() throws NoSuchFieldException, IllegalAccessException {
		PointF pointOnSurface = getSurfacePointFromScreenPoint(pointOnScreenMiddle);
		PointF pointOnCanvas = PaintroidApplication.perspective.getCanvasPointFromSurfacePoint(pointOnSurface);

		int currentColor = PaintroidApplication.drawingSurface.getPixel(pointOnCanvas);

		assertEquals("Color before doing anything has to be transparent", Color.TRANSPARENT, currentColor);

		openToolOptionsForCurrentTool();

		onView(withId(R.id.stroke_rbtn_rect)).perform(click());

		Paint strokePaint = getCurrentToolPaint();
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals("Paint width not changed after tool options changes", DEFAULT_STROKE_WIDTH, paintStrokeWidth);
		assertEquals("Wrong line form", Cap.SQUARE, strokePaint.getStrokeCap());

		// Close tool options
		clickSelectedToolButton();
	}
}
