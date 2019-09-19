/*
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

package org.catrobat.paintroid.test.espresso;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.colorpicker.HSVColorPickerView;
import org.catrobat.paintroid.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.tools.Tool;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.options.ToolOptionsViewController;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getMainActivity;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LandscapeIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	private Tool getCurrentTool() {
		return getMainActivity().toolReference.get();
	}

	private ToolOptionsViewController getToolOptionsViewController() {
		return getMainActivity().toolOptionsViewController;
	}

	@Test
	public void testLandscapeMode() {
		setOrientation(SCREEN_ORIENTATION_PORTRAIT);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Test
	public void testTopBarPosition() {
		onView(withId(R.id.pocketpaint_layout_top_bar))
				.check(matches(isDisplayed()))
				.check(isCompletelyLeftOf(withId(R.id.pocketpaint_drawing_surface_view)));
	}

	@Test
	public void testTools() {
		for (ToolType toolType : ToolType.values()) {
			if (toolType == ToolType.IMPORTPNG
					|| toolType == ToolType.COLORCHOOSER
					|| toolType == ToolType.REDO
					|| toolType == ToolType.UNDO
					|| toolType == ToolType.LAYER
					|| !toolType.hasOptions()) {
				continue;
			}

			onToolBarView()
					.performSelectTool(toolType);

			assertEquals(toolType, getCurrentTool().getToolType());

			if (!getToolOptionsViewController().isVisible()) {
				onToolBarView()
						.performClickSelectedToolButton();
			}

			onView(withId(R.id.pocketpaint_main_tool_options))
					.check(matches(isDisplayed()));

			onBottomNavigationView()
					.onCurrentClicked();

			onView(withId(R.id.pocketpaint_main_tool_options))
					.check(matches(not(isDisplayed())));
		}
	}

	@Test
	public void testCorrectSelectionInBothOrientations() {
		for (ToolType toolType : ToolType.values()) {
			if (toolType == ToolType.IMPORTPNG
					|| toolType == ToolType.COLORCHOOSER
					|| toolType == ToolType.REDO
					|| toolType == ToolType.UNDO
					|| toolType == ToolType.LAYER) {
				continue;
			}

			onToolBarView()
					.performSelectTool(toolType);

			setOrientation(SCREEN_ORIENTATION_PORTRAIT);
			assertEquals(toolType, getCurrentTool().getToolType());
			setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	@Test
	public void testNavigationDrawerAppears() {
		onView(withId(R.id.pocketpaint_toolbar))
				.perform(click());
		onView(withId(R.id.pocketpaint_nav_view))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOpenColorPickerDialogInLandscape() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_view))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOpenColorPickerDialogChooseColorInLandscape() {
		onColorPickerView()
				.performOpenColorPicker();

		int[] colors = getColorArrayFromResource(activityTestRule.getActivity(), R.array.pocketpaint_color_picker_preset_colors);

		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);

			if (colors[i] != Color.TRANSPARENT) {
				onView(withId(R.id.color_picker_button_ok))
						.perform(scrollTo())
						.check(matches(withBackgroundColor(colors[i])));
			}
		}
	}

	@Test
	public void testOpenColorPickerDialogApplyColorInLandscape() {
		int[] colors = getColorArrayFromResource(activityTestRule.getActivity(), R.array.pocketpaint_color_picker_preset_colors);

		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performOpenColorPicker();

			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);

			onColorPickerView()
					.onOkButton()
					.perform(scrollTo())
					.perform(click());

			int selectedColor = getCurrentTool().getDrawPaint().getColor();
			assertEquals(colors[i], selectedColor);
		}
	}

	@Test
	public void testColorPickerCancelButtonKeepsColorInLandscape() {

		int initialColor = getCurrentTool().getDrawPaint().getColor();

		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView()
				.performClickColorPickerPresetSelectorButton(2);

		onColorPickerView()
				.checkCancelButtonColor(initialColor);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onColorPickerView()
				.checkCancelButtonColor(initialColor);
	}

	@Test
	public void testScrollToColorChooserOk() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_button_ok))
				.perform(scrollTo());
	}

	@Test
	public void testColorPickerDialogSwitchTabsInLandscape() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withClassName(is(PresetSelectorView.class.getName())))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_hsv)))
				.perform(click());
		onView(withClassName(is(HSVColorPickerView.class.getName())))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_rgba)))
				.perform(click());
		onView(withClassName(is(RgbSelectorView.class.getName())))
				.check(matches(isDisplayed()));

		onView(withId(R.id.color_picker_rgb_base_layout)).perform(swipeDown());

		onView(allOf(withId(R.id.color_picker_tab_icon), withBackground(R.drawable.ic_color_picker_tab_preset)))
				.perform(click());
		onView(withClassName(is(PresetSelectorView.class.getName())))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testFullscreenPortraitOrientationChangeWithBrush() {
		setOrientation(SCREEN_ORIENTATION_PORTRAIT);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_show_menu)).perform(click());

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	@Test
	public void testFullscreenLandscapeOrientationChangeWithBrush() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_PORTRAIT);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_show_menu)).perform(click());

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	@Test
	public void testFullscreenPortraitOrientationChangeWithShape() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		setOrientation(SCREEN_ORIENTATION_PORTRAIT);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_show_menu)).perform(click());

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	@Test
	public void testFullscreenLandscapeOrientationChangeWithShape() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_PORTRAIT);

		onNavigationDrawer()
				.performOpen();

		onView(withText(R.string.menu_show_menu)).perform(click());

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	private void setOrientation(int orientation) {
		activityTestRule.getActivity().setRequestedOrientation(orientation);
	}

	@ColorInt
	private static int[] getColorArrayFromResource(Context context, @ArrayRes int id) {
		TypedArray typedColors = context.getResources().obtainTypedArray(id);
		try {
			@ColorInt
			int[] colors = new int[typedColors.length()];
			for (int i = 0; i < typedColors.length(); i++) {
				colors[i] = typedColors.getColor(i, Color.BLACK);
			}
			return colors;
		} finally {
			typedColors.recycle();
		}
	}
}
