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

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.PointF;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCanvasPointFromScreenPoint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getCurrentToolPaint;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetColorPicker;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EraserToolIntegrationTest {

	private static final String TEXT_DEFAULT_STROKE_WIDTH = Integer.toString(DEFAULT_STROKE_WIDTH);

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;
	private PointF screenPoint;
	private PointF canvasPoint;

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		PaintroidApplication.drawingSurface.destroyDrawingCache();

		screenPoint = new PointF(activityHelper.getDisplayWidth() / 2, activityHelper.getDisplayHeight() / 2);
		canvasPoint = getCanvasPointFromScreenPoint(screenPoint);

		selectTool(ToolType.BRUSH);
		resetColorPicker();
		resetDrawPaintAndBrushPickerView();
	}

	@After
	public void tearDown() {
		screenPoint = null;
		canvasPoint = null;
		activityHelper = null;
	}

	@Test
	public void testEraseNothing() {

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Get transparent background color", Color.TRANSPARENT, colorBeforeErase);

		selectTool(ToolType.ERASER);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Pixel should still be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	@Test
	public void testErase() {

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);
	}

	@Test
	public void testSwitchingBetweenBrushAndEraser() {

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);

		selectTool(ToolType.BRUSH);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterBrush = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be black again like before erasing", Color.BLACK, colorAfterBrush);

		selectTool(ToolType.ERASER);

		onView(isRoot()).perform(touchAt(screenPoint));

		colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After erasing, pixel should be transparent again", Color.TRANSPARENT, colorAfterErase);
	}

	@Test
	public void testChangeEraserBrushSize() throws NoSuchFieldException, IllegalAccessException {

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);
		openToolOptionsForCurrentTool();

		final ViewInteraction seekBarViewInteraction = onView(withId(R.id.stroke_width_seek_bar));
		final ViewInteraction strokeWidthTextViewInteraction = onView(withId(R.id.stroke_width_width_text));

		seekBarViewInteraction.check(matches(isDisplayed()));
		strokeWidthTextViewInteraction.check(matches(isDisplayed()));

		strokeWidthTextViewInteraction.check(matches(withText(TEXT_DEFAULT_STROKE_WIDTH)));
		seekBarViewInteraction.check(matches(withProgress(DEFAULT_STROKE_WIDTH)));

		int newStrokeWidth = 80;
		seekBarViewInteraction.perform(setProgress(newStrokeWidth));

		seekBarViewInteraction.check(matches(withProgress(newStrokeWidth)));

		// Close tool options
		clickSelectedToolButton();

		Paint strokePaint = getCurrentToolPaint();
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals("Paint width not changed after tool options changes", paintStrokeWidth, newStrokeWidth);

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	@Test
	public void testChangeEraserBrushForm() throws NoSuchFieldException, IllegalAccessException {

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);
		openToolOptionsForCurrentTool();

		onView(withId(R.id.stroke_rbtn_rect)).perform(click());

		// Close tool options
		clickSelectedToolButton();

		Paint strokePaint = getCurrentToolPaint();
		assertEquals("Wrong eraser form", Cap.SQUARE, strokePaint.getStrokeCap());

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorAfterErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("Brushing after erase should be transparent", Color.TRANSPARENT, colorAfterErase);
	}

	@Test
	public void testRestorePreviousToolSettings() throws NoSuchFieldException, IllegalAccessException {

		onView(isRoot()).perform(touchAt(screenPoint));

		int colorBeforeErase = PaintroidApplication.drawingSurface.getPixel(canvasPoint);
		assertEquals("After painting black, pixel should be black", Color.BLACK, colorBeforeErase);

		selectTool(ToolType.ERASER);
		openToolOptionsForCurrentTool();

		final ViewInteraction seekBarViewInteraction = onView(withId(R.id.stroke_width_seek_bar));
		final ViewInteraction strokeWidthTextViewInteraction = onView(withId(R.id.stroke_width_width_text));

		seekBarViewInteraction.check(matches(isDisplayed()));
		strokeWidthTextViewInteraction.check(matches(isDisplayed()));

		strokeWidthTextViewInteraction.check(matches(withText(TEXT_DEFAULT_STROKE_WIDTH)));
		seekBarViewInteraction.check(matches(withProgress(DEFAULT_STROKE_WIDTH)));

		int newStrokeWidth = 80;
		seekBarViewInteraction.perform(setProgress(newStrokeWidth));
		seekBarViewInteraction.check(matches(withProgress(newStrokeWidth)));

		onView(withId(R.id.stroke_rbtn_rect)).perform(click());

		Paint strokePaint = getCurrentToolPaint();
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals("Paint width not changed after tool options changes", newStrokeWidth, paintStrokeWidth);
		assertEquals("Wrong eraser form", Cap.SQUARE, strokePaint.getStrokeCap());

		// Close tool options
		clickSelectedToolButton();

		selectTool(ToolType.ERASER);
		openToolOptionsForCurrentTool();

		seekBarViewInteraction.check(matches(withProgress(newStrokeWidth)));

		int eraserStrokeWidth = 60;
		seekBarViewInteraction.perform(setProgress(eraserStrokeWidth));
		seekBarViewInteraction.check(matches(withProgress(eraserStrokeWidth)));

		onView(withId(R.id.stroke_rbtn_circle)).perform(click());

		strokePaint = getCurrentToolPaint();
		paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		assertEquals("Paint width not changed after tool options changes", eraserStrokeWidth, paintStrokeWidth);
		assertEquals("Wrong eraser form", Cap.ROUND, strokePaint.getStrokeCap());

		// Close tool options
		clickSelectedToolButton();

		selectTool(ToolType.BRUSH);

		//TODO: check functionality
//		Paint lastStrokePaint = getCurrentToolPaint();
//		int lastStrokeWidth = (int) lastStrokePaint.getStrokeWidth();
//		assertEquals("Paint width not changed after tool options changes", eraserStrokeWidth, lastStrokeWidth);
//		assertEquals("Wrong eraser form", Cap.SQUARE, lastStrokePaint.getStrokeCap());
	}

}
