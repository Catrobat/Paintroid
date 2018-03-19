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

package org.catrobat.catroid.uiespresso.paintroid.intro;

import android.content.ComponentName;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.catroid.paintroid.MainActivity;
import org.catrobat.catroid.paintroid.R;
import org.catrobat.catroid.uiespresso.paintroid.intro.util.WelcomeActivityIntentsTestRule;
import org.catrobat.catroid.uiespresso.paintroid.util.EspressoUtils;
import org.catrobat.catroid.uiespresso.paintroid.util.IntroUtils;
import org.catrobat.catroid.common.paintroid.SystemAnimationsRule;
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

import static org.catrobat.catroid.uiespresso.paintroid.util.UiMatcher.checkDotsColors;
import static org.catrobat.catroid.uiespresso.paintroid.util.UiMatcher.equalsNumberDots;
import static org.catrobat.catroid.uiespresso.paintroid.util.UiMatcher.isNotVisible;
import static org.catrobat.catroid.uiespresso.paintroid.util.UiMatcher.isOnLeftSide;
import static org.catrobat.catroid.uiespresso.paintroid.util.UiMatcher.isOnRightSide;

@RunWith(AndroidJUnit4.class)
public class RTLTest {

	@Rule
	public WelcomeActivityIntentsTestRule activityRule = new WelcomeActivityIntentsTestRule(false, true);

	@Rule
	public SystemAnimationsRule systemAnimationsRule = new SystemAnimationsRule();

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
		EspressoUtils.changeIntroPage(IntroUtils.getPageIndexFromLayout(activityRule.getLayouts(), R.layout.islide_getstarted));
		onView(withId(R.id.btn_skip))
				.check(isNotVisible());
		onView(withId(R.id.btn_next))
				.check(matches(isCompletelyDisplayed()))
				.check(matches(withText(R.string.lets_go)))
				.perform(click());

		intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
	}

	@Test
	public void testButtonsCompleteVisible() {
		for (int i = activityRule.getLayouts().length - 1; i < 0; i--) {
			EspressoUtils.changeIntroPage(i);
			onView(withId(R.id.btn_skip))
					.check(matches(isCompletelyDisplayed()))
					.check(matches(withText(R.string.next)));
			onView(withId(R.id.btn_next))
					.check(matches(isCompletelyDisplayed()))
					.check(matches(withText(R.string.skip)));
		}
	}

	@Test
	public void testCheckDotsColor() {
		int colorActive = activityRule.getColorActive();
		int colorInactive = activityRule.getColorInactive();

		for (int i = activityRule.getLayouts().length - 1; i < 0; i--) {
			EspressoUtils.changeIntroPage(i);
			onView(withId(R.id.layoutDots))
					.check(matches(checkDotsColors(i, colorActive, colorInactive)));
		}
	}

	@Test
	public void isSkipButtonOppositeSide() {
		onView(withId(R.id.btn_skip))
				.check(matches(isOnRightSide()));
	}

	@Test
	public void isNextButtonOppositeSide() {
		onView(withId(R.id.btn_next))
				.check(matches(isOnLeftSide()));
	}
}
