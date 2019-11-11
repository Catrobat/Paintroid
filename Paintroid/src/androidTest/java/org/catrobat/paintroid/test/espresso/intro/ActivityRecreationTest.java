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

package org.catrobat.paintroid.test.espresso.intro;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.catrobat.paintroid.test.espresso.util.IntroUtils.getPageIndexFromLayout;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.checkDotsColors;

@RunWith(Parameterized.class)
public class ActivityRecreationTest {
	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule();

	@Parameter
	public String name;

	@Parameter(1)
	public int layout;

	@Parameter(2)
	public int textView;

	@Parameters(name = "{0}")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{"Welcome", R.layout.pocketpaint_slide_intro_welcome, R.id.pocketpaint_intro_welcome_text},
				{"Tools", R.layout.pocketpaint_slide_intro_tools_selection, R.id.pocketpaint_intro_tools_textview},
				{"Possibilities", R.layout.pocketpaint_slide_intro_possibilities, R.id.pocketpaint_intro_possibilities_textview},
				{"Landscape", R.layout.pocketpaint_slide_intro_landscape, R.id.pocketpaint_intro_landscape_text},
				{"Getstarted", R.layout.pocketpaint_slide_intro_getstarted, R.id.pocketpaint_intro_started_text}
				});
	}

	@Test
	public void testToolsPageDoesNotCrashAfterRecreate() {
		int toolsPageIndex = getPageIndexFromLayout(activityRule.getLayouts(), layout);
		IntroUtils.changeIntroPage(toolsPageIndex);

		activityRule.recreateActivity();

		onView(withId(textView))
				.check(matches(isDisplayed()));
	}

	@Test
	public void testToolsPageRestoreDotsColor() {
		int toolsPageIndex = getPageIndexFromLayout(activityRule.getLayouts(), layout);
		IntroUtils.changeIntroPage(toolsPageIndex);

		activityRule.recreateActivity();

		int colorActive = activityRule.getColorActive();
		int colorInactive = activityRule.getColorInactive();

		onView(withId(R.id.pocketpaint_layout_dots))
				.check(matches(checkDotsColors(toolsPageIndex, colorActive, colorInactive)));
	}
}
