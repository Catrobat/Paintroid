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
import org.catrobat.paintroid.test.espresso.intro.base.IntroTestBase;
import org.catrobat.paintroid.test.espresso.util.EspressoUtils;
import org.junit.Before;
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
import static org.catrobat.paintroid.test.espresso.util.EspressoUtils.waitMillis;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.checkDotsColors;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.equalsNumberDots;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isNotVisible;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isOnLeftSide;
import static org.catrobat.paintroid.test.espresso.util.UiMatcher.isOnRightSide;
import static org.junit.Assert.assertTrue;


@RunWith(AndroidJUnit4.class)
public class RTLTest extends IntroTestBase {

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        rtl = true;
        super.setUpAndLaunchActivity();
    }

    @Test
    public void testNumberDots() {
        onView(withId(R.id.layoutDots)).check(matches(equalsNumberDots(layouts.length)));
    }

    @Test
    public void clickSkip() {
        waitMillis(100);
        onView(withId(R.id.btn_skip)).check(matches(isDisplayed())).perform(click());
        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }

    @Test
    public void testCheckLastPage() {
        EspressoUtils.changeIntroPage(getPageIndexFormLayout(R.layout.islide_getstarted));
        onView(withId(R.id.btn_skip)).check(isNotVisible());
        onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.btn_next)).check(matches(withText(R.string.got_it)));
        onView(withId(R.id.btn_next)).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }

    @Test
    public void testButtonsCompleteVisible() {
        for (int i = layouts.length-1; i < 0 ; i--) {
            EspressoUtils.changeIntroPage(i);
            onView(withId(R.id.btn_skip)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_skip)).check(matches(withText(R.string.next)));
            onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_next)).check(matches(withText(R.string.skip)));
        }
    }

    @Test
    public void testCheckDotsColor() {
        for (int i = layouts.length-1; i < 0 ; i--) {
            EspressoUtils.changeIntroPage(i);
            onView(withId(R.id.layoutDots)).check(matches(checkDotsColors(i, colorActive, colorInactive)));
        }
    }

    @Test
    public void isSkipButtonOppositeSide() {
        waitMillis(100);
        onView(withId(R.id.btn_skip)).check(matches(isOnRightSide()));
    }

    @Test
    public void isNextButtonOppositeSide() {
        waitMillis(100);
        onView(withId(R.id.btn_next)).check(matches(isOnLeftSide()));
    }

}
