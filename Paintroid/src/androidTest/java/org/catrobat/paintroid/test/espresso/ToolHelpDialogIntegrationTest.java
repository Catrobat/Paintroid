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

import android.support.test.rule.ActivityTestRule;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.tools.ToolType;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.wrappers.ToolBarViewInteraction.onToolBarView;

@RunWith(Parameterized.class)
public class ToolHelpDialogIntegrationTest {

	@Rule
	public ActivityTestRule<MainActivity> launchActivityRule = new ActivityTestRule<>(MainActivity.class);

	@Parameterized.Parameter
	public ToolType currentToolType;

	@Parameterized.Parameters(name = "{0}")
	public static ToolType[] data() {
		return new ToolType[] {
				ToolType.BRUSH,
				ToolType.CURSOR,
				ToolType.PIPETTE,
				ToolType.STAMP,
				ToolType.FILL,
				ToolType.SHAPE,
				ToolType.TRANSFORM,
				ToolType.ERASER,
				ToolType.IMPORTPNG,
				ToolType.TEXT
		};
	}

	@Test
	public void testHelpDialog() {
		onToolBarView()
				.performLongClickOnTool(currentToolType);

		onView(withText(currentToolType.getHelpTextResource()))
				.check(matches(isDisplayed()));

		onView(withText(android.R.string.ok))
				.check(matches(isDisplayed()));

		onView(withText(currentToolType.getNameResource()))
				.check(matches(isDisplayed()));

		onView(withText(android.R.string.ok))
				.perform(click());
	}
}
