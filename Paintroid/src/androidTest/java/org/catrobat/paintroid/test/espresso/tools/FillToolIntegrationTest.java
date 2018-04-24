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

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Color;
import android.net.Uri;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.widget.EditText;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.NavigationDrawerMenuActivity;
import org.catrobat.paintroid.PaintroidApplication;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.dialog.IndeterminateProgressDialog;
import org.catrobat.paintroid.test.espresso.util.ActivityHelper;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DialogHiddenIdlingResource;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.BROWN2_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.GREEN_COLOR_PICKER_BUTTON_POSITION;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.clickSelectedToolButton;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.getToolMemberColorTolerance;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.openToolOptionsForCurrentTool;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectColorPickerPresetSelectorColor;
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.selectTool;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.setProgress;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FillToolIntegrationTest {

	private static final double TOLERANCE_DELTA = 0.05d;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	private ActivityHelper activityHelper;

	private IdlingResource dialogWait;

	@Before
	public void setUp() {
		dialogWait = new DialogHiddenIdlingResource(IndeterminateProgressDialog.getInstance());
		IdlingRegistry.getInstance().register(dialogWait);

		activityHelper = new ActivityHelper(launchActivityRule.getActivity());

		onToolBarView()
				.performSelectTool(ToolType.FILL);
	}

	@After
	public void tearDown() {
		IdlingRegistry.getInstance().unregister(dialogWait);
	}

	@Test
	public void testFloodFillIfImageLoaded() {
		NavigationDrawerMenuActivity.savedPictureUri = Uri.fromFile(new File("dummy"));

		onToolProperties()
				.checkColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		NavigationDrawerMenuActivity.savedPictureUri = null;
	}

	@Test
	public void testBitmapIsFilled() {
		onToolProperties()
				.checkColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testNothingHappensWhenClickedOutsideDrawingArea() {
		PaintroidApplication.perspective.multiplyScale(.5f);
		onToolProperties()
				.checkColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_RIGHT));
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.OUTSIDE_MIDDLE_RIGHT);
	}

	@Test
	public void testOnlyFillInnerArea() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onToolProperties()
				.checkColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.FILL);

		selectColorPickerPresetSelectorColor(GREEN_COLOR_PICKER_BUTTON_POSITION);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.color_chooser_green1, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE_RIGHT)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);
	}

	@Test
	public void testFillToolOptionsDialog() {
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;
		assertEquals(
				"Wrong fill tool member value for color tolerance",
				fillTool.getToleranceAbsoluteValue(FillTool.DEFAULT_TOLERANCE_IN_PERCENT),
				getToolMemberColorTolerance(fillTool),
				TOLERANCE_DELTA
		);

		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		String testToleranceText = "100";

		colorToleranceInput.check(matches(withText(Integer.toString(FillTool.DEFAULT_TOLERANCE_IN_PERCENT))));

		colorToleranceInput.perform(typeText(testToleranceText), closeSoftKeyboard());

		colorToleranceInput.check(matches(withText(testToleranceText)));
		colorToleranceSeekBar.check(matches(withProgress(Integer.parseInt(testToleranceText))));

		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(100);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		int seekBarTestValue = 50;
		colorToleranceSeekBar.perform(setProgress(seekBarTestValue));
		colorToleranceSeekBar.check(matches(withProgress(seekBarTestValue)));
		colorToleranceInput.check(matches(withText(Integer.toString(seekBarTestValue))));

		expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(50);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		// Close tool options
		clickSelectedToolButton();
	}

	@Test
	public void testFillToolDialogAfterToolSwitch() {
		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;

		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		int toleranceInPercent = 50;
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);

		colorToleranceSeekBar.perform(setProgress(toleranceInPercent));

		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		// Close tool options
		clickSelectedToolButton();

		selectTool(ToolType.BRUSH);

		selectTool(ToolType.FILL);
		openToolOptionsForCurrentTool();

		colorToleranceInput.check(matches(withText(Integer.toString(FillTool.DEFAULT_TOLERANCE_IN_PERCENT))));
		colorToleranceSeekBar.check(matches(withProgress(FillTool.DEFAULT_TOLERANCE_IN_PERCENT)));
	}

	@Test
	public void testFillToolToleranceCursorVisibility() {

		FillTool fillTool = (FillTool) PaintroidApplication.currentTool;

		openToolOptionsForCurrentTool();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.color_tolerance_seek_bar));

		EditText colorToleranceEditText = (EditText) activityHelper.findViewById(R.id.fill_tool_dialog_color_tolerance_input);
		assertFalse("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		colorToleranceInput.perform(click(), closeSoftKeyboard());

		assertTrue("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		int toleranceInPercent = 50;
		colorToleranceSeekBar.perform(setProgress(toleranceInPercent));
		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, getToolMemberColorTolerance(fillTool), TOLERANCE_DELTA);

		assertFalse("Cursor should not be visible", colorToleranceEditText.isCursorVisible());

		// Close tool options
		clickSelectedToolButton();
	}

	@Test
	public void testFillToolUndoRedoWithTolerance() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		selectColorPickerPresetSelectorColor(BROWN2_COLOR_PICKER_BUTTON_POSITION);

		onToolProperties()
				.checkColorResource(R.color.color_chooser_brown2);

		onToolBarView()
				.performSelectTool(ToolType.FILL)
				.performOpenToolOptions();

		onView(withId(R.id.color_tolerance_seek_bar))
				.perform(setProgress(100));

		onToolBarView()
				.performCloseToolOptions();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.color_chooser_brown2, BitmapLocationProvider.MIDDLE)
				.checkPixelColorResource(R.color.color_chooser_brown2, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.color_chooser_brown2, BitmapLocationProvider.MIDDLE)
				.checkPixelColorResource(R.color.color_chooser_brown2, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);
	}
}
