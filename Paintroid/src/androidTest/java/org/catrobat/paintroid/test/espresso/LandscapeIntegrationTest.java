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

package org.catrobat.paintroid.test.espresso;

import android.content.res.Resources;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.Utils;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.PositionAssertions.isCompletelyLeftOf;
import static android.support.test.espresso.assertion.PositionAssertions.isCompletelyRightOf;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.NavigationDrawerInteraction.onNavigationDrawer;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
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

	@Test
	public void testLandscapeMode() {
		setOrientation(SCREEN_ORIENTATION_PORTRAIT);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Test
	public void testBottomBarPosition() {
		onView(withId(R.id.pocketpaint_main_bottom_bar))
				.check(matches(isDisplayed()))
				.check(isCompletelyRightOf(withId(R.id.pocketpaint_drawing_surface_view)));
	}

	@Test
	public void testTopBarPosition() {
		onView(withId(R.id.pocketpaint_layout_top_bar))
				.check(matches(isDisplayed()))
				.check(isCompletelyLeftOf(withId(R.id.pocketpaint_drawing_surface_view)));
	}

	@Test
	public void testToolBarOptionWidth() {
		onToolBarView()
				.performOpenToolOptions();

		onView(withId(R.id.pocketpaint_main_tool_options))
				.check(matches(isDisplayed()))
				.check(isCompletelyRightOf(withId(R.id.pocketpaint_layout_top_bar)))
				.check(isCompletelyLeftOf(withId(R.id.pocketpaint_main_bottom_bar)));

		onView(withId(R.id.pocketpaint_layout_top_bar))
				.check(matches(isDisplayed()));
		onView(withId(R.id.pocketpaint_main_bottom_bar))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testToolBarOption() {
		onToolBarView()
				.performSelectTool(ToolType.PIPETTE)
				.performOpenToolOptions();

		onView(withId(R.id.pocketpaint_main_tool_options))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testTools() {
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

			assertEquals(toolType, PaintroidApplication.currentTool.getToolType());

			if (!PaintroidApplication.currentTool.getToolOptionsAreShown()) {
				onToolBarView()
						.performOpenToolOptions();
			}

			onView(withId(R.id.pocketpaint_main_tool_options))
					.check(matches(isDisplayed()));

			onToolBarView()
					.performCloseToolOptions();

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
			assertEquals(toolType, PaintroidApplication.currentTool.getToolType());
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
				.performOpenColorPicker()
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOpenColorPickerDialogChooseColorInLandscape() {
		onColorPickerView()
				.performOpenColorPicker();

		Resources resources = activityTestRule.getActivity().getResources();
		int[] colors = resources.getIntArray(R.array.pocketpaint_color_chooser_preset_colors);
		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);

			int selectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
			Utils.assertColorEquals(colors[i], selectedColor);

			onView(withId(R.id.color_chooser_button_ok))
					.perform(scrollTo())
					.check(matches(withBackgroundColor(colors[i])));
		}
	}

	@Test
	public void testScrollToColorChooserOk() {
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_chooser_button_ok))
				.perform(scrollTo());
	}

	@Test
	public void testColorPickerDialogSwitchTabsInLandscape() {
		onColorPickerView()
				.performOpenColorPicker();

		onColorPickerView().onPresetSelectorView()
				.check(matches(isDisplayed()));

		onColorPickerView().onColorChooserTabHSV()
				.perform(scrollTo(), click());
		onColorPickerView().onHSVColorPickerView()
				.check(matches(isDisplayed()));

		onColorPickerView().onColorChooserTabRgba()
				.perform(scrollTo(), click());
		onColorPickerView().onRgbSelectorView()
				.check(matches(isDisplayed()));

		onColorPickerView().onColorChooserTabPreset()
				.perform(scrollTo(), click());
		onColorPickerView().onPresetSelectorView()
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
				.performOpenToolOptions()
				.performCloseToolOptions();
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
				.performOpenToolOptions()
				.performCloseToolOptions();
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
				.performOpenToolOptions()
				.performCloseToolOptions();
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
				.performOpenToolOptions()
				.performCloseToolOptions();
	}

	private void setOrientation(int orientation) {
		activityTestRule.getActivity().setRequestedOrientation(orientation);
	}
}
