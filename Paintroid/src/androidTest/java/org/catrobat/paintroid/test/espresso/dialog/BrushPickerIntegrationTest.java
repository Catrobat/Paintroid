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

package org.catrobat.paintroid.test.espresso.dialog;

import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isChecked;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isNotChecked;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class BrushPickerIntegrationTest {
	private static final int MIN_STROKE_WIDTH = 1;
	private static final int MIDDLE_STROKE_WIDTH = 50;
	private static final int MAX_STROKE_WIDTH = 100;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH)
				.performOpenToolOptions();

		/*
		 * Reset brush picker view and paint color, because BRUSH and LINE tool share the same
		 * tool options
		 */
		resetDrawPaintAndBrushPickerView();
	}

	private Paint getCurrentToolBitmapPaint() {
		return BaseTool.BITMAP_PAINT;
	}

	private Paint getCurrentToolCanvasPaint() {
		return BaseTool.CANVAS_PAINT;
	}

	private void assertStrokePaint(Paint strokePaint, int expectedStrokeWidth, Cap expectedCap) {
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		Cap paintCap = strokePaint.getStrokeCap();

		assertEquals("Stroke did not change", expectedStrokeWidth, paintStrokeWidth);
		assertEquals("Stroke cap not " + expectedCap.toString(), expectedCap, paintCap);
	}

	private void setStrokeWidth(int strokeWidth, int expectedStrokeWidth) {
		onView(withId(R.id.stroke_width_seek_bar))
				.perform(setProgress(strokeWidth))
				.check(matches(withProgress(expectedStrokeWidth)));
		onView(withId(R.id.stroke_width_width_text))
				.check(matches(withText(Integer.toString(expectedStrokeWidth))));
	}

	private void setStrokeWidth(int strokeWidth) {
		setStrokeWidth(strokeWidth, strokeWidth);
	}

	@Test
	public void brushPickerDialogDefaultLayoutAndToolChanges() throws NoSuchFieldException, IllegalAccessException {
		onView(withId(R.id.stroke_width_seek_bar))
				.check(matches(isDisplayed()))
				.check(matches(withProgress(DEFAULT_STROKE_WIDTH)));
		onView(withId(R.id.stroke_width_width_text))
				.check(matches(isDisplayed()))
				.check(matches(withText(Integer.toString(DEFAULT_STROKE_WIDTH))));
		onView(withId(R.id.stroke_rbtn_rect))
				.check(matches(isDisplayed()))
				.check(matches(isNotChecked()));
		onView(withId(R.id.stroke_rbtn_circle))
				.check(matches(isDisplayed()))
				.check(matches(isChecked()));

		setStrokeWidth(MIN_STROKE_WIDTH);
		setStrokeWidth(MIDDLE_STROKE_WIDTH);
		setStrokeWidth(MAX_STROKE_WIDTH);

		assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.ROUND);

		onView(withId(R.id.stroke_rbtn_rect))
				.perform(click())
				.check(matches(isChecked()));
		onView(withId(R.id.stroke_rbtn_circle))
				.check(matches(isNotChecked()));

		assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptions();

		assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE);
	}

	@Test
	public void brushPickerDialogKeepStrokeOnToolChange() throws NoSuchFieldException, IllegalAccessException {
		final int newStrokeWidth = 80;

		setStrokeWidth(newStrokeWidth);
		onView(withId(R.id.stroke_rbtn_rect))
				.perform(click());

		assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptions()
				.performSelectTool(ToolType.CURSOR)
				.performOpenToolOptions();

		onView(withId(R.id.stroke_width_seek_bar))
				.check(matches(withProgress(newStrokeWidth)));
		assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptions();
	}

	@Test
	public void brushPickerDialogMinimumBrushWidth() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		setStrokeWidth(0, MIN_STROKE_WIDTH);
		setStrokeWidth(MIN_STROKE_WIDTH);

		onToolBarView()
				.performCloseToolOptions();
	}

	@Test
	public void brushPickerAntiAliasingOffAtMinimumBrushSize() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		onView(withId(R.id.stroke_width_seek_bar))
				.perform(touchCenterLeft());

		onToolBarView()
				.performCloseToolOptions();

		Paint bitmapPaint = getCurrentToolBitmapPaint();
		Paint canvasPaint = getCurrentToolCanvasPaint();

		assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias());
		assertFalse("CANVAS_PAINT antialiasing should be off", canvasPaint.isAntiAlias());
	}

	@Test
	public void brushPickerDialogRadioButtonsBehaviour() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		onView(withId(R.id.stroke_rbtn_rect))
				.check(matches(isNotChecked()));
		onView(withId(R.id.stroke_rbtn_circle))
				.check(matches(isChecked()));

		onView(withId(R.id.stroke_rbtn_rect))
				.perform(click())
				.check(matches(isChecked()));

		onView(withId(R.id.stroke_rbtn_circle))
				.check(matches(isNotChecked()));

		onToolBarView()
				.performCloseToolOptions();

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.SQUARE);

		onToolBarView()
				.performOpenToolOptions();

		onView(withId(R.id.stroke_rbtn_circle))
				.perform(click())
				.check(matches(isChecked()));

		onView(withId(R.id.stroke_rbtn_rect))
				.check(matches(isNotChecked()));

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND);

		onToolBarView()
				.performCloseToolOptions();

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND);
	}
}
