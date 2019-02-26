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

import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.utils.ScreenshotOnFailRule;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class LandscapeToolIntegrationTest {

	@Rule
	public ScreenshotOnFailRule screenshotOnFailRule = new ScreenshotOnFailRule();

	@Parameter
	public ToolType toolType;

	@Parameters(name = "{0}")
	public static Iterable<ToolType> data() {
		return Arrays.asList(
				ToolType.BRUSH,
				ToolType.SHAPE,
				ToolType.TRANSFORM,
				ToolType.LINE,
				ToolType.CURSOR,
				ToolType.FILL,
				ToolType.PIPETTE,
				ToolType.STAMP,
				ToolType.ERASER,
				ToolType.TEXT);
	}

	@Rule
	public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Before
	public void setUp() {
		onToolBarView()
				.performSelectTool(ToolType.BRUSH);

		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	@Test
	public void testToolOptionsVisible() {

		onToolBarView()
				.performSelectTool(toolType);

		assertEquals(toolType, getCurrentToolType());

		if (!toolType.showOptionsInitially()) {
			onToolBarView()
					.performClickSelectedToolButton();
		}

		if (toolType.hasOptions()) {
			onView(withId(R.id.pocketpaint_main_tool_options))
					.check(matches(isDisplayed()));

			onToolBarView()
					.performClickSelectedToolButton();

			onView(withId(R.id.pocketpaint_main_tool_options))
					.check(matches(not(isDisplayed())));
		}
	}

	@Test
	public void testCorrectSelectionInBothOrientations() {
		onToolBarView()
				.performSelectTool(toolType);

		setOrientation(SCREEN_ORIENTATION_PORTRAIT);
		assertEquals(toolType, getCurrentToolType());
		setOrientation(SCREEN_ORIENTATION_LANDSCAPE);
	}

	private ToolType getCurrentToolType() {
		return activityTestRule.getActivity().currentTool.get().getToolType();
	}

	private void setOrientation(int orientation) {
		activityTestRule.getActivity().setRequestedOrientation(orientation);
	}
}
