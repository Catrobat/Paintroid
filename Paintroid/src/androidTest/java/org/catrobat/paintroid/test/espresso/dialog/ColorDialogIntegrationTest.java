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

import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.test.utils.Utils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterRight;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withTextColor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
public class ColorDialogIntegrationTest {
	private static final String TEXT_RGB_MIN = "0";
	private static final String TEXT_RGB_MAX = "255";

	private static final String TEXT_ALPHA_MIN = "0%";
	private static final String TEXT_ALPHA_MAX = "100%";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	private int getColorById(int colorId) {
		return launchActivityRule.getActivity().getResources().getColor(colorId);
	}

	@Test
	public void testStandardTabSelected() {
		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView().onPresetSelectorView()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testTabsAreSelectable() {
		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView().onPresetSelectorView()
				.check(matches(isDisplayed()));

		onColorPickerView().onColorChooserTabHSV()
				.perform(click());
		onColorPickerView().onHSVColorPickerView()
				.check(matches(isDisplayed()));

		onColorPickerView().onColorChooserTabRgba()
				.perform(click());
		onColorPickerView().onRgbSelectorView()
				.check(matches(isDisplayed()));

		onColorPickerView().onColorChooserTabPreset()
				.perform(click());
		onColorPickerView().onPresetSelectorView()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testColorNewColorButtonChangesStandard() {
		onColorPickerView()
				.performOpenColorPicker();

		final Resources resources = launchActivityRule.getActivity().getResources();
		int[] colors = resources.getIntArray(R.array.pocketpaint_color_chooser_preset_colors);
		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);
			int selectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

			Utils.assertColorEquals(colors[i], selectedColor);

			onColorPickerView().onOkButton()
					.check(matches(withBackgroundColor(colors[i])));
		}
	}

	@Test
	public void testButtonTextColorPickerButtonShouldBeDifferentFromBackground() {
		onColorPickerView()
				.performOpenColorPicker();

		final Resources resources = launchActivityRule.getActivity().getResources();
		int[] colors = resources.getIntArray(R.array.pocketpaint_color_chooser_preset_colors);
		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);

			onColorPickerView().onOkButton()
					.check(matches(allOf(withTextColor(anyOf(is(Color.BLACK), is(Color.WHITE))),
							not(allOf(withBackgroundColor(colors[i]), withTextColor(colors[i]))))
							));
		}
	}

	@Test
	public void testColorPickerDialogOnBackPressedSelectedColorShouldNotChange() {
		int expectedSelectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		Resources resources = launchActivityRule.getActivity().getResources();
		int[] colors = resources.getIntArray(R.array.pocketpaint_color_chooser_preset_colors);

		int colorToSelectIndex = colors.length / 2;
		int colorToSelect = colors[colorToSelectIndex];

		assertNotEquals(colorToSelect, expectedSelectedColor);

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(colorToSelectIndex);

		Espresso.pressBack();

		int currentSelectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertNotEquals(expectedSelectedColor, currentSelectedColor);
	}

	@Test
	public void testIfRGBSeekBarsDoChangeColor() {
		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(0);

		onColorPickerView().onColorChooserTabRgba()
				.perform(click());

		onView(withId(R.id.color_chooser_color_rgb_textview_red)).check(matches(allOf(isDisplayed(), withText(R.string.color_red), withTextColor(getColorById(R.color.pocketpaint_color_chooser_rgb_red)))));
		onView(withId(R.id.color_chooser_color_rgb_textview_green)).check(matches(allOf(isDisplayed(), withText(R.string.color_green), withTextColor(getColorById(R.color.pocketpaint_color_chooser_rgb_green)))));
		onView(withId(R.id.color_chooser_color_rgb_textview_blue)).check(matches(allOf(isDisplayed(), withText(R.string.color_blue), withTextColor(getColorById(R.color.pocketpaint_color_chooser_rgb_blue)))));
		onView(withId(R.id.color_chooser_color_rgb_textview_alpha)).check(matches(allOf(isDisplayed(), withText(R.string.color_alpha), withTextColor(getColorById(R.color.pocketpaint_color_chooser_rgb_alpha)))));

		onView(withId(R.id.color_chooser_color_rgb_seekbar_red)).check(matches(isDisplayed()));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_green)).check(matches(isDisplayed()));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_blue)).check(matches(isDisplayed()));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_alpha)).check(matches(isDisplayed()));

		onView(withId(R.id.color_chooser_rgb_red_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_chooser_rgb_green_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_chooser_rgb_blue_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_chooser_rgb_alpha_value)).check(matches(isDisplayed()));

		int currentSelectColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		onView(withId(R.id.color_chooser_rgb_red_value)).check(matches(withText(Integer.toString(Color.red(currentSelectColor)))));
		onView(withId(R.id.color_chooser_rgb_green_value)).check(matches(withText(Integer.toString(Color.green(currentSelectColor)))));
		onView(withId(R.id.color_chooser_rgb_blue_value)).check(matches(withText(Integer.toString(Color.blue(currentSelectColor)))));
		onView(withId(R.id.color_chooser_rgb_alpha_value)).check(matches(withText(Integer.toString(
				(int) (Color.alpha(currentSelectColor) / 2.55f)) + "%")));

		onView(withId(R.id.color_chooser_color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.color_chooser_rgb_red_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_red)).perform(touchCenterRight());
		onView(withId(R.id.color_chooser_rgb_red_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_chooser_color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.color_chooser_rgb_green_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_green)).perform(touchCenterRight());
		onView(withId(R.id.color_chooser_rgb_green_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_chooser_color_rgb_seekbar_blue)).perform(touchCenterLeft());
		onView(withId(R.id.color_chooser_rgb_blue_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.color_chooser_rgb_blue_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_chooser_color_rgb_seekbar_alpha)).perform(touchCenterLeft());
		onView(withId(R.id.color_chooser_rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MIN)));
		onView(withId(R.id.color_chooser_color_rgb_seekbar_alpha)).perform(touchCenterRight());
		onView(withId(R.id.color_chooser_rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MAX)));

		// Select color blue #FF0000FF by using seekbars
		onView(withId(R.id.color_chooser_color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.color_chooser_color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.color_chooser_color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.color_chooser_color_rgb_seekbar_alpha)).perform(touchCenterRight());

		assertEquals("Selected color is not blue", PaintroidApplication.currentTool.getDrawPaint().getColor(), Color.BLUE);
	}

	@Test
	public void testOpenColorPickerOnClickOnColorButton() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_chooser_base_layout))
				.check(matches(isDisplayed()));
		onColorPickerView()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testColorPickerRemainsOpenOnOrientationChange() {
		onColorPickerView()
				.performOpenColorPicker();

		launchActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		onColorPickerView().check(matches(isDisplayed()));
	}

	@Test
	public void testColorPickerTabRestoredOnOrientationChange() {
		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView().onColorChooserTabRgba()
				.perform(click());

		launchActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		onView(withClassName(containsString(RgbSelectorView.class.getSimpleName())))
				.check(matches(isDisplayed()));
	}
}
