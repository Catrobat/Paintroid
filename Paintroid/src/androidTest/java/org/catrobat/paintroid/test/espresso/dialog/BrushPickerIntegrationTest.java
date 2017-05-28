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
import android.support.annotation.IntegerRes;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.BaseTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterRight;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class BrushPickerIntegrationTest {

	private static final String TEXT_DEFAULT_STROKE_WIDTH = Integer.toString(DEFAULT_STROKE_WIDTH);

	private static final String MIN_STROKE_WIDTH_TEXT = "1";
	private static final String MIDDLE_STROKE_WIDTH_TEXT = "50";
	private static final String MAX_STROKE_WIDTH_TEXT = "100";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		selectTool(ToolType.BRUSH);

		/*
		 * Reset brush picker view and paint color, because BRUSH and LINE tool share the same
		 * tool options
		 */
		resetDrawPaintAndBrushPickerView();
	}

	@After
	public void tearDown() {

	}

	protected Paint getCurrentToolBitmapPaint() throws NoSuchFieldException, IllegalAccessException {
		return (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mBitmapPaint");
	}

	protected Paint getCurrentToolCanvasPaint() throws NoSuchFieldException, IllegalAccessException {
		return (Paint) PrivateAccess.getMemberValue(BaseTool.class, PaintroidApplication.currentTool, "mCanvasPaint");
	}

	protected void assertStrokePaint(Paint strokePaint, int expectedStrokeWidth, Cap expectedCap) {
		int paintStrokeWidth = (int) strokePaint.getStrokeWidth();
		Cap paintCap = strokePaint.getStrokeCap();

		assertEquals("Stroke did not change", expectedStrokeWidth, paintStrokeWidth);
		assertEquals("Stroke cap not " + expectedCap.toString(), expectedCap, paintCap);
	}

	@Test
	public void brushPickerDialog_defaultLayoutAndToolChanges() throws NoSuchFieldException, IllegalAccessException {

		openToolOptionsForCurrentTool();

		final ViewInteraction seekBarViewInteraction = onView(withId(R.id.stroke_width_seek_bar));
		final ViewInteraction strokeWidthTextViewInteraction = onView(withId(R.id.stroke_width_width_text));
		final ViewInteraction strokeRectRadioButtonViewInteraction = onView(withId(R.id.stroke_rbtn_rect));
		final ViewInteraction strokeCircleRadioButtonViewInteraction = onView(withId(R.id.stroke_rbtn_circle));

		seekBarViewInteraction.check(matches(isDisplayed()));
		strokeWidthTextViewInteraction.check(matches(isDisplayed()));
		strokeRectRadioButtonViewInteraction.check(matches(isDisplayed()));
		strokeCircleRadioButtonViewInteraction.check(matches(isDisplayed()));

		strokeWidthTextViewInteraction.check(matches(withText(TEXT_DEFAULT_STROKE_WIDTH)));
		seekBarViewInteraction.check(matches(withProgress(DEFAULT_STROKE_WIDTH)));
		strokeRectRadioButtonViewInteraction.check(matches(isNotChecked()));
		strokeCircleRadioButtonViewInteraction.check(matches(isChecked()));

		seekBarViewInteraction.perform(touchCenterLeft());
		strokeWidthTextViewInteraction.check(matches(withText(MIN_STROKE_WIDTH_TEXT)));

		seekBarViewInteraction.perform(touchCenterMiddle());
		strokeWidthTextViewInteraction.check(matches(withText(MIDDLE_STROKE_WIDTH_TEXT)));

		seekBarViewInteraction.perform(touchCenterRight());
		strokeWidthTextViewInteraction.check(matches(withText(MAX_STROKE_WIDTH_TEXT)));

		int expectedStrokeWidth = Integer.parseInt(MAX_STROKE_WIDTH_TEXT);

		assertStrokePaint(getCurrentToolCanvasPaint(), expectedStrokeWidth, Cap.ROUND);

		strokeRectRadioButtonViewInteraction.perform(click());
		strokeRectRadioButtonViewInteraction.check(matches(isChecked()));
		strokeCircleRadioButtonViewInteraction.check(matches(isNotChecked()));

		assertStrokePaint(getCurrentToolCanvasPaint(), expectedStrokeWidth, Cap.SQUARE);

		// Close tool options
		clickSelectedToolButton();

		assertStrokePaint(getCurrentToolCanvasPaint(), expectedStrokeWidth, Cap.SQUARE);
	}

	@Test
	public void brushPickerDialog_keepStrokeOnToolChange() throws NoSuchFieldException, IllegalAccessException {

		openToolOptionsForCurrentTool();

		final ViewInteraction seekBarViewInteraction = onView(withId(R.id.stroke_width_seek_bar));
		final ViewInteraction strokeRectRadioButtonViewInteraction = onView(withId(R.id.stroke_rbtn_rect));

		final int newStrokeWidth = 80;

		seekBarViewInteraction.perform(setProgress(newStrokeWidth));
		strokeRectRadioButtonViewInteraction.perform(click());

		assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE);

		// Close tool options
		clickSelectedToolButton();

		// Open cursor tool options
		selectTool(ToolType.CURSOR);
		openToolOptionsForCurrentTool();

		seekBarViewInteraction.check(matches(withProgress(newStrokeWidth)));
		assertStrokePaint(getCurrentToolCanvasPaint(), newStrokeWidth, Cap.SQUARE);

		// Close tool options
		clickSelectedToolButton();
	}

	@Test
	public void brushPickerDialog_minimumBrushWidth() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		openToolOptionsForCurrentTool();

		final ViewInteraction seekBarViewInteraction = onView(withId(R.id.stroke_width_seek_bar));
		final ViewInteraction strokeWidthTextViewInteraction = onView(withId(R.id.stroke_width_width_text));

		int zeroStrokeWidthProgress = 0;
		int oneStrokeWidthProgress  = 1;

		seekBarViewInteraction.perform(setProgress(zeroStrokeWidthProgress));
		strokeWidthTextViewInteraction.check(matches(withText(MIN_STROKE_WIDTH_TEXT)));

		seekBarViewInteraction.perform(setProgress(oneStrokeWidthProgress));
		strokeWidthTextViewInteraction.check(matches(withText(MIN_STROKE_WIDTH_TEXT)));

		// Close tool options
		clickSelectedToolButton();
	}

	@Test
	public void brushPicker_antiAliasingOffAtMinimumBrushSize() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		openToolOptionsForCurrentTool();

		final ViewInteraction seekBarViewInteraction = onView(withId(R.id.stroke_width_seek_bar));
		seekBarViewInteraction.perform(touchCenterLeft());

		// Close tool options
		clickSelectedToolButton();

		Paint bitmapPaint = getCurrentToolBitmapPaint();
		Paint canvasPaint = getCurrentToolCanvasPaint();

		assertFalse("bitmapPaint antialiasing should be off", bitmapPaint.isAntiAlias());
		assertFalse("canvasPaint antialiasing should be off", canvasPaint.isAntiAlias());
	}

	/**
	 * TODO: fails with "java.lang.IllegalStateException: The current thread must have a looper!"
	 *  ...
	 *  at org.catrobat.paintroid.listener.BrushPickerView.setCurrentPaint(BrushPickerView.java:146)
	 *  at org.catrobat.paintroid.test.espresso.dialog.BrushPickerIntegrationTest.setUp(BrushPickerIntegrationTest.java:84)
	 *  ...
	 */
	@Ignore
	@Test
	public void brushPickerDialog_radioButtonsBehaviour() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {

		final ViewInteraction strokeRectRadioButtonViewInteraction = onView(withId(R.id.stroke_rbtn_rect));
		final ViewInteraction strokeCircleRadioButtonViewInteraction = onView(withId(R.id.stroke_rbtn_circle));

		openToolOptionsForCurrentTool();

		strokeRectRadioButtonViewInteraction.check(matches(isNotChecked()));
		strokeCircleRadioButtonViewInteraction.check(matches(isChecked()));

		strokeRectRadioButtonViewInteraction.perform(click());

		strokeRectRadioButtonViewInteraction.check(matches(isChecked()));
		strokeCircleRadioButtonViewInteraction.check(matches(isNotChecked()));

		// Close tool options
		clickSelectedToolButton();

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.SQUARE);

		openToolOptionsForCurrentTool();

		strokeCircleRadioButtonViewInteraction.perform(click());

		strokeRectRadioButtonViewInteraction.check(matches(isNotChecked()));
		strokeCircleRadioButtonViewInteraction.check(matches(isChecked()));

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND);

		// Close tool options
		clickSelectedToolButton();

		assertStrokePaint(getCurrentToolCanvasPaint(), DEFAULT_STROKE_WIDTH, Cap.ROUND);
	}
}
