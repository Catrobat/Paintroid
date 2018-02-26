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

import android.content.ComponentName;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.test.espresso.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.catrobat.paintroid.test.utils.SystemAnimationsRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
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

@RunWith(AndroidJUnit4.class)
public class SlidesTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule();

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

	@Test
	public void testButtonsCompleteVisible() {
		for (int i = 0; i < activityRule.getLayouts().length - 1; i++) {
			EspressoUtils.changeIntroPage(i);
			onView(withId(R.id.btn_next))
					.check(matches(isCompletelyDisplayed()))
					.check(matches(withText(R.string.next)));
			onView(withId(R.id.btn_skip))
					.check(matches(isCompletelyDisplayed()))
					.check(matches(withText(R.string.skip)));
		}
	}

	@Test
	public void testNumberDots() {
		onView(withId(R.id.layoutDots))
				.check(matches(equalsNumberDots(activityRule.getLayouts().length)));
	}

	@Test
	public void clickSkip() {
		onView(withId(R.id.btn_skip))
				.check(matches(isDisplayed()))
				.perform(click());

		intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
	}

	@Test
	public void testCheckLastPage() {
		EspressoUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_getstarted));
		onView(withId(R.id.btn_skip))
				.check(isNotVisible());
		onView(withId(R.id.btn_next))
				.check(matches(isCompletelyDisplayed()))
				.check(matches(withText(R.string.lets_go)))
				.perform(click());

		intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
	}

	@Test
	public void testCheckDotsColor() {
		int colorActive = activityRule.getColorActive();
		int colorInactive = activityRule.getColorInactive();

		for (int i = 0; i < activityRule.getLayouts().length; i++) {
			EspressoUtils.changeIntroPage(i);

			onView(withId(R.id.layoutDots))
					.check(matches(checkDotsColors(i, colorActive, colorInactive)));
		}
	}

	@Test
	public void testWelcomeSlide() {
		EspressoUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_welcome));
		EspressoUtils.checkViewMatchesText(R.id.intro_welcome_head, R.string.welcome_to_pocket_paint);
		EspressoUtils.checkViewMatchesText(R.id.intro_welcome_text, R.string.intro_welcome_text);
	}

	@Test
	public void testPossibilitiesSlide() {
		EspressoUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_possibilities));
		EspressoUtils.checkViewMatchesText(R.id.intro_possibilities_head, R.string.more_possibilities);
		EspressoUtils.checkViewMatchesText(R.id.intro_possibilities_text, R.string.intro_possibilities_text);
	}

	@Test
	public void testLandscapeSlide() {
		EspressoUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_landscape));
		EspressoUtils.checkViewMatchesText(R.id.intro_landscape_head, R.string.landscape);
		EspressoUtils.checkViewMatchesText(R.id.intro_landscape_text, R.string.intro_landscape_text);

		onView(withId(R.id.image_getstarded))
				.check(matches(withDrawable(R.drawable.intro_portrait)));
	}

	@Test
	public void testGetStaredSlide() {
		EspressoUtils.changeIntroPage(getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_getstarted));
		EspressoUtils.checkViewMatchesText(R.id.intro_started_head, R.string.enjoy_pocket_paint);
		EspressoUtils.checkViewMatchesText(R.id.intro_started_text, R.string.intro_get_started);

		onView(withId(R.id.image_landscape))
				.check(matches(withDrawable(R.drawable.intro_landscape)));
	}

	@Test
	public void checkSkipButtonPosition() {
		onView(withId(R.id.btn_skip))
				.check(matches(isOnLeftSide()));
	}

	@Test
	public void checkNextButtonPosition() {
		onView(withId(R.id.btn_next))
				.check(matches(isOnRightSide()));
	}
}

