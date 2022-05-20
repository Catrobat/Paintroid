/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2022 The Catrobat Team
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

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.view.View;
import android.widget.Button;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.colorpicker.HSVColorPickerView;
import org.catrobat.paintroid.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.espresso.util.UiInteractions;
import org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.ui.Perspective;
import org.hamcrest.core.AllOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.common.ConstantsKt.CATROBAT_IMAGE_ENDING;
import static org.catrobat.paintroid.common.ConstantsKt.PAINTROID_PICTURE_NAME;
import static org.catrobat.paintroid.common.ConstantsKt.PAINTROID_PICTURE_PATH;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterMiddle;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterRight;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withTextColor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressBack;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.hasTextColor;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
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
	private static final String IMAGE_NAME = "colorDialogTestCatrobatImage";
	private static final String IMAGE_TO_LOAD_NAME = "loadFile";

	private static final String TEXT_RGB_MIN = "0";
	private static final String TEXT_RGB_MAX = "255";

	private static final String TEXT_ALPHA_MIN = "0";
	private static final String TEXT_ALPHA_MAX = "100";

	private static final String TEXT_PERCENT_SIGN = "%";

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public IntentsTestRule<MainActivity> launchActivityRuleWithIntent = new IntentsTestRule<>(MainActivity.class, false, false);

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	private ToolReference toolReference;

	private List<File> deletionFileList = new ArrayList<>();

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
		Paint initialPaint = toolReference.getTool().getDrawPaint();

		onColorPickerView()
				.performOpenColorPicker()
				.onPositiveButton()
				.perform(click());

		assertEquals(initialPaint.getColor(), toolReference.getTool().getDrawPaint().getColor());
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

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(scrollTo(), click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));
	}

	@Test
	public void dontShowAlphaRelatedStuffFromCatroidFormulaEditor() {
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);
		launchActivityRule.getActivity().model.setOpenedFromFormulaEditorInCatroid(true);

		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(not(isDisplayed())));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv))).perform(click());

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(not(isDisplayed())));

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		int currentSelectColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_picker_alpha_row))
				.check(matches(not(isDisplayed())));

		onView(withId(R.id.color_picker_color_rgb_hex))
				.check(matches(withText(String.format("#%02X%02X%02X", Color.red(currentSelectColor), Color.green(currentSelectColor), Color.blue(currentSelectColor)))));
	}

	@Test
	public void showAlphaSliderFromCatroid() {
		launchActivityRule.getActivity().model.setOpenedFromCatroid(true);

		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv))).perform(click());

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(isDisplayed()));
	}

	@Test
	public void showAlphaSliderIfNotCatroidFlagSet() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv))).perform(click());

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(isDisplayed()));
	}

	@Test
	public void dontShowAlphaSliderInRgb() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());

		onView(withId(R.id.color_picker_base_layout))
				.perform(swipeUp());

		onView(withId(R.id.color_alpha_slider))
				.check(matches(not(isDisplayed())));
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
			int selectedColor = toolReference.getTool().getDrawPaint().getColor();

			assertEquals("Color in array and selected color not the same", arrayColor, selectedColor);
		}
		presetColors.recycle();
	}

	@Test
	public void testCurrentColorViewHasInitialColor() {
		int selectedColor = toolReference.getTool().getDrawPaint().getColor();
		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView()
				.checkCurrentViewColor(selectedColor);
	}

	@Test
	public void testCurrentColorViewDoesNotChangeColor() {
		onColorPickerView()
				.performOpenColorPicker();

		int initialColor = toolReference.getTool().getDrawPaint().getColor();
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
	public void testColorPickerDialogOnBackPressedSelectedColorShouldChange() {
		int initialColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		TypedArray presetColors = launchActivityRule.getActivity().getResources().obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		int colorToSelectIndex = presetColors.length() / 2;
		int colorToSelect = presetColors.getColor(colorToSelectIndex, Color.WHITE);
		presetColors.recycle();

		assertNotEquals("Selected color should not be the same as the initial color", colorToSelect, initialColor);

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(colorToSelectIndex);

		// Close color picker dialog
		onView(isRoot()).perform(pressBack());

		int currentSelectedColor = toolReference.getTool().getDrawPaint().getColor();
		assertEquals("Selected color has not changed", colorToSelect, currentSelectedColor);
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

		onView(withId(R.id.color_picker_color_rgb_textview_red)).perform(scrollTo());
		onView(withId(R.id.color_picker_color_rgb_textview_red)).check(matches(allOf(isDisplayed(), withText(R.string.color_red), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_red)))));
		onView(withId(R.id.color_picker_color_rgb_textview_green)).check(matches(allOf(isDisplayed(), withText(R.string.color_green), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_green)))));
		onView(withId(R.id.color_picker_color_rgb_textview_blue)).check(matches(allOf(isDisplayed(), withText(R.string.color_blue), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_blue)))));
		onView(withId(R.id.color_picker_color_rgb_textview_alpha)).perform(scrollTo());
		onView(withId(R.id.color_picker_color_rgb_textview_alpha)).check(matches(allOf(isDisplayed(), withText(R.string.color_alpha), withTextColor(getColorById(R.color.pocketpaint_color_picker_rgb_alpha)))));

		onView(withId(R.id.color_picker_color_rgb_textview_red)).perform(scrollTo());
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(scrollTo());
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(scrollTo());
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_rgb_red_value)).perform(scrollTo());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(isDisplayed()));
		onView(withId(R.id.color_picker_rgb_alpha_value)).perform(scrollTo());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(isDisplayed()));
		onView(allOf(withText(TEXT_PERCENT_SIGN), hasSibling(withId(R.id.color_picker_rgb_alpha_value)))).check(matches(isDisplayed()));

		int currentSelectedColor = presetColors.getColor(0, Color.BLACK);

		onView(withId(R.id.color_picker_rgb_red_value)).perform(scrollTo());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(Integer.toString(Color.red(currentSelectedColor)))));
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(Integer.toString(Color.green(currentSelectedColor)))));
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(Integer.toString(Color.blue(currentSelectedColor)))));
		onView(withId(R.id.color_picker_rgb_alpha_value)).perform(scrollTo());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(
				withText(
						Integer.toString(
								(int) (Color.alpha(currentSelectedColor) / 2.55f)
						)
				)
		));
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(scrollTo(), touchCenterRight());
		onView(withId(R.id.color_picker_rgb_red_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(scrollTo(), touchCenterRight());
		onView(withId(R.id.color_picker_rgb_green_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(scrollTo(), touchCenterRight());
		onView(withId(R.id.color_picker_rgb_blue_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MIN)));
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(scrollTo(), touchCenterRight());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MAX)));

		// Select color red #FFFF0000 by using hex input
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(replaceText("#FFFF0000"));

		onColorPickerView()
				.checkNewColorViewColor(Color.RED);

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(scrollTo(), click());

		// Select color blue #FF0000FF by using seekbars
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(scrollTo(), touchCenterRight());
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(scrollTo(), touchCenterRight());

		onColorPickerView()
				.onPositiveButton()
				.perform(scrollTo(), click());

		assertNotEquals("Selected color changed to blue from black", toolReference.getTool().getDrawPaint().getColor(), Color.BLACK);
		assertEquals("Selected color is not blue", toolReference.getTool().getDrawPaint().getColor(), Color.BLUE);
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

		int currentSelectColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", 0xFFFFFF & currentSelectColor))));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv))).perform(scrollTo(), click());
		onView(withClassName(containsString(TAB_VIEW_HSV_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onColorPickerView().perform(scrollTo(), touchCenterMiddle());

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		currentSelectColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", 0xFFFFFF & currentSelectColor))));

		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(scrollTo(), touchCenterLeft());
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(scrollTo(), touchCenterRight());

		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		currentSelectColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", 0xFFFFFF & currentSelectColor))));
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
	public void testStandardColorDoesNotChangeOnCancelButtonPress() {
		int initialColor = toolReference.getTool().getDrawPaint().getColor();

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
	public void testStandardColorDoesChangeOnCancel() {
		int initialColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(0);
		onColorPickerView()
				.perform(ViewActions.pressBack());
		onToolProperties()
				.checkDoesNotMatchColor(initialColor);
	}

	@Test
	public void testColorOnlyUpdatesOncePerColorPickerIntent() {
		int initialColor = toolReference.getTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(0);
		onToolProperties()
				.checkMatchesColor(initialColor);
		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(1);
		onToolProperties()
				.checkMatchesColor(initialColor);
		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(2);
		onToolProperties()
				.checkMatchesColor(initialColor);

		onColorPickerView()
				.perform(ViewActions.pressBack());
		onToolProperties()
				.checkDoesNotMatchColor(initialColor);
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
								(int) (Color.alpha(Color.TRANSPARENT) / 2.55f)
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

		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(scrollTo(), touchCenterRight());
		onView(withId(R.id.color_picker_color_rgb_hex)).perform(scrollTo());
		onView(withId(R.id.color_picker_color_rgb_hex)).check(matches(
				withText(
						String.format("#FF%06X", 0xFFFFFF & 0xFF0000FF))));
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

	@Test
	public void alphaValueIsSetInSliderWhenChangedInSeekBar() {
		onColorPickerView()
				.performOpenColorPicker();
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());

		// set color to value #7F000000, alpha seekbar 49%
		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(touchCenterMiddle());
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(scrollTo(), click());
		onColorPickerView()
				.onPositiveButton()
				.perform(click());
		onToolProperties()
				.checkMatchesColor(Color.parseColor("#7F000000"));
	}

	@Test
	public void alphaValueIsSetInSeekBarWhenChangedInSlider() {
		onColorPickerView()
				.performOpenColorPicker();
		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset))).perform(click());

		// set color to value #80000000, alpha seekbar 50%
		onView(withId(R.id.color_alpha_slider)).perform(scrollTo(), touchCenterMiddle());

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withId(R.id.color_picker_rgb_alpha_value)).check(matches(
				withText("50")
		));
	}

	@Test
	public void testPreserveZoomAfterPipetteUsage() {
		Perspective perspective = launchActivityRule.getActivity().getPerspective();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		float scale = 4f;

		perspective.setScale(scale);

		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_pipette_btn)).perform(click());
		onView(withId(R.id.doneAction)).perform(click());

		onColorPickerView()
				.performCloseColorPickerWithDialogButton();

		assertEquals(scale, perspective.getScale(), Float.MIN_VALUE);
	}

	@Test
	public void testColorHistoryShowsPresetSelectorColors() {
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);
		}
		onColorPickerView()
				.onPositiveButton()
				.perform(click());
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performOpenColorPicker();
			onColorPickerView()
					.performClickOnHistoryColor(MAXIMUM_COLORS_IN_HISTORY - 1);

			onColorPickerView()
					.onPositiveButton()
					.perform(click());

			int arrayColor = presetColors.getColor(counterColors, Color.BLACK);
			int selectedColor = Objects.requireNonNull(toolReference.getTool()).getDrawPaint().getColor();

			assertEquals("Color in history doesn't match selection", arrayColor, selectedColor);
		}
		presetColors.recycle();
	}

	@Test
	public void testColorHistorySelectMoreThanMaxHistoryColors() {
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors <= ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);
		}
		for (int historyCounter = 0, colorCounter = MAXIMUM_COLORS_IN_HISTORY; historyCounter < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; historyCounter++, colorCounter--) {
			onColorPickerView()
					.checkHistoryColor(historyCounter, presetColors.getColor(colorCounter, Color.BLACK));
		}
		presetColors.recycle();
	}

	@Test
	public void testColorHistoryShowsRGBSelectorColors() {
		launchActivityRule.getActivity();

		onColorPickerView()
				.performOpenColorPicker();

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(scrollTo(), touchCenterRight());
		onColorPickerView().checkHistoryColor(0, 0xFFFF0000);
		onView(withId(R.id.color_picker_color_rgb_seekbar_red)).perform(scrollTo(), touchCenterLeft());
		onColorPickerView().checkHistoryColor(0, 0xFF000000);

		onView(withId(R.id.color_picker_color_rgb_seekbar_green)).perform(scrollTo(), touchCenterRight());
		onColorPickerView().checkHistoryColor(0, 0xFF00FF00);
		onView(withId(R.id.color_picker_color_rgb_seekbar_blue)).perform(scrollTo(), touchCenterRight());
		onColorPickerView().checkHistoryColor(0, 0xFF00FFFF);
		onColorPickerView().checkHistoryColor(3, 0xFFFF0000);

		onView(withId(R.id.color_picker_color_rgb_seekbar_alpha)).perform(scrollTo(), touchCenterLeft());
		onColorPickerView().checkHistoryColor(0, 0x0000FFFF);
		onColorPickerView().checkHistoryColor(3, 0xFF000000);
	}

	@Test
	public void testColorHistoryPreservedWhenClickingNewImage() {
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);
		}
		onColorPickerView()
				.onPositiveButton()
				.perform(click());
		onTopBarView().performOpenMoreOptions();
		onView(withText(R.string.menu_new_image)).perform(click());

		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView().checkHistoryColor(3, presetColors.getColor(0, Color.BLACK));
		presetColors.recycle();
	}

	@Test
	public void testColorHistoryPreservedWhenLoadingFile() {
		Intent intent = new Intent();
		intent.putExtra(PAINTROID_PICTURE_PATH, "");
		intent.putExtra(PAINTROID_PICTURE_NAME, IMAGE_TO_LOAD_NAME);
		launchActivityRuleWithIntent.launchActivity(intent);
		final Activity activity = launchActivityRuleWithIntent.getActivity();
		final Resources resources = activity.getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		createImageIntent(activity);
		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);
		}
		onColorPickerView()
				.onPositiveButton()
				.perform(click());
		onTopBarView().performOpenMoreOptions();
		onView(withText(R.string.menu_load_image)).perform(click());

		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView().checkHistoryColor(3, presetColors.getColor(0, Color.BLACK));
		presetColors.recycle();
		if (!deletionFileList.isEmpty() && deletionFileList.get(0) != null && deletionFileList.get(0).exists()) {
			assertTrue(deletionFileList.get(0).delete());
		}
	}

	@Test
	public void testColorHistoryDeletedWhenRestartingApp() {
		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);
		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);
		}
		launchActivityRule.finishActivity();
		launchActivityRule.launchActivity(new Intent());

		onColorPickerView()
				.performOpenColorPicker();
		onView(withId(R.id.color_history_text_view)).check(matches(not(isDisplayed())));
		presetColors.recycle();
	}

	@Test
	public void testSaveColorHistoryInCatrobatFile() {
		final MainActivity activity = launchActivityRule.getActivity();
		final Resources resources = activity.getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.pocketpaint_color_picker_preset_colors);

		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors);
		}
		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		saveCatrobatImage();
		Uri uri = activity.model.getSavedPictureUri();
		launchActivityRule.finishActivity();

		Intent intent = new Intent();
		intent.putExtra(PAINTROID_PICTURE_PATH, "");
		intent.putExtra(PAINTROID_PICTURE_NAME, IMAGE_NAME);
		launchActivityRuleWithIntent.launchActivity(intent);
		intent = new Intent();
		intent.setData(uri);
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);

		onColorPickerView()
				.performOpenColorPicker();
		for (int counterColors = 0; counterColors + MAXIMUM_COLORS_IN_HISTORY < presetColors.length() && counterColors < ColorPickerViewInteraction.MAXIMUM_COLORS_IN_HISTORY; counterColors++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(counterColors + MAXIMUM_COLORS_IN_HISTORY);
		}
		onColorPickerView()
				.onPositiveButton()
				.perform(click());

		onDrawingSurfaceView()
				.perform(UiInteractions.touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onTopBarView().performOpenMoreOptions();
		onView(withText(R.string.menu_load_image)).perform(click());

		onView(allOf(withId(android.R.id.button2), withText(R.string.discard_button_text), isAssignableFrom(Button.class))).perform(click());

		onColorPickerView()
				.performOpenColorPicker();
		onColorPickerView().checkHistoryColor(3, presetColors.getColor(0, Color.BLACK));
		presetColors.recycle();
		if (!deletionFileList.isEmpty() && deletionFileList.get(0) != null && deletionFileList.get(0).exists()) {
			assertTrue(deletionFileList.get(0).delete());
		}
	}

	private Uri createTestImageFile(Activity activity) {
		Bitmap bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0F, 0F, null);

		File imageFile = new File(activity.getExternalFilesDir(null).getAbsolutePath(), IMAGE_TO_LOAD_NAME + ".jpg");
		Uri imageUri = Uri.fromFile(imageFile);
		try {
			OutputStream fos = activity.getContentResolver().openOutputStream(Objects.requireNonNull(imageUri));
			assertTrue(bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos));
			assert fos != null;
			fos.close();
		} catch (IOException e) {
			throw new AssertionError("Picture file could not be created.", e);
		}

		deletionFileList.add(imageFile);
		return imageUri;
	}

	private void createImageIntent(Activity activity) {
		Intent intent = new Intent();
		intent.setData(createTestImageFile(activity));
		Instrumentation.ActivityResult resultOK = new Instrumentation.ActivityResult(Activity.RESULT_OK, intent);
		intending(hasAction(Intent.ACTION_GET_CONTENT)).respondWith(resultOK);
	}

	private void saveCatrobatImage() {
		onTopBarView()
				.performOpenMoreOptions();
		onView(withText(R.string.menu_save_image))
				.perform(scrollTo(), click());
		onView(withId(R.id.pocketpaint_save_dialog_spinner))
				.perform(click());
		onData(AllOf.allOf(is(instanceOf(String.class)),
				is(CATROBAT_IMAGE_ENDING))).inRoot(isPlatformPopup()).perform(click());

		onView(withId(R.id.pocketpaint_image_name_save_text))
				.perform(replaceText(IMAGE_NAME));
		onView(withText(R.string.save_button_text))
				.perform(ViewActions.click());
	}
}
