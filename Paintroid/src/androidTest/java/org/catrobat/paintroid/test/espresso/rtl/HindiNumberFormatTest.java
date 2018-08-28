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

package org.catrobat.paintroid.test.espresso.rtl;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickColorPickerPresetSelectorButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openColorPickerDialog;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class HindiNumberFormatTest {
	private static final String EXPECTED_RED_VALUE = "٢٤٠";
	private static final String EXPECTED_GREEN_VALUE = "٢٢٨";
	private static final String EXPECTED_BLAU_VALUE = "١٦٨";
	private static final String EXPECTED_ALFA_VALUE = "١٠٠";
	private static final String EXPECTED_STROKE_WIDTH_VALUE = "٢٥";
	private static final String EXPECTED_COLOR_TOLERANCE_VALUE = "١٢";
	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new RtlActivityTestRule<>(MainActivity.class, "ar");

	@Test
	public void testHindiNumberAtTool() {
		selectTool(ToolType.BRUSH);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));

		selectTool(ToolType.LINE);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));

		selectTool(ToolType.CURSOR);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));

		selectTool(ToolType.FILL);
		onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
				.check(matches(withText(containsString(EXPECTED_COLOR_TOLERANCE_VALUE))));

		selectTool(ToolType.ERASER);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));
	}

	@Test
	public void testHindiNumberAtColorDialog() {
		openColorPickerDialog();
		clickColorPickerPresetSelectorButton(7);
		onView(allOf(withId(R.id.color_chooser_tab_icon), withBackground(R.drawable.ic_color_chooser_tab_rgba)))
				.perform(click());
		onView(withId(R.id.color_chooser_rgb_red_value))
				.check(matches(withText(containsString(EXPECTED_RED_VALUE))));
		onView(withId(R.id.color_chooser_rgb_green_value))
				.check(matches(withText(containsString(EXPECTED_GREEN_VALUE))));
		onView(withId(R.id.color_chooser_rgb_blue_value))
				.check(matches(withText(containsString(EXPECTED_BLAU_VALUE))));
		onView(withId(R.id.color_chooser_rgb_alpha_value))
				.check(matches(withText(containsString(EXPECTED_ALFA_VALUE))));
	}
}
