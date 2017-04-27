<<<<<<< HEAD
=======
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

package org.catrobat.paintroid.test.junit.ui;


import android.content.ComponentName;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import org.catrobat.paintroid.MainActivity;
import org.catrobat.paintroid.R;
import org.catrobat.paintroid.WelcomeActivity;
import org.catrobat.paintroid.test.utils.PrivateAccess;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.checkDotsColors;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.equalsNumberDots;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.isNotVisible;
import static org.catrobat.paintroid.test.junit.EspressoHelpers.withDrawable;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class IntroTest extends IntroTestBase {


    private int colorActive;
    private int colorInactive;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        super.setUp();
        colorActive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorActive");
        colorInactive = (int) PrivateAccess.getMemberValue(WelcomeActivity.class, activity, "colorInactive");
    }


    @Test
    public void testButtonsCompleteVisible() {

        for (int i = 0; i < layouts.length - 1; i++) {
            changePage(i);
            onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_next)).check(matches(withText(R.string.next)));
            onView(withId(R.id.btn_skip)).check(matches(isCompletelyDisplayed()));
            onView(withId(R.id.btn_skip)).check(matches(withText(R.string.skip)));
        }
    }

    @Test
    public void testNumberDots() {
        onView(withId(R.id.layoutDots)).check(matches(equalsNumberDots(layouts.length)));
    }

    @Test
    public void testCheckLastPage() {
        changePageFromLayoutResource(R.layout.islide_getstarted);
        onView(withId(R.id.btn_skip)).check(isNotVisible());
        onView(withId(R.id.btn_next)).check(matches(isCompletelyDisplayed()));
        onView(withId(R.id.btn_next)).check(matches(withText(R.string.got_it)));
        onView(withId(R.id.btn_next)).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }

    @Test
    public void testCheckDotsColor() {
        for (int i = 0; i < layouts.length; i++) {
            changePage(i);
            onView(withId(R.id.layoutDots)).check(matches(checkDotsColors(i, colorActive, colorInactive)));
        }
    }

    @Test
    public void testWelcomeSlide() {
        changePageFromLayoutResource(R.layout.islide_welcome);
        checkSlideText(R.id.intro_welcome_head, R.string.welcome_to_pocket_paint);
        checkSlideText(R.id.intro_welcome_text, R.string.intro_welcome_text);
    }

    @Test
    public void testToolsSlide() {
        changePageFromLayoutResource(R.layout.islide_tools);
        checkSlideText(R.id.intro_tools_head, R.string.dialog_tools_title);
        checkSlideText(R.id.intro_tools_text, R.string.intro_tool_more_information);
    }

    @Test
    public void testPossibilitiesSlide() {
        changePageFromLayoutResource(R.layout.islide_possibilities);
        checkSlideText(R.id.intro_possibilities_head, R.string.more_possibilities);
        checkSlideText(R.id.intro_possibilities_text, R.string.intro_possibilities_text);
    }

    @Test
    public void testLandscapeSlide() {
        changePageFromLayoutResource(R.layout.islide_landscape);
        checkSlideText(R.id.intro_landscape_head, R.string.landscape);
        checkSlideText(R.id.intro_landscape_text, R.string.intro_landscape_text);
        onView(withId(R.id.image_getstarded)).check(matches(withDrawable(R.drawable.intro_portrait)));
    }

    @Test
    public void testGetStaredSlide() {
        changePageFromLayoutResource(R.layout.islide_getstarted);
        checkSlideText(R.id.intro_started_head, R.string.enjoy_pocket_code);
        checkSlideText(R.id.intro_started_text, R.string.intro_get_started);
        onView(withId(R.id.image_landscape)).check(matches(withDrawable(R.drawable.intro_landscape)));
    }

}
>>>>>>> Added UI Testcase, Added new Espresso View Assertions, Refactoring

