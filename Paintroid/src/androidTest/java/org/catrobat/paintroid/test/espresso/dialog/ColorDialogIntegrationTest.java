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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.Button;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.dialog.colorpicker.ColorPickerDialog;
import org.catrobat.paintroid.dialog.colorpicker.HSVColorPickerView;
import org.catrobat.paintroid.dialog.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.dialog.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasSibling;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickColorPickerPresetSelectorButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openColorPickerDialog;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterLeft;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchCenterRight;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withTextColor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

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
	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() throws NoSuchFieldException, IllegalAccessException {
		PaintroidApplication.drawingSurface.destroyDrawingCache();

		selectTool(ToolType.BRUSH);
	}

	@After
	public void tearDown() {

		IndeterminateProgressDialog.getInstance().dismiss();
		ColorPickerDialog.getInstance().dismiss();
	}

	protected int getColorById(int colorId) {
		return launchActivityRule.getActivity().getResources().getColor(colorId);
	}

	@Test
	public void testStandardTabSelected() throws Throwable {
		openColorPickerDialog();

		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));
	}

	@Test
	public void testTabsAreSelectable() throws Throwable {
		openColorPickerDialog();

		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_circle))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_HSV_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_palette))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));
	}

	@Test
	public void testColorNewColorButtonChangesStandard() {

		openColorPickerDialog();

		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.preset_colors);
		for (int counterColors = 0; counterColors < presetColors.length(); counterColors++) {

			clickColorPickerPresetSelectorButton(counterColors);

			int arrayColor = presetColors.getColor(counterColors, Color.BLACK);
			int selectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

			assertEquals("Color in array and selected color not the same", arrayColor, selectedColor);

			onView(allOf(withId(R.id.btn_colorchooser_ok), instanceOf(Button.class)))
					.check(matches(withBackgroundColor(arrayColor)));
		}
		presetColors.recycle();
	}

	@Test
	public void buttonTextColorPickerButtonShouldBeDifferentFromBackground() {
		openColorPickerDialog();

		final Resources resources = launchActivityRule.getActivity().getResources();
		final TypedArray presetColors = resources.obtainTypedArray(R.array.preset_colors);
		for (int counterColors = 0; counterColors < presetColors.length(); counterColors++) {

			clickColorPickerPresetSelectorButton(counterColors);

			int arrayColor = presetColors.getColor(counterColors, Color.BLACK);

			onView(allOf(withId(R.id.btn_colorchooser_ok), instanceOf(Button.class)))
					.check(matches(allOf(withTextColor(anyOf(is(Color.BLACK), is(Color.WHITE))),
							not(allOf(withBackgroundColor(arrayColor), withTextColor(arrayColor))))
							));
		}
		presetColors.recycle();
	}

	@Test
	public void colorPickerDialogOnBackPressedSelectedColorShouldNotChange() {
		int expectedSelectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		openColorPickerDialog();

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_palette))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		TypedArray presetColors = launchActivityRule.getActivity().getResources().obtainTypedArray(R.array.preset_colors);
		int colorToSelectIndex = presetColors.length() / 2;
		int colorToSelect = presetColors.getColor(colorToSelectIndex, Color.WHITE);
		presetColors.recycle();

		assertNotEquals("Selected color should not be the same as the color to select", colorToSelect, expectedSelectedColor);

		clickColorPickerPresetSelectorButton(colorToSelectIndex);

		// Close color picker dialog
		onView(isRoot()).perform(pressBack());

		int currentSelectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
		assertNotEquals("Selected color has not changed", expectedSelectedColor, currentSelectedColor);
	}

	@Test
	public void testIfRGBSeekBarsDoChangeColor() throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
		openColorPickerDialog();

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_palette))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_PRESET_SELECTOR_CLASS))).check(matches(isDisplayed()));

		clickColorPickerPresetSelectorButton(0);

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_rgba))).perform(click());
		onView(withClassName(containsString(TAB_VIEW_RGBA_SELECTOR_CLASS))).check(matches(isDisplayed()));

		onView(withId(R.id.color_rgb_textview_red)).check(matches(allOf(isDisplayed(), withText(R.string.color_red), withTextColor(getColorById(R.color.color_chooser_rgb_red)))));
		onView(withId(R.id.color_rgb_textview_green)).check(matches(allOf(isDisplayed(), withText(R.string.color_green), withTextColor(getColorById(R.color.color_chooser_rgb_green)))));
		onView(withId(R.id.color_rgb_textview_blue)).check(matches(allOf(isDisplayed(), withText(R.string.color_blue), withTextColor(getColorById(R.color.color_chooser_rgb_blue)))));
		onView(withId(R.id.color_rgb_textview_alpha)).check(matches(allOf(isDisplayed(), withText(R.string.color_alpha), withTextColor(getColorById(R.color.color_chooser_rgb_alpha)))));

		onView(withId(R.id.color_rgb_seekbar_red)).check(matches(isDisplayed()));
		onView(withId(R.id.color_rgb_seekbar_green)).check(matches(isDisplayed()));
		onView(withId(R.id.color_rgb_seekbar_blue)).check(matches(isDisplayed()));
		onView(withId(R.id.color_rgb_seekbar_alpha)).check(matches(isDisplayed()));

		onView(withId(R.id.rgb_red_value)).check(matches(isDisplayed()));
		onView(withId(R.id.rgb_green_value)).check(matches(isDisplayed()));
		onView(withId(R.id.rgb_blue_value)).check(matches(isDisplayed()));
		onView(withId(R.id.rgb_alpha_value)).check(matches(isDisplayed()));
		onView(allOf(withText(TEXT_PERCENT_SIGN), hasSibling(withId(R.id.rgb_alpha_value)))).check(matches(isDisplayed()));

		int currentSelectColor = PaintroidApplication.currentTool.getDrawPaint().getColor();

		onView(withId(R.id.rgb_red_value)).check(matches(withText(Integer.toString(Color.red(currentSelectColor)))));
		onView(withId(R.id.rgb_green_value)).check(matches(withText(Integer.toString(Color.green(currentSelectColor)))));
		onView(withId(R.id.rgb_blue_value)).check(matches(withText(Integer.toString(Color.blue(currentSelectColor)))));
		onView(withId(R.id.rgb_alpha_value)).check(matches(
				withText(
						Integer.toString(
								((int) (Color.alpha(currentSelectColor) / 2.55f))
						)
				)
		));

		onView(withId(R.id.color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.rgb_red_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_rgb_seekbar_red)).perform(touchCenterRight());
		onView(withId(R.id.rgb_red_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.rgb_green_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_rgb_seekbar_green)).perform(touchCenterRight());
		onView(withId(R.id.rgb_green_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_rgb_seekbar_blue)).perform(touchCenterLeft());
		onView(withId(R.id.rgb_blue_value)).check(matches(withText(TEXT_RGB_MIN)));
		onView(withId(R.id.color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.rgb_blue_value)).check(matches(withText(TEXT_RGB_MAX)));

		onView(withId(R.id.color_rgb_seekbar_alpha)).perform(touchCenterLeft());
		onView(withId(R.id.rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MIN)));
		onView(withId(R.id.color_rgb_seekbar_alpha)).perform(touchCenterRight());
		onView(withId(R.id.rgb_alpha_value)).check(matches(withText(TEXT_ALPHA_MAX)));

		// Select color blue #FF0000FF by using seekbars
		onView(withId(R.id.color_rgb_seekbar_red)).perform(touchCenterLeft());
		onView(withId(R.id.color_rgb_seekbar_green)).perform(touchCenterLeft());
		onView(withId(R.id.color_rgb_seekbar_blue)).perform(touchCenterRight());
		onView(withId(R.id.color_rgb_seekbar_alpha)).perform(touchCenterRight());

		assertEquals("Selected color is not blue", PaintroidApplication.currentTool.getDrawPaint().getColor(), Color.BLUE);
	}

	@Test
	public void testOpenColorPickerOnClickOnColorButton() {
		openColorPickerDialog();
		onView(withId(R.id.colorchooser_base_layout)).check(matches(isDisplayed()));
		onView(withId(R.id.view_colorpicker)).check(matches(isDisplayed()));
	}
}
