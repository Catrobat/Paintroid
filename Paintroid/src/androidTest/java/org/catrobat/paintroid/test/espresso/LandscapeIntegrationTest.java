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

package org.catrobat.paintroid.test.espresso;

import android.graphics.Color;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.colorpicker.HSVColorPickerView;
import org.catrobat.paintroid.dialog.colorpicker.PresetSelectorView;
import org.catrobat.paintroid.dialog.colorpicker.RgbSelectorView;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
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
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickColorPickerPresetSelectorButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getColorArrayFromResource;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openColorPickerDialog;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.resetDrawPaintAndBrushPickerView;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class LandscapeIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Before
	public void setUp() {
		resetDrawPaintAndBrushPickerView();
		selectTool(ToolType.BRUSH);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Test
	public void testLandscapeMode() {
		setOrientation(SCREEN_ORIENTATION_PORTRAIT);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Test
	public void testBottomBarPosition() {
		onView(withId(R.id.main_bottom_bar))
				.check(matches(isDisplayed()))
				.check(isCompletelyRightOf(withId(R.id.drawingSurfaceView)));
	}

	@Test
	public void testTopBarPosition() {
		onView(withId(R.id.layout_top_bar))
				.check(matches(isDisplayed()))
				.check(isCompletelyLeftOf(withId(R.id.drawingSurfaceView)));
	}

	@Test
	public void testToolBarOptionWidth() {
		openToolOptionsForCurrentTool();

		onView(withId(R.id.main_tool_options))
				.check(matches(isDisplayed()))
				.check(isCompletelyRightOf(withId(R.id.layout_top_bar)))
				.check(isCompletelyLeftOf(withId(R.id.main_bottom_bar)));

		onView(withId(R.id.layout_top_bar))
				.check(matches(isDisplayed()));
		onView(withId(R.id.main_bottom_bar))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testToolBarOption() {
		selectTool(ToolType.PIPETTE);
		openToolOptionsForCurrentTool();

		onView(withId(R.id.main_tool_options))
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

			selectTool(toolType);

			assertEquals(toolType, PaintroidApplication.currentTool.getToolType());

			if (!PaintroidApplication.currentTool.getToolOptionsAreShown()) {
				openToolOptionsForCurrentTool();
			}

			onView(withId(R.id.main_tool_options))
					.check(matches(isDisplayed()));

			openToolOptionsForCurrentTool();

			onView(withId(R.id.main_tool_options))
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

			selectTool(toolType);

			setOrientation(SCREEN_ORIENTATION_PORTRAIT);
			assertEquals(toolType, PaintroidApplication.currentTool.getToolType());
			setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		}
	}

	@Test
	public void testNavigationDrawerAppears() {
		onView(withId(R.id.toolbar))
				.perform(click());
		onView(withId(R.id.nav_view))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOpenColorPickerDialogInLandscape() {
		openColorPickerDialog();

		onView(withId(R.id.view_colorpicker))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOpenColorPickerDialogChooseColorInLandscape() {
		openColorPickerDialog();

		int[] colors = getColorArrayFromResource(activityTestRule.getActivity(), R.array.preset_colors);

		for (int i = 0; i < colors.length; i++) {
			clickColorPickerPresetSelectorButton(i);

			if (colors[i] != Color.TRANSPARENT) {
				int selectedColor = PaintroidApplication.currentTool.getDrawPaint().getColor();
				assertEquals(colors[i], selectedColor);

				onView(withId(R.id.btn_colorchooser_ok))
						.perform(scrollTo())
						.check(matches(withBackgroundColor(colors[i])));
			}
		}
	}

	@Test
	public void testScrollToColorChooserOk() {
		openColorPickerDialog();

		onView(withId(R.id.btn_colorchooser_ok))
				.perform(scrollTo());
	}

	@Test
	public void testColorPickerDialogSwitchTabsInLandscape() {
		openColorPickerDialog();

		onView(withClassName(is(PresetSelectorView.class.getName())))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_circle)))
				.perform(click());
		onView(withClassName(is(HSVColorPickerView.class.getName())))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_rgba)))
				.perform(click());
		onView(withClassName(is(RgbSelectorView.class.getName())))
				.check(matches(isDisplayed()));

		onView(allOf(withId(R.id.tab_icon), withBackground(R.drawable.icon_color_chooser_tab_palette)))
				.perform(click());
		onView(withClassName(is(PresetSelectorView.class.getName())))
				.check(matches(isDisplayed()));
	}

	private void setOrientation(int orientation) {
		activityTestRule.getActivity().setRequestedOrientation(orientation);
	}
}
