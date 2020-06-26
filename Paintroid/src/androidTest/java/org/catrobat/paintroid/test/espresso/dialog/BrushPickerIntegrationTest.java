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

package org.catrobat.paintroid.test.espresso.dialog;

import android.graphics.Paint;
import android.graphics.Paint.Cap;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.DEFAULT_STROKE_WIDTH;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isSelected;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class BrushPickerIntegrationTest {
	private static final int MIN_STROKE_WIDTH = 1;
	private static final int MIDDLE_STROKE_WIDTH = 50;
	private static final int MAX_STROKE_WIDTH = 100;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH)
				.performOpenToolOptionsView();
	}

	private Paint getCurrentToolBitmapPaint() {
		return launchActivityRule.getActivity().toolPaint.getPaint();
	}

	private Paint getCurrentToolCanvasPaint() {
		return launchActivityRule.getActivity().toolPaint.getPreviewPaint();
	}

	private void assertStrokePaint(Paint strokePaint, int expectedStrokeWidth, Cap expectedCap) {
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		Cap paintCap = strokePaint.getStrokeCap();

		assertEquals("Stroke did not change", expectedStrokeWidth, paintStrokeWidth);
		assertEquals("Stroke cap not " + expectedCap.toString(), expectedCap, paintCap);
	}

	private void setStrokeWidth(int strokeWidth, int expectedStrokeWidth) {
		onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
				.perform(setProgress(strokeWidth))
				.check(matches(withProgress(expectedStrokeWidth)));
	}

	private void setStrokeWidth(int strokeWidth) {
		setStrokeWidth(strokeWidth, strokeWidth);
	}

	@Test
	public void brushPickerDialogDefaultLayoutAndToolChanges() {
		onView(withId(R.id.pocketpaint_brush_tool_preview))
				.check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
				.check(matches(isDisplayed()))
				.check(matches(withProgress(DEFAULT_STROKE_WIDTH)));
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(isDisplayed()))
				.check(matches(withText(Integer.toString(DEFAULT_STROKE_WIDTH))));
		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.check(matches(isDisplayed()))
				.check(matches(not(isSelected())));
		onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
				.check(matches(isDisplayed()))
				.check(matches(isSelected()));

		setStrokeWidth(MIN_STROKE_WIDTH);
		setStrokeWidth(MIDDLE_STROKE_WIDTH);
		setStrokeWidth(MAX_STROKE_WIDTH);

		assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.ROUND);

		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.perform(click())
				.check(matches(isSelected()));
		onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
				.check(matches(not(isSelected())));

		assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptionsView();

		assertStrokePaint(getCurrentToolCanvasPaint(), MAX_STROKE_WIDTH, Cap.SQUARE);
	}

	@Test
	public void brushPickerDialogKeepStrokeOnToolChange() {
		final int newStrokeWidth = 80;

		setStrokeWidth(newStrokeWidth);
		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.perform(click());

		assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptionsView()
				.performSelectTool(ToolType.CURSOR)
				.performOpenToolOptionsView();

		onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
				.check(matches(withProgress(newStrokeWidth)));
		assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE);

		onToolBarView()
				.performCloseToolOptionsView();
	}

	@Test
	public void brushPickerDialogMinimumBrushWidth() {
		setStrokeWidth(0, MIN_STROKE_WIDTH);
		setStrokeWidth(MIN_STROKE_WIDTH);

		onToolBarView()
				.performCloseToolOptionsView();
	}

	@Test
	public void brushPickerAntiAliasingOffAtMinimumBrushSize() {
		onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
				.perform(touchCenterLeft());

		onToolBarView()
				.performCloseToolOptionsView();

		Paint bitmapPaint = getCurrentToolBitmapPaint();
		Paint canvasPaint = getCurrentToolCanvasPaint();

		assertFalse("BITMAP_PAINT antialiasing should be off", bitmapPaint.isAntiAlias());
		assertFalse("CANVAS_PAINT antialiasing should be off", canvasPaint.isAntiAlias());
	}

	@Test
	public void brushPickerDialogRadioButtonsBehaviour() {
		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.check(matches(not(isSelected())));
		onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
				.check(matches(isSelected()));

		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.perform(click())
				.check(matches(isSelected()));

		onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
				.check(matches(not(isSelected())));

		onToolBarView()
				.performCloseToolOptionsView();

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.SQUARE);

		onToolBarView()
				.performOpenToolOptionsView();

		onView(withId(R.id.pocketpaint_stroke_ibtn_circle))
				.perform(click())
				.check(matches(isSelected()));

		onView(withId(R.id.pocketpaint_stroke_ibtn_rect))
				.check(matches(not(isSelected())));

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND);

		onToolBarView()
				.performCloseToolOptionsView();

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND);
	}

	@Test
	public void brushPickerDialogEditTextBehaviour() {
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.perform(replaceText(String.valueOf(MIDDLE_STROKE_WIDTH)));

		Espresso.closeSoftKeyboard();

		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(String.valueOf(MIDDLE_STROKE_WIDTH))));

		onView(withId(R.id.pocketpaint_stroke_width_seek_bar))
				.check(matches(withProgress(MIDDLE_STROKE_WIDTH)));
	}
}
