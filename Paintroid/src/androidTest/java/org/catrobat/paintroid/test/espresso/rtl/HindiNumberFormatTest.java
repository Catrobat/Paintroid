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

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.rtl.util.RtlActivityTestRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

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
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));

		onToolBarView()
				.performSelectTool(ToolType.LINE);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));

		onToolBarView()
				.performSelectTool(ToolType.CURSOR);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));

		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
				.check(matches(withText(containsString(EXPECTED_COLOR_TOLERANCE_VALUE))));

		onToolBarView()
				.performSelectTool(ToolType.ERASER);
		onView(withId(R.id.pocketpaint_stroke_width_width_text))
				.check(matches(withText(containsString(EXPECTED_STROKE_WIDTH_VALUE))));
	}

	@Test
	public void testHindiNumberAtColorDialog() {
		onColorPickerView()
				.performOpenColorPicker()
				.performClickColorPickerPresetSelectorButton(7);
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba)))
				.perform(click());
		onView(withId(R.id.color_picker_rgb_red_value))
				.check(matches(withText(containsString(EXPECTED_RED_VALUE))));
		onView(withId(R.id.color_picker_rgb_green_value))
				.check(matches(withText(containsString(EXPECTED_GREEN_VALUE))));
		onView(withId(R.id.color_picker_rgb_blue_value))
				.check(matches(withText(containsString(EXPECTED_BLAU_VALUE))));
		onView(withId(R.id.color_picker_rgb_alpha_value))
				.check(matches(withText(containsString(EXPECTED_ALFA_VALUE))));
	}
}
