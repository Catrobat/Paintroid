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

package org.catrobat.paintroid.test.espresso.tools;

import android.graphics.Color;
import android.net.Uri;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.util.BitmapLocationProvider;
import org.catrobat.paintroid.test.espresso.util.DrawingSurfaceLocationProvider;
import org.catrobat.paintroid.tools.ToolReference;
import org.catrobat.paintroid.tools.ToolType;
import org.catrobat.paintroid.tools.implementation.FillTool;
import org.catrobat.paintroid.ui.Perspective;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static org.catrobat.paintroid.test.espresso.util.UiInteractions.swipeAccurate;
import static org.catrobat.paintroid.test.espresso.util.UiInteractions.touchAt;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withProgress;
import static org.catrobat.paintroid.test.espresso.util.wrappers.DrawingSurfaceInteraction.onDrawingSurfaceView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolPropertiesInteraction.onToolProperties;
import static org.catrobat.paintroid.test.espresso.util.wrappers.TopBarViewInteraction.onTopBarView;
import static org.junit.Assert.assertEquals;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class FillToolIntegrationTest {

	private static final double TOLERANCE_DELTA = 0.05d;

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	private Perspective perspective;
	private ToolReference toolReference;
	private MainActivity mainActivity;

	@Before
	public void setUp() {
		mainActivity = launchActivityRule.getActivity();
		perspective = mainActivity.perspective;
		toolReference = mainActivity.toolReference;

		onToolBarView()
				.performSelectTool(ToolType.FILL);
	}

	@Test
	public void testFloodFillIfImageLoaded() {
		mainActivity.model.setSavedPictureUri(Uri.fromFile(new File("dummy")));

		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		mainActivity.model.setSavedPictureUri(null);
	}

	@Test
	public void testBitmapIsFilled() {
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testNothingHappensWhenClickedOutsideDrawingArea() {
		perspective.multiplyScale(.5f);
		onToolProperties()
				.checkMatchesColor(Color.BLACK);
		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.OUTSIDE_MIDDLE_RIGHT));
		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE);
	}

	@Test
	public void testOnlyFillInnerArea() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onToolProperties()
				.checkMatchesColor(Color.BLACK);

		onDrawingSurfaceView()
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_RIGHT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_BOTTOM_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE))
				.perform(swipeAccurate(DrawingSurfaceLocationProvider.HALFWAY_LEFT_MIDDLE, DrawingSurfaceLocationProvider.HALFWAY_TOP_MIDDLE));

		onToolBarView()
				.performSelectTool(ToolType.FILL);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_green1);

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.pocketpaint_color_picker_green1, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE_RIGHT)
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);
	}

	@Test
	public void testFillToolOptionsDialog() {
		FillTool fillTool = (FillTool) toolReference.get();
		assertEquals(
				"Wrong fill tool member value for color tolerance",
				fillTool.getToleranceAbsoluteValue(FillTool.DEFAULT_TOLERANCE_IN_PERCENT),
				fillTool.colorTolerance,
				TOLERANCE_DELTA
		);

		onToolBarView()
				.performClickSelectedToolButton();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.pocketpaint_color_tolerance_seek_bar));

		String testToleranceText = "100";

		colorToleranceInput.check(matches(withText(Integer.toString(FillTool.DEFAULT_TOLERANCE_IN_PERCENT))));

		colorToleranceInput.perform(replaceText(testToleranceText), closeSoftKeyboard());

		colorToleranceInput.check(matches(withText(testToleranceText)));
		colorToleranceSeekBar.check(matches(withProgress(Integer.parseInt(testToleranceText))));

		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(100);
		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, fillTool.colorTolerance, TOLERANCE_DELTA);

		// Close tool options
		onToolBarView()
				.performClickSelectedToolButton();
	}

	@Test
	public void testFillToolDialogAfterToolSwitch() {
		FillTool fillTool = (FillTool) toolReference.get();

		onToolBarView()
				.performClickSelectedToolButton();

		final ViewInteraction colorToleranceInput = onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input));
		final ViewInteraction colorToleranceSeekBar = onView(withId(R.id.pocketpaint_color_tolerance_seek_bar));

		int toleranceInPercent = 50;

		colorToleranceInput.perform(replaceText(String.valueOf(toleranceInPercent)));

		float expectedAbsoluteTolerance = fillTool.getToleranceAbsoluteValue(toleranceInPercent);

		assertEquals("Wrong fill tool member value for color tolerance", expectedAbsoluteTolerance, fillTool.colorTolerance, TOLERANCE_DELTA);

		// Close tool options
		onToolBarView()
				.performClickSelectedToolButton();

		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onToolBarView()
				.performSelectTool(ToolType.FILL);
		onToolBarView()
				.performClickSelectedToolButton();

		colorToleranceInput.check(matches(withText(Integer.toString(FillTool.DEFAULT_TOLERANCE_IN_PERCENT))));
		colorToleranceSeekBar.check(matches(withProgress(FillTool.DEFAULT_TOLERANCE_IN_PERCENT)));
	}

	@Ignore("Fails on Jenkins, trying out if everything works without this test or if error is due to a bug on Jenkins")
	@Test
	public void testFillToolUndoRedoWithTolerance() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		onDrawingSurfaceView()
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.MIDDLE)
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE);

		onToolProperties()
				.setColorResource(R.color.pocketpaint_color_picker_brown2)
				.checkMatchesColorResource(R.color.pocketpaint_color_picker_brown2);

		onToolBarView()
				.performSelectTool(ToolType.FILL)
				.performOpenToolOptionsView();

		onView(withId(R.id.pocketpaint_fill_tool_dialog_color_tolerance_input))
				.perform(replaceText(String.valueOf(100)));

		onToolBarView()
				.performCloseToolOptionsView();

		onDrawingSurfaceView()
				.perform(touchAt(DrawingSurfaceLocationProvider.MIDDLE));
		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.pocketpaint_color_picker_brown2, BitmapLocationProvider.MIDDLE)
				.checkPixelColorResource(R.color.pocketpaint_color_picker_brown2, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performUndo();

		onDrawingSurfaceView()
				.checkPixelColor(Color.BLACK, BitmapLocationProvider.MIDDLE)
				.checkPixelColor(Color.TRANSPARENT, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);

		onTopBarView()
				.performRedo();

		onDrawingSurfaceView()
				.checkPixelColorResource(R.color.pocketpaint_color_picker_brown2, BitmapLocationProvider.MIDDLE)
				.checkPixelColorResource(R.color.pocketpaint_color_picker_brown2, BitmapLocationProvider.HALFWAY_RIGHT_MIDDLE);
	}
}
