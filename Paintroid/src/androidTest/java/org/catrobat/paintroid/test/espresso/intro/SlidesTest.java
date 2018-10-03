/**
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2015 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.espresso.intro;

import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.espresso.util.IntroUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

import static org.catrobat.paintroid.test.espresso.util.IntroUtils.getPageIndexFromLayout;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.checkDotsColors;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.equalsNumberDots;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isNotVisible;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isOnLeftSide;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isOnRightSide;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.withDrawable;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SlidesTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule();

	@Test
	public void testButtonsCompleteVisible() {
		for (int i = 0; i < activityRule.getLayouts().length - 1; i++) {
			IntroUtils.changeIntroPage(i);
			onView(withId(R.id.pocketpaint_btn_next))
					.check(matches(isCompletelyDisplayed()))
					.check(matches(withText(R.string.next)));
			onView(withId(R.id.pocketpaint_btn_skip))
					.check(matches(isCompletelyDisplayed()))
					.check(matches(withText(R.string.skip)));
		}
	}

	@Test
	public void testNumberDots() {
		onView(withId(R.id.pocketpaint_layout_dots))
				.check(matches(equalsNumberDots(activityRule.getLayouts().length)));
	}

	@Test
	public void clickSkip() {
		onView(withId(R.id.pocketpaint_btn_skip))
				.check(matches(isDisplayed()))
				.perform(click());

		assertTrue(activityRule.getActivity().isFinishing());
	}

	@Test
	public void testCheckLastPage() {
		IntroUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.pocketpaint_slide_intro_getstarted));
		onView(withId(R.id.pocketpaint_btn_skip))
				.check(isNotVisible());
		onView(withId(R.id.pocketpaint_btn_next))
				.check(matches(isCompletelyDisplayed()))
				.check(matches(withText(R.string.lets_go)))
				.perform(click());

		assertTrue(activityRule.getActivity().isFinishing());
	}

	@Test
	public void testCheckDotsColor() {
		int colorActive = activityRule.getColorActive();
		int colorInactive = activityRule.getColorInactive();

		for (int i = 0; i < activityRule.getLayouts().length; i++) {
			IntroUtils.changeIntroPage(i);

			onView(withId(R.id.pocketpaint_layout_dots))
					.check(matches(checkDotsColors(i, colorActive, colorInactive)));
		}
	}

	@Test
	public void testWelcomeSlide() {
		IntroUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.pocketpaint_slide_intro_welcome));
		onView(withId(R.id.pocketpaint_intro_welcome_head)).check(matches(withText(R.string.welcome_to_pocket_paint)));
		onView(withId(R.id.pocketpaint_intro_welcome_text)).check(matches(withText(R.string.intro_welcome_text)));
	}

	@Test
	public void testPossibilitiesSlide() {
		IntroUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.pocketpaint_slide_intro_possibilities));
		onView(withId(R.id.pocketpaint_intro_possibilities_head)).check(matches(withText(R.string.more_possibilities)));
		onView(withId(R.id.pocketpaint_intro_possibilities_text)).check(matches(withText(R.string.intro_possibilities_text)));
	}

	@Test
	public void testLandscapeSlide() {
		IntroUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.pocketpaint_slide_intro_landscape));
		onView(withId(R.id.pocketpaint_intro_landscape_head)).check(matches(withText(R.string.landscape)));
		onView(withId(R.id.pocketpaint_intro_landscape_text)).check(matches(withText(R.string.intro_landscape_text)));

		onView(withId(R.id.pocketpaint_image_getstarded))
				.check(matches(withDrawable(R.drawable.pocketpaint_intro_portrait)));
	}

	@Test
	public void testGetStaredSlide() {
		IntroUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.pocketpaint_slide_intro_getstarted));
		onView(withId(R.id.pocketpaint_intro_started_head)).check(matches(withText(R.string.enjoy_pocket_paint)));
		onView(withId(R.id.pocketpaint_intro_started_text)).check(matches(withText(R.string.intro_get_started)));

		onView(withId(R.id.pocketpaint_image_landscape))
				.check(matches(withDrawable(R.drawable.pocketpaint_intro_landscape)));
	}

	@Test
	public void checkSkipButtonPosition() {
		onView(withId(R.id.pocketpaint_btn_skip))
				.check(matches(isOnLeftSide()));
	}

	@Test
	public void checkNextButtonPosition() {
		onView(withId(R.id.pocketpaint_btn_next))
				.check(matches(isOnRightSide()));
	}
}

