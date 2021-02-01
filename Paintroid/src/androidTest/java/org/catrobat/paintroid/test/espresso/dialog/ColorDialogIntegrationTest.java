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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.colorpicker.HSVColorPickerView;
import org.catrobat.paintroid.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.tools.ToolReference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterRight;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withTextColor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class ColorDialogIntegrationTest {

	private static final String TAB_VIEW_PRESET_SELECTOR_CLASS = PresetSelectorView.class.getSimpleName();
	private static final String TAB_VIEW_HSV_SELECTOR_CLASS = HSVColorPickerView.class.getSimpleName();
	private static final String TAB_VIEW_RGBA_SELECTOR_CLASS = RgbSelectorView.class.getSimpleName();

	private static final String TEXT_RGB_MIN = "0";
	private static final String TEXT_RGB_MAX = "255";

	private static final String TEXT_ALPHA_MIN = "0";
	private static final String TEXT_ALPHA_MAX = "100";

	private static final String TEXT_PERCENT_SIGN = "%";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);
	private ToolReference toolReference;

	@Before
	public void setUp() {
		toolReference = launchActivityRule.getActivity().toolReference;
	}

	private int getColorById(int colorId) {
		return launchActivityRule.getActivity().getResources().getColor(colorId);
	}

	@Test
	public void testStandardTabSelected() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));
	}

	@Test
	public void testCorrectColorAfterApplyWithoutNewColorSelected() {
		Paint initialPaint = toolReference.get().getDrawPaint();

		onColorPickerView()
				.performOpenColorPicker()
				.onPositiveButton()
				.perform(click());

		assertEquals(initialPaint.getColor(), toolReference.get().getDrawPaint().getColor());
	}

	@Test
	public void testTabsAreSelectable() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_HSV_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));
	}

	@Test
	public void testColorSelectionChangesNewColorViewColor() {

		onColorPickerView()
				.performOpenColorPicker();

		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		for (int counterColors = 0; counterColors < presetColors.length(); counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);

			int arrayColor = presetColors.getColor(counterColors, Color.BLACK);

			onView(allOf(withId(R.id.color_picker_new_color_view), instanceOf(View.class)))
					.check(matches(withBackgroundColor(arrayColor)));
		}
		presetColors.recycle();
	}

	@Test
	public void testColorNewColorViewChangesStandard() {
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		for (int counterColors = 0; counterColors < presetColors.length(); counterColors++) {
			onColorPickerView()
					.performOpenColorPicker();

			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);

			onColorPickerView()
					.onPositiveButton()
					.perform(click());

			int arrayColor = presetColors.getColor(counterColors, Color.BLACK);
			int selectedColor = toolReference.get().getDrawPaint().getColor();

			assertEquals("Color in array and selected color not the same", arrayColor, selectedColor);
		}
		presetColors.recycle();
	}

	@Test
	public void testCurrentColorViewHasInitialColor() {
		int selectedColor = toolReference.get().getDrawPaint().getColor();
		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView()
				.checkCurrentViewColor(selectedColor);
	}

	@Test
	public void testCurrentColorViewDoesNotChangeColor() {
		onColorPickerView()
				.performOpenColorPicker();

		int initialColor = toolReference.get().getDrawPaint().getColor();
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		for (int counterColors = 0; counterColors < presetColors.length(); counterColors++) {

			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);

			onView(allOf(withId(R.id.color_picker_current_color_view), instanceOf(View.class)))
					.check(matches(withBackgroundColor(initialColor)));
		}
		presetColors.recycle();
	}

	@Test
	public void testColorPickerDialogOnBackPressedSelectedColorShouldNotChange() {
		int expectedSelectedColor = toolReference.get().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		TypedArray presetColors = launchActivityRule.getActivity().getResources().obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		int colorToSelectIndex = presetColors.length() / 2;
		int colorToSelect = presetColors.getColor(colorToSelectIndex, Color.WHITE);
		presetColors.recycle();

		assertNotEquals("Selected color should not be the same as the color to select", colorToSelect, expectedSelectedColor);

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(colorToSelectIndex);

		// Close color picker dialog
		onView(isRoot()).perform(pressBack());

		int currentSelectedColor = toolReference.get().getDrawPaint().getColor();
		assertEquals("Selected color has not changed", expectedSelectedColor, currentSelectedColor);
	}

	@Test
	public void testIfRGBSeekBarsDoChangeColor() {
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(0);

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_textview_red)).check(matches(allOf(isDisplayed(), withText(R.string.color_red), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_red)))));
		onView(withId(R.id.color_picker_color_rgb_textview_green)).check(matches(allOf(isDisplayed(), withText(R.string.color_green), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_green)))));
		onView(withId(R.id.color_picker_color_rgb_textview_blue)).check(matches(allOf(isDisplayed(), withText(R.string.color_blue), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_blue)))));
		onView(withId(R.id.color_picker_color_rgb_textview_alpha)).check(matches(allOf(isDisplayed(), withText(R.string.color_alpha), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_alpha)))));

		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(isDisplayed()));
		onView(allOf(withText(TEXT_PERCENT_SIGN), hasSibling(withId(R.id.color_picker_rgb_alpha_value)))).check(matches(isDisplayed()));

		int currentSelectedColor = presetColors.getColor(0, Color.BLACK);

		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(Integer.toString(Color.red(currentSelectedColor)))));
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(Integer.toString(Color.green(currentSelectedColor)))));
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(Integer.toString(Color.blue(currentSelectedColor)))));
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(
				withText(
						Integer.toString(
								((int) (Color.alpha(currentSelectedColor) / 2.55f))
						)
				)
		));

		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(touchCenterRight());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(touchCenterRight());
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(touchCenterRight());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MAX)));

		// Select color red #FFFF0000 by using hex input
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FFFF0000"));

		onColorPickerView()
				.checkNewColorViewColor(Color.RED);

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());

		// Select color blue #FF0000FF by using seekbars
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(touchCenterRight());

		assertNotEquals("Selected color changed to blue", toolReference.get().getDrawPaint().getColor(), Color.BLUE);

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		assertEquals("Selected color is not blue", toolReference.get().getDrawPaint().getColor(), Color.BLUE);
	}

	@Test
	public void testHEXEditTextMaxInputLength() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#0123456789ABCDEF01234"));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#0123456789ABCDEF0"))));
	}

	@Test
	public void testHEXUpdatingOnColorChange() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(10);

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		int currentSelectColor = toolReference.get().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", (0xFFFFFF & currentSelectColor)))));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_HSV_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onColorPickerView().perform(touchCenterMiddle());

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		currentSelectColor = toolReference.get().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", (0xFFFFFF & currentSelectColor)))));

		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(touchCenterRight());

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		currentSelectColor = toolReference.get().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", (0xFFFFFF & currentSelectColor)))));
	}

	@Test
	public void testHEXEditTextMarkingWrongInput() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		//set to invalid length of 6 (alpha missing)
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FF0000"));
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)));

		//set to invalid value
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FFXXYYZZ"));
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)));

		//set to invalid value (# missing)
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("FF000000"));
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(hasTextColor(R.color.pocketpaint_color_picker_hex_wrong_value_red)));
	}

	@Test
	public void testHEXEditTextMarkingCorrectInputAfterWrongInput() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		//set to invalid length of 6 (alpha missing)
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FF0000"));

		//set correct HEX value
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FF000000"));
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)));
	}

	@Test
	public void testHEXEditTextInitialColorIsSetCorrectly() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		//Inital text color should be black
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(hasTextColor(R.color.pocketpaint_color_picker_hex_correct_black)));
	}

	@Test
	public void testOpenColorPickerOnClickOnColorButton() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_base_layout))
				.check(matches(isDisplayed()));
		onColorPickerView()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testStandardColorDoesNotChangeOnCancel() {
		int initialColor = toolReference.get().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(0);

		onColorPickerView()
				.onNegativeButton()
				.perform(click());

		onToolProperties()
				.checkMatchesColor(initialColor);
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
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba)))
				.perform(click());

		launchActivityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS)))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testColorPickerInitializesRgbTabTransparentColor() {
		TypedArray presetColors = launchActivityRule.getActivity().getResources().obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(presetColors.length() - 1);
		onColorPickerView()
				.onPositiveButton()
				.perform(click());
		onColorPickerView()
				.performOpenColorPicker();
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(Integer.toString(Color.red(Color.TRANSPARENT)))));
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(Integer.toString(Color.green(Color.TRANSPARENT)))));
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(Integer.toString(Color.blue(Color.TRANSPARENT)))));
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(
				withText(
						Integer.toString(
								((int) (Color.alpha(Color.TRANSPARENT) / 2.55f))
						)
				)
		));
		presetColors.recycle();
	}

	@Test
	public void testInsertInvalidHexInputAndSlideSeekbar() {
		onColorPickerView()
				.performOpenColorPicker();
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FFFF0000xxxx"));

		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", (0xFFFFFF & 0xFF0000FF)))));
	}

	@Test
	public void testPipetteButtonIsDisplayed() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_pipette_btn))
				.check(matches(isDisplayed()))
				.check(matches(withText(R.string.color_picker_pipette)));
	}

	@Test
	public void testColorViewsAreDisplayed() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_new_color_view))
				.check(matches(isDisplayed()))
				.check(matches(withBackgroundColor(Color.BLACK)));

		onView(withId(R.id.color_picker_current_color_view))
				.check(matches(isDisplayed()))
				.check(matches(withBackgroundColor(Color.BLACK)));
	}
}
