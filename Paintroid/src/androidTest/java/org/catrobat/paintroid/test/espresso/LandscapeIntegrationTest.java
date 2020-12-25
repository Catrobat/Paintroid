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

import androidx.annotation.ArrayRes;
import androidx.annotation.ColorInt;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackground;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withBackgroundColor;
import static org.catrobat.paintroid.test.espresso.util.wrappers.BottomNavigationViewInteraction.onBottomNavigationView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ColorPickerViewInteraction.onColorPickerView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.OptionsMenuViewInteraction.onOptionsMenu;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class LandscapeIntegrationTest {

	private MainActivity mainActivity;

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	private Tool getCurrentTool() {
		return mainActivity.toolReference.get();
	}

	private ToolOptionsViewController getToolOptionsViewController() {
		return mainActivity.toolOptionsViewController;
	}

	@Before
	public void setUp() {
		mainActivity = activityTestRule.getActivity();
	}

	@Test
	public void testLandscapeMode() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		setOrientation(SCREEN_ORIENTATION_PORTRAIT);
	}

	@Test
	public void testTools() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
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
	public void testCorrectSelectionInBothOrientationsBrushTool() {
		ToolType toolType = ToolType.BRUSH;

		onToolBarView()
			.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsCursorTool() {
		ToolType toolType = ToolType.CURSOR;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsTransformTool() {
		ToolType toolType = ToolType.TRANSFORM;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsFillTool() {
		ToolType toolType = ToolType.FILL;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsHandTool() {
		ToolType toolType = ToolType.HAND;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsEraserTool() {
		ToolType toolType = ToolType.ERASER;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsLineTool() {
		ToolType toolType = ToolType.LINE;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsPipetteTool() {
		ToolType toolType = ToolType.PIPETTE;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsShapeTool() {
		ToolType toolType = ToolType.SHAPE;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsStampTool() {
		ToolType toolType = ToolType.STAMP;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testCorrectSelectionInBothOrientationsTextTool() {
		ToolType toolType = ToolType.TEXT;

		onToolBarView()
				.performSelectTool(toolType);
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		assertEquals(toolType, getCurrentTool().getToolType());
	}

	@Test
	public void testMoreOptionsDrawerAppearsAndAllItemsExist() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onTopBarView()
				.performOpenMoreOptions();

		onOptionsMenu()
				.checkItemExists(R.string.menu_load_image)
				.checkItemExists(R.string.menu_hide_menu)
				.checkItemExists(R.string.help_title)
				.checkItemExists(R.string.pocketpaint_menu_about)
				.checkItemExists(R.string.menu_rate_us)
				.checkItemExists(R.string.menu_save_image)
				.checkItemExists(R.string.menu_save_copy)
				.checkItemExists(R.string.menu_new_image)
				.checkItemExists(R.string.share_image_menu)

				.checkItemDoesNotExist(R.string.menu_discard_image)
				.checkItemDoesNotExist(R.string.menu_export);
	}

	@Test
	public void testOpenColorPickerDialogInLandscape() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		onColorPickerView()
				.performOpenColorPicker();

		onView(withId(R.id.color_picker_view))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testOpenColorPickerDialogChooseColorInLandscape() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		onColorPickerView()
				.performOpenColorPicker();

		int[] colors = getColorArrayFromResource(activityTestRule.getActivity(), R.array.pocketpaint_color_picker_preset_colors);

		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);

			if (colors[i] != Color.TRANSPARENT) {
				onView(withId(R.id.color_picker_new_color_view))
						.perform(scrollTo())
						.check(matches(withBackgroundColor(colors[i])));
			}
		}
	}

	@Test
	public void testOpenColorPickerDialogApplyColorInLandscape() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		int[] colors = getColorArrayFromResource(activityTestRule.getActivity(), R.array.pocketpaint_color_picker_preset_colors);

		for (int i = 0; i < colors.length; i++) {
			onColorPickerView()
					.performOpenColorPicker();

			onColorPickerView()
					.performClickColorPickerPresetSelectorButton(i);

			onColorPickerView()
					.onPositiveButton()
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
				.checkCurrentViewColor(initialColor);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onColorPickerView()
				.checkCurrentViewColor(initialColor);
	}

	@Test
	public void testScrollToColorChooserOk() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		onColorPickerView()
				.performOpenColorPicker();

		onView(withText(R.string.color_picker_apply))
				.perform(scrollTo());
	}

	@Test
	public void testColorPickerDialogSwitchTabsInLandscape() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
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
		pressBack();
	}

	@Test
	public void testFullscreenPortraitOrientationChangeWithBrush() {

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		pressBack();

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	@Test
	public void testFullscreenLandscapeOrientationChangeWithBrush() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_PORTRAIT);

		pressBack();
	}

	@Test
	public void testFullscreenPortraitOrientationChangeWithShape() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		pressBack();

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	@Test
	public void testFullscreenLandscapeOrientationChangeWithShape() {
		onToolBarView()
				.performSelectTool(ToolType.SHAPE);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);

		onTopBarView()
				.performOpenMoreOptions();

		onView(withText(R.string.menu_hide_menu)).perform(click());

		setOrientation(SCREEN_ORIENTATION_PORTRAIT);

		pressBack();

		onToolBarView()
				.performOpenToolOptionsView()
				.performCloseToolOptionsView();
	}

	@Test
	public void testIfCurrentToolIsShownInBottomNavigation() {
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
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
			onBottomNavigationView()
					.checkShowsCurrentTool(toolType);
		}
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
